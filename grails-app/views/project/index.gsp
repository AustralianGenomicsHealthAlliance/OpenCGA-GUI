<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Flagships</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>

<script>

    function createProject() {
        $('#flagshipForm').submit();
    }

    $( function() {
        dialog = $( "#flagshipDialog" ).dialog({
          autoOpen: false,
          height: 400,
          width: 450,
          modal: true,
          buttons: {
            "Create": createProject,
            Cancel: function() {
              dialog.dialog( "close" );
            }
          }
        });

        $( "#createFlagship" ).button().on( "click", function() {
          dialog.dialog( "open" );
        });

    });


</script>

<div id="flagshipDialog" title="Create Flagship" style="display:none;">

    <g:form name="flagshipForm" controller="project" action="create">
        <div>
            Name: <g:textField name="name" size="40"/>
        </div>
    </g:form>

</div>

<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>Flagships</h1>

        <div>
            <button id="createFlagship">Create new Flagship</button>
        </div>

        <g:if test="${projects}">
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
        </g:if>
        <g:else>
            <div>
                No flagships available
            </div>
        </g:else>
    </section>
</div>

</body>
</html>
