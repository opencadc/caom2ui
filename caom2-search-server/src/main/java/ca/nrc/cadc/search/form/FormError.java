/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.nrc.cadc.search.form;

public class FormError
{
    public String name;
    public String value;

    public FormError() {}

    public FormError(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "FormError[" + name + ", " + value + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        else if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        final FormError formError = (FormError) o;

        return !(name != null ? !name.equals(formError.name)
                              : formError.name != null)
               && !(value != null ? !value.equals(formError.value)
                                  : formError.value != null);
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
