package agha.opencga.gui

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

/**
 * Controller for downloading data
 *
 * @author Philip Wu
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class DownloadController {

    Logger logger = Logger.getLogger(DownloadController.class)

    OpenCGAService openCGAService

    def download() {

        String fileId = params.id

        def(user, password) = openCGAService.userPassword()

        String sessionId = openCGAService.login(user.username, password)

        // Get the filename
        def fileInfo = openCGAService.fileInfo(sessionId, fileId)
        logger.info("file name="+fileInfo.name)

        response.setContentType('APPLICATION/OCTET-STREAM')
        response.setHeader("Content-Disposition", "Attachment;Filename=" + fileInfo.name)

        openCGAService.fileDownload(sessionId, fileId, response)

    }
}
