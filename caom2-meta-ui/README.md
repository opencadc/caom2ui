# caom2-meta-ui
2017.11.27
CAOM-2 web user interface for Observation details (v1008)

<a href="https://travis-ci.org/opencadc/caom2ui"><img src="https://travis-ci.org/opencadc/caom2ui.svg?branch=master" /></a>

The Meta UI will show a detailed tree-like view of Observations.

## Running with Docker.

The default configuration will use the CAOM-2 Meta service at CADC.

```
docker run --name meta-ui -p 8080:8080 --rm opencadc/caom2-meta-ui:1008
```

### Build your own Docker image

The Gradle build file contains necessary items to build your own image.

```
gradle -Pdocker_image_name=myuser/myimage clean dockerize
```

Will create an image called `myuser/myimage`.

## API

### HTTP GET

The only supported operation is a GET in the form of `view?ID=<CAOM2_PUBLISHER_ID>`, like so:

http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/caom2ui/view?ID=ivo%3A%2F%2Fcadc.nrc.ca%2FDAO%3Fdao_c122_2017_020890%2Fdao_c122_2017_020890
