package agha.opencga.gui

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.transaction.Transactional
import htsjdk.samtools.BAMFileReader
import htsjdk.samtools.DefaultSAMRecordFactory
import htsjdk.samtools.SAMFileHeader
import htsjdk.samtools.SAMReadGroupRecord
import htsjdk.samtools.ValidationStringency
import htsjdk.variant.vcf.VCFFileReader
import htsjdk.variant.vcf.VCFHeader
import org.apache.log4j.Logger
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate

/**
 * Service for interacting with OpenCGA-1.0.0-rc3
 *
 */
@Transactional
class OpenCGAService {

    Logger logger = Logger.getLogger(OpenCGAService.class)
    def grailsApplication
    SpringSecurityService springSecurityService

    /**
     * Get the current user and default password
     * @return
     */
    def userPassword() {
        User user = springSecurityService.currentUser
        String password = grailsApplication.config.opencga.password

        return [user, password]
    }

    /**
     * Build the URL for the openCGA rest services using the provided suffix
     * @param suffix
     * @return
     */
    public String buildOpencgaRestUrl(String suffix) {
        String opencgaUrl = grailsApplication.config.opencga.rest.url
        if (! suffix.startsWith('/')) {
            opencgaUrl += '/'
        }

        opencgaUrl += suffix

        return opencgaUrl
    }

    /**
     * Login to openCGA rest webservices returning the sessionId to be used in subsequent requests
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        logger.info("login")

        Map passwordMap = ['password': password]
        String json = (passwordMap as JSON).toString()

        String loginUrl = buildOpencgaRestUrl('/users/'+username+'/login')
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(loginUrl) {
            contentType('application/json')
            body(json)
        }

        logger.info('json response: '+resp.json)

        String sessionId = resp.json?.response?.get(0)?.result?.get(0)?.sessionId
        logger.info('sessionId: '+sessionId)
        return sessionId
    }

    /**
     * Helper method to login current user quickly
     * @return
     */
    public String loginCurrentUser() {
        def(user, password) = this.userPassword()
        //String opencgaUsername = this.opencgaUsername(user.username)
        String sessionId = this.login(user.username, password)
        return sessionId
    }

    /**
     * Login the AGHA user
     * @return
     */
    public String loginAgha() {
        String username = grailsApplication.config.opencga.agha.user
        String password = grailsApplication.config.opencga.password
        return login(username, password)
    }

    public loginCli(String username, String password) {

        // echo agha | ./opencga.sh users login -u philip.wu@anu.edu.au -p

        String opencgaBin = grailsApplication.config.opencga.bin

        List loginCmd = ['/bin/bash', '-c', 'echo agha | '+opencgaBin+'/opencga.sh users login -u philip.wu@anu.edu.au -p']

        // Create user command
//        String opencgaBin = grailsApplication.config.opencga.bin
//        String script = opencgaBin + '/opencga-admin.sh'
//        List createUserCommand = ['sh', '-c', script, 'users', 'create', '-u', userId, '--email', email, '--name', name, '--user-password', password]
//        List createUserCommand = ['sh', '-c', script, userId, email, name]
//        String std = CliExec.execCommand(createUserCommand)
//        logger.info('std: '+std)
//        // Password command
//        String adminPassword = grailsApplication.config.opencga.password
//        List passwordCommand = ['echo', adminPassword]
//        def p = createUserCommand.execute()
        //def p = ['sh', '-c', script+' users create -u '+userId+' --email '+email+' --name '+name+' --user-password agha'].execute()
        //def std = p.waitFor()
//        logger.info('std: '+p.text)

    }

