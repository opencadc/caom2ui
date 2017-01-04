package ca.nrc.cadc.caom2;


/**
 * @author pdowler
 */
public class Util
{
    public static String formatSQL(String sql)
    {
        sql = sql.replaceAll("SELECT ", "\nSELECT ");
        sql = sql.replaceAll("FROM ", "\nFROM ");
        sql = sql.replaceAll("LEFT ", "\n  LEFT ");
        sql = sql.replaceAll("RIGHT ", "\n  RIGHT ");
        sql = sql.replaceAll("WHERE ", "\nWHERE ");
        sql = sql.replaceAll("AND ", "\n  AND ");
        sql = sql.replaceAll("OR ", "\n  OR ");
        sql = sql.replaceAll("ORDER", "\nORDER");
        sql = sql.replaceAll("GROUP ", "\nGROUP ");
        sql = sql.replaceAll("HAVING ", "\nHAVING ");
        sql = sql.replaceAll("UNION ", "\nUNION ");

        // note: \\s* matches one or more whitespace chars
        //sql = sql.replaceAll("OUTER JOIN", "\n  OUTER JOIN");
        return sql;
    }

    public static String escapeChar(String s, char p)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (c == p)
            {
                sb.append(c); // an extra one
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String replaceAll(String s, char p, char r)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (c == p)
            {
                sb.append(r);
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
