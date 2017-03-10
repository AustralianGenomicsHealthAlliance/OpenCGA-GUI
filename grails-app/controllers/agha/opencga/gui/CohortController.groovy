package agha.opencga.gui

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

/**
 * Controller for Cohorts
 * @author Philip Wu
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class CohortController {

    Logger logger = Logger.getLogger(CohortController.class)

    OpenCGAService openCGAService
    SpringSecurityService springSecurityService

    def create() {

        String sessionId = openCGAService.loginCurrentUser()
        def json = openCGAService.cohortsCreate(sessionId, params.name, params.studyId)

        redirect(controller:'study', action:'show', params:[id: params.studyId])
    }

    def show() {
        logger.info('cohortId: '+params.id+' studyId: '+params.studyId)
        def user = springSecurityService.currentUser
        String sessionId = openCGAService.loginCurrentUser()
        def cohortInfo = openCGAService.cohortsInfo(sessionId, params.id)

        def study = openCGAService.studyInfo(sessionId, params.studyId)

        def project = openCGAService.findProjectByStudyId(sessionId, params.studyId, user.username)

        // Get all samples in this cohort
        def samples = openCGAService.cohortsSamples(sessionId, params.id)

        List sampleIds = samples.collect { it.id }
        logger.info('sampleIds: '+sampleIds)

        // Get all files by sampleIds
        def files = openCGAService.filesSearch(sessionId, params.studyId, sampleIds)

        // Does this study have files?
        Boolean hasFiles = Boolean.FALSE
        files.each {
            if (it.type == 'FILE') {
                hasFiles = Boolean.TRUE
            }
        }

        [cohort:cohortInfo, studyId: params.studyId, study: study, project: project, files: files, hasFiles: hasFiles]
    }
}
