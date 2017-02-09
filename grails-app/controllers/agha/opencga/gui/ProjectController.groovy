package agha.opencga.gui

import org.apache.log4j.Logger

class ProjectController {

    Logger logger = Logger.getLogger(ProjectController.class)

    OpenCGAService openCGAService



    def index() {

        String sessionId = openCGAService.login('hongyu', 'agha')

        logger.info('sessionId='+sessionId)

        List projects = openCGAService.accessibleUserProjects(sessionId, 'hongyu')

        [projects: projects]
    }


    def show() {

        Integer projectId = params.id?.toInteger()

        String sessionId = openCGAService.login('hongyu', 'agha')

        Map projectMap = openCGAService.mapAccessibleProjects(sessionId, 'hongyu')

        logger.info("projectMap="+projectMap)

        def project = projectMap.get(projectId)

        [project: project]
    }
}
