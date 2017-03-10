<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Study - ${study?.name?.encodeAsHTML() }</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <script src="${resource(dir: 'fine-uploader', file:'fine-uploader.js')}" ></script>
    <link rel="stylesheet" href="${resource(dir:'fine-uploader', file:'fine-uploader-new.css') }" />

    <script>

    function submitShareDialog() {
        $('#shareForm').submit();
    }

    function submitCohortDialog() {
        $('#cohortForm').submit();
    }

    function removeAccess(el, studyId) {
        var user = $(el).attr('user');
        console.log('removingAccess: '+user);

        $.post("${createLink(controller:'study', action:'unshare')}?user="+user+'&studyId='+${study.id})
            .done(function(data) {
                console.log('unshared: '+data);
                $(el).parent().remove();
            });
    }

    $( function() {
        dialog = $( "#shareDlg" ).dialog({
          autoOpen: false,
          height: 400,
          width: 550,
          modal: true,
          buttons: {
            "Share": submitShareDialog,
            Cancel: function() {
              dialog.dialog( "close" );
            }
          }
        });

        cohortDialog = $( "#createCohortDlg" ).dialog({
          autoOpen: false,
          height: 250,
          width: 550,
          modal: true,
          buttons: {
            "Create cohort": submitCohortDialog,
            Cancel: function() {
              cohortDialog.dialog( "close" );
            }
          }
        });


        $( "#shareBtn" ).button().on( "click", function() {
          dialog.dialog( "open" );
        });

        $( "#createCohortBtn" ).button().on( "click", function() {
          cohortDialog.dialog( "open" );
        });

        $( "#variantSearchBtn" ).button().on( "click", function() {
          window.location = '${createLink(controller:"variantSearch", action:"index", params:[studyId: study.id])}';
        });


        $('span.aclMember').mouseover(function(event) {
            $(this).find('img').show();
        });
        $('span.aclMember').mouseout(function(event) {
            $(this).find('img').hide();
        });



    });

    </script>

</head>
<body>



<!-- Fine Uploader Thumbnails template w/ customization
====================================================================== -->
<script type="text/template" id="qq-template-manual-trigger">
    <div class="qq-uploader-selector qq-uploader" qq-drop-area-text="Drop files here">
        <div class="qq-total-progress-bar-container-selector qq-total-progress-bar-container">
            <div role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" class="qq-total-progress-bar-selector qq-progress-bar qq-total-progress-bar"></div>
        </div>
        <div class="qq-upload-drop-area-selector qq-upload-drop-area" qq-hide-dropzone>
            <span class="qq-upload-drop-area-text-selector"></span>
        </div>
        <div class="buttons">
            <div class="qq-upload-button-selector qq-upload-button">
                <div>Select files</div>
            </div>
            <button type="button" id="trigger-upload" class="btn btn-primary">
                <i class="icon-upload icon-white"></i> Upload
            </button>
        </div>
        <span class="qq-drop-processing-selector qq-drop-processing">
                <span>Processing dropped files...</span>
                <span class="qq-drop-processing-spinner-selector qq-drop-processing-spinner"></span>
            </span>
        <ul class="qq-upload-list-selector qq-upload-list" aria-live="polite" aria-relevant="additions removals">
            <li>
                <div class="qq-progress-bar-container-selector">
                    <div role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" class="qq-progress-bar-selector qq-progress-bar"></div>
                </div>
                <span class="qq-upload-spinner-selector qq-upload-spinner"></span>
                <img class="qq-thumbnail-selector" qq-max-size="100" qq-server-scale>
                <span class="qq-upload-file-selector qq-upload-file"></span>
                <span class="qq-edit-filename-icon-selector qq-edit-filename-icon" aria-label="Edit filename"></span>
                <input class="qq-edit-filename-selector qq-edit-filename" tabindex="0" type="text">
                <span class="qq-upload-size-selector qq-upload-size"></span>
                <button type="button" class="qq-btn qq-upload-cancel-selector qq-upload-cancel">Cancel</button>
                <button type="button" class="qq-btn qq-upload-retry-selector qq-upload-retry">Retry</button>
                <button type="button" class="qq-btn qq-upload-delete-selector qq-upload-delete">Delete</button>
                <span role="status" class="qq-upload-status-text-selector qq-upload-status-text"></span>
            </li>
        </ul>

        <dialog class="qq-alert-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <div class="qq-dialog-buttons">
                <button type="button" class="qq-cancel-button-selector">Close</button>
            </div>
        </dialog>

        <dialog class="qq-confirm-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <div class="qq-dialog-buttons">
                <button type="button" class="qq-cancel-button-selector">No</button>
                <button type="button" class="qq-ok-button-selector">Yes</button>
            </div>
        </dialog>

        <dialog class="qq-prompt-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <input type="text">
            <div class="qq-dialog-buttons">
                <button type="button" class="qq-cancel-button-selector">Cancel</button>
                <button type="button" class="qq-ok-button-selector">Ok</button>
            </div>
        </dialog>
    </div>
