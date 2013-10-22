package com.facebook.samples.sessionlogin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

	private final static SimpleDateFormat m_normalizedFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
			Locale.US);
	private final static SimpleDateFormat m_uiDateFormatter = new SimpleDateFormat("dd MMM yyyy");

	public static String toNormalizedString(Date date) {
		return m_normalizedFormatter.format(date);
	}

	public static Date fromNormalizedString(String date) {
		try {
			return m_normalizedFormatter.parse(date);
		} catch (ParseException e) {
			// Should never happen
			return null;
		}
	}

	public static String toUiDate(Date date) {
		return (String) m_uiDateFormatter.format(date);
	}
	
	public static String toShortUiDate(Date date) {
		return (String) m_uiDateFormatter.format(date);
	}
	
	public static int normalizeCalendarDays(int day) {
		switch (day) {
		case Calendar.MONDAY:
			return 0;
		case Calendar.TUESDAY:
			return 1;
		case Calendar.WEDNESDAY:
			return 2;
		case Calendar.THURSDAY:
			return 3;
		case Calendar.FRIDAY:
			return 4;
		case Calendar.SATURDAY:
			return 5;
		case Calendar.SUNDAY:
			return 6;
		}

		return -1;
	}

}
