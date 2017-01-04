package ca.nrc.cadc.caom2;

/**
 * SearchTemplate for matching Metrics.
 *
 * @author pdowler
 */
public class PropertyTemplate extends And
{
    private TextSearch key;

    public PropertyTemplate(String name, TextSearch key)
    {
        this(name, key, null);
    }

    // eg. Plane.properties, quality, ... 
    public PropertyTemplate(String name, TextSearch key, SearchTemplate[] val)
    {
        super(name);
        this.key = key;

        if (key == null)
        {
            throw new IllegalArgumentException("key cannot be null");
        }

        add(key);
        add(val);
    }

    public String toString()
    {
        return "PropertyTemplate[" + getName() + "," + key + "]";
    }
}
