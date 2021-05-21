CAOM-2 Search interface v2.13.3
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This is the User Interface to perform complicated searches to a running
`TAP <http://www.ivoa.net/documents/TAP/>`__ web service.

Building
~~~~~~~~

Simply run ``gradle clean build``

To have a constructed JAR file in the ``build/libs`` directory that can
be used to create a WAR to then be run in a Java Servlet Container.

See the Properties File section below for properties to set, although it
is very recommended to leave them as their defaults.

Deployment options
~~~~~~~~~~~~~~~~~~

Properties File
^^^^^^^^^^^^^^^

Location: config/org.opencadc.search.properties
'''''''''''''''''''''''''''''''''''''''''''''''

Example content:
''''''''''''''''

::

    # The TAP Service ID to resolve.
    org.opencadc.search.tap-service-id = ivo://cadc.nrc.ca/argus

    # The endpoint for this application. 
    # org.opencadc.search.app-service-endpoint = /search

    # Whether to show the ObsCore tab
    # org.opencadc.search.obs-core = true

    # Max row count for results
    # org.opencadc.search.max-row-count = 10000


Running
~~~~~~~

Running in a Servlet Container
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Simply drop the WAR into a Java Servlet Container, then point a browser
to: http://localhost:8080/search/

To bring up the form. By default, this will connect to the `CADC TAP
service <http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/tap>`__, but can be
configured with the ``org.opencadc.search.tap-service-id`` system
property like so:

``-Dca.nrc.cadc.reg.client.RegistryClient.host=%YOUR HOST% -Dorg.opencadc.search.tap-service-id=ivo://%YOUR DOMAIN%/tap-service``

Where ``%YOUR HOST%`` is the location of a running TAP web service, and
the service id is made up of your Oragnization's service URI policy.

Example implementation
^^^^^^^^^^^^^^^^^^^^^^

The `examples/default`_ folder contains an example implementation of the ``caom2-search-server`` library.

.. _examples/default: examples/default