# caom2-meta-ui

CAOM-2 web user interface for Observation details (1.1.4)

<a href="https://travis-ci.org/opencadc/caom2ui"><img src="https://travis-ci.org/opencadc/caom2ui.svg?branch=master" /></a>

The Meta UI will show a detailed tree-like view of Observations.

## Running with Docker.

The default configuration will use the CAOM-2 Meta service at CADC.

```
docker run --name meta-ui -p 8080:8080 --rm opencadc/caom2-meta-ui:1009
```

### Build your own Docker image

The Gradle build file contains necessary items to build your own image.

```
gradle -Pdocker_image_name=myuser/myimage clean dockerize
```

This command will create an image called `myuser/myimage`.
