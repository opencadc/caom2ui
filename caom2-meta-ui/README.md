# caom2-meta-ui

CAOM-2 web user interface for Observation details

The Meta UI will show a detailed tree-like view of Observations.

## Running with Docker

The default configuration will use the CAOM-2 Meta service at CADC.

```
$ docker build -t caom2-meta-ui .
$ docker run --name meta-ui -p 8080:8080 --rm caom2-meta-ui
```

### Running against your own CAOM-2 Meta service

The built-in Registry Client can look up to different registries.  By default, it queries the CADC Production registry (`https://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca`, but this can be configured by passing in an environment variable:

```
docker run --name meta-ui -p 8080:8080 -e CATALINA_OPTS="-Dca.nrc.cadc.reg.client.RegistryClient.host=myhost.com" --rm caom2-meta-ui
```

Where `myhost.com` contains the `resource-caps.config` file to list the available services.
