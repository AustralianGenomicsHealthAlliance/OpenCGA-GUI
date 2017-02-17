<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Studies</title>

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

    <g:form name="studyForm" controller="study" action="create">
        <div>
            Name: <g:textField name="name" size="40"/>
        </div>
    </g:form>

</div>

<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>Studies</h1>

        <div>
            <button id="createStudy">Create new Study</button>
        </div>

        <g:if test="${studies}">

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
        </g:if>
        <g:else>
            <div>No studies available</div>
        </g:else>
    </section>
</div>

</body>
</html>
