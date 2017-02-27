package agha.opencga.gui

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class VariantSearchController {

    Logger logger = Logger.getLogger(VariantSearchController.class)

    OpenCGAService openCGAService

    def index() {
        logger.info('params.studyId='+params.studyId+' region: '+params.region)

        def searchResults = null

        if (params.studyId && params.region) {
            String sessionId = openCGAService.loginCurrentUser()
            searchResults = openCGAService.analysisVariantQuery(sessionId, params.studyId, params.region)
        }

        [searchResults: searchResults]
    }
}
