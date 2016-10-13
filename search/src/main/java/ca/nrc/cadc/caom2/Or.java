
package ca.nrc.cadc.caom2;

import ca.nrc.cadc.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author pdowler
 */
public class Or extends AbstractTemplate
{
    private static final long serialVersionUID = 200902191100L;
    
    private List<SearchTemplate> templates;
    
    public Or()
    {
        this.templates = new ArrayList<SearchTemplate>();
    }
    
    public Or(List<SearchTemplate> templates)
    {
        this.templates = templates;
    }


    public void add(final SearchTemplate... templates)
    {
        if (!ArrayUtil.isEmpty(templates))
        {
            getTemplates().addAll(Arrays.asList(templates));
        }
    }

    public List<SearchTemplate> getTemplates()
    {
        return templates;
    }
    
    public String toString()
    {
        return "Or[" + templates.size() + "]";
    }
}
