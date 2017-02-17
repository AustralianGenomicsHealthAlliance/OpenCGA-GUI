package agha.opencga.gui

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

/**
 * Controller for display flagships
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class ProjectController {

    Logger logger = Logger.getLogger(ProjectController.class)

    SpringSecurityService springSecurityService
    OpenCGAService openCGAService

    def index() {
        def user = springSecurityService.currentUser
        String sessionId = openCGAService.loginCurrentUser()

        logger.info('sessionId='+sessionId)

        if (sessionId) {
            List projects = openCGAService.accessibleUserProjects(sessionId, user.username)

            [projects: projects]
        } else {
            render "No access to OpenCGA. Please contact the administrator."
        }
    }


    def show() {

        Integer projectId = params.id?.toInteger()

        String sessionId = openCGAService.loginCurrentUser()

        def user = springSecurityService.currentUser
        Map projectMap = openCGAService.mapAccessibleProjects(sessionId, user.username)

        def project = projectMap.get(projectId)

        [project: project]
    }

    def create() {
        String projectName = params.name
        logger.info('projectName='+projectName)
        String sessionId = openCGAService.loginCurrentUser()
        def resp = openCGAService.projectCreate(sessionId, projectName)

        logger.info("resp: "+resp.json)

        redirect (action: 'index')
    }


    def createStudy() {
        String studyName = params.name
        logger.info('studyName='+studyName)
        String sessionId = openCGAService.loginCurrentUser()
        def resp = openCGAService.studyCreate(sessionId, studyName, params.projectId)

        logger.info("resp: "+resp.json)

        redirect (action: 'show', id:params.projectId)
    }

}
