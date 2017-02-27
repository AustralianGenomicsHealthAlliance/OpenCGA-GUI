<!doctype html>
<%@ page import="agha.opencga.gui.*" %>

<html>
<head>
    <meta name="layout" content="main"/>
    <title>Variant Search</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <style>

        .truncate {
          max-width: 150px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

    </style>

</head>
<body>

    <div id="content" role="main">
        <section class="row colset-2-its">

            <h1>Variant Search</h1>

            <g:form action="index">
                Region: <g:textField name="region" value="${region}" />

                <g:hiddenField name="studyId" value="${params.studyId}" />

                <g:submitButton name="search" value="Search" />
            </g:form>

            <g:if test="${searchResults}">
                <h2>Search Results</h2>
                <table>
                    <tr>
                        <th>Chr</th>
                        <th>Coordinates</th>
                        <th>Variation</th>
                        <th>Strand</th>
                        <th>Type</th>
                        <th>Gene(s)</th>
                        <th>Disease associations</th>
                        <th>CADD Score</th>
                    </tr>
                    <g:each in="${searchResults}" var="variant" >
                        <tr>
                            <td>${variant.chromosome}</td>
                            <td>${variant.start}-${variant.end}</td>
                            <td>${variant.strand}</td>
                            <td>${variant.type}</td>

                            <td><div class="truncate">${variant.reference} -> ${variant.alternate}</div></td>
                            <td>
                                <g:each in="${variant.annotation?.consequenceTypes}" var="consequence" status="i">
                                    <g:if test="${consequence.geneName}" >
                                        <g:if test="${ i > 0 }" >, </g:if>${consequence.geneName.encodeAsHTML()}
                                    </g:if>
                                </g:each>
                            </td>
                            <td>
                                <div class="truncate">
                                <g:each in="${variant.annotation?.geneTraitAssociation}" var="geneTraitAssociation" status="i">
                                    <g:if test="${ i > 0 }" >, </g:if>
                                    ${ geneTraitAssociation?.name?.encodeAsHTML()}
                                </g:each>
                                </div>
                            </td>
                            <td>
                                <g:each in="${variant.annotation?.functionalScore}" var="functionalScore" status="i">
                                    <g:if test="${i > 0}"><br/> </g:if>
                                    ${functionalScore.score} (${functionalScore.source})
                                </g:each>
                            </td>
                        </tr>
                    </g:each>
                </table>
            </g:if>

        </section>
    </div>

</body>
</html>
