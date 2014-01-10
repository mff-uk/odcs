package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.utils;

public class PscUtil {
    /**
     * Determine, if given string looks like PCS.
     * 
     * @param s
     *            string to be tested
     * @return {@code true} if given string looks like PSC, otherwise {@code false}
     */
    public static boolean isPsc(String s) {
        return s.trim().matches("[0-9]{3}\\s*[0-9]{2}");
    }

    /**
     * Normalize given string containing PCS, i.e. remove all white spaces from it.
     * 
     * Example: "058 01" -> "05801"
     * 
     * Purpose: We're processing PSC from various sources. To make sure we do not store duplicated and we're searching for proper PCS, we normalize PCS before
     * storage and also before we include it in the search query.
     * 
     * @param pcs
     *            to normalize
     * @return normalized PCS
     */
    public static String normalize(String pcs) {
        return pcs.trim().replaceAll("\\s+", "");
    }
}
