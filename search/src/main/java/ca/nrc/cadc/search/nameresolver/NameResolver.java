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

import ca.nrc.cadc.astro.ConversionUtil;
import ca.nrc.cadc.search.nameresolver.exception.ClientException;
import ca.nrc.cadc.search.nameresolver.exception.TargetNotFoundException;
import ca.nrc.cadc.search.nameresolver.exception.WebServiceException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ca.nrc.cadc.util.StringUtil;


/**
 * Client Class to query the Name Resolver servlet using an HTTP GET request. The Name Resolver servlet
 * attempts to resolve a target name to RA and DEC coordinates in degrees.
 * <p></p>
 * Four methods are provided to resolve a target name. Each method takes the target name,
 * then optionally either a service argument, or cached argument , or both.
 * <p></p>
 * By default Name Resolver concurrently queries NED at CalTech, Simbad at CDS, and VizieR at CDS and CADC,
 * returning the first positive result. The service argument can specify any combination of NED, SIMBAD,
 * or VIZIER, comma delimited if more than one is specified, no spaces allowed. The service argument
 * is not case sensitive.
 * <p></p>
 * The cached argument is true to return results from the Name Resolver cache (the default), or false
 * to force the Name Resolver to query the services for the target without checking it's cached results.
 *
 * @author jburke
 */
public class NameResolver
{
    private static final int NAME_RESOLVER_HTTP_ERROR_CODE = 425;

    private static final String REQUEST_URL =
            "http://localhost/NameResolver/find?format=ascii&target=";
    private static final String SERVICE_PARAMETER = "&service=";
    private static final String CACHED_PARAMETER = "&cached=";
    private static final String MAX_DETAIL_PARAMETER = "&detail=max";
    private static final String TARGET = "target=";
    private static final String SERVICE = "service=";
    private static final String COORDSYS = "coordsys=";
    private static final String RA = "ra=";
    private static final String DEC = "dec=";
    private static final String ONAME = "oname=";
    private static final String OTYPE = "otype=";
    private static final String MTYPE = "mtype=";
    private static final String TIME = "time(ms)=";
    private static final String[] SERVICES = new String[]{"NED", "SIMBAD",
                                                          "VIZIER", "ALL"};
    private final ConversionUtil conversionUtil;

    /**
     * Constructs a new CADCResolver initialized with the default services and cached values.
     */
    public NameResolver()
    {
        this(new ConversionUtil());
    }

    public NameResolver(final ConversionUtil conversionUtil)
    {
        this.conversionUtil = conversionUtil;
    }

