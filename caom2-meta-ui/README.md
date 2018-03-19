# caom2-meta-ui

CAOM-2 web user interface for Observation details (1.1.4)

<a href="https://travis-ci.org/opencadc/caom2ui"><img src="https://travis-ci.org/opencadc/caom2ui.svg?branch=master" /></a>

The Meta UI will show a detailed tree-like view of Observations.

## Running with Docker

The default configuration will use the CAOM-2 Meta service at CADC.

```
docker run --name meta-ui -p 8080:8080 --rm opencadc/caom2-meta-ui:1009
```

### Running against your own caom2 meta service

The built-in Registry Client can look up to different registries.  By default, it queries the CADC Production registry (`www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca`, but this can be configured by passing in an environment variable:

```
docker run --name meta-ui -p 8080:8080 -e CATALINA_OPTS="-Dca.nrc.cadc.reg.client.RegistryClient.host=myhost.com" --rm opencadc/caom2-meta-ui:1009
```

Where `myhost.com` contains the `resource-caps.config` file to list the available services.

### Build your own Docker image

The Gradle build file contains necessary items to build your own image.

```
gradle -Pdocker_image_name=myuser/myimage clean dockerize
```

This command will create an image called `myuser/myimage`.
