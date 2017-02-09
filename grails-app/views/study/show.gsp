<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Study - ${study?.name?.encodeAsHTML() }</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">

        <g:if test="${study}">
            <h1>Study - ${study.name?.encodeAsHTML() }</h1>

            <fieldset>
                <legend>Files</legend>

                <table>
                    <tr>
                        <th>Name</th>
                        <th>Format</th>
                        <th>Size</th>
                    </tr>
                    <g:each in="${ files }" var="file">
                        <g:if test="${file.type == 'FILE'}">
                            <tr>
                                <td>
                                    <g:link controller="download" action="download" params="[id: file.id]">
                                        ${file.name?.encodeAsHTML()}
                                    </g:link>
                                </td>
                                <td>${file.format?.encodeAsHTML()}</td>
                                <td>
                                    <g:if test="${file.size}">
                                        ${Math.ceil(file.size / 1024 / 1024).toInteger() } MB
                                    </g:if>
                                </td>
                            </tr>
                        </g:if>
                    </g:each>
                </table>
            </fieldset>


        </g:if>
        <g:else>
            Study not found
        </g:else>
    </section>
</div>

</body>
</html>
