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
 * 11/5/13 - 12:45 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.parser.resolver;


import ca.nrc.cadc.search.parser.TargetData;

import java.io.IOException;


/**
 * Interface to resolve target names to a service.  This is ignorant to
 * whatever name resolver service implementation is used.
 *
 * It does, however, make use of the NameResolver module's
 * NameResolverException.
 */
public interface TargetNameResolverClient
{
    /**
     * Resolve this resolver's name.
     *
     * @param target            The target to resolve.
     * @param resolverName      The name of the resolver to use.
     *
     * @return TargetData instance, or null if no such target.
     * @throws java.io.IOException  For any resolution errors.
     */
    TargetData resolve(final String target, final String resolverName)
            throws IOException;
}
