package gamezone.domain;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Formats {
    public static final String timestampWithoutTimezoneString = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String timestampISOStandard = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String timestampTimeZoneString = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String timestampTimeZoneStringWithPlus = "yyyy-MM-dd'T'HH:mm:ss.SSS+";
    public static final String timestampDB = "yyyy-MM-dd HH:mm:ss";
    public static final String timestamp = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String dateFormat = "yyyy-MM-dd";

    public static DateFormat getTimestampBD() {
        return new SimpleDateFormat(timestampDB);
    }

    public static DateFormat getTimeFormat() {
        return new SimpleDateFormat("HH:mm");
    }

    public static DateFormat getTimestamp() {
        return new SimpleDateFormat(timestamp);
    }

    public static DateFormat getDateFormat() {
        return new SimpleDateFormat(dateFormat);
    }

    public static DateFormat getDateFormatAadharV2() {
        return new SimpleDateFormat("dd-MM-yyy");
    }

    public static DateFormat getDateFormatTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static DateTimeFormatter getDateFormatLD() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public static DateFormat getDayOfWeekFull() {
        return new SimpleDateFormat("EEEE");
    }

    public static DateFormat getDateFormatWithoutMinus() {
        return new SimpleDateFormat("yyyyMMdd");
    }

    public static DateFormat getTimestampTimeZoneWithPlus() {
        return new SimpleDateFormat(timestampTimeZoneStringWithPlus);
    }

    public static DateFormat getTimestampTimeZone() {
        return new SimpleDateFormat(timestampTimeZoneString);
    }

    public static DateFormat getReportDateFormat() {
        return new SimpleDateFormat("MMM dd, yyyy");
    }

    public static DateFormat getReportTimeFormat() {
        return new SimpleDateFormat("h:mm:ss a");
    }

    public static DateFormat getAMPMFormat() {
        return new SimpleDateFormat("hh:mm a");
    }

    public static DateFormat getFormatMMMSpaceDdCommaSpaceYyyySpaceHhColonMm() {
        return new SimpleDateFormat("MMM dd, yyyy hh:mm");
    }

    public static DateFormat getMMM_Space_d() {
        return new SimpleDateFormat("MMM d");
    }

    public static DecimalFormat getDecimalFormat() {
        return new DecimalFormat("###.##");
    }

    public static DateFormat getDateFormatUI() {
        return new SimpleDateFormat("MMM dd, yyyy");
    }

    public static DateFormat getTimestampWithoutTimeZone() {
        return new SimpleDateFormat(timestampWithoutTimezoneString);
    }

    public static DateFormat getTimestampISOStandard() {
        return new SimpleDateFormat(timestampISOStandard);
    }

    public static DateFormat getDateTimeFormatWithoutMinus() {
        return new SimpleDateFormat("yyyyMMddHH");
    }

    public static DateFormat getTimestampFormatWithoutMinus() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static DecimalFormat get2DecimalFormatter() {
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        decimalFormat.setMinimumFractionDigits(2);
        return decimalFormat;
    }

    public static DecimalFormat get3DecimalFormatter() {
        DecimalFormat decimalFormat = new DecimalFormat("###.###");
        decimalFormat.setMinimumFractionDigits(3);
        return decimalFormat;
    }

    public static DecimalFormat get4DecimalFormatter() {
        DecimalFormat decimalFormat = new DecimalFormat("###.####");
        decimalFormat.setMinimumFractionDigits(4);
        return decimalFormat;
    }

    public static DecimalFormat get6DecimalFormatter() {
        DecimalFormat decimalFormat = new DecimalFormat("###.#######");
        decimalFormat.setMinimumFractionDigits(6);
        return decimalFormat;
    }

    public static Double hackACent(Double calcAmount, Double existingAmount) {
        double diff = existingAmount - calcAmount;
        return diff <= 0.01 && diff >= -0.01 ? diff : 0;
    }

    public static DateFormat getReportTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
    }

    public static DateFormat getMonthFormat() {
        return new SimpleDateFormat("yyyy-MM");
    }

    public static DateFormat getYearFormat() {
        return new SimpleDateFormat("yyyy");
    }

    public static String formatLongToTime(long num) {
        return "[" + num + " millis into timestamp " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(num)) + "]";
    }

    public static DateFormat get_hh_mm_a() {
        return new SimpleDateFormat("hh:mm a");
    }

    public static DateFormat get_hh_00_a() {
        return new SimpleDateFormat("hh:00 a");
    }

    public static String minutesIntoHours(int minutes) {
        return minutesIntoHours(minutes, Formats.getAMPMFormat());
    }

    public static String minutesIntoHours(int minutes, DateFormat format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, minutes / 60);
        calendar.set(Calendar.MINUTE, minutes % 60);
        calendar.set(Calendar.SECOND, 0);
        return format.format(calendar.getTime());
    }

    public static Date getDateAdd(Calendar cal, int field, int days) {
        Calendar calendar = (Calendar) cal.clone();
        calendar.add(field, days);
        return calendar.getTime();
    }

    public static String getDateWithTZAbbreviation(String timezoneAbbreviation, Date date) {
        SimpleDateFormat sdfAmerica = new SimpleDateFormat(timestampTimeZoneString);
        sdfAmerica.setTimeZone(TimeZone.getTimeZone(timezoneAbbreviation));
        return sdfAmerica.format(date);
    }

    public static DateFormat getDateAndHours() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    }

    public static DateFormat getTimestampTimeZoneWithOffset(String offset) {
        return new SimpleDateFormat(timestampWithoutTimezoneString + offset);
    }

    public static DateFormat getDayOfWeekEEEE() {
        return new SimpleDateFormat("EEEE");
    }

    public static DateFormat get_EEE_MMM_dd_yyyy() {
        return new SimpleDateFormat("EEE, MMM dd yyyy");
    }

    public static DateFormat getMMDDYY() {
        return new SimpleDateFormat("MMddyy");
    }

    public static DateFormat getMMYY() {
        return new SimpleDateFormat("MM/yy");
    }

    public static DateFormat getYYMM() {
        return new SimpleDateFormat("yyMM");
    }

    public static DateFormat getHHMM() {
        return new SimpleDateFormat("HH:mm");
    }

    public static DateFormat getDate6d() {
        return new SimpleDateFormat("yyMMdd");
    }


    public static String intToHours(int min) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        Date dt = sdf.parse(String.valueOf(min));
        sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(dt);
    }

    public static boolean isStrictValidFormat(String format, String value) {
        DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern(format)
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalTime.parse(value, strictTimeFormatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isStrictValidFormatDate(String format, String value) {
        DateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static DateFormat getTimeStampWithOffsetForAyden() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    }

    public static Time getTimeFromString(String time) throws Exception {
        return new Time(getTimeFormat().parse(time).getTime());
    }

    public static Time getTimeHHMMFromString(String time) throws Exception {
        return new Time(new SimpleDateFormat("HH:mm").parse(time).getTime());
    }
}
