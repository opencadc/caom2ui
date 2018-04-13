
### CAOM-2 Search interface v2.7.0

<a href="https://travis-ci.org/opencadc/caom2ui"><img src="https://travis-ci.org/opencadc/caom2ui.svg?branch=master" /></a>

This is the User Interface to perform complicated searches to a running [TAP](http://www.ivoa.net/documents/TAP/) web service.

### Building

Simply run
`gradle clean build`

To have a constructed JAR file in the `build/libs` directory that can be used to create a WAR to then be run in a Java Servlet Container.

See the Properties File section below for properties to set, although it is very recommended to leave
them as their defaults.

### Deployment options
#### Properties File
##### Location: config/org.opencadc.search.properties

##### Example content:
```
# The TAP Service ID to resolve.
# org.opencadc.search.tap-service-id = ivo://cadc.nrc.ca/tap

# Alternate TAP Service ID to resolve.
# org.opencadc.search.maq-tap-service-id = ivo://cadc.nrc.ca/tap/maq

# Activate (toggle on) the MAQ slider switch.
# org.opencadc.search.maq-activated = false

# The endpoint for this application. 
# org.opencadc.search.app-service-endpoint = /search

# Whether to show the ObsCore tab
# org.opencadc.search.obs-core = true

# Max row count for results
# org.opencadc.search.max-row-count = 10000
```

#### Secondary TAP data source
On the query form, there is the option of enabling a toggle to allow Archive Search to switch between two TAP data services.

The primary service is defined in the Properties file using:
`org.opencadc.search.tap-service-id`

The secondary as
`org.opencadc.search.maq-tap-service-id`

To enable the toggle, include a key/value pair using the secondary key in the Properties File.

You can also optionally enable the switch by default by setting:
`org.opencadc.search.maq-activated` to `true`


### Running

#### Running in a Servlet Container
Simply drop the WAR into a Java Servlet Container, then point a browser to:
[http://localhost:8080/search/](http://localhost:8080/search/)

To bring up the form.  By default, this will connect to the [CADC TAP service](http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/tap), but can be configured with the `org.opencadc.search.tap-service-id` system property like so:

`-Dca.nrc.cadc.reg.client.RegistryClient.host=%YOUR HOST% -Dorg.opencadc.search.tap-service-id=ivo://%YOUR DOMAIN%/tap-service`

Where `%YOUR HOST%` is the location of a running TAP web service, and the service id is made up of your Oragnization's service URI policy.

#### Running with Docker

Alternatively, this can be built into a Docker image and run as a container anywhere.

To then construct a Docker image that can be run anywhere, modify the `build.gradle` file's variable `docker_image_name` to your liking, then run

`gradle dockerize`

To have a constructed docker image ready to run (run `docker images` to see if it's listed).  As of this writing, the web application images are based off the `tomcat:8.5-alpine` image.

The provided [`docker-compose`](docker-compose.yml) file will construct a fully working system using the pre-built images in [OpenCADC](https://hub.docker.com/r/opencadc/).

Ideally, the databases (`tappg`, `uwspg`, `caom2opspg`) should have mounted volumes to move state out of the container:

```YAML
...
    uwspg:
    image: opencadc/uws_postgres
    networks:
        - 'caom2'
    environment:
        - POSTGRES_USER=uws
        - POSTGRES_PASSWORD=astr0query
        - PGDATA=/var/lib/postgresql/data/uws
    volumes:
        - /var/lib/postgresql/data
        - /var/run/postgresql
    tappg:
    image: opencadc/tap_postgres
    networks:
        - 'caom2'
    environment:
        - POSTGRES_USER=tap
        - POSTGRES_PASSWORD=astr0query
        - PGDATA=/var/lib/postgresql/data/tap
    volumes:
        - /var/lib/postgresql/data
        - /var/run/postgresql
    caom2opspg:
    image: opencadc/uws_postgres
    networks:
        - 'caom2'
    environment:
        - POSTGRES_USER=caom2ops
        - POSTGRES_PASSWORD=astr0query
        - PGDATA=/var/lib/postgresql/data/uws
    volumes:
        - /var/lib/postgresql/data
        - /var/run/postgresql
...
```

Notice that the `PGDATA` variable is set to `/var/lib/postgresql/data/tap`, so the `tap` directory will need to be created in the host's `/var/lib/postgresql/data` directory.

See the [Docker PostgreSQL](https://hub.docker.com/_/postgres) documentation.

Not mounting the volumes from the host will keep all of the `postgresql` data in the container, which is volatile.
