
package ca.nrc.cadc.caom2;

import ca.nrc.cadc.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author pdowler
 */
public class And extends AbstractTemplate
{
    private static final long serialVersionUID = 200902191100L;
    
    private List<SearchTemplate> templates;

    
    public And(final String name)
    { 
        super(name);
        this.templates = new ArrayList<SearchTemplate>();
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
        return "And[" + getName() + "," + templates.size() + "]";
    }
}
