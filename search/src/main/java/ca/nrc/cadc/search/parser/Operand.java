/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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
 * 8/26/11 - 11:12 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.parser;


/**
 * Query operand.
 */
public enum Operand
{
    EQUALS("=", Position.BEGINNING), 
    LESS_THAN("<", Position.BEGINNING),
    LESS_THAN_EQUALS("<=", Position.BEGINNING),
    GREATER_THAN(">", Position.BEGINNING),
    GREATER_THAN_EQUALS(">=", Position.BEGINNING),
    RANGE("..", Position.MIDDLE);


    public static final Operand[] STARTER_OPERANDS = new Operand[]
            {
                    EQUALS, LESS_THAN, GREATER_THAN
            };

    public static final Operand[] MIDDLE_OPERANDS = new Operand[]
            {
                    RANGE
            };


    private String operand;
    private Position position;


    Operand(final String operand, final Position position)
    {
        this.operand = operand;
        this.position = position;
    }


    public String getOperand()
    {
        return operand;
    }

    public Position getPosition()
    {
        return position;
    }

    /**
     * Where in the constraint this Operand goes.
     */
    public enum Position
    {
        BEGINNING, MIDDLE
    }
}
