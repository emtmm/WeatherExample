package com.emtmm.weatherexample.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class Toolkit {
	private static String LOG = Toolkit.class.getName();

	/**
	 * Checks wether there is an internet connection available
	 * 
	 * */
	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null)
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		else
			return false;

	}

	public static String convertDirection(int d) {
		String result = "";
		if (d >= 348.75 && d < 11.25) {
			result = "N";
		} else if (d >= 11.25 && d < 33.75) {
			result = "NNE";
		} else if (d >= 33.75 && d < 56.25) {
			result = "NE";
		} else if (d >= 56.25 && d < 78.75) {
			result = "ENE";
		} else if (d >= 78.75 && d < 101.25) {
			result = "E";
		} else if (d >= 101.25 && d < 123.75) {
			result = "ESE";
		} else if (d >= 123.75 && d < 146.25) {
			result = "SE";
		} else if (d >= 146.25 && d < 168.75) {
			result = "SSE";
		} else if (d >= 168.75 && d < 191.25) {
			result = "S";
		} else if (d >= 191.25 && d < 213.75) {
			result = "SSW";
		} else if (d >= 213.75 && d < 236.25) {
			result = "SW";
		} else if (d >= 236.25 && d < 258.75) {
			result = "WSW";
		} else if (d >= 258.75 && d < 281.25) {
			result = "W";
		} else if (d >= 281.25 && d < 303.75) {
			result = "WNW";
		} else if (d >= 303.75 && d < 326.25) {
			result = "NW";
		} else if (d >= 326.25 && d < 348.75) {
			result = "NNW";
		}
		return result;
	}
	
	public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}
