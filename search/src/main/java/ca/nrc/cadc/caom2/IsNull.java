package ca.nrc.cadc.caom2;

/**
 * @author pdowler
 */
public class IsNull extends AbstractTemplate
{
    private static final long serialVersionUID = 200602151500L;

    public IsNull(String name)
    {
        super(name);
    }


    @Override
    public String toString()
    {
        return "IsNull[" + getName() + "]";
    }
}
