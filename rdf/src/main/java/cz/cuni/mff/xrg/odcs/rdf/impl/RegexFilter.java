package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for FILTER regex for given query.
 *
 * @author Jiri Tomes
 */
public class RegexFilter implements QueryFilter {

	private String varName;

	private String pattern;

	public RegexFilter(String varName, String pattern) {
		this.varName = varName;
		this.pattern = pattern;
	}

	/**
	 * Return string representation for name of filter.
	 *
	 * @return name of filter.
	 */
	@Override
	public String getFilterName() {
		return varName + pattern;
	}

	/**
	 * Return string representation of query transformed by filter.
	 *
	 * @param originalQuery query as input to filter
	 * @return transformed query using filter.
	 */
	@Override
	public String applyFilterToQuery(String originalQuery) {

		Matcher matcher = getMatcher(originalQuery);

		String resultQuery = originalQuery;

		boolean hasResult = matcher.find();

		int startIndex = 0;

		StringBuilder queryParts = new StringBuilder();

		while (hasResult) {

			int endIndex = matcher.end() - 1;

			String partBefore = resultQuery.substring(startIndex, endIndex);
			String partFILTER = String.format(
					" FILTER regex(str(?%s),\"%s\", 'i')", varName, pattern);

			queryParts.append(partBefore);
			queryParts.append(partFILTER);

			hasResult = matcher.find();

			if (!hasResult) {
				String partAfter = resultQuery.substring(endIndex, resultQuery
						.length());
				queryParts.append(partAfter);

				resultQuery = queryParts.toString();

			} else {
				startIndex = endIndex;
			}

		}

		return resultQuery;
	}

	private Matcher getMatcher(String query) {
		String regex = "where[\\s+]?\\{[\\s\\w-_\"\'(),\\*\\?:/\\.#<>]*\\?" + varName
				.toLowerCase() + "[\\s\\w-_\"\'(),\\*\\?:/\\.#<>]*\\}";
		Pattern patterns = Pattern.compile(regex);
		Matcher matcher = patterns.matcher(query.toLowerCase());

		return matcher;
	}
}
