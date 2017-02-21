package agha.opencga.gui

import grails.plugin.springsecurity.annotation.Secured
import groovy.io.FileType
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

import javax.servlet.http.HttpServletRequest

/**
 * An upload controller using fineuploader as the frontend
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
        logger.debug('====== fineuploader =====')
        logger.info('qquuid:'+params.qquuid)
        logger.debug('qqpartindex: '+params.qqpartindex)
        logger.debug('qqpartbyteoffset: '+params.qqpartbyteoffset)
        logger.debug('qqtotalfilesize: '+params.qqpartbyteoffset)
        logger.debug('qqtotalparts: '+params.qqtotalparts)
        logger.debug('qqfilename: '+params.qqfilename)
        logger.debug('qqchunksize: '+params.qqchunksize)
        logger.debug('studyId: '+params.studyId)
        logger.debug('qqresume: '+params.qqresume)


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
            logger.debug('fileMoved: '+fileMoved)
        }

        JSONObject json = new JSONObject()
        json.put('success', Boolean.TRUE)
        render(status: HttpStatus.OK, contentType: 'application/json', text: json.toString())
    }

    /**
     * You may specify a chunking.success.endpoint if you'd like your server to be called when all chunks have been successfully uploaded.
     * Note that this only applies to traditional endpoint.
     * @return
     */
    def fineuploaderChunkSuccess() {

        logger.debug('===== fineuploaderChunkSuccess =====')

        logger.info('qquuid:'+params.qquuid)
        logger.info('qqpartindex: '+params.qqpartindex)
        logger.debug('qqpartbyteoffset: '+params.qqpartbyteoffset)
        logger.debug('qqtotalfilesize: '+params.qqpartbyteoffset)
        logger.debug('qqtotalparts: '+params.qqtotalparts)
        logger.debug('qqfilename: '+params.qqfilename)
        logger.debug('qqchunksize: '+params.qqchunksize)

        logger.debug('studyId: '+params.studyId)


        File destFolder = createProjectStudyFolder(params.studyId)
        File destFile = new File(destFolder.absolutePath, params.qqfilename)
        // Clean up any previous uploads and overwrite
        if (destFile.exists()) {
            destFile.delete()
        }
        mergePartitionedFiles(params.qquuid, destFile, params.qqtotalparts.toInteger())

        JSONObject json = new JSONObject()
        json.put('success', Boolean.TRUE)
        render(status: HttpStatus.OK, contentType: 'application/json', text: json.toString())

    }

    def cancel() {
        logger.info('cancelling qquuid:'+params.qquuid)

        String uuid = params.qquuid

        // Find the folder
        File dir = createTmpFolder(uuid)

        // Delete the folder to save space
        if (dir.exists()) {
            // Due to concurrency, files in the directory may still be open.
            // So retry multiple times to give concurrent processes time to complete
            boolean deleted = false
            int retryCount = 0;
            while (! deleted && retryCount < 5) {
                deleted = dir.deleteDir()
                logger.info('deleted='+deleted)
                Thread.sleep(2000)
                retryCount++
            }
        }

        JSONObject json = new JSONObject()
        json.put('success', Boolean.TRUE)
        render(status: HttpStatus.OK, contentType: 'application/json', text: json.toString())
    }


    private File createTmpFolder(String uuid) {
        String uploadDir = grailsApplication.config.datastore.tmp.path
        File dir = new File(uploadDir, uuid);
        boolean dirMade = dir.mkdirs();

        return dir
    }

    private File createProjectStudyFolder(String studyId) {
        String folder = grailsApplication.config.datastore.path

        // Get the studyInfo
        String sessionId = openCGAService.loginCurrentUser()
        def studyInfo = openCGAService.studyInfo(sessionId, params.studyId)

        // Get the projectId
        String projectId = OpencgaHelper.parseProjectIdFromUri(studyInfo.uri)


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
        logger.debug('dir='+dir)

        logger.debug('content length: '+req.contentLengthLong)

        String filename = (partIndex >= 0) ? createFilePartName(uuid, partIndex) : params.qqfilename
        File destFile = new File(dir, filename)
        FileOutputStream fos = new FileOutputStream(destFile)

        if (req instanceof StandardMultipartHttpServletRequest) {
            req.fileNames.each {
                def mFile = req.getFile(it)
                logger.debug('file: '+mFile)
                fos << mFile.bytes
            }
        } else {
            fos << req.inputStream
        }
        fos.flush()

        return destFile
    }

    private String createFilePartName(String uuid, Integer partIndex) {
        return uuid +'_' + partIndex+ '.part'
    }

    private void mergePartitionedFiles(String uuid, File destFile, Integer totalParts) {
        logger.debug('merging files to: '+destFile)
        // Create UUID Directory
        File dir = createTmpFolder(uuid)

        // Must merge in correct order
        for (int i=0; i < totalParts; i++) {
            String filename  = createFilePartName(uuid, i)
            File filePart = new File(dir.absolutePath, filename)
            destFile << filePart.newInputStream()
            // Once appended, delete the part
            boolean deleted = filePart.delete()
        }

        // Delete the temporary folder
        dir.delete()
    }

}
