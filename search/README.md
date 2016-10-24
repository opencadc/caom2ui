### CAOM-2 Search interface

<!--<a href="https://travis-ci.org/at88mph/caom2ui"><img src="https://travis-ci.org/at88mph/caom2ui.svg?branch=master" /></a>-->

This is the User Interface to perform complicated searches to a running [TAP](http://www.ivoa.net/documents/TAP/) web service.

### Building

Simply run
`gradle clean build`

To have a constructed WAR file in the `build/libs` directory that can be run in a Java Servlet Container.

The current status uses an external server to supply static resources at CADC.  It relies on
technologies ([Web Experience Toolkit](http://wet-boew.github.io/wet-boew-legacy/v3.1/index.html)), that are no longer supported.
Efforts are underway to replace them with a much better (and modern) user experience.

See the [index.jsp](src/main/webapp/index.jsp) for properties to set, although it is very recommended to leave
them as their defaults.

### Running

#### Running in a Servlet Container
Simply drop the WAR into a Java Servlet Container, then point a browser to:
[http://localhost:8080/search/](http://localhost:8080/search/)

To bring up the form.  By default, this will connect to the [CADC TAP service](http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/tap), but can be configured with the `org.opencadc.search.tap-service-id` system property like so:

`-Dca.nrc.cadc.reg.client.RegistryClient.host=<YOUR HOST> -Dorg.opencadc.search.tap-service-id=ivo://<YOUR DOMAIN>/tap-service`

Where `<YOUR HOST>` is the location of a running TAP web service, and the service id is made up of your Oragnization's service URI policy.

#### Running with Docker

Alternatively, this can be built into a Docker image and run as a container anywhere.

To then construct a Docker image that can be run anywhere, modify the `build.gradle` file's variable `docker_image_name` to your liking, then run

`gradle dockerize`

To have a constructed docker image ready to run (run `docker images` to see if it's listed).  As of this writing, the web application images are based off the `tomcat:8.5-alpine` image.

The provided [`docker-compose`](docker-compose.yml) file will construct a fully working system using the pre-built images in [OpenCADC](https://hub.docker.com/r/opencadc/).

Ideally, the database (`tappg`) should have mounted volumes to move state out of the container:

```YAML
...
    tappg:
    image: opencadc/tap_postgres
    networks:
    - 'caom2'
    environment:
    - POSTGRES_USER=tap
    - POSTGRES_PASSWORD=astr0query
    - PGDATA=/var/lib/postgresql/data/tap
    volumes:
    - /var/lib/postgresql/data:/var/lib/postgresql/data
    - /var/run/postgresql/tap:/var/run/postgresql
...
```

Where the `/var/lib/postgresql/data` and `/var/run/postgresql/tap` directories are on the host, and are mounted as their mapped volumes (i.e. after the colon).

Notice that the `PGDATA` variable is set to `/var/lib/postgresql/data/tap`, so the `tap` directory will need to be created in the host's `/var/lib/postgresql/data` directory.

See the [Docker PostgreSQL](https://hub.docker.com/_/postgres) documentation.

Not mounting the volumes from the host will keep all of the `postgresql` data in the container, which is volatile.