    /**
     * Resolves the given target name using the default services and returning cached results if available.
     *
     * @param target the target name to resolve.
     * @return ResolverData object containing the results of the Name Resolver query.
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(String target)
            throws ClientException, WebServiceException, TargetNotFoundException
    {
        return resolve(target, null, true, false);
    }

    /**
     * Resolves the given target name using the default services with the specified cached value.
     *
     * @param target the target name to resolve.
     * @param cached whether the target can be resolved from the name resolver cache, default is true.
     * @return ResolverData object containing the results of the Name Resolver query.
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(String target, boolean cached)
            throws ClientException, WebServiceException, TargetNotFoundException
    {
        return resolve(target, null, cached, false);
    }

    /**
     * Resolves the given target name using the specified service(s) and returning cached results if available.
     *
     * @param target  the target name to resolve.
     * @param service the services to query to resolve the target name, default is to query all services.
     * @return ResolverData object containing the results of the Name Resolver query.
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(String target, String service)
            throws ClientException, WebServiceException, TargetNotFoundException
    {
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
     * @throws ClientException         if an error occurs processing the query results
     *                                 from the Name Resolver Web Service.
     * @throws WebServiceException     if the Name Resolver Web Service does not return
     *                                 either a HTTP 200 or a HTTP TargetNotFound code.
     * @throws TargetNotFoundException if the Name Resolver Web Service returns
     *                                 a HTTP TargetNotFound response code.
     */
    public NameResolverData resolve(final String target, final String service,
                                    final boolean cached, final boolean maxDetail)
            throws ClientException, WebServiceException, TargetNotFoundException
    {
        // if target not valid, return
        if (target == null || target.length() == 0)
        {
            throw new ClientException("Target not specified");
        }

        // check if service is valid
        validateService(service);

        NameResolverData data = new NameResolverData();
        HttpURLConnection con = null;
        try
        {
            // create the request url 
            URL url = new URL(getUrlString(URLEncoder.encode(target, "UTF-8"),
                                           service, cached, maxDetail));

            // open a connection to the name resolver and check the response
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");

            if (con.getResponseCode() == NAME_RESOLVER_HTTP_ERROR_CODE)
            {
                throw new TargetNotFoundException("Unable to resolve target "
                                                  + target);
            }

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new WebServiceException("Name Resolver Web Service error, "
                                              + "HTTP response code "
                                              + con.getResponseCode());
            }

            // read the repsonse into a StringBuffer
            final StringBuilder sb = new StringBuilder();
            final InputStreamReader in =
                    new InputStreamReader(con.getInputStream());
            int c;
            while ((c = in.read()) != -1)
            {
                sb.append((char) c);
            }
            in.close();

            // go through each line of the response parsing out the results
            final String[] results = sb.toString().split("\n");
            for (final String result : results)
            {
                if (result.startsWith(TARGET))
                {
                    if (TARGET.length() < result.length())
                    {
                        data.target = result.substring(TARGET.length());
                    }
                    else
                    {
                        throw new ClientException("Target not found in query results");
                    }

                }
                else if (result.startsWith(SERVICE))
                {
                    if (SERVICE.length() < result.length())
                    {
                        data.service = result.substring(SERVICE.length());
                    }
                    else
                    {
                        throw new ClientException("Service not found in query results");
                    }

                }
                else if (result.startsWith(COORDSYS))
                {
                    if (COORDSYS.length() < result.length())
                    {
                        data.coordsys = result.substring(COORDSYS.length());
                    }
                    else
                    {
                        throw new ClientException("Coordinate system not found in query results");
                    }
                }
                else if (result.startsWith(RA))
                {
                    if (RA.length() < result.length())
                    {
                        data.ra = conversionUtil.raToDegrees(
                                result.substring(RA.length()));
                    }
                    else
                    {
                        throw new ClientException("RA not found in query results");
                    }
                }
                else if (result.startsWith(DEC))
                {
                    if (DEC.length() < result.length())
                    {
                        data.dec = conversionUtil.decToDegrees(
                                result.substring(DEC.length()));
                    }
                    else
                    {
                        throw new ClientException("DEC not found in query results");
                    }
                }
                else if (result.startsWith(TIME))
                {
                    if (TIME.length() < result.length())
                    {
                        data.time = Integer.parseInt(
                                result.substring(TIME.length()));
                    }
                    else
                    {
                        throw new ClientException("Time not found in query results");
                    }
                }
                else
                {
                    if (maxDetail)
                    {
                        if (result.startsWith(ONAME))
                        {
                            if (ONAME.length() < result.length())
                            {
                                data.objectName = result
                                        .substring(ONAME.length());
                            }
                        }
                        else if (result.startsWith(OTYPE))
                        {
                            if (OTYPE.length() < result.length())
                            {
                                data.objectType = result
                                        .substring(OTYPE.length());
                            }
                        }
                        else if (result.startsWith(MTYPE))
                        {
                            if (MTYPE.length() < result.length())
                            {
                                data.morphologyType = result
                                        .substring(MTYPE.length());
                            }
                        }
                    }
                }
            }
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new ClientException("Unsupported url encoding: " + uee
                    .getMessage());
        }
        catch (MalformedURLException mue)
        {
            throw new ClientException("Malformed url: " + mue.getMessage());
        }
        catch (NumberFormatException nfe)
        {
            throw new ClientException("Number format exception: " + nfe
                    .getMessage());
        }
        catch (IllegalArgumentException iae)
        {
            throw new ClientException("Illegal argument exception: " + iae
                    .getMessage());
        }
        catch (IOException ioe)
        {
            throw new ClientException("IO exception: " + ioe.getMessage());
        }
        finally
        {
            // close connections
            if (con != null)
            {
                con.disconnect();
            }
        }
        return data;
    }

    /**
     * Validates the service, throwing an exception if any errors are found.
     * Throws a CadcResolverException when:
     * - The service contains whitespace characters.
     * - The first invalid service name is found.
     */
    private void validateService(final String service) throws ClientException
    {
        if (StringUtil.hasText(service))
        {
            // check for whitespace characters
            if (service.matches("[a-zA-Z,]*\\s+.*"))
            {
                throw new ClientException("Invalid whitespace character in "
                                          + "service argument");
            }

            final String[] services = service.split(",");
            for (final String name : services)
            {
                boolean found = false;

                for (final String serviceName : SERVICES)
                {
                    if (name.equalsIgnoreCase(serviceName))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    throw new ClientException("Invalid service argument "
                                              + name);
                }
            }
        }
    }

    /**
     * Construct the Name Resolver query url string.
     */
    private String getUrlString(final String encodedTarget, final String service,
                                final boolean cached, final boolean maxDetail)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(REQUEST_URL);
        sb.append(encodedTarget);
        if (service != null)
        {
            sb.append(SERVICE_PARAMETER).append(service.toLowerCase());
        }
        if (!cached)
        {
            sb.append(CACHED_PARAMETER).append("no");
        }
        if (maxDetail)
        {
            sb.append(MAX_DETAIL_PARAMETER);
        }
        return sb.toString();
    }

}
