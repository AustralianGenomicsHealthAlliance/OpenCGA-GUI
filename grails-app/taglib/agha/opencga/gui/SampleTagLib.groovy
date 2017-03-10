package agha.opencga.gui

class SampleTagLib {

    OpenCGAService openCGAService

    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def sampleName = { attrs, body ->
        if (attrs.sampleId) {
            String sessionId = openCGAService.loginCurrentUser()
            def sampleInfo = openCGAService.samplesInfo(sessionId, attrs.sampleId)
            out << sampleInfo.name?.encodeAsHTML()

        }
    }
}
