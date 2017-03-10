<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Flagship</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>

<script>

    function submitDialog() {
        $('#studyForm').submit();
    }

    $( function() {
        dialog = $( "#dialog" ).dialog({
          autoOpen: false,
          height: 400,
          width: 450,
          modal: true,
          buttons: {
            "Create": submitDialog,
            Cancel: function() {
              dialog.dialog( "close" );
            }
          }
        });

        $( "#createStudy" ).button().on( "click", function() {
          dialog.dialog( "open" );
        });

    });


</script>

<div id="dialog" title="Create Study" style="display:none;">

    <g:form name="studyForm" controller="project" action="createStudy">
        <div>
            Name: <g:textField name="name" size="40"/>

            <g:hiddenField name="projectId" value="${project.id}" />
        </div>
    </g:form>

</div>


<div id="content" role="main">

    <div class="navPath">
        <g:link controller="project" action="index" >
            Home
        </g:link>
        &gt;
        ${project?.name?.encodeAsHTML()}


    </div>

    <section class="row colset-2-its">

        <g:if test="${project}">
            <h1>Flagship - ${project.name?.encodeAsHTML()}</h1>

            <div>
                <button id="createStudy">Create new Study</button>
            </div>

            <fieldset>
                <legend>Studies</legend>

                <g:if test="${project.studies}">
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
                </g:if>
                <g:else>
                    <div>No studies available</div>
                </g:else>
            </fieldset>


        </g:if>
        <g:else>
            Project not found
        </g:else>
    </section>
</div>

</body>
</html>
