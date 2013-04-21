package com.emtmm.weatherexample.data;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.emtmm.weatherexample.Constants;

public class QueryYahooWeather {
	private static final String CLASSTAG = QueryYahooWeather.class.getSimpleName();
    private static final String QBASE = "http://weather.yahooapis.com/forecastrss?w=";

    private String query;
    private String woeid;

    public QueryYahooWeather(String woeid, boolean overrideSevere) {

        // validate location is a woeid
        /*if (woeid == null || woeid.length() != 5 || !Toolkit.isNumeric(woeid)) {
            return;
        }*/

        this.woeid = woeid;

        // build query
        this.query = QueryYahooWeather.QBASE + this.woeid;
        //Log.v(Constants.LOGTAG, " " + CLASSTAG + " query - " + query);
        
    }
    
    public QueryYahooWeather(String woeid) {
    	this.woeid = woeid;
    	this.query = QueryYahooWeather.QBASE + this.woeid;
    }

    public WeatherRecord getWeather() {
        // /long start = System.currentTimeMillis();
        WeatherRecord r = new WeatherRecord();

        try {
            URL url = new URL(this.query);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            Log.i("query", sp.toString());
            XMLReader xr = sp.getXMLReader();
            YWeatherDataHandler handler = new YWeatherDataHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(url.openStream()));
            // after parsed, get record
            r = handler.getWeatherRecord();
            //r.setOverrideSevere(true); // override severe for dev/testing
        } catch (Exception e) {
            Log.e(Constants.LOGTAG, " " + QueryYahooWeather.CLASSTAG, e);
        }

        // /long duration = (System.currentTimeMillis() - start) / 1000;
        // /Log.v(Constants.LOGTAG, " " + CLASSTAG + " call duration - " + duration);
        Log.v(Constants.LOGTAG, " " + CLASSTAG + " WeatherReport = " + r);
        return r;
    }
}
