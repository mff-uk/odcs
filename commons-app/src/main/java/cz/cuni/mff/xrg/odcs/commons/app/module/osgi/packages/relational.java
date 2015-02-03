package cz.cuni.mff.xrg.odcs.commons.app.module.osgi.packages;

public class relational {

    /**
     * List of OSGI packages to export. Does not start nor end with separator.
     */
    public static final String PACKAGE_LIST =
            "eu.unifiedviews.dataunit.relational;uses:=\"eu.unifiedviews.dataunit,java.sql\";version=\"${uv-dataunit-relational.version}\","
                    + "eu.unifiedviews.helpers.dataunit.relationalhelper;uses:=\"eu.unifiedviews.dataunit.relational,eu.unifiedviews.dataunit\";version=\"${uv-dataunit-helpers.version}\","
                    + "java.sql";

}
