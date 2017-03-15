package agha.opencga.gui

class SampleTagLib {

    OpenCGAService openCGAService

    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def sampleName = { attrs, body ->
        if (attrs.sampleId) {

            // initialize request cache
            if (request.sampleCacheMap == null) {
                request.sampleCacheMap = [:]
            }

            // Check cache
            String cachedSampleName = request.sampleCacheMap.get(attrs.sampleId)
            if (cachedSampleName) {
                out << cachedSampleName
            } else {

                String sessionId = openCGAService.loginCurrentUser()
                def sampleInfo = openCGAService.samplesInfo(sessionId, attrs.sampleId)
                String sampleName = sampleInfo.name?.encodeAsHTML()
                // Update cache
                request.sampleCacheMap.put(attrs.sampleId, sampleName)
                out << sampleName
            }

        }
    }
}
