package agha.opencga.gui

import org.apache.log4j.Logger

/**
 * Created by philip on 9/02/17.
 */
class OpencgaHelper {

    static Logger logger = Logger.getLogger(OpencgaHelper.class)

    /**
     * Example uri: file:///opt/opencga/sessions/users/hongyu/projects/15/16/
     * @param uri
     * @return
     */
    public static String parseProjectOwnerFromUri(String uri) {
        if (uri) {
            def (index, uriParts) = findIndexOfProjects(uri)

            return uriParts[index-1]

        } else {
            return null
        }
    }

    public static String parseProjectIdFromUri(String uri) {
        if (uri) {
            def (index, uriParts) = findIndexOfProjects(uri)

            return uriParts[index+1]

        } else {
            return null
        }
    }

    /**
     * Find the index of the 'projects' in the URI
     * @param uri
     * @return
     */
    def static findIndexOfProjects(String uri) {
        String[] uriParts = uri.split('/')

        int index = 0
        for (String uriPart: uriParts) {
            if (uriPart.equals('projects')) {
                break
            }
            index++
        }

        return [index, uriParts]
    }

    /**
     * All openCGA rest responses have similar json templates.
     *
     * This is just a convenience method to quickly retrieve the primary result of interest.
     *
     * @param json
     * @return
     */
    def static getJsonResult(def json) {
        //logger.info('response: '+json?.response)
        if (json.response.get(0).errorMsg) {
            throw new Exception(json.response.get(0).errorMsg)
        } else {
            return json?.response?.get(0).result
        }
    }

}
