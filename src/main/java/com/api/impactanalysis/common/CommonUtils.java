package com.api.impactanalysis.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public final class CommonUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
	private static final String[] EMPTY_STRING_ARRAY = {};

	public static LocalDate parseLocalDate(List<String> formats, String date) {
		DateTimeFormatterBuilder dateTimeFormatterBuilder = null;
		DateTimeFormatter formatter = null;
		dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
		LocalDate dateTime = null;
		try {
			for (String format : formats) {
				dateTimeFormatterBuilder.appendOptional(DateTimeFormatter.ofPattern(format));
			}
			formatter = dateTimeFormatterBuilder.toFormatter();
			dateTime = LocalDate.parse(date, formatter);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		return dateTime;
	}

	public static Date parseToDate(String format, String date) {
		Date dateObj = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			dateObj = formatter.parse(date);
		} catch (ParseException e) {
			LOGGER.info(e.getMessage(), e);
		}
		return dateObj;
	}

	public static Date getSystemDateWithoutTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(System.currentTimeMillis()));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static List<String> tokenizeToList(String input, String delimeter, boolean isEscapeDelimeter, String patternToKeep) {
		String[] tokens = null;
		List<String> list = null;
		try {
			tokens = tokenizeStringToArray(input, delimeter, isEscapeDelimeter);
			list = new ArrayList<>(tokens.length);
			for (String str : tokens) {
				str = null != patternToKeep ? str.replaceAll(patternToKeep, "") : str.trim();
				list.add(str);
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		return list;
	}

	public static String[] tokenizeStringToArray(String input, String delimeter, boolean isEscapeDelimeter) {
		String[] tokens = null;
		try {
			if (!StringUtils.hasText(input)) {
				return EMPTY_STRING_ARRAY;
			}
			tokens = isEscapeDelimeter ? input.split("\\" + delimeter, -1) : input.split(delimeter, -1);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
		return tokens;
	}

	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> collection) {
		if (null == collection) {
			return new ArrayList<T>(0);
		}
		List<T> listToReturn = new ArrayList<T>(collection.size());
		for (Object object : collection) {
			listToReturn.add(clazz.cast(object));
		}
		return listToReturn;
	}

	public static String formatDate(String newformat, Date dateValue) {
		String formattedDate = null;
		SimpleDateFormat sdf1 = null;
		try {
			if (StringUtils.hasText(newformat) && null != dateValue) {
				sdf1 = new SimpleDateFormat(newformat);
				formattedDate = sdf1.format(dateValue);
			}
		} catch (Exception e) {
			LOGGER.warn(String.format("Couldn't convert date received Date[%s]  new format [%s]", dateValue, newformat), e);
		}
		return formattedDate;
	}

}
