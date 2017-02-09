<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Flagship</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">

        <g:if test="${project}">
            <h1>Flagship - ${project.name?.encodeAsHTML()}</h1>

            <fieldset>
                <legend>Studies</legend>

                <table>
                    <tr>
                        <th>Name</th>
                        <th>Owner</th>
                    </tr>
                    <g:each in="${ project.studies }" var="study">
                        <tr>
                            <td>
                                <g:link controller="study" action="show" params="[id: study.id]">
                                    ${study.name?.encodeAsHTML()}
                                </g:link>
                            </td>
                            <td>${OpencgaHelper.parseProjectOwnerFromUri(study.uri)?.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                </table>
            </fieldset>


        </g:if>
        <g:else>
            Project not found
        </g:else>
    </section>
</div>

</body>
</html>
