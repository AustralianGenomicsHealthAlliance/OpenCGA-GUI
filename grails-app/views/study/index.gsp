<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Studies</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>Studies</h1>

            <table>
                <tr>
                    <th>Flagship</th>
                    <th>Study name</th>
                    <th>Owner</th>
                </tr>
                <g:each in="${studies}" var="study" >
                    <tr>
                        <td>

                        </td>
                        <td>
                            <g:link controller="study" action="show" params="[id:study.id]">
                                ${study.name.encodeAsHTML()}
                            </g:link>
                        </td>
                        <td>
                            ${OpencgaHelper.parseProjectOwnerFromUri(study.uri)?.encodeAsHTML()}
                        </td>
                    </tr>
                </g:each>
            </table>
    </section>
</div>

</body>
</html>
