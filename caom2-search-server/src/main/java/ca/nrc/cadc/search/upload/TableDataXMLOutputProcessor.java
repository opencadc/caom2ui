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
 * 3/4/13 - 3:58 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.upload;

import org.apache.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.util.NamespaceStack;

import java.io.IOException;
import java.io.Writer;
import java.util.List;


public class TableDataXMLOutputProcessor extends AbstractXMLOutputProcessor
{
    private static final Logger LOGGER =
            Logger.getLogger(TableDataXMLOutputProcessor.class);

    // Line separator.
    private static final String NEW_LINE = System.getProperty("line.separator");


    // Number of TableData rows written;
    private int rowCount;

    // Max number of rows to write.
    private int maxRows;

    // Indicates an error occurred writing results.
    private boolean error;


    public TableDataXMLOutputProcessor(int maxRows)
    {
        super();
        this.maxRows = maxRows;
        this.rowCount = 0;
        this.error = false;
    }


    /**
     * This will handle printing of an {@link org.jdom2.Element}.
     * <p/>
     * This method arranges for outputting the Element infrastructure including
     * Namespace Declarations and Attributes.
     *
     * @param out     <code>Writer</code> to use.
     * @param fstack  the FormatStack
     * @param nstack  the NamespaceStack
     * @param element <code>Element</code> to write.
     * @throws java.io.IOException if the destination Writer fails
     */
    @Override
    protected void printElement(Writer out, FormatStack fstack,
                                NamespaceStack nstack, Element element)
            throws IOException
    {
        if (element.getName().equalsIgnoreCase("TABLE"))
        {
            super.printElement(out, fstack, nstack, element);
            if (rowCount > maxRows)
            {
                out.write(NEW_LINE);
                Element info = new Element("INFO", element.getNamespace());
                info.setAttribute("name", "QUERY_STATUS");
                info.setAttribute("value", "OVERFLOW");
                super.printElement(out, fstack, nstack, info);
            }
            if (error)
            {
                out.write(NEW_LINE);
                Element info = new Element("INFO", element.getNamespace());
                info.setAttribute("name", "QUERY_STATUS");
                info.setAttribute("value", "ERROR");
                super.printElement(out, fstack, nstack, info);
            }
        }
        else if (element instanceof TableDataElement)
        {
            out.write("<");
            out.write(element.getQualifiedName());
            out.write(">");
            out.write(NEW_LINE);
            final List<Content> contents = element.getContent();
            for (final Content row : contents)
            {
                rowCount++;
                if (rowCount > maxRows)
                {
                    break;
                }

                out.write(fstack.getLevelIndent());

                try
                {
                    super.printElement(out, fstack, nstack, (Element) row);
                }
                catch (Throwable t)
                {
                    LOGGER.error("failed while iterating over TableDataElement",
                                 t);
                    error = true;
                    break;
                }
                out.write(NEW_LINE);
            }
            out.write(fstack.getIndent());
            out.write("</");
            out.write(element.getQualifiedName());
            out.write(">");
        }
        else
        {
            super.printElement(out, fstack, nstack, element);
        }
    }
}
