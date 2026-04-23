package vnp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {
	public static Date AddDays(Date originalDate, int daysToAdd) {
		Calendar c = Calendar.getInstance();
		c.setTime(originalDate);
		c.add(Calendar.DATE, daysToAdd);
		return c.getTime();
	}

	public static Date AddMonths(Date originalDate, int monthsToAdd) {
		Calendar c = Calendar.getInstance();
		c.setTime(originalDate);
		c.add(Calendar.MONTH, monthsToAdd);
		return c.getTime();
	}

	public static long getDiffDate(Date fromTime, Date toTime) {
		long diff = toTime.getTime() - fromTime.getTime();
		return Math.abs(diff) / (24 * 3600 * 1000);
	}

	public static long getDiffHours(Date fromTime, Date toTime) {
		long diff = toTime.getTime() - fromTime.getTime();
		return Math.abs(diff) / (3600 * 1000);
	}

	public static String Format(Date inpDate) {
		return Format(inpDate, null);
	}

	public static String FormatTime(Date inpDate) {
		return Format(inpDate, "HH:mm:ss");
	}

	public static String FormatDate(Date inpDate) {
		return Format(inpDate, "dd/MM/yyyy");
	}

	public static String Format(Date inpDate, String pattern) {
		String myPattern = "dd/MM/yyyy HH:mm:ss";
		if (pattern != null && pattern != "")
			myPattern = pattern;
		SimpleDateFormat sdf = new SimpleDateFormat(myPattern);
		return sdf.format(inpDate);
	}

	public static Date Parse(String inpDate, String pattern) throws ParseException {
		String myPattern = "dd/MM/yyyy HH:mm:ss";
		if (pattern != null && pattern != "")
			myPattern = pattern;
		SimpleDateFormat sdf = new SimpleDateFormat(myPattern);
		return sdf.parse(inpDate);
	}

	public static Date Parse(String inpDate) throws ParseException {
		return Parse(inpDate, "dd/MM/yyyy HH:mm:ss");
	}
}
