// Place your Spring DSL code here
beans = {

    // Shibboleth integration
    userDetailsServiceWrapper(org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper) {
        userDetailsService = ref('userDetailsService')
    }

    preauthAuthenticationProvider(org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider) {
        preAuthenticatedUserDetailsService = ref('userDetailsServiceWrapper')
    }

    shibAuthFilter(agha.opencga.gui.ShibbolethRequestHeaderAuthenticationFilter) {
        principalRequestHeader = 'mail' //this is the shib header that contains the user ID
        checkForPrincipalChanges = true
        invalidateSessionOnPrincipalChange = true
        continueFilterChainOnUnsuccessfulAuthentication = false
        authenticationManager = ref('authenticationManager')
        userDetailsService = ref('userDetailsService')
        exceptionIfHeaderMissing = false
        checkForPrincipalChanges = false
        enable = true
    }

}
