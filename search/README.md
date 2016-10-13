### CAOM-2 Search interface

<!--<a href="https://travis-ci.org/at88mph/caom2ui"><img src="https://travis-ci.org/at88mph/caom2ui.svg?branch=master" /></a>-->

This is the User Interface to perform complicated searches to a running [TAP](http://www.ivoa.net/documents/TAP/) web service.

### Building

Simply run
`gradle clean build`

To have a constructed WAR file in the `build/libs` directory that can be run in a Java Servlet Container.

### Running

#### Running in a Servlet Container
Simply drop the WAR into a Java Servlet Container, then point a browser to:
http://localhost:8080/search/

To bring up the form.  By default, this will connect to the [CADC TAP service](http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/tap), but can be configured with the `cadc.search.tapServiceID` system property like so:

`-Dca.nrc.cadc.reg.client.RegistryClient.host=<YOUR HOST>`

Where `<YOUR HOST>` is the location of a running TAP web service.

#### Running with Docker

Alternatively, this can be built into a Docker image and run as a container anywhere.

To then construct a Docker image that can be run anywhere, modify the `build.gradle` file's variable `docker_image_name` to your liking, then run
`gradle dockerize`

To have a constructed docker image ready to run.  As of this writing, this image is based off the `tomcat:8.5-alpine` image.

The provided [`docker-compose`](docker-compose.yml) file will construct a fully working system using the pre-built images in [OpenCADC](https://hub.docker.com/r/opencadc/).
