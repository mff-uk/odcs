package eu.unifiedviews.master.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import eu.unifiedviews.master.model.ApiException;

public class ConvertUtils {
    private static final String DATE_FORMAT = "yyyyMMdd'T'HH:mm:ss.SSSZ";

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        if (date != null) {
            return df.format(date);
        } else {
            return "";
        }
    }

    public static Date stringToDate(String strDate) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date result = null;
        if (!StringUtils.isBlank(strDate)) {
            try {
                result = df.parse(strDate);
            } catch (ParseException e) {
                throw new ApiException(Response.Status.BAD_REQUEST, String.format("Problem parsing date=%s. Correct date format is: %s", strDate, DATE_FORMAT));
            }
        }
        return result;
    }
}
