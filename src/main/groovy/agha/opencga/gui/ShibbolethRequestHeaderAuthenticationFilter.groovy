package agha.opencga.gui

/**
 * Created by philip on 13/02/17.
 */
import agha.opencga.gui.User
import org.apache.log4j.Logger
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter

import javax.servlet.http.HttpServletRequest

/**
 * Handles for Shibboleth request headers to create Authorization ids.
 * Map the request headers provided by shibboleth to properties of the User domain class
 * Automatically create user accounts if none already exists.
 * Use the email address of the user as the username
 *
 * @author Philip Wu
 */
public class ShibbolethRequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter {

    Logger logger = Logger.getLogger(ShibbolethRequestHeaderAuthenticationFilter.class)

    UserDetailsService userDetailsService
    /**
     * Required. Used to check if users exist.
     * @param userDetailsManager
     */
    boolean enable = true;
    /**
     * Since the superclass definition for princiaplRequestHeader is private, we store the value
     * in 2 places by overriding the setPrincipalRequestHeader() method, so that we can access this value
     */
    String accessiblePrincipalRequestHeader

    /**
     * Override to store an accessible version of the principalRequestHeader
     */
    @Override
    public void setPrincipalRequestHeader(String principalRequestHeader) {
        super.setPrincipalRequestHeader(principalRequestHeader)
        this.accessiblePrincipalRequestHeader = principalRequestHeader
    }

    /**
     * This is called when a request is made, the returned object identifies the
     * user and will either be Null or a String. This method will throw an exception if
     * exceptionIfHeaderMissing is set to true (default) and the required header is missing.
     * @param request
     */
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        if (!enable) return null;

        // Extract username
        // ShibUseHeaders On
        String username = (String)(super.getPreAuthenticatedPrincipal(request));
        // Or if AJP is used instead of ShibUseHeaders, then pull from attributes instead of headers
        if (! username) {
            username = request.getAttribute(accessiblePrincipalRequestHeader)
        }

        // Extract displayName
        String displayName = request.getHeader("displayName")
        if (! displayName) {
            displayName = request.getAttribute("displayName")
        }


        logger.debug("authenticatedPrincipal: "+username)
        if (username ) {

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username, false)
                logger.debug("userDetails="+userDetails)
            } catch (UsernameNotFoundException ex) {
                logger.info("User does not exist. Creating new user")
                User.withTransaction {
                    User u = new User();
                    u.username = username
                    u.email = username
                    u.enabled = true
                    u.displayName = displayName
                    boolean saved = u.save()
                    logger.info("New user created: "+saved)
                    if (! saved) {
                        logger.error("Errors saving user: "+u.errors)
                    }
                }
            }


        }
        return username;
    }

}