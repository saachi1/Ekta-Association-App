package in.getparking.npgroups;


import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * All relevant methods in this class are corrected for business start time. If for any reason you
 * need equivalent methods that are not corrected for business time, please include the word
 * 'Calendar' in your method name, such as 'getCalendarDateFromLongVal' similar to existing method
 * 'getDateFromLongVal' but without the biz start time correction.
 */

// Todo -  replace all custom strings in SDF with DatetimeFormats.....

public class CalendarManager {
    private static final String TAG = CalendarManager.class.getSimpleName();
    private static final String BUSINESS_START_TIME = "00:00";
    private static DateTimeZone timeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+5:30"));

    private static MutableDateTime epochZeroDay = new MutableDateTime(0, timeZone);
    static {
        epochZeroDay.setHourOfDay(13);
    }




    private static final int DEFAULT_START_HOUR = 2; //2 am


    public static String getTodaysDate() {
        Calendar c = Calendar.getInstance();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        return getBusinessDate(c.getTimeInMillis(), "dd-MM-yyyy", BUSINESS_START_TIME);
    }

    public static String getTimeFromLongVal(long epochMillis) {

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
//        String timeString = simpleDateFormat.format(epochMillis);//this variable time contains the time in the format of "day/month/year".
//        Log.d(TAG, "getTimeFromLongVal: epochMillis =" + epochMillis + ", time = " + timeString);
        return getBusinessDate(epochMillis, "HH:mm", BUSINESS_START_TIME);
    }

    public static String getDateFromLongVal(long epochMillis) {

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
//        String dateString = simpleDateFormat.format(epochMillis);//this variable time contains the time in the format of "day/month/year".
        return getBusinessDate(epochMillis, "dd-MM-yyyy", BUSINESS_START_TIME);
    }

    public static String getFormattedDateFromLongVal(long epochMillis) {

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
//        String dateString = simpleDateFormat.format(epochMillis);//this variable time contains the time in the format of "day/month/year".
//        return dateString;
        return getBusinessDate(epochMillis, "dd-MMM-yyyy", BUSINESS_START_TIME).replace(".", "");


    }

    public static long getDeviceLocalTime() {
        return (System.currentTimeMillis());
    }

    public static String getDuration(long time_val) {
        long durationMillis = System.currentTimeMillis() - time_val;
        String time = String.format("%02d : %02d",
                TimeUnit.MILLISECONDS.toHours(durationMillis),
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationMillis)));
        return time;
    }

    public static int getDurationInHour(String time_val) throws ParseException {
        String tie2 = CalendarManager.getTimeFromLongVal(System.currentTimeMillis() + 50000);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(time_val);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = simpleDateFormat.parse(tie2);

        long difference = endDate.getTime() - startDate.getTime();
        if (difference < 0) {
            Date dateMax = simpleDateFormat.parse("24:00");
            Date dateMin = simpleDateFormat.parse("00:00");
            difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
        }
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        Log.i("log_tag", "Hours: " + hours + ", Mins: " + min);


        if (min > 0) {
            hours = hours + 1;
        }
        return hours;
    }

    public static String getDuration(long endTimeMillis, long startTimeMillis) {
        long durationmillis = endTimeMillis - startTimeMillis;
        String time = String.format("%02d : %02d",
                TimeUnit.MILLISECONDS.toHours(durationmillis),
                TimeUnit.MILLISECONDS.toMinutes(durationmillis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationmillis)));
        return time;
    }


    public static String getTodaysDay() {
        Calendar c = Calendar.getInstance();
        return getBusinessDate(c.getTimeInMillis(), "dd", BUSINESS_START_TIME);
    }

    public static String gettodaysMonthYear() {
        Calendar c = Calendar.getInstance();
        return getBusinessDate(c.getTimeInMillis(), "MM-yyyy", BUSINESS_START_TIME);
    }


    public static String getTodaysMonth() {
        Calendar c = Calendar.getInstance();
        return getBusinessDate(c.getTimeInMillis(), "MM", BUSINESS_START_TIME);
    }

    public static String getTodaysYear() {
        Calendar c = Calendar.getInstance();
        return getBusinessDate(c.getTimeInMillis(), "yyyy", BUSINESS_START_TIME);
    }

    public static String getYesterdaysDay() {
        String current_day = "";
        Calendar c = Calendar.getInstance();
        return getBusinessDate(c.getTimeInMillis() - 24*60*60*1000, "dd", BUSINESS_START_TIME);
    }

    public static String getDayFromEpochDay(int epochDay) {
        MutableDateTime someDay =  new MutableDateTime(0);
        someDay.addDays(epochDay);
        DecimalFormat df = new DecimalFormat("00");
        return df.format(someDay.getDayOfMonth());
    }


    public static String getMonthFromEpochDay(int epochDay) {
        MutableDateTime someDay =  new MutableDateTime(0);
        someDay.addDays(epochDay);
        DecimalFormat df = new DecimalFormat("00");
        return df.format(someDay.getMonthOfYear());
    }


    public static String getYearFromEpochDay(int epochDay) {
        MutableDateTime someDay =  new MutableDateTime(0);
        someDay.addDays(epochDay);
        DecimalFormat df = new DecimalFormat("0000"); //probably not required
        return df.format(someDay.getYear());
    }

    public static String getMonthYearFromEpochDay(int epochDay) {

        return getMonthFromEpochDay(epochDay) + "-" +
                getYearFromEpochDay(epochDay);
    }


    /***
     *
     *
     * @param epochMillis The time in milliseconds, for which the epoch day count is to be calculated.
     *                    Passing the current time will get the epoch day count for today.
     * @return Days since Epoch (00:00 hrs on Jan 1, 1970). So Jan 1, 1970 itself is day zero.
     */
    public static int getEpochDay(long epochMillis) {

        epochMillis = getCurrentBusinessTimeStamp(epochMillis, BUSINESS_START_TIME); //adjustment based on biz start time, if required
        MutableDateTime now = new MutableDateTime(epochMillis, timeZone);
        now.setHourOfDay(15);
        Days daysSinceEpoch = Days.daysBetween(epochZeroDay, now);

        return daysSinceEpoch.getDays();
    }

    /** Convenience method to return today's epoch day **/
    public static int getEpochDay(){
        return getEpochDay(new Date().getTime());
    }

    public static int getEpochDayFromCalendarDate(Calendar calendarDate) {
        MutableDateTime now = new MutableDateTime(calendarDate);
        // set hour to rough middle of day so we don't have to deal with boundary conditions around timezone
        now.setHourOfDay(15);
        Days daysSinceEpoch = Days.daysBetween(epochZeroDay, now);

        return daysSinceEpoch.getDays();
    }


    public static String getDateFromEpochDay(int epochDay) {
        DateTime today = new DateTime(0).plusDays(epochDay);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
        return dtf.print(today);
    }


    /**
     * @param timeStamp             : The Current Time Stamp
     * @param dateFormat            : String In the format dd/MM/yyyy
     * @param businessStartTime     : String in the format 07:00
     * @return BusinessDate String  : Business date corrected for start time. So if we are past midnight,
     *                                but start time is set to 2am, then it returns yesterday's date.
     */

    public static String getBusinessDate(Long timeStamp, String dateFormat, String businessStartTime) {

        String dateString = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String[] startTimeArray = businessStartTime.split(":");
        int startHour, startMinute;
        try {
            startHour = Integer.valueOf(startTimeArray[0]);
            startMinute = Integer.valueOf(startTimeArray[1]);
        } catch (NumberFormatException e){
            startHour = DEFAULT_START_HOUR  ;
            startMinute = 0;
            Log.e(TAG, "getBusinessDate: error converting start time string " +
                    businessStartTime + " to integer components", e);
        }

        if ((hour < startHour)
                || (hour == startHour && minute < startMinute)) {
            //Give Current date - 1
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));      //This is IST - I am not sure if it is needed or not.

        dateString = df.format(calendar.getTime());
        return dateString;
    }

    public static Long getCurrentBusinessTimeStamp(Long timeStamp, String businessStartTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String[] startTimeArray = businessStartTime.split(":");

        if ((hour < Integer.valueOf(startTimeArray[0])) || (hour == Integer.valueOf(startTimeArray[0]) && minute < Integer.valueOf(startTimeArray[1]))) {
            //Give Current date
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        return calendar.getTimeInMillis();
    }


    public static boolean isLongWeekend() {
        Calendar c = Calendar.getInstance();
        int weekDay = getCurrentBusinessWeekDay(c.getTimeInMillis());

        return weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY || weekDay == Calendar.FRIDAY;
    }

    public static boolean isWeekend() {
        Calendar c = Calendar.getInstance();
        int weekDay = getCurrentBusinessWeekDay(c.getTimeInMillis());

        return weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY;
    }


    public static int getCurrentBusinessWeekDay(Long timeStamp) {

        String dateString = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        if (isBeforeBizStartTime(calendar)) {
            // Set to Current_date minus 1 since we still fall in Yesterdays' business date
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        return calendar.get(Calendar.DAY_OF_WEEK);

    }

    public static int getCurrentWeekDay(Long timeStamp) {

        String dateString = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(Calendar.DAY_OF_WEEK);

    }

    /***
     * Given a point in time, checks if it fallsreturns
     * @param now
     * @return
     */
    private static boolean isBeforeBizStartTime(Calendar now) {
        String dateString = "";

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);


        String[] startTimeArray = BUSINESS_START_TIME.split(":");
        if (startTimeArray.length < 2) {
            // Nothing to do since we don't have a valid start time to work against
            Log.d(TAG, "adjustForBizStartTime: we don't have a valid string for the Business Start Time");
            return false;
        }

        return (hour < Integer.valueOf(startTimeArray[0]))
                || (hour == Integer.valueOf(startTimeArray[0]) && minute < Integer.valueOf(startTimeArray[1]));

    }


    public static boolean isCurrentTimeGreaterThan(String validFrom) {
        String dateString = "";
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        String[] startTimeArray = validFrom.split(":");
        if (startTimeArray.length < 2) {
            return false;
        }

        return (hour >= Integer.valueOf(startTimeArray[0].trim()))
                && (hour != Integer.valueOf(startTimeArray[0].trim()) || minute >= Integer.valueOf(startTimeArray[1].trim()));
    }

    public static boolean isCurrentTimeLessThan(String validFrom) {
        String dateString = "";
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        String[] startTimeArray = validFrom.split(":");
        if (startTimeArray.length < 2) {
            return false;
        }

        return (hour < Integer.valueOf(startTimeArray[0]))
                || (hour == Integer.valueOf(startTimeArray[0]) && minute < Integer.valueOf(startTimeArray[1]));

    }

    /***
     * Converts a string duration of format hh:mm into minutes
     * So 00:25 will return 25 minutes, while "03:25: will return 205 minutes.
     * @param duration
     * @return
     */
    public static int getDurationMinutes(String duration) {

        String[] durationArray = duration.split(":");
        int mins = Integer.valueOf(durationArray[0].trim().startsWith("0") ? String.valueOf(durationArray[0].trim().charAt(1)) :
                durationArray[0].trim()) * 60 + Integer.valueOf(durationArray[1].trim().startsWith("0") ? String.valueOf(durationArray[1].trim().charAt(1)) :
                durationArray[1].trim());
        return mins;
    }

    private static final String[] monthDescs = {
            "",
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
    };

    public static String getPrevOrFutureDates(String referenceDate, int offset, String format){
        String ddMMYYYY = getPrevOrFutureDates(referenceDate, offset);

        String [] components = ddMMYYYY.split("-");
        int month;
        try{
            month = Integer.parseInt(components[1]);
        } catch (NumberFormatException e){
            return ddMMYYYY;
        }

        if(month < 1 || month > 12)
            return ddMMYYYY;

        String monthStr = monthDescs[month];

        return components[0] + "-" + monthStr;
    }

    public static String getPrevOrFutureDates(String referenceDate, int offset){
        if (offset == 0) return referenceDate;

        if(!referenceDate.matches("([0-3]\\d)-([01]\\d)-(\\d{4})"))
            throw new IllegalArgumentException("Illegal date format");

        String [] components = referenceDate.split("-");
        int day = Integer.parseInt(components[0]);
        int month = Integer.parseInt(components[1]);
        int year = Integer.parseInt(components[2]);

        if(offset < 0){
            offset = -offset;
            while(offset > 0){
                --day; --offset;
                if(day == 0){
                    --month;
                    if(month == 0){
                        --year;
                        month = 12;
                    }
                    day = daysInMonth(month, year);
                }
            }
        } else { //offset > 0
            int monthLimit = daysInMonth(month, year);
            while(offset > 0) {
                ++day; --offset;
                if(day > monthLimit){
                    ++month;
                    if(month > 12){
                        ++year;
                        month = 1;
                    }
                    day = 1;
                    monthLimit = daysInMonth(month, year);
                }
            }
        }

        return String.format(Locale.ENGLISH, ""+"%02d-%02d-%4d", day, month, year);
    }


    public static int daysInMonth(int month, int year){
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: return 31;

            case 4:
            case 6:
            case 9:
            case 11: return 30;

            case 2: // Not handling the divisible by 100 scenario since I don't anticipate the code running till 2100 CE
                if (year%4 == 0)
                    return 29;
                else return 28;
            default:
                throw new IllegalArgumentException("Valid months are 1 thru 12");
        }
    }



    public static long getRealtimeVehiclesValidationTime(int time) {
        int minutes = time * 60 * 1000;
        long minuteAge = System.currentTimeMillis() - minutes;
        return minuteAge;
    }

}
