package agha.opencga.gui

import org.apache.log4j.Logger

/**
 * Controller for display study information
 *
 * @author Philip Wu
 */
class StudyController {

    Logger logger = Logger.getLogger(StudyController.class)

    OpenCGAService openCGAService

    String user = 'hongyu'
    String password = 'agha'

    def index() {

        String sessionId = openCGAService.login(user, password)
        def studies = openCGAService.searchStudies(sessionId)

        for (def study: studies) {
            openCGAService.studyInfo(sessionId, study.id.toString())
        }

        [studies: studies]
    }

    def show() {

        String studyId = params.id

        String sessionId = openCGAService.login(user, password)

        def studyInfo = openCGAService.studyInfo(sessionId, studyId)

        def studyFiles = openCGAService.studyFiles(sessionId, studyId)

        logger.info('studyInfo: '+studyInfo)

        [study: studyInfo, files: studyFiles]

    }
}
