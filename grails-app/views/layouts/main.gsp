<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <asset:stylesheet src="application.css"/>
    <asset:javascript src="jquery-2.2.0.min.js"/>
    <asset:javascript src="jquery-ui.min.js"/>


    <asset:stylesheet src="jquery-ui.min.css" />
    <asset:stylesheet src="jquery-ui.theme.min.css" />


    <g:layoutHead/>
</head>
<body>

    <div class="navbar navbar-default navbar-static-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/#">
                    <i class="fa grails-icon">
                        <asset:image src="agha_logo.png"/>
                    </i> Sequence Storage
                </a>
            </div>
            <div class="navbar-collapse collapse" aria-expanded="false">
                <div style="float:right; margin:0; margin-top: 20px; color: white;">
                    <g:pageProperty name="page.nav" />
                    <sec:ifLoggedIn>
                        Logged in: <sec:loggedInUserInfo field='username'/>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <g:link controller='login' action='auth'>Login</g:link>
                    </sec:ifNotLoggedIn>
                </div>
            </div>
        </div>
    </div>

    <g:layoutBody/>

    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>

    <asset:javascript src="application.js"/>

</body>
</html>
