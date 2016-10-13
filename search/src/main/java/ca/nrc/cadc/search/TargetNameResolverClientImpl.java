/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
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
 *
 * @author jenkinsd
 * 11/5/13 - 12:47 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import ca.nrc.cadc.search.nameresolver.NameResolver;
import ca.nrc.cadc.search.nameresolver.NameResolverData;
import ca.nrc.cadc.search.nameresolver.NameResolverException;
import ca.nrc.cadc.search.nameresolver.exception.TargetNotFoundException;
import ca.nrc.cadc.search.parser.TargetData;
import ca.nrc.cadc.search.parser.resolver.TargetNameResolverClient;

import java.io.IOException;


/**
 * Default implementation of the resolver client.
 */
public class TargetNameResolverClientImpl implements TargetNameResolverClient
{
    private static final NameResolver DEFAULT_NAME_RESOLVER_IMPL =
            new NameResolver();

    private final NameResolver nameResolver;


    public TargetNameResolverClientImpl()
    {
        this(DEFAULT_NAME_RESOLVER_IMPL);
    }

    /**
     * Complete constructor.
     *
     * @param nameResolver      The NameResolver base to resolve.
     */
    public TargetNameResolverClientImpl(final NameResolver nameResolver)
    {
        this.nameResolver = nameResolver;
    }


    /**
     * Resolve this resolver's name.
     *
     * @param target            The target to resolve.
     * @param resolverName      The name of the resolver to use.
     *
     * @return TargetData instance, or null if no such target.
     * @throws java.io.IOException   For any resolution errors.
     */
    @Override
    public TargetData resolve(final String target, final String resolverName)
            throws IOException
    {
        try
        {
            final NameResolverData data =
                    nameResolver.resolve(target, resolverName,
                                         false, // cached
                                         true); // maxDetail

            return new TargetData(data.target, //target
                                  data.ra, null, // raRange
                                  data.dec, null, // decRange
                                  null, // radius
                                  data.coordsys, data.service,
                                  data.time,
                                  data.objectName, data.objectType,
                                  data.morphologyType);
        }
        catch (TargetNotFoundException tnfe)
        {
            return null;
        }
        catch (NameResolverException e)
        {
            throw new IOException(e.getMessage());
        }
    }
}
