package agha.opencga.gui

import org.apache.log4j.Logger

/**
 * Controller for downloading data
 *
 * @author Philip Wu
 */
class DownloadController {

    Logger logger = Logger.getLogger(DownloadController.class)

    OpenCGAService openCGAService

    def download() {

        String fileId = params.id

        String sessionId = openCGAService.login('hongyu', 'agha')

        // Get the filename
        def fileInfo = openCGAService.fileInfo(sessionId, fileId)
        logger.info("file name="+fileInfo.name)

        response.setContentType('APPLICATION/OCTET-STREAM')
        response.setHeader("Content-Disposition", "Attachment;Filename=" + fileInfo.name)

        openCGAService.fileDownload(sessionId, fileId, response)

    }
}
