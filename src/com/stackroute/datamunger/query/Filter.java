package com.stackroute.datamunger.query;

import com.stackroute.datamunger.query.parser.Restriction;

import java.text.ParseException;
import java.text.SimpleDateFormat;

//This class contains methods to evaluate expressions
public class Filter {

    /*
     * The evaluateExpression() method of this class is responsible for evaluating
     * the expressions mentioned in the query. It has to be noted that the process
     * of evaluating expressions will be different for different data types. there
     * are 6 operators that can exist within a query i.e. >=,<=,<,>,!=,= This method
     * should be able to evaluate all of them.
     * Note: while evaluating string expressions, please handle uppercase and lowercase
     *
     */
    public boolean evaluateExpression(Restriction restriction, String fieldValue, String dataType) {
        if (restriction.getCondition().equals("="))
            return isEqual(fieldValue, restriction.getPropertyValue(), dataType);
        else if (restriction.getCondition().matches("!="))
            return isNotEqual(fieldValue, restriction.getPropertyValue(), dataType);
        else if (restriction.getCondition().equals(">"))
            return isGreaterThan(fieldValue, restriction.getPropertyValue(), dataType);
        else if (restriction.getCondition().equals(">="))
            return isGreaterThanOrEqualTo(fieldValue, restriction.getPropertyValue(), dataType);
        else if (restriction.getCondition().equals("<"))
            return isLessThan(fieldValue, restriction.getPropertyValue(), dataType);
        else
            return isLessThanOrEqualTo(fieldValue, restriction.getPropertyValue(), dataType);
    }

    private String getDateFormat(String date) {
        if (date.matches("\\d{2}/\\d{2}/\\d{4}"))
            return "dd/mm/yyyy";
        else if (date.matches("\\d{4}-\\d{2}-\\d{2}"))
            return "yyyy-mm-dd";
        else if (date.matches("\\d{2}-[aA-zZ]{3}-\\d{2}"))
            return "dd-mon-yy";
        else if (date.matches("\\d{2}-[aA-zZ]{3}-\\d{4}"))
            return "dd-mon-yyyy";
        else if (date.matches("\\d{2}-[aA-zZ]{3,9}-\\d{2}"))
            return "dd-month-yy";
        else
            return "dd-month-yyyy";

    }


    //Method containing implementation of equalTo operator
    private boolean isEqual(String s1, String s2, String dataType) {
        if (dataType.equals("java.lang.Integer")) {
            return Integer.parseInt(s1) == Integer.parseInt(s2);
        } else if (dataType.equals("java.lang.Double")) {
            return Double.parseDouble(s1) == Double.parseDouble(s2);
        } else if (dataType.equals("java.util.Date")) {
            SimpleDateFormat formatter = new SimpleDateFormat(getDateFormat(s1));
            try {
                if (formatter.parse(s1).compareTo(formatter.parse(s2)) == 0)
                    return true;
                else
                    return false;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        } else if (dataType.equals("java.util.Object"))
            return false;
        else {
            if (s1.compareTo(s2) == 0)
                return true;
            else
                return false;
        }
    }


    //Method containing implementation of notEqualTo operator
    private boolean isNotEqual(String s1, String s2, String dataType) {
        return !isEqual(s1, s2, dataType);
    }


    //Method containing implementation of greaterThan operator
    private boolean isGreaterThan(String s1, String s2, String dataType) {
        if (dataType.equals("java.lang.Integer")) {
            return Integer.parseInt(s1) > Integer.parseInt(s2);
        } else if (dataType.equals("java.lang.Double")) {
            return Double.parseDouble(s1.toLowerCase()) > Double.parseDouble(s2.toLowerCase());
        } else if (dataType.equals("java.util.Date")) {
            SimpleDateFormat formatter = new SimpleDateFormat(getDateFormat(s1));
            try {
                if (formatter.parse(s1).compareTo(formatter.parse(s2)) > 0)
                    return true;
                else
                    return false;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        } else if (dataType.equals("java.util.Object"))
            return false;
        else {
            if (s1.compareTo(s2) > 0)
                return true;
            else
                return false;
        }
    }


    //Method containing implementation of greaterThanOrEqualTo operator
    private boolean isGreaterThanOrEqualTo(String s1, String s2, String dataType) {
        return isEqual(s1, s2, dataType) | isGreaterThan(s1, s2, dataType);
    }


    //Method containing implementation of lessThan operator
    private boolean isLessThan(String s1, String s2, String dataType) {
        return !isGreaterThanOrEqualTo(s1, s2, dataType);
    }


    //Method containing implementation of lessThanOrEqualTo operator
    private boolean isLessThanOrEqualTo(String s1, String s2, String dataType) {
        return isEqual(s1, s2, dataType) | isLessThan(s1, s2, dataType);
    }

}
