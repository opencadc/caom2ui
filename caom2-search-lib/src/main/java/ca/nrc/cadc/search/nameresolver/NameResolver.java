/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2007.                            (c) 2007.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.nameresolver;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.net.InputStreamWrapper;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.nameresolver.exception.ClientException;
import ca.nrc.cadc.search.nameresolver.exception.TargetNotFoundException;
import ca.nrc.cadc.search.nameresolver.exception.WebServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ca.nrc.cadc.astro.CoordUtil;
import ca.nrc.cadc.util.StringUtil;


/**
 * Client Class to query the Name Resolver servlet using an HTTP GET request. The Name Resolver servlet
 * attempts to resolve a target name to RA and DEC coordinates in degrees.
 * Four methods are provided to resolve a target name. Each method takes the target name,
 * then optionally either a service argument, or cached argument , or both.
 * By default Name Resolver concurrently queries NED at CalTech, Simbad at CDS, and VizieR at CDS and CADC,
 * returning the first positive result. The service argument can specify any combination of NED, SIMBAD,
 * or VIZIER, comma delimited if more than one is specified, no spaces allowed. The service argument
 * is not case sensitive.
 * The cached argument is true to return results from the Name Resolver cache (the default), or false
 * to force the Name Resolver to query the services for the target without checking it's cached results.
 *
 * @author jburke
 */
public class NameResolver {

    private static final int NAME_RESOLVER_HTTP_ERROR_CODE = 425;

    private static final String SERVICE_PARAMETER = "&service=";
    private static final String CACHED_PARAMETER = "&cached=";
    private static final String MAX_DETAIL_PARAMETER = "&detail=max";
    private static final String[] SERVICES = new String[]
            {
                    "NED", "SIMBAD", "VIZIER", "ALL"
            };

    /**
     * Constructs a new CADCResolver initialized with the default services and cached values.
     */
    public NameResolver() {
    }

    /**
     * Resolves the given target name using the default services and returning cached results if available.
     *
     * @param target the target name to resolve.
     * @return ResolverData object containing the results of the Name Resolver query.
     *
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(String target)
            throws ClientException, WebServiceException, TargetNotFoundException {
        return resolve(target, null, true, false);
    }

    /**
     * Resolves the given target name using the default services with the specified cached value.
     *
     * @param target the target name to resolve.
     * @param cached whether the target can be resolved from the name resolver cache, default is true.
     * @return ResolverData object containing the results of the Name Resolver query.
     *
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(String target, boolean cached)
            throws ClientException, WebServiceException, TargetNotFoundException {
        return resolve(target, null, cached, false);
    }

    /**
     * Resolves the given target name using the specified service(s) and returning cached results if available.
     *
     * @param target  the target name to resolve.
     * @param service the services to query to resolve the target name, default is to query all services.
     * @return ResolverData object containing the results of the Name Resolver query.
     *
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(String target, String service)
            throws ClientException, WebServiceException, TargetNotFoundException {
        return resolve(target, service, true, false);
    }

    /**
     * Resolves the given target name using the specified service(s) and cached value.
     *
     * @param target    the target name to resolve.
     * @param service   the services to query to resolve the target name, default is to query all services.
     * @param cached    whether the target can be resolved from the name resolver cache, default is true.
     * @param maxDetail display all fields returned from the resolver.
     * @return ResolverData object containing the results of the Name Resolver query.
     *
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(final String target, final String service,
                                    final boolean cached, final boolean maxDetail)
            throws ClientException, WebServiceException, TargetNotFoundException {
        // if target not valid, return
        if (target == null || target.length() == 0) {
            throw new ClientException("Target not specified");
        }

        // check if service is valid
        validateService(service);

        try {
            // create the request url 
            final URL url = new URL(getUrlString(URLEncoder.encode(target, "UTF-8"),
                                                 service, cached, maxDetail));
            final Properties properties = new Properties();

            final HttpDownload httpDownload = new HttpDownload(url, new InputStreamWrapper() {
                /**
                 * Read the bytes of the inputStream.
                 *
                 * @param inputStream  InputStream from the Request.
                 */
                @Override
                public void read(final InputStream inputStream) throws IOException {
                    properties.load(inputStream);
                }
            });

            httpDownload.run();
            final int responseCode = httpDownload.getResponseCode();

            if (responseCode == NAME_RESOLVER_HTTP_ERROR_CODE) {
                throw new TargetNotFoundException("Unable to resolve target "
                                                          + target);
            } else if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new WebServiceException("Name Resolver Web Service error, HTTP response code " + responseCode);
            } else {
                return new NameResolverData(properties);
            }

        } catch (UnsupportedEncodingException uee) {
            throw new ClientException("Unsupported url encoding: "
                                              + uee.getMessage());
        } catch (MalformedURLException mue) {
            throw new ClientException("Malformed url: "
                                              + mue.getMessage());
        } catch (NumberFormatException nfe) {
            throw new ClientException("Number format exception: "
                                              + nfe.getMessage());
        } catch (IllegalArgumentException iae) {
            throw new ClientException("Illegal argument exception: "
                                              + iae.getMessage());
        }
    }

    /**
     * Validates the service, throwing an exception if any errors are found.
     * Throws a CadcResolverException when:
     * - The service contains whitespace characters.
     * - The first invalid service name is found.
     */
    private void validateService(final String service) throws ClientException {
        if (StringUtil.hasText(service)) {
            // check for whitespace characters
            if (service.matches("[a-zA-Z,]*\\s+.*")) {
                throw new ClientException(
                        "Invalid whitespace character in service argument");
            }

            final String[] services = service.split(",");
            for (final String name : services) {
                boolean found = false;

                for (final String serviceName : SERVICES) {
                    if (name.equalsIgnoreCase(serviceName)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    throw new ClientException("Invalid service argument "
                                                      + name);
                }
            }
        }
    }

    /**
     * Construct the Name Resolver query url string.
     */
    private String getUrlString(final String encodedTarget,
                                final String service, final boolean cached,
                                final boolean maxDetail) {
        final RegistryClient registryClient = new RegistryClient();
        final URL resolverURL = registryClient.getServiceURL(
                URI.create("ivo://cadc.nrc.ca/resolver"), Standards.RESOLVER_10,
                AuthMethod.ANON);
        final StringBuilder sb = new StringBuilder();

        sb.append(resolverURL.toString());
        sb.append("?format=ascii&target=");
        sb.append(encodedTarget);

        if (service != null) {
            sb.append(SERVICE_PARAMETER).append(service.toLowerCase());
        }

        if (!cached) {
            sb.append(CACHED_PARAMETER).append("no");
        }

        if (maxDetail) {
            sb.append(MAX_DETAIL_PARAMETER);
        }

        return sb.toString();
    }
}
