<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Study - ${study?.name?.encodeAsHTML() }</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <script src="${resource(dir: 'fine-uploader', file:'fine-uploader.js')}" ></script>
    <link rel="stylesheet" href="${resource(dir:'fine-uploader', file:'fine-uploader-new.css') }" />

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
            width: 36%;
        }

        #fine-uploader-manual-trigger .qq-uploader .qq-total-progress-bar-container {
            width: 60%;
        }
    </style>


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

<!-- Fine Uploader DOM Element
====================================================================== -->
<div id="fine-uploader-manual-trigger"></div>

<!-- Your code to create an instance of Fine Uploader and bind to the DOM/template
 ====================================================================== -->
<script>
        var manualUploader = new qq.FineUploader({
            element: document.getElementById('fine-uploader-manual-trigger'),
            template: 'qq-template-manual-trigger',
            request: {
                endpoint: '${createLink(controller:"study", action:"upload")}'
            },
            thumbnails: {
                placeholders: {
                    waitingPath: '${resource(dir: "fine-uploader/placeholders", file:"waiting-generic.png")}',
                    notAvailablePath: '${resource(dir: "fine-uploader/placeholders", file:"not_available-generic.png")}'
                }
            },
            autoUpload: false,
            debug: true
        });

        qq(document.getElementById("trigger-upload")).attach("click", function() {
            manualUploader.uploadStoredFiles();
        });
    </script>


</body>
</html>
