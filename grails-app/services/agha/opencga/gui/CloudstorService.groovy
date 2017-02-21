package agha.opencga.gui

import com.github.sardine.DavResource
import com.github.sardine.Sardine
import com.github.sardine.SardineFactory
import grails.transaction.Transactional
import org.apache.log4j.Logger

@Transactional
class CloudstorService {

    Logger logger = Logger.getLogger(CloudstorService.class)

    def grailsApplication

    public Sardine connect() {

        String username = grailsApplication.config.cloudstor.username
        String password = grailsApplication.config.cloudstor.password

        Sardine sardine = SardineFactory.begin()
        sardine.setCredentials(username, password)

        return sardine
    }

    public List<DavResource> list() {
        String url = grailsApplication.config.cloudstor.url

        Sardine sardine = connect()
        List<DavResource> resources = sardine.list(url)

        for (DavResource res: resources) {
            logger.info('resource: '+res+ ' customProps:'+res.customPropsNS+ ' etag: '+res.etag+' resourceTypes: '+res.resourceTypes+' name: '+res.name+' supportedReports: '+res.supportedReports)

        }

        return resources
    }

}
