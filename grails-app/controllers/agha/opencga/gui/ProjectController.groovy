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

    OpenCGAService openCGAService

    def index() {

        def(user, password) = openCGAService.userPassword()

        String sessionId = openCGAService.login(user.username, password)

        logger.info('sessionId='+sessionId)

        List projects = openCGAService.accessibleUserProjects(sessionId, user.username)

        [projects: projects]
    }


    def show() {

        Integer projectId = params.id?.toInteger()

        def(user, password) = openCGAService.userPassword()

        String sessionId = openCGAService.login(user.username, password)

        Map projectMap = openCGAService.mapAccessibleProjects(sessionId, user.username)

        def project = projectMap.get(projectId)

        [project: project]
    }
}