    /**
     * Make sure the admin password is already defaulted in /opt/opencga/conf/configuration.yml
     * @param userId
     * @param name
     * @param password
     * @param email
     * @return
     */
    public String userCreate(String userId, String name, String password, String email) {


        JSONObject json = new JSONObject()
        json.put('userId', userId)
        json.put('name', name)
        json.put('password', password)
        json.put('email', email)

        String url = buildOpencgaRestUrl('/users/create')
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(url) {
            contentType('application/json')
            body(json.toString())
        }

        logger.info('resp='+resp.json)

        // echo agha | ./opencga-admin.sh  users create -u hongyu.ma2@anu.edu.au --email hongyu.ma@anu.edu.au --name hongyu2 --user-password agha --password

        // Create user command
//        String opencgaBin = grailsApplication.config.opencga.bin
//        String script = opencgaBin + '/opencga-admin.sh'
//        List createUserCommand = ['sh', '-c', script, 'users', 'create', '-u', userId, '--email', email, '--name', name, '--user-password', password]
//        List createUserCommand = ['sh', '-c', script, userId, email, name]
//        String std = CliExec.execCommand(createUserCommand)
//        logger.info('std: '+std)
//        // Password command
//        String adminPassword = grailsApplication.config.opencga.password
//        List passwordCommand = ['echo', adminPassword]
//        def p = createUserCommand.execute()
        //def p = ['sh', '-c', script+' users create -u '+userId+' --email '+email+' --name '+name+' --user-password agha'].execute()
        //def std = p.waitFor()
//        logger.info('std: '+p.text)

    }

    /**
     * Projects accessible for the given user
     *
     * Example project:
     *
     {
     "id": 15,
     "name": "myproject",
     "alias": "myproject",
     "creationDate": "20170208113435",
     "description": "",
     "organization": "",
     "organism": {
     "scientificName": "Homo sapiens",
     "commonName": "GRCh37",
     "taxonomyCode": 9606,
     "assembly": "GRCh37"
     },
     "status": {
     "name": "READY",
     "date": "20170208113435",
     "message": ""
     },
     "size": 0,
     "studies": [
     {
     "acl": [],
     "id": 16,
     "name": "mystudy",
     "alias": "mystudy",
     "type": "CASE_CONTROL",
     "creationDate": "20170208113640",
     "description": "",
     "status": {
     "name": "READY",
     "date": "20170208113640",
     "message": ""
     },
     "lastModified": "20170208113640",
     "size": 0,
     "cipher": "none",
     "groups": [],
     "experiments": [],
     "files": [],
     "jobs": [],
     "individuals": [],
     "samples": [],
     "datasets": [],
     "cohorts": [],
     "panels": [],
     "variableSets": [],
     "uri": "file:///opt/opencga/sessions/users/hongyu/projects/15/16/",
     "dataStores": {},
     "stats": {},
     "attributes": {}
     }
     ],
     "dataStores": {},
     "attributes": {}
     }
     ]
     }

     *
     * @param sessionId
     * @param user
     */
    def userProjects(String sessionId, String user, boolean shared=false) {

        String url = buildOpencgaRestUrl('/users/'+user+'/projects?sid='+sessionId+'&shared='+shared)
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('json='+resp.json)
        return resp.json
    }

    def projectInfo(String sessionId, String projectId) {

        String url = buildOpencgaRestUrl('/projects/'+projectId+'/info?sid='+sessionId)
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('json='+resp.json)
        return resp.json
    }

    def projectCreate(String sessionId, String name) {

        String alias = name.replaceAll("\\s", "")

        JSONObject json = new JSONObject()
        // name
        json.put('name', name)
        // alias
        json.put('alias', alias)
        // organism.scientificName
        json.put('organism.scientificName', 'Homo Sapiens')
        // organism.assembly
        json.put('organism.assembly', 'GRCh37')


        String loginUrl = buildOpencgaRestUrl('/projects/create?sid='+sessionId)
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(loginUrl) {
            contentType('application/json')
            body(json.toString())
        }

        logger.info('json='+resp.json)
        return resp.json


    }

    /**
     * List of all projects accessible for the user
     * @param sessionId
     * @param user
     * @return
     */
    public List accessibleUserProjects(String sessionId, String user) {

        List projects = []

        // THE ORDER HERE IS VERY IMPORTANT WHEN BUILDING A MAP OF PROJECTS AS THE ACCESSIBLE STUDIES WILL CHANGE
        // PROJECTS THAT ARE BOTH SHARED AND OWNED
        def jsonSharedProjects = userProjects(sessionId, user, true)
        jsonSharedProjects.response.get(0).result.each { project ->
            projects << project
        }


        def jsonOwnedProjects = userProjects(sessionId, user, false)
        jsonOwnedProjects.response.get(0).result.each { project ->
            projects << project
        }


        return projects
    }

