package opencga.gui

import agha.opencga.gui.Role
import agha.opencga.gui.RoleType
import agha.opencga.gui.User
import agha.opencga.gui.UserRole
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils

class BootStrap {

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
        createUser('philip.wu@anu.edu.au', 'philip.wu@anu.edu.au', 'agha', RoleType.ADMIN)
        //createUser('philipwu', 'philip.wu@anu.edu.au', 'agha', RoleType.ADMIN)
        //createUser('hongyu', 'hongyu.ma@anu.edu.au', 'agha', RoleType.ADMIN)
    }

    def initSecurityRoles() {
        // Initialize each user role
        for (RoleType rt : RoleType.values()) {
            new Role(authority: rt.toString()).save(flush: true)
        }
    }

    private void createUser(String username,String email, String password, RoleType roleType){
        Role role = Role.findByAuthority(roleType.toString())
        User user = User.findByUsername(username)
        if (user == null) {
            user = new User(username: username, email: email, displayName: username, enabled: true, password: password)
            boolean saved = user.save(flush: true)
            System.out.println("Saved? "+saved+" Errors saving user [" + username + "]: "+user.errors)
        }

        UserRole userRole = UserRole.findByUserAndRole(user, role)
        if (userRole == null) {
            userRole = new UserRole(user: user, role: role)
            boolean saved = userRole.save(flush: true)
            System.out.println("Saved? "+saved+" Errors saving user role [user:" + username + ", role:" + roleType.toString() + "]: "+userRole.errors)
        }
    }



}
