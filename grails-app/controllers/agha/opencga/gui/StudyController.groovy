package agha.opencga.gui

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

/**
 * Controller for display study information
 *
 * @author Philip Wu
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class StudyController {

    Logger logger = Logger.getLogger(StudyController.class)

    OpenCGAService openCGAService


    def index() {

        def(user, password) = openCGAService.userPassword()

        String sessionId = openCGAService.login(user.username, password)
        def studies = openCGAService.searchStudies(sessionId)

        for (def study: studies) {
            openCGAService.studyInfo(sessionId, study.id.toString())
        }

        [studies: studies]
    }

    def show() {

        String studyId = params.id

        def(user, password) = openCGAService.userPassword()

        String sessionId = openCGAService.login(user.username, password)

        def studyInfo = openCGAService.studyInfo(sessionId, studyId)

        def studyFiles = openCGAService.studyFiles(sessionId, studyId)

        logger.info('studyInfo: '+studyInfo)

        [study: studyInfo, files: studyFiles]

    }


}
