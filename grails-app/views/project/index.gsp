<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Flagships</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>Flagships</h1>

        <table>
            <tr>
                <th>Flagship</th>

            </tr>
            <g:each in="${projects}" var="project" >
                <tr>
                    <td>
                        <g:link controller="project" action="show" params="[id: project.id]">
                            ${project.name}
                        </g:link>
                    </td>

                </tr>
            </g:each>
        </table>
    </section>
</div>

</body>
</html>
