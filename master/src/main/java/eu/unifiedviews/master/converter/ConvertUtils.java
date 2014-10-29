package eu.unifiedviews.master.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {
    private static final String DATE_FORMAT = "yyyyMMdd'T'HH:mm:ss.SSSZ";

    private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);

    public static String dateToString(Date date) {
        if (date != null) {
            return df.format(date);
        } else {
            return "";
        }
    }
}
