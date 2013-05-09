package com.eldoraludo.tripexpense.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
	private static final String FORMAT_DATE_DB = "yyyy-MM-dd HH:mm:ss.sss";

	public static String convertirDateToString(Date dateAConvertir) {
		DateFormat df = new SimpleDateFormat(FORMAT_DATE_DB);
		return df.format(dateAConvertir);
	}

	public static Date convertirIntsToDate(int jour, int mois, int annee) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).parse(jour
					+ "/" + mois + "/" + annee);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static int recupererDepuisDate(Date date, int type) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(type);

	}

	public static Date convertirStringToDate(String date) {
		try {
			return new SimpleDateFormat(FORMAT_DATE_DB, Locale.FRANCE)
					.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String prettyDate(Date dateAConvertir) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.format(dateAConvertir);
	}
}
