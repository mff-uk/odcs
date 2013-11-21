package cz.cuni.mff.xrg.odcs.rdf.query.utils;

import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For given SELECT/CONTRUCT query you can split it for 2 parts - prefixes and
 * rest. Support for {@link SPARQLQueryValidator}.
 *
 * @author Jiri Tomes
 */
public class QueryPart {

    private String query;
    private int prefixEndIndex;

    public QueryPart(String query) {
        setQuery(query);
        setPrefixEndIndex();

    }

    private void setPrefixEndIndex() {
        String regex = ".*prefix\\s+[\\w-_]+[:]\\s*[<]http://[\\w:/\\.#-_]+[>][\\s]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query.toLowerCase());

        boolean hasResult = matcher.find();

        if (hasResult) {

            prefixEndIndex = matcher.end();

            while (matcher.find()) {
                prefixEndIndex = matcher.end();
            }

        } else {
            prefixEndIndex = 0;
        }
    }

    /**
     *
     * @return String representation of query without defined prefixes.
     */
    public String getQueryWithoutPrefixes() {
        return query.substring(prefixEndIndex, query.length());
    }

    /**
     *
     * @return all defined prefixes in given query.
     */
    public String getQueryPrefixes() {
        return query.substring(0, prefixEndIndex);
    }

    /**
     *
     * @return string representation of query.
     */
    public String getQuery() {
        return query;
    }

    private void setQuery(String newQuery) {
        this.query = newQuery;
    }

    /**
     * Return one of enum as query type - SELECT, CONTRUCT, UNKNOWN.
     *
     * @return
     */
    public SPARQLQueryType getSPARQLQueryType() {

        String myQyery = getQueryWithoutPrefixes().toLowerCase();

        SPARQLQueryType myType = SPARQLQueryType.UNKNOWN;

        if (myQyery.startsWith("select")) {
            myType = SPARQLQueryType.SELECT;
        } else if (myQyery.startsWith("construct")) {
            myType = SPARQLQueryType.CONSTRUCT;
        } else if (myQyery.startsWith("describe")) {
            myType = SPARQLQueryType.DESCRIBE;
        }
        return myType;
    }
}
