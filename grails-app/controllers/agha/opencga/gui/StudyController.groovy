package agha.opencga.gui

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

/**
 * Controller for display study information
 *
 * @author Philip Wu
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class StudyController {

    Logger logger = Logger.getLogger(StudyController.class)

    OpenCGAService openCGAService
    UserService userService


    def index() {
        String sessionId = openCGAService.loginCurrentUser()
        def studies = openCGAService.searchStudies(sessionId)

        for (def study: studies) {
            openCGAService.studyInfo(sessionId, study.id.toString())
        }

        [studies: studies]
    }

    def show() {

        String studyId = params.id

        def(user, password) = openCGAService.userPassword()

        String sessionId = openCGAService.loginCurrentUser()

        def studyInfo = openCGAService.studyInfo(sessionId, studyId)
        logger.info('studyInfo: '+studyInfo)

        def studyFiles = openCGAService.studyFiles(sessionId, studyId)

        // Does this study have files?
        Boolean hasFiles = Boolean.FALSE
        studyFiles.each {
            if (it.type == 'FILE') {
                hasFiles = Boolean.TRUE
            }
        }

        // Can user share study?
        Boolean canShare = Boolean.FALSE
        String owner = OpencgaHelper.parseProjectOwnerFromUri(studyInfo.uri)
        logger.info('owner='+owner+' username: '+user.username)
        if (user.username == owner) {
            canShare = Boolean.TRUE
        } else { // Check access control list
            for (def acl: studyInfo.acl) {
                if (acl.member == user.username) {
                    // User match, now check permissions
                    if (acl.permissions.contains('SHARE_STUDY')) {
                        canShare = Boolean.TRUE
                    }
                }
            }
        }


        [study: studyInfo, files: studyFiles, hasFiles: hasFiles, canShare: canShare]

    }

    /**
     * Share study with users
     */
    def share() {

        String studyId = params.id
        String usersCSV = params.users
        String permissions = params.permissions
        String password = grailsApplication.config.opencga.password

        logger.info('share studyId: '+studyId+ ' usersCSV: '+usersCSV+' permissions: '+permissions)

        if (usersCSV && permissions) {
            // Share on user by user basis
            logger.info('permission set')
            // Parse users list
            String[] users = usersCSV.split(",")

            // Check to see that the user already exists. If not, then create user
            for (String user : users) {
                user = user.trim()  // clean

                // Try to create the user, even if they already exist
                userService.createUser(user, user, password, RoleType.RESEARCHER)
            }

            // Share study with users
            String sessionId = openCGAService.loginCurrentUser()
            openCGAService.studyAclCreate(sessionId, studyId, usersCSV, permissions)
        }

        redirect(action:'show', params:[id: studyId])
    }

    def unshare() {
        String sessionId = openCGAService.loginCurrentUser()
        openCGAService.studyAclDelete(sessionId, params.studyId, params.user)

        JSONObject json = new JSONObject()
        json.put('success', Boolean.TRUE)
        render(status: HttpStatus.OK, contentType: 'application/json', text: json.toString())
    }


}
