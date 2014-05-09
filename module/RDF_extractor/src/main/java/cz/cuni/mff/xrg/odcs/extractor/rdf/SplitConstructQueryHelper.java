package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLQueryValidator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for the construct query extension using ORDER BY,
 * LIMIT and OFFSET clause. The extension is necessary for split construct query
 * in more construct queries, which produce the same RDF data as the original
 * construct query.
 * 
 * @author Jiri Tomes
 */
public class SplitConstructQueryHelper {

    private String constructQuery;

    /**
     * Size of one data part.
     */
    private int LIMIT;

    private int OFFSET = 0;

    private boolean checkedKeyWords = false;

    /**
     * Create new instance of {@link SplitConstructQueryHelper}.
     * 
     * @param constructQuery
     *            SPARQL construct query you could be split.
     * @param splitSize
     *            Maximum count of RDF triples as construct query
     *            restriction
     */
    public SplitConstructQueryHelper(String constructQuery, int splitSize) {
        this.constructQuery = constructQuery;
        this.LIMIT = splitSize;
    }

    /**
     * Returns the original construct query extended by ORDER BY, LIMIT and
     * OFFSET clauses.
     * 
     * @return The original construct query extended by ORDER BY, LIMIT and
     *         OFFSET clauses.
     * @throws InvalidQueryException
     *             if the given construct query contains some
     *             of forbidden words ('ORDER BY', 'LIMIT',
     *             'OFFSET') except their occurrence in
     *             subqueries or it is not valid construct
     *             query.
     */
    public String getSplitConstructQuery() throws InvalidQueryException {
        if (!checkedKeyWords) {
            checkForbiddenKeyWords();
            checkedKeyWords = true;
        }

        StringBuilder resultQuery = new StringBuilder();

        String orderByPart = getConstructHeaderVariables();

        resultQuery.append(constructQuery);
        resultQuery.append(" ORDER BY ");
        resultQuery.append(orderByPart);
        resultQuery.append(" LIMIT ");
        resultQuery.append(LIMIT);
        resultQuery.append(" OFFSET ");
        resultQuery.append(OFFSET);

        return resultQuery.toString();

    }

    /**
     * Change behavior of calling method {@link #getSplitConstructQuery()}. It
     * is returned restricted construct OFFSET increased by the LIMIT value.
     */
    public void goToNextQuery() {
        OFFSET += LIMIT;
    }

    private String getConstructHeaderVariables() {
        String regex = "construct(\\s)*\\{";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(constructQuery.toLowerCase());

        matcher.find();
        int startIndex = matcher.end();
        int endIndex = constructQuery.indexOf("}", startIndex);

        String header = constructQuery.substring(startIndex, endIndex);
        String variables = getVariablesFromHeader(header);

        return variables;

    }

    private String getVariablesFromHeader(String header) {
        StringBuilder result = new StringBuilder();

        boolean addChar = false;

        for (int i = 0; i < header.length(); i++) {
            char c = header.charAt(i);
            switch (c) {
                case '?':
                    addChar = true;
                    result.append(' ');
                    break;
                case ' ':
                    addChar = false;
                    break;
                default:
                    break;

            }
            if (addChar) {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Check if construct query does not contain some of forbidden words ('ORDER
     * BY', 'LIMIT', 'OFFSET') except their occurrence in subqueries.
     * 
     * @throws InvalidQueryException
     *             if the given construct query contains some
     *             of forbidden words ('ORDER BY', 'LIMIT',
     *             'OFFSET') except their occurrence in
     *             subqueries or it is not valid construct
     *             query.
     */
    private void checkForbiddenKeyWords() throws InvalidQueryException {

        SPARQLQueryValidator validator = new SPARQLQueryValidator(constructQuery,
                SPARQLQueryType.CONSTRUCT);

        if (!validator.isQueryValid()) {
            throw new InvalidQueryException(validator.getErrorMessage());
        }

        String regex = "((((limit|offset)(\\s)*[0-9]+(\\s)*)+)|order by)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(constructQuery.toLowerCase());

        boolean hasFound = matcher.find();

        while (hasFound) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            boolean isSubqueryRestriction = (constructQuery.indexOf('}',
                    endIndex) > -1);

            if (!isSubqueryRestriction) {
                String keyWord = constructQuery.substring(startIndex, endIndex);
                throw new InvalidQueryException(
                        "Cannot convert one query to set of queries because " + keyWord + " is already used.");
            }
            hasFound = matcher.find();
        }
    }
}
