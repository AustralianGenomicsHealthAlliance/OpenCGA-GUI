package opencga.gui

import agha.opencga.gui.Role
import agha.opencga.gui.RoleType
import agha.opencga.gui.User
import agha.opencga.gui.UserRole

class BootStrap {

    def init = { servletContext ->

        initSecurityRoles()
        initAdmin()

    }
    def destroy = {
    }


    def initAdmin() {
        createUser('philipwu', 'philip.wu@anu.edu.au', 'agha', RoleType.ADMIN)
        createUser('hongyu', 'hongyu.ma@anu.edu.au', 'agha', RoleType.ADMIN)
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
