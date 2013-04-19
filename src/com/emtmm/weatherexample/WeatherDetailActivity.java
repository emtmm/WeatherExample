package com.emtmm.weatherexample;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class WeatherDetailActivity extends SherlockFragmentActivity {
	public static final String TAG = SavedLocations.class.getSimpleName();
	ActionBar actionBar;

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	TextView weather;

	class MyWeather {

		String description;
		String city;
		String region;
		String country;

		String windChill;
		String windDirection;
		String windSpeed;

		String sunrise;
		String sunset;

		String conditiontext;
		String conditiondate;

		public String toString() {

			String s;

			s = description + " -\n\n" + "city: " + city + "\n" + "region: "
					+ region + "\n" + "country: " + country + "\n\n" + "Wind\n"
					+ "chill: " + windChill + "\n" + "direction: "
					+ windDirection + "\n" + "speed: " + windSpeed + "\n\n"
					+ "Sunrise: " + sunrise + "\n" + "Sunset: " + sunset
					+ "\n\n" + "Condition: " + conditiontext + "\n"
					+ conditiondate + "\n";

			return s;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_detail);
		weather = (TextView) findViewById(R.id.weather);

		Bundle bundle = this.getIntent().getExtras();
		String sel_woeid = (String) bundle.getCharSequence("SEL_WOEID");

		new MyQueryYahooWeatherTask(sel_woeid).execute();

		Toast.makeText(getApplicationContext(), sel_woeid, Toast.LENGTH_LONG)
				.show();
	}

	private class MyQueryYahooWeatherTask extends AsyncTask<Void, Void, Void> {

		String woeid;
		String weatherResult;
		String weatherString;

		MyQueryYahooWeatherTask(String w) {
			woeid = w;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			weatherString = QueryYahooWeather();
			Document weatherDoc = convertStringToDocument(weatherString);

			if (weatherDoc != null) {
				weatherResult = parseWeather(weatherDoc).toString();
			} else {
				weatherResult = "Cannot convertStringToDocument!";
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			weather.setText(weatherResult);
			super.onPostExecute(result);
		}

		private String QueryYahooWeather() {
			String qResult = "";
			String queryString = "http://weather.yahooapis.com/forecastrss?w="
					+ woeid;

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(queryString);

			try {
				HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

				if (httpEntity != null) {
					InputStream inputStream = httpEntity.getContent();
					Reader in = new InputStreamReader(inputStream);
					BufferedReader bufferedreader = new BufferedReader(in);
					StringBuilder stringBuilder = new StringBuilder();

					String stringReadLine = null;

					while ((stringReadLine = bufferedreader.readLine()) != null) {
						stringBuilder.append(stringReadLine + "\n");
					}

					qResult = stringBuilder.toString();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return qResult;
		}

		private Document convertStringToDocument(String src) {
			Document dest = null;

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser;

			try {
				parser = dbFactory.newDocumentBuilder();
				dest = parser.parse(new ByteArrayInputStream(src.getBytes()));
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return dest;
		}

		private MyWeather parseWeather(Document srcDoc) {

			MyWeather myWeather = new MyWeather();

			// <description>Yahoo! Weather for New York, NY</description>
			NodeList descNodelist = srcDoc.getElementsByTagName("description");
			if (descNodelist != null && descNodelist.getLength() > 0) {
				myWeather.description = descNodelist.item(0).getTextContent();
			} else {
				myWeather.description = "EMPTY";
			}

			// <yweather:location city="New York" region="NY"
			// country="United States"/>
			NodeList locationNodeList = srcDoc
					.getElementsByTagName("yweather:location");
			if (locationNodeList != null && locationNodeList.getLength() > 0) {
				Node locationNode = locationNodeList.item(0);
				NamedNodeMap locNamedNodeMap = locationNode.getAttributes();

				myWeather.city = locNamedNodeMap.getNamedItem("city")
						.getNodeValue().toString();
				myWeather.region = locNamedNodeMap.getNamedItem("region")
						.getNodeValue().toString();
				myWeather.country = locNamedNodeMap.getNamedItem("country")
						.getNodeValue().toString();
			} else {
				myWeather.city = "EMPTY";
				myWeather.region = "EMPTY";
				myWeather.country = "EMPTY";
			}

			// <yweather:wind chill="60" direction="0" speed="0"/>
			NodeList windNodeList = srcDoc
					.getElementsByTagName("yweather:wind");
			if (windNodeList != null && windNodeList.getLength() > 0) {
				Node windNode = windNodeList.item(0);
				NamedNodeMap windNamedNodeMap = windNode.getAttributes();

				myWeather.windChill = windNamedNodeMap.getNamedItem("chill")
						.getNodeValue().toString();
				myWeather.windDirection = windNamedNodeMap
						.getNamedItem("direction").getNodeValue().toString();
				myWeather.windSpeed = windNamedNodeMap.getNamedItem("speed")
						.getNodeValue().toString();
			} else {
				myWeather.windChill = "EMPTY";
				myWeather.windDirection = "EMPTY";
				myWeather.windSpeed = "EMPTY";
			}

			// <yweather:astronomy sunrise="6:52 am" sunset="7:10 pm"/>
			NodeList astNodeList = srcDoc
					.getElementsByTagName("yweather:astronomy");
			if (astNodeList != null && astNodeList.getLength() > 0) {
				Node astNode = astNodeList.item(0);
				NamedNodeMap astNamedNodeMap = astNode.getAttributes();

				myWeather.sunrise = astNamedNodeMap.getNamedItem("sunrise")
						.getNodeValue().toString();
				myWeather.sunset = astNamedNodeMap.getNamedItem("sunset")
						.getNodeValue().toString();
			} else {
				myWeather.sunrise = "EMPTY";
				myWeather.sunset = "EMPTY";
			}

			// <yweather:condition text="Fair" code="33" temp="60"
			// date="Fri, 23 Mar 2012 8:49 pm EDT"/>
			NodeList conditionNodeList = srcDoc
					.getElementsByTagName("yweather:condition");
			if (conditionNodeList != null && conditionNodeList.getLength() > 0) {
				Node conditionNode = conditionNodeList.item(0);
				NamedNodeMap conditionNamedNodeMap = conditionNode
						.getAttributes();

				myWeather.conditiontext = conditionNamedNodeMap
						.getNamedItem("text").getNodeValue().toString();
				myWeather.conditiondate = conditionNamedNodeMap
						.getNamedItem("date").getNodeValue().toString();
			} else {
				myWeather.conditiontext = "EMPTY";
				myWeather.conditiondate = "EMPTY";
			}

			return myWeather;
		}

	}
}
