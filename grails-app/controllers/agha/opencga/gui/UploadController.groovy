package agha.opencga.gui

import grails.plugin.springsecurity.annotation.Secured
import groovy.io.FileType
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

import javax.servlet.http.HttpServletRequest

/**
 * An upload controller
 *
 * Reference for fineuploader: http://docs.fineuploader.com/branch/master/features/chunking.html
 *
 * @author Philip Wu
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class UploadController {

    Logger logger = Logger.getLogger(UploadController.class)

    //def grailsApplication
    OpenCGAService openCGAService

    def fineuploader() {
        logger.info('====== fineuploader =====')
        logger.info('qquuid:'+params.qquuid)
        logger.info('qqpartindex: '+params.qqpartindex)
        logger.info('qqpartbyteoffset: '+params.qqpartbyteoffset)
        logger.info('qqtotalfilesize: '+params.qqpartbyteoffset)
        logger.info('qqtotalparts: '+params.qqtotalparts)
        logger.info('qqfilename: '+params.qqfilename)
        logger.info('qqchunksize: '+params.qqchunksize)
        logger.info('studyId: '+params.studyId)


        File tmpFile = writeFile(request, params)

        // If not chunking, move file immediately to proper location once upload has completed
        if (params.qqpartindex == null) {
            File destFolder = createProjectStudyFolder(params.studyId)
            File destFile = new File(destFolder.absolutePath, tmpFile.name)
            // Clean up any previous uploads and overwrite
            if (destFile.exists()) {
                destFile.delete()
            }
            boolean fileMoved = tmpFile.renameTo(destFile)
            logger.info('fileMoved: '+fileMoved)
        }

        JSONObject json = new JSONObject()
        json.put('success', Boolean.TRUE)
        render(status: HttpStatus.OK, contentType: 'application/json', text: json.toString())
    }

    def fineuploaderChunkSuccess() {

        logger.info('===== fineuploaderChunkSuccess =====')

        logger.info('qquuid:'+params.qquuid)
        logger.info('qqpartindex: '+params.qqpartindex)
        logger.info('qqpartbyteoffset: '+params.qqpartbyteoffset)
        logger.info('qqtotalfilesize: '+params.qqpartbyteoffset)
        logger.info('qqtotalparts: '+params.qqtotalparts)
        logger.info('qqfilename: '+params.qqfilename)
        logger.info('qqchunksize: '+params.qqchunksize)

        logger.info('studyId: '+params.studyId)


        File destFolder = createProjectStudyFolder(params.studyId)
        File destFile = new File(destFolder.absolutePath, params.qqfilename)
        // Clean up any previous uploads and overwrite
        if (destFile.exists()) {
            destFile.delete()
        }
        mergePartitionedFiles(params.qquuid, destFile)

        JSONObject json = new JSONObject()
        json.put('success', Boolean.TRUE)
        render(status: HttpStatus.OK, contentType: 'application/json', text: json.toString())

    }


    private File createTmpFolder(String uuid) {
        String uploadDir = grailsApplication.config.datastore.tmp.path
        File dir = new File(uploadDir, uuid);
        boolean dirMade = dir.mkdirs();
        logger.info('dirMade: '+dirMade)

        return dir
    }

    private File createProjectStudyFolder(String studyId) {
        String folder = grailsApplication.config.datastore.path

        // Get the studyInfo
        String sessionId = openCGAService.loginCurrentUser()
        def studyInfo = openCGAService.studyInfo(sessionId, params.studyId)
        logger.info('studyInfo: '+studyInfo)

        String projectId = OpencgaHelper.parseProjectIdFromUri(studyInfo.uri)
        logger.info('projectId')

        folder += '/'+projectId+'/'+studyId

        File fileFolder = new File(folder)
        fileFolder.mkdirs()

        return fileFolder
    }


    /**
     * Adapted from https://github.com/FineUploader/server-examples/blob/master/java/UploadReceiver.java
     * @param req
     * @param params
     * @throws Exception
     */
    private File writeFile(HttpServletRequest req, def params) throws Exception
    {
        // Extract needed params
        String uuid = params.qquuid
        Integer partIndex = params.qqpartindex?.toInteger()

        // Create UUID Directory
        File dir = createTmpFolder(uuid)
        logger.info('dir='+dir)

        logger.info('content length: '+req.contentLengthLong)

        String filename = (partIndex >= 0) ? uuid +'_'+ partIndex + '.part' : params.qqfilename
        File destFile = new File(dir, filename)
        FileOutputStream fos = new FileOutputStream(destFile)

        if (req instanceof StandardMultipartHttpServletRequest) {
            req.fileNames.each {
                def mFile = req.getFile(it)
                logger.info('file: '+mFile)
                fos << mFile.bytes
            }
        } else {
            fos << req.inputStream
        }
        fos.flush()

        return destFile
    }

    private void mergePartitionedFiles(String uuid, File destFile) {
        logger.info('merging files to: '+destFile)
        // Create UUID Directory
        File dir = createTmpFolder(uuid)

        // Collect names and files into data structures
        List filenames = []
        Map<String, File> fileMap = [:]

        // Must merge in correct order
        dir.eachFileMatch(FileType.FILES, ~/.*\.part/) {
            filenames << it.name
            fileMap.put(it.name, it)
        }
        filenames.sort()

        // Now merge in order and delete temporary files
        filenames.each {
            File filePart = fileMap.get(it)
            destFile << filePart.newInputStream()
            // Once appended, delete the part
            boolean deleted = filePart.delete()
            logger.info('deleted part: '+deleted)
        }


    }

}
