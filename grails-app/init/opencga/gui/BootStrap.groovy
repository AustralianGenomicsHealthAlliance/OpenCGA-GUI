package opencga.gui

import agha.opencga.gui.OpenCGAService
import agha.opencga.gui.Role
import agha.opencga.gui.RoleType
import agha.opencga.gui.User
import agha.opencga.gui.UserRole
import agha.opencga.gui.UserService
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils

class BootStrap {

    OpenCGAService openCGAService
    UserService userService

    def init = { servletContext ->

        initSecurityRoles()
        initAdmin()

        // Shibboleth configuration
        environments {
            development {
            }
            test {
                System.out.println('setting the shibAuthFilter')
                SpringSecurityUtils.clientRegisterFilter('shibAuthFilter', SecurityFilterPosition.PRE_AUTH_FILTER.order + 10)
            }
            production {
                SpringSecurityUtils.clientRegisterFilter('shibAuthFilter', SecurityFilterPosition.PRE_AUTH_FILTER.order + 10)
            }
            shibboleth {
                SpringSecurityUtils.clientRegisterFilter('shibAuthFilter', SecurityFilterPosition.PRE_AUTH_FILTER.order + 10)
            }

        }


    }
    def destroy = {
    }


    def initAdmin() {
        userService.createUser('philip.wu@anu.edu.au', 'philip.wu@anu.edu.au', 'agha', RoleType.ADMIN)
        userService.createUser('hongyu.ma@anu.edu.au', 'hongyu.ma@anu.edu.au', 'agha', RoleType.ADMIN)
    }

    def initSecurityRoles() {
        // Initialize each user role
        for (RoleType rt : RoleType.values()) {
            new Role(authority: rt.toString()).save(flush: true)
        }
    }

}