    /**
     * Map project ID to project
     * @param sessionId
     * @param user
     */
    def mapAccessibleProjects(String sessionId, String user) {

        Map projectIdMap = [:]

        List projects =  accessibleUserProjects(sessionId, user)
        for (def project: projects) {
            projectIdMap.put(project.id, project)
        }

        return projectIdMap
    }


    def studyCreate(String sessionId, String name, String projectId) {
        logger.info('Creating study: '+name)
        String alias = name.replaceAll("\\s", "")



        JSONObject json = new JSONObject()

        // name
        json.put('name', name)
        // alias
        json.put('alias', alias)
        // type
        json.put('type', 'CASE_CONTROL')

        String loginUrl = buildOpencgaRestUrl('/studies/create?sid='+sessionId+'&projectId='+projectId)
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(loginUrl) {
            contentType('application/json')
            body(json.toString())
        }

        logger.info('json='+resp.json)
        return resp.json


    }

    /**
     * Return a list of studies accessible for the user associated with the given sessionId
     *
     *
     {
     "acl": [],
     "id": 16,
     "name": "mystudy",
     "alias": "mystudy",
     "type": "CASE_CONTROL",
     "creationDate": "20170208113640",
     "description": "",
     "status": {
     "name": "READY",
     "date": "20170208113640",
     "message": ""
     },
     "lastModified": "20170208113640",
     "size": 0,
     "cipher": "none",
     "groups": [],
     "experiments": [],
     "files": [],
     "jobs": [],
     "individuals": [],
     "samples": [],
     "datasets": [],
     "cohorts": [],
     "panels": [],
     "variableSets": [],
     "uri": "file:///opt/opencga/sessions/users/hongyu/projects/15/16/",
     "dataStores": {},
     "stats": {},
     "attributes": {}
     }
     *
     * @param sessionId
     * @return
     */
    def searchStudies(String sessionId) {

        String url = buildOpencgaRestUrl('/studies/search?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('json='+resp.json)

        // Return a list of studies in JSON format
        return resp.json.response.get(0).result
    }

    /**
     * Get study info
     *
     * @param sessionId
     * @param studyId
     * @return
     */
    def studyInfo(String sessionId, String studyId) {
        String url = buildOpencgaRestUrl('/studies/'+studyId+'/info?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('json='+resp.json)

        return getJsonResult(resp.json).get(0)
    }

    /**
     * Example output:
     *
     *         {
     "acl": [],
     "id": 5,
     "name": "APOSLE_cohort88",
     "type": "DIRECTORY",
     "format": "PLAIN",
     "bioformat": "NONE",
     "uri": "file:///opt/opencga/sessions/users/philipwu/projects/1/2/vcfs/APOSLE_cohort88",
     "path": "vcfs/APOSLE_cohort88/",
     "creationDate": "20170203113450",
     "modificationDate": "20170203113450",
     "description": "",
     "status": {
     "name": "READY",
     "date": "20170203113450",
     "message": ""
     },
     "external": false,
     "size": 0,
     "experiment": {
     "id": -1
     },
     "sampleIds": [],
     "job": {
     "acl": [],
     "id": -1,
     "creationDate": "20170208115707",
     "startTime": 1486515427421,
     "endTime": -1,
     "outputError": "",
     "visits": -1,
     "status": {
     "name": "PREPARED",
     "date": "20170208115707",
     "message": ""
     },
     "size": 0,
     "outDirId": -1,
     "input": [],
     "output": [],
     "tags": [],
     "params": {},
     "attributes": {
     "type": "ANALYSIS"
     },
     "resourceManagerAttributes": {
     "jobSchedulerName": ""
     }
     },
     "relatedFiles": [],
     "index": {
     "status": {
     "name": "NONE",
     "date": "20170203113450",
     "message": ""
     },
     "jobId": -1,
     "attributes": {}
     }
     }
     *
     * @param sessionId
     * @param studyId
     * @return
     */
    def studyFiles(String sessionId, String studyId) {

        String url = buildOpencgaRestUrl('/studies/'+studyId+'/files?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('studyFiles json='+resp.json)

        return getJsonResult(resp.json)
    }

    def studyAclCreate(String sessionId, String studyId, String members, String templateId) {
        logger.info('studyAclCreate studyId:'+studyId+' members:'+members)
        /*
        {"members":"jingjing.tan@anu.edu.au","templateId":"view_only"}
         */
        JSONObject json = new JSONObject()
        json['members'] = members
        json['templateId'] = templateId

        // post /{version}/studies/{study}/acl/create
        String url = buildOpencgaRestUrl('/studies/'+studyId+'/acl/create?sid='+sessionId)
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(url) {
            contentType('application/json')
            body(json.toString())
        }

        return resp.json
    }

    // get /{version}/studies/{study}/acl/{memberId}/delete
    def studyAclDelete(String sessionId, String studyId, String user) {
        logger.info('delete ACL for studyId: '+studyId+ ' and user: '+user)
        String url = buildOpencgaRestUrl('/studies/'+studyId+'/acl/'+user+'/delete?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        return resp.json
    }

    /**
     * Downlad the file via opencga rest webservices
     * @param sessionId
     * @param fileId
     * @param outputStream
     * @return
     */
    def fileDownload(String sessionId, String fileId, def httpResponse) {
        logger.info('sessionId: '+sessionId+' fileId: '+fileId)
        String url = buildOpencgaRestUrl('/files/'+fileId+'/download?sid='+sessionId)

        //RestBuilder rest = new RestBuilder()
        //RestResponse resp = rest.get(url)
        //logger.info('file download='+resp.text)

        RequestCallback requestCallback = new RequestCallback() {
            void doWithRequest(ClientHttpRequest clientRequest) throws IOException {
                //clientRequest.headers.add('accept', 'application/octet-stream')
            }
        }
        ResponseExtractor responseExtractor = new ResponseExtractor() {
            Object extractData(ClientHttpResponse clientResponse) throws IOException {
                System.out.println('Headers: '+clientResponse.headers)
                httpResponse.outputStream << clientResponse.body
            }
        }
        RestBuilder rest = new RestBuilder()
        RestTemplate restTemplate = rest.getRestTemplate()
        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor)

        httpResponse.outputStream.flush()
    }

    /**
     * Retrieve info about a file
     * @param sessionId
     * @param fileId
     * @return
     */
    def fileInfo(String sessionId, String fileId) {

        String url = buildOpencgaRestUrl('/files/'+fileId+'/info?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        return getJsonResult(resp.json).get(0)
    }

    def filesLink(String sessionId, File file, String studyId) {
        logger.info('linking file: '+file+' to studyId '+studyId)
        String uri = 'file://'+file.absolutePath
        String path = file.parentFile.absolutePath
        // Remove prefixed slash if present
        if (path.startsWith("/")) {
            path = path.substring(1)
        }

        String url = buildOpencgaRestUrl('/files/link?sid='+sessionId+'&study='+studyId+'&parents=true&uri='+uri+'&path='+path)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        //logger.info('resp.json='+resp.json)
        return getJsonResult(resp.json).get(0)
    }


    def linkFileAndIndex(String sessionId, File file, String studyId) {

        def fileJson = filesLink(sessionId, file, studyId)
        String fileId = fileJson.id
        logger.info('linkFileAndIndex fileId: '+fileId)
        // Index and analyze file only if it's a variant
        if (fileJson.bioformat == 'VARIANT') {
            analysisVariantIndex(sessionId, [fileId])
        }

        logger.info('END linkFileAndIndex')
        return fileJson
    }

    def filesDelete(String sessionId, String fileId) {

        String url = buildOpencgaRestUrl('/files/'+fileId+'/delete?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        return getJsonResult(resp.json).get(0)

    }

    def filesUpdate(String sessionId, String fileId, Collection sampleIds) {

        String url = buildOpencgaRestUrl('/files/'+fileId+'/update?sid='+sessionId)

        JSONObject json = new JSONObject()
        json.put('sampleIds', sampleIds.join(","))

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(url) {
            contentType('application/json')
            body(json.toString())
        }

        return getJsonResult(resp.json)
    }

    def filesSearch(String sessionId, String studyId, Collection sampleIds) {

        if (sampleIds) {
            String url = buildOpencgaRestUrl('/files/search?sid='+sessionId)
            url += '&study='+studyId
            url += '&sample='+sampleIds.join(",")

            RestBuilder rest = new RestBuilder()
            RestResponse resp = rest.get(url)
            return getJsonResult(resp.json)
        } else {
            return []
        }
    }


    // /{version}/analysis/variant/index
    def analysisVariantIndex(String sessionId, List fileIds, Boolean annotate=Boolean.FALSE, Boolean calculateStats=Boolean.FALSE) {

        logger.info('analysisVariantIndex')

        String url = buildOpencgaRestUrl('/analysis/variant/index?sid='+sessionId)
        url += '&file='+fileIds.join(',')
        url += '&annotate='+annotate
        url += '&calculateStats='+calculateStats
        url += '&overwrite=true'

        logger.info('url='+url)
        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        //logger.info('resp.json='+resp.json)
        return resp.json
    }


    def analysisVariantQuery(String sessionId, String studyId, String region) {

        logger.info('analysisVariantQuery - region: '+region)

        String url = buildOpencgaRestUrl('/analysis/variant/query?sid='+sessionId)
        url += '&studies='+studyId
        url += '&region='+region

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        return getJsonResult(resp.json)
    }


    def cohortsCreate(String sessionId, String name, String studyId) {
        logger.info('create cohort: '+name+' studyId='+studyId)
        String url = buildOpencgaRestUrl('/cohorts/create?sid='+sessionId)
        url += '&study='+studyId

        JSONObject json =  new JSONObject()
        json.put('name', name)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(url) {
            contentType('application/json')
            body(json.toString())
        }

        logger.info('json response: '+resp.json)

        return resp.json
    }

    def cohortsInfo(String sessionId, String cohortId) {

        logger.info('cohortId: '+cohortId)
        String url = buildOpencgaRestUrl('/cohorts/'+cohortId+'/info?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        return getJsonResult(resp.json).get(0)
    }

    /**
     * Retrun the cohorts belonging to the specified study
     * @param sessionId
     * @param studyId
     * @return
     */
    def cohortsSearch(String sessionId, String studyId) {
        logger.info('cohort search: '+studyId)
        String url = buildOpencgaRestUrl('/cohorts/search?sid='+sessionId)
        url += '&study='+studyId

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('resp.json='+resp.json)
        return getJsonResult(resp.json)
    }

    def cohortsUpdate(String sessionId, String cohortId, Collection sampleIds) {

        logger.info('cohort update cohortId: '+cohortId+' sampleIds='+sampleIds)
        String url = buildOpencgaRestUrl('/cohorts/'+cohortId+'/update?sid='+sessionId)


        JSONObject json =  new JSONObject()
        json.put('samples', sampleIds.join(","))

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(url) {
            contentType('application/json')
            body(json.toString())
        }

        logger.info('json response: '+resp.json)

        return getJsonResult(resp.json)
    }

    def cohortsSamples(String sessionId, String cohortId) {

        logger.info('cohort samples: '+cohortId)
        String url = buildOpencgaRestUrl('/cohorts/'+cohortId+'/samples?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('resp.json='+resp.json)
        return getJsonResult(resp.json)
    }

    /**
     * WARNING: Potential race condition here, since we are calling multiple REST api calls outside of a transaction.
     * @param sessionId
     * @param cohortId
     * @param sampleIds
     * @return
     */
    def cohortsAddSamples(String sessionId, String cohortId, Collection sampleIds) {
        def cohort = cohortsInfo(sessionId, cohortId)
        Set mergedSampleIds = cohort.samples as Set
        mergedSampleIds.addAll(sampleIds)

        cohortsUpdate(sessionId, cohortId,  mergedSampleIds)
    }


    def samplesSearch(String sessionId, String studyId, String name) {

        logger.info('samples search name: '+name)
        String url = buildOpencgaRestUrl('/samples/search?sid='+sessionId)
        url += '&study='+studyId
        url += '&name='+name

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('resp.json='+resp.json)
        return getJsonResult(resp.json)

    }

    def samplesCreate(String sessionId, String studyId, String name) {

        logger.info('create sample: '+name+' studyId='+studyId)
        String url = buildOpencgaRestUrl('/samples/create?sid='+sessionId)
        url += '&study='+studyId

        JSONObject json =  new JSONObject()
        json.put('name', name)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.post(url) {
            contentType('application/json')
            body(json.toString())
        }

        logger.info('json response: '+resp.json)

        return getJsonResult(resp.json).get(0)

    }

    def searchOrCreateSample(String sessionId, studyId, sampleName) {

        def samples = samplesSearch(sessionId, studyId, sampleName)
        logger.info('samples json='+samples)
        String sampleId = samples?.get(0)?.id

        if (sampleId == null) {
            // Create the sample
            def sampleJson = samplesCreate(sessionId, studyId, sampleName)
            sampleId = sampleJson.id
        }

        return sampleId
    }

    def samplesInfo(String sessionId, String sampleId) {

        String url = buildOpencgaRestUrl('/samples/'+sampleId+'/info?sid='+sessionId)

        RestBuilder rest = new RestBuilder()
        RestResponse resp = rest.get(url)

        logger.info('resp.json='+resp.json)
        return getJsonResult(resp.json).get(0)

    }

    def addFileToCohort(String sessionId, String cohortId, String studyId, File file, String fileId) {

        logger.info(' addFileToCohort fileId: '+fileId+ ' cohortId: '+cohortId+ ' studyId: '+studyId+' file: '+file)

        def fileJson = this.fileInfo(sessionId, fileId)
        logger.info('file format: '+fileJson.format)

        // Create the samples associated with the file
        Set<String> sampleIds = new HashSet()
        if (cohortId) {
            // Grab the sample name from the VCF
            if (fileJson.format == 'VCF') {
                // Process as VCF
                VCFFileReader vcfFileReader = new VCFFileReader(file, false)
                VCFHeader vcfHeader = vcfFileReader.getFileHeader()
                Set<String> sampleNames = vcfHeader.getGenotypeSamples() as Set
                logger.info('sampleNames: ' + sampleNames)

                // Search for existing samples
                for (String sampleName: sampleNames) {
                    String sampleId = searchOrCreateSample(sessionId, studyId, sampleName)
                    logger.info(sampleId)
                    sampleIds << sampleId
                }
            } else if (fileJson.format == 'BAM') {
                logger.info('reading BAM file')
                // Process BAM
                BAMFileReader bamReader = new BAMFileReader(file, null, false, false, ValidationStringency.SILENT, DefaultSAMRecordFactory.getInstance())
                SAMFileHeader samFileHeader = bamReader.getFileHeader()
                logger.info('attributes: '+samFileHeader.attributes)
                List<SAMReadGroupRecord> readGroups = samFileHeader.readGroups
                if (readGroups) {
                    SAMReadGroupRecord readGroup = readGroups.get(0)
                    String sampleName = readGroup.getSample()
                    logger.info('sampleName: '+sampleName)
                    if (sampleName) {
                        String sampleId = searchOrCreateSample(sessionId, studyId, sampleName)
                        sampleIds << sampleId
                    }
                }
            }

            if (sampleIds) {
                // Link samples to cohort
                cohortsAddSamples(sessionId, cohortId, sampleIds)

                // Link file to sample
                filesUpdate(sessionId, fileId, sampleIds)
            }

        }
    }

    /**
     * Parse json to simply get the primary result
     * @param json
     * @return
     */
    def getJsonResult(def json) {
        return OpencgaHelper.getJsonResult(json)
    }

    // Map username to an opencga username.
    // Opencga doesn't not allow usernames that contain periods
    public String opencgaUsername(String username) {
        return username.replaceAll('\\.','_')
    }

}
