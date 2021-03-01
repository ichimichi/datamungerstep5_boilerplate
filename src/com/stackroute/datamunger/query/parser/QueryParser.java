package com.stackroute.datamunger.query.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class QueryParser {
    private QueryParameter queryParameter = new QueryParameter();

    /*
     * This method will parse the queryString and will return the object of
     * QueryParameter class
     */
    public QueryParameter parseQuery(String queryString) {
        /*
         * extract the name of the file from the query. File name can be found after the
         * "from" clause.
         */
        queryParameter.setFileName(getFileName(queryString));

        /*
         * extract the order by fields from the query string. Please note that we will
         * need to extract the field(s) after "order by" clause in the query, if at all
         * the order by clause exists. For eg: select city,winner,team1,team2 from
         * data/ipl.csv order by city from the query mentioned above, we need to extract
         * "city". Please note that we can have more than one order by fields.
         */
        queryParameter.setOrderByFields(getOrderByFields(queryString));


        /*
         * extract the group by fields from the query string. Please note that we will
         * need to extract the field(s) after "group by" clause in the query, if at all
         * the group by clause exists. For eg: select city,max(win_by_runs) from
         * data/ipl.csv group by city from the query mentioned above, we need to extract
         * "city". Please note that we can have more than one group by fields.
         */
        queryParameter.setGroupByFields(getGroupByFields(queryString));


        /*
         * extract the selected fields from the query string. Please note that we will
         * need to extract the field(s) after "select" clause followed by a space from
         * the query string. For eg: select city,win_by_runs from data/ipl.csv from the
         * query mentioned above, we need to extract "city" and "win_by_runs". Please
         * note that we might have a field containing name "from_date" or "from_hrs".
         * Hence, consider this while parsing.
         */
        queryParameter.setFields(getFields(queryString));




        /*
         * extract the conditions from the query string(if exists). for each condition,
         * we need to capture the following:
         * 1. Name of field
         * 2. condition
         * 3. value
         *
         * For eg: select city,winner,team1,team2,player_of_match from data/ipl.csv
         * where season >= 2008 or toss_decision != bat
         *
         * here, for the first condition, "season>=2008" we need to capture:
         * 1. Name of field: season
         * 2. condition: >=
         * 3. value: 2008
         *
         * the query might contain multiple conditions separated by OR/AND operators.
         * Please consider this while parsing the conditions.
         *
         */
        queryParameter.setRestrictions(getRestrictions(queryString));


        /*
         * extract the logical operators(AND/OR) from the query, if at all it is
         * present. For eg: select city,winner,team1,team2,player_of_match from
         * data/ipl.csv where season >= 2008 or toss_decision != bat and city =
         * bangalore
         *
         * the query mentioned above in the example should return a List of Strings
         * containing [or,and]
         */
        queryParameter.setLogicalOperators(getLogicalOperators(queryString));



        /*
         * extract the aggregate functions from the query. The presence of the aggregate
         * functions can determined if we have either "min" or "max" or "sum" or "count"
         * or "avg" followed by opening braces"(" after "select" clause in the query
         * string. in case it is present, then we will have to extract the same. For
         * each aggregate functions, we need to know the following:
         * 1. type of aggregate function(min/max/count/sum/avg)
         * 2. field on which the aggregate function is being applied
         *
         * Please note that more than one aggregate function can be present in a query
         *
         *
         */
        queryParameter.setAggregateFunctions(getAggregateFunctions(queryString));

        return queryParameter;
    }

    public String getFileName(final String queryString) {
        return queryString.toLowerCase(Locale.ROOT).split("\\sfrom")[1].split("\\s+")[1];
    }

    public String getBaseQuery(final String queryString) {
        return queryString.toLowerCase(Locale.ROOT).split("\\swhere|\\sgroup by|\\sorder by")[0].trim();
    }

    public List<String> getOrderByFields(final String queryString) {
        if (!queryString.contains("order by")) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(queryString.toLowerCase(Locale.ROOT).split("\\sorder by")[1].trim().split(",")));
    }

    public List<String> getGroupByFields(final String queryString) {
        if (!queryString.contains("group by")) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(queryString.toLowerCase(Locale.ROOT).split("\\sgroup by")[1].trim().split("\\sorder by")[0].trim().split(",")));
    }

    public List<String> getFields(final String queryString) {
        return new ArrayList<>(Arrays.asList(queryString.toLowerCase(Locale.ROOT).split("select")[1].split("\\sfrom\\s")[0].trim().split(",")));
    }

    public String getConditionsPartQuery(final String queryString) {
        if (!queryString.contains("where")) {
            return null;
        }
        return queryString.split("\\swhere")[1].split("group by|order by")[0].trim();
    }

    public List<Restriction> getRestrictions(final String queryString) {
        if (getConditionsPartQuery(queryString) == null) {
            return null;
        }
        List<Restriction> restrictions = new ArrayList<>();
        for (String restriction : getConditionsPartQuery(queryString).split("\\sor\\s|\\sand\\s")) {
            String name = restriction.split(">|<|=|!=|>=|<=")[0].trim();
            String conditon = restriction.split("\\s|'")[1];
            String value = restriction.split(">|<|=|!=|>=|<=")[1].replace('\'', ' ').trim();
            restrictions.add(new Restriction(name, value, conditon));
        }
        return restrictions;
    }

    public List<String> getLogicalOperators(final String queryString) {
        if (getConditionsPartQuery(queryString) == null) {
            return null;
        }

        final List<String> tempList = new ArrayList<>();

        for (String token : getConditionsPartQuery(queryString).split("\\s+")) {
            if ("and".equals(token) || "or".equals(token)) {
                tempList.add(token);
            }
        }

        return tempList;
    }

    public List<AggregateFunction> getAggregateFunctions(final String queryString) {
        final List<AggregateFunction> aggregateFunctionList = new ArrayList<>();

        if (queryString.contains("count") || queryString.contains("sum") || queryString.contains("min") || queryString.contains("max") || queryString.contains("avg")) {
            for (String field : getFields(queryString)) {
                if (!field.contains("(")) {
                    continue;
                }
                String fieldName = field.split("\\(|\\)")[1];
                String function = field.split("\\(")[0];
                aggregateFunctionList.add(new AggregateFunction(fieldName, function));
            }
            return aggregateFunctionList;
        }

        return null;
    }


}
