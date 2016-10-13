/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONN√âES ASTRONOMIQUES  **************
*
*  (c) 2009.                            (c) 2009.
*  Government of Canada                 Gouvernement du Canada
*  National Research Council            Conseil national de recherches
*  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
*  All rights reserved                  Tous droits r√©serv√©s
*
*  NRC disclaims any warranties,        Le CNRC d√©nie toute garantie
*  expressed, implied, or               √©nonc√©e, implicite ou l√©gale,
*  statutory, of any kind with          de quelque nature que ce
*  respect to the software,             soit, concernant le logiciel,
*  including without limitation         y compris sans restriction
*  any warranty of merchantability      toute garantie de valeur
*  or fitness for a particular          marchande ou de pertinence
*  purpose. NRC shall not be            pour un usage particulier.
*  liable in any event for any          Le CNRC ne pourra en aucun cas
*  damages, whether direct or           √™tre tenu responsable de tout
*  indirect, special or general,        dommage, direct ou indirect,
*  consequential or incidental,         particulier ou g√©n√©ral,
*  arising from the use of the          accessoire ou fortuit, r√©sultant
*  software.  Neither the name          de l'utilisation du logiciel. Ni
*  of the National Research             le nom du Conseil National de
*  Council of Canada nor the            Recherches du Canada ni les noms
*  names of its contributors may        de ses  participants ne peuvent
*  be used to endorse or promote        √™tre utilis√©s pour approuver ou
*  products derived from this           promouvoir les produits d√©riv√©s
*  software without specific prior      de ce logiciel sans autorisation
*  written permission.                  pr√©alable et particuli√®re
*                                       par √©crit.
*
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.search;

import ca.nrc.cadc.net.TransientException;
import ca.nrc.cadc.search.form.FormError;
import ca.nrc.cadc.search.form.FormErrors;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.ErrorSummary;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.Result;
import ca.nrc.cadc.uws.server.JobManager;
import ca.nrc.cadc.uws.server.JobNotFoundException;
import ca.nrc.cadc.uws.server.JobPersistenceException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONWriter;


public class DisplayServlet extends HttpServlet
{
    private static final long serialVersionUID = 201003240900L;

    private static Logger log = Logger.getLogger(DisplayServlet.class);

    private static final String JOB_MANAGER = JobManager.class.getName();

    private JobManager jobManager;

    /**
     * @param config The servlet config.
     * @throws ServletException If servlet init exception.
     */
    @Override
    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);

        try
        {
            String cname = config.getInitParameter(JOB_MANAGER);
            Class c = Class.forName(cname);
            this.jobManager = (JobManager) c.newInstance();
            log.info("created JobManager: " + cname);
        }
        catch(Exception ex)
        {
            log.error("CONFIGURATION ERROR", ex);
        }
    }

    /**
     * 
     *
     * @param request The servlet request.
     * @param response The servlet response.
     * @throws ServletException If servlet exception.
     * @throws IOException If IO exception.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if (jobManager == null)
        {
            throw new RuntimeException("CONFIGURATION ERROR: no JobManager");
        }

        // JobID from the request path.
        String jobID = request.getPathInfo();

        // Strip off leading / in pathInfo to get the jobID.
        if (jobID.startsWith("/"))
        {
            jobID = jobID.substring(1);
        }

        log.debug("JobID: " + jobID);

        try
        {
            // Get the Job from the DAO.
            Job job = jobManager.get(jobID);
            request.setAttribute("job", job);

            // Check if we are in an error state.
            final ErrorSummary errorSummary = job.getErrorSummary();
            if ((errorSummary != null)
                && (errorSummary.getSummaryMessage() != null))
            {
                // Populate request with the form parameters.
                parametersToRequest(job, request);

                // Check for formName parameter.
                String formName = "";
                for (Parameter parameter : job.getParameterList())
                {
                    if (parameter.getName().equals("formName"))
                    {
                        formName = parameter.getValue();
                        break;
                    }
                }

                String jspName = "/error.jsp";

                if (!formName.equals(""))
                    jspName = "/" + formName + ".jsp";

                if (formName.equals("adsform"))
                {
                    jspName = "/index.jsp";
                }
                else
                {
                    // Get the target from the parameters.
                    String target = "";
                    for (Parameter parameter : job.getParameterList())
                    {
                        if (parameter.getName().equals("target"))
                        {
                            target = parameter.getValue();
                            break;
                        }
                    }
                    request.setAttribute("target", target);

                    // Parse out the message between the opening and closing [].
                    String error = errorSummary.getSummaryMessage();
                    int open = error.indexOf("[");
                    int close = error.indexOf("]");
                    if (open != -1 && close != -1)
                        error = error.substring(open + 1, close);
                    request.setAttribute("error", error);
                }

                // Forward to the index jsp.
                log.debug("redirecting to " + jspName);
                final RequestDispatcher dispatcher =
                        request.getRequestDispatcher(jspName);
                dispatcher.forward(request, response);
            }
            else
            {
                // Look for the tap job url in results.
                String queryJobUrl = "";
                for (Result result : job.getResultsList())
                {
                    if (result.getName().equals("query"))
                    {
                        queryJobUrl = result.getURI().toString();
                        break;
                    }
                }

                // Look for a quick search.
                String target = "";
                for (Parameter parameter : job.getParameterList())
                {
                    if (parameter.getName().equals("target"))
                    {
                        target = parameter.getValue();
                        break;
                    }
                }

                if (StringUtil.hasLength(target))
                {
                    request.setAttribute("queryJobUrl", queryJobUrl);
                    request.setAttribute("runId", job.getID());
                    request.setAttribute("job", job);

                    final RequestDispatcher dispatcher =
                            request.getRequestDispatcher("/index.jsp");
                    dispatcher.forward(request, response);
                }
                else
                {
                    response.setContentType("application/json");
                    final Writer writer =
                            new OutputStreamWriter(response.getOutputStream());
                    final JSONWriter jsonWriter = new JSONWriter(writer);

                    jsonWriter.object().key("queryJobUrl").value(queryJobUrl).
                            key("runId").value(job.getID()).endObject();

                    writer.flush();
                    writer.close();
                }
            }
        }
        catch (TransientException e)
        {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            log.info("Server too busy", e);
        }
        catch (JSONException e)
        {
            // TODO: forward to error page?
            log.error("unexpected failure", e);
        }
        catch (JobNotFoundException ex)
        {
            // TODO: forward to error page?
            log.error("unexpected failure", ex);
        }
        catch (JobPersistenceException ex)
        {
            // TODO: forward to error page?
            log.error("unexpected failure", ex);
        }
    }

    private void parametersToRequest(Job job, HttpServletRequest request)
    {
        try
        {
            // Validate search form.
            FormData formData = new FormData(job);
            FormErrors formErrors = new FormErrors();
            formData.isValid(formErrors);
            request.setAttribute("errors", formErrors);

            // Copy form errors into request scope.
            for (FormError formError : formErrors.get())
            {
                request.setAttribute(formError.name, formError.value);
                log.debug(formError);
            }

            // Copy form parameters into request scope.
            for (Parameter parameter : job.getParameterList())
            {
                request.setAttribute(parameter.getName(), parameter.getName());
                log.debug("Parameter[" + parameter.getName() + ", " + parameter.getValue() + "]");
            }
        }
        catch (Exception e)
        {
            log.error("Exception copying job parameters to Servlet request " + e.toString());
        }

    }

}
