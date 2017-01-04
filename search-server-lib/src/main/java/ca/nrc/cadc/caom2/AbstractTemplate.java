package ca.nrc.cadc.caom2;

/**
 * @author pdowler
 */
public class AbstractTemplate implements SearchTemplate
{
    private String name;


    public AbstractTemplate()
    {
    }

    public AbstractTemplate(final String name)
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }
}
