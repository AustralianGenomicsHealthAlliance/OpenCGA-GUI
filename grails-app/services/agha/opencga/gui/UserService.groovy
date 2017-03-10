package agha.opencga.gui

import grails.transaction.Transactional

@Transactional
class UserService {

    OpenCGAService openCGAService

    public void createUser(String username,String email, String password, RoleType roleType) {

        // Check if user exists in local database
        User user = User.findByUsername(username)
        if (user == null) {
            createLocalUser(username, email, password, roleType)
        }


        // Create user in opencga
        //String opencgaUsername = openCGAService.opencgaUsername(username)
        openCGAService.userCreate(username, username, password, username)

    }

    public void createLocalUser(String username,String email, String password, RoleType roleType){
        Role role = Role.findByAuthority(roleType.toString())
        User user = User.findByUsername(username)
        System.out.println('existing user='+user)
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