</script>


<style>
        #trigger-upload {
            color: white;
            background-color: #00ABC7;
            font-size: 14px;
            padding: 7px 20px;
            background-image: none;
        }

        #fine-uploader-manual-trigger .qq-upload-button {
            margin-right: 15px;
        }

        #fine-uploader-manual-trigger .buttons {
            width: 20%;
            margin: 10px;
            padding: 5px;
        }

        #fine-uploader-manual-trigger .qq-uploader .qq-total-progress-bar-container {
            width: 60%;
        }

        .delete {
            border: 1px solid gray;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>


<div id="content" role="main">
    <section class="row colset-2-its">

        <g:if test="${study}">
            <h1>Study - ${study.name?.encodeAsHTML() }</h1>

            <div>
                <g:if test="${canShare}">
                    <button id="shareBtn">Share</button>

                    <button id="variantSearchBtn">Variant Search</button>

                    <button id="createCohortBtn">Create Cohort</button>
                </g:if>
            </div>

            <div id="shareDlg" title="Share study" style="display:none">
                <g:form name="shareForm" action="share">
                    <div>
                        User emails:
                        <p style="color: gray;">To share with all AGHA members, enter the asterik character: *</p>
                        <g:textField name="users" size="50" />
                    </div>
                    <br/>
                    <div>
                        Permissions:
                        <g:select name="permissions" from="${['', 'admin', 'analyst', 'view_only' ]}" valueMessagePrefix="study.permission" required="true" />
                    </div>

                    <g:hiddenField name="id" value="${study.id}" />
                </g:form>
            </div>

            <div id="createCohortDlg" title="Create Cohort" style="display:none">

                <g:form name="cohortForm" controller="cohort" action="create">
                    <div>
                        Cohort name:
                        <g:textField name="name" size="50" />
                    </div>

                    <g:hiddenField name="studyId" value="${study.id}" />
                </g:form>
            </div>

            <fieldset>
                <legend>Details</legend>
                <table>
                    <tr>
                        <td>Owner:</td>
                        <td>${OpencgaHelper.parseProjectOwnerFromUri(study.uri)}</td>
                    </tr>
                    <g:if test="${study.acl}">
                        <tr>
                            <td>Shared with:</td>
                            <td>
                                <g:each in="${study.acl}" var="user" status="i">
                                    <g:if test="${ i > 0}">
                                        ,
                                    </g:if>
                                    <span class="aclMember">
                                        ${user.member?.encodeAsHTML()}
                                        <g:if test="${canShare}">
                                            <asset:image src="skin/delete.png" class="delete" style="display:none;" user="${user.member}" onclick="removeAccess(this, ${study.id});" />
                                        </g:if>
                                    </span>

                                </g:each>

                            </td>
                        </tr>
                    </g:if>
                </table>
            </fieldset>

            <fieldset>
                <legend>Cohorts</legend>

                <g:if test="${cohorts}">
                    <table>
                        <tr>
                            <th>Name</th>
                        </tr>
                        <g:each in="${cohorts}" var="cohort">
                            <g:if test="${cohort.name != 'ALL'}" >
                            <tr>
                                <td>
                                    <g:link controller="cohort" action="show" params="[id: cohort.id, studyId: study.id]">
                                        ${cohort.name.encodeAsHTML()}
                                    </g:link>
                                </td>

                            </tr>
                            </g:if>
                        </g:each>
                    </table>
                </g:if>
            </fieldset>

            <fieldset>
                <legend>Files</legend>

                <g:if test="${hasFiles}">
                    <table>
                        <tr>
                            <th>Name</th>
                            <th>Sample</th>
                            <th>Format</th>
                            <th>Size</th>
                            <th>Indexed</th>
                            <th></th>
                        </tr>
                        <g:each in="${ files }" var="file">
                            <g:if test="${file.type == 'FILE' && (file.format=='VCF' || file.format=='BAM' || file.format=='FASTQ')}">
                                <tr>
                                    <td>
                                        <g:link controller="download" action="download" params="[id: file.id]">
                                            ${file.name?.encodeAsHTML()}
                                        </g:link>
                                    </td>
                                    <td>
                                        <g:each in="${file.sampleIds}" var="sampleId">
                                            <g:sampleName sampleId="${sampleId?.toString()}" />
                                        </g:each>
                                    </td>
                                    <td>${file.format?.encodeAsHTML()}</td>
                                    <td>
                                        <g:if test="${file.size}">
                                            ${Math.ceil(file.size / 1024 / 1024).toInteger() } MB
                                        </g:if>
                                    </td>
                                    <td>
                                        ${file.index.status.name}
                                    </td>
                                    <td>
                                        <g:link controller="file" action="delete" params="[fileId: file.id, studyId: study.id]">
                                            <asset:image src="skin/delete.png" class="delete" />
                                        </g:link>
                                    </td>

                                </tr>
                            </g:if>
                        </g:each>
                    </table>
                </g:if>
                <g:else>
                    No files available
                </g:else>
            </fieldset>

            <h2>Add files</h2>
            <!-- Fine Uploader DOM Element
            ====================================================================== -->
            <div id="fine-uploader-manual-trigger"></div>

        </g:if>
        <g:else>
            Study not found
        </g:else>
    </section>
</div>



<!-- Your code to create an instance of Fine Uploader and bind to the DOM/template
 ====================================================================== -->
<script>
        var manualUploader = new qq.FineUploader({
            element: document.getElementById('fine-uploader-manual-trigger'),
            template: 'qq-template-manual-trigger',
            request: {
                endpoint: '${createLink(controller:"file", action:"fineuploader")}',
                params: {studyId: ${study.id} }
            },
            thumbnails: {
                placeholders: {
                    waitingPath: '${resource(dir: "fine-uploader/placeholders", file:"waiting-generic.png")}',
                    notAvailablePath: '${resource(dir: "fine-uploader/placeholders", file:"not_available-generic.png")}'
                }
            },
            chunking: {
                enabled: true,
                partSize: 50000000,
                concurrent: {
                    enabled:true
                },
                success: {
                    endpoint: '${createLink(controller:"file", action:"fineuploaderChunkSuccess")}'
                }
            },
            resume: { enabled: true },
            autoUpload: false,
            debug: true,
            callbacks: {
                onCancel: function(id) {
                    console.log('deleting server side file from cancelled uploads for id: '+id)
                    var uuid = manualUploader.getUuid(id)
                    console.log('uuid='+uuid);
                    $.post("${createLink(controller:'file', action:'cancel')}?qquuid="+uuid)
                },
                onAllComplete: function(succeeded, failed) {
                    console.log('succeeded: '+succeeded);
                    console.log('failed: '+failed);

                    if (failed.length == 0) {
                        alert('Uploaded successful. Reloading page');
                        location.reload();
                    } else {
                        alert('Failed to upload the following files: '+ failed);
                    }
                }
            }
        });

        qq(document.getElementById("trigger-upload")).attach("click", function() {
            manualUploader.uploadStoredFiles();
        });
    </script>


</body>
</html>
