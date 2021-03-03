package com.stackroute.datamunger.query;

/*
 * Implementation of DataTypeDefinitions class. This class contains a method getDataTypes()
 * which will contain the logic for getting the datatype for a given field value. This
 * method will be called from QueryProcessors.
 * In this assignment, we are going to use Regular Expression to find the
 * appropriate data type of a field.
 * Integers: should contain only digits without decimal point
 * Double: should contain digits as well as decimal point
 * Date: Dates can be written in many formats in the CSV file.
 * However, in this assignment,we will test for the following date formats('dd/mm/yyyy',
 * 'mm/dd/yyyy','dd-mon-yy','dd-mon-yyyy','dd-month-yy','dd-month-yyyy','yyyy-mm-dd')
 */
public class DataTypeDefinitions {

    //method stub
    public static String getDataType(String input) {
        if (input.matches("\\d+")) {
            return "java.lang.Integer";
        }
        // checking for floating point numbers
        else if (input.matches("\\d+.\\d+")) {
            return "java.lang.Double";
        } else if (
            // checking for date format mm/dd/yyyy
                input.matches("\\d{2}/\\d{2}/\\d{4}") ||
                        // checking for date format dd-mon-yy
                        input.matches("\\d{2}-[aA-zZ]{3}-\\d{2}") ||
                        // checking for date format dd-mon-yyyy
                        input.matches("\\d{2}-[aA-zZ]{3}-\\d{4}") ||
                        // checking for date format dd-month-yy
                        input.matches("\\d{2}-[aA-zZ]{3,9}-\\d{2}") ||
                        // checking for date format dd-month-yyyy
                        input.matches("\\d{2}-[aA-zZ]{3,9}-\\d{4}") ||
                        // checking for date format yyyy-mm-dd
                        input.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "java.util.Date";

        } else if (input.isEmpty()) {
            return "java.lang.Object";
        } else {
            return "java.lang.String";
        }
    }
}
