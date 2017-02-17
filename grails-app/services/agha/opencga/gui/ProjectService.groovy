package agha.opencga.gui

import grails.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize

@Transactional
class ProjectService {

    OpenCGAService openCGAService


    @PreAuthorize("hasPermission(#projectId, 'agha.opencga.gui.Project', read)")
    def projectInfo (Long projectId) {
        String sessionId = openCGAService.loginAgha()
        return openCGAService.projectInfo(sessionId, projectId)
    }
}
