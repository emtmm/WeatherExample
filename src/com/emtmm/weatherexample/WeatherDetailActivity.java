package com.emtmm.weatherexample;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.emtmm.weatherexample.data.DBHelper;
import com.emtmm.weatherexample.data.DBHelper.Location;
import com.emtmm.weatherexample.data.QueryYahooWeather;
import com.emtmm.weatherexample.data.WeatherForecast;
import com.emtmm.weatherexample.data.WeatherRecord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherDetailActivity extends SherlockFragmentActivity {
	public static final String TAG = SavedLocations.class.getSimpleName();
	ActionBar actionBar;
	//MyWeather myWeather = new MyWeather();
	WeatherRecord weatherRecord = new WeatherRecord();
	//TextView weather;
	String sel_woeid;
	String zip;
	String city;
	String region;
	private TextView location;
    private TextView date;
    private TextView condition;
    private TextView forecast;
    private ImageView conditionImage;
    

	private DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_detail);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//weather = (TextView) findViewById(R.id.weather);
		location = (TextView) findViewById(R.id.view_location);
        date = (TextView) findViewById(R.id.view_date);
        condition = (TextView) findViewById(R.id.view_condition);
        forecast = (TextView) findViewById(R.id.view_forecast);
        conditionImage = (ImageView) findViewById(R.id.condition_image);
        
		Bundle bundle = this.getIntent().getExtras();
		sel_woeid = (String) bundle.getCharSequence("SEL_WOEID");
		zip = (String) bundle.getCharSequence("zip");
		new MyQueryYahooWeatherTask(sel_woeid).execute();

		Toast.makeText(getApplicationContext(), sel_woeid, Toast.LENGTH_LONG)
				.show();
	}

	private class MyQueryYahooWeatherTask extends AsyncTask<Void, Void, Void> {

		String woeid;
		WeatherRecord weatherResult;
		QueryYahooWeather weatherString;

		MyQueryYahooWeatherTask(String w) {
			this.woeid = w;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
		
			weatherString = new QueryYahooWeather(woeid);
			
			if (weatherString != null) {
				weatherResult = weatherString.getWeather();
				
			} else {
				weatherResult = null;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//weather.setText(weatherResult);
			city = weatherResult.getCity();
			region = weatherResult.getRegion();
			Log.d("result", weatherResult.toString());
			location.setText(weatherResult.getCity() + ", " + weatherResult.getRegion() + " " + weatherResult.getCountry());
            date.setText(weatherResult.getDate());

            StringBuffer cond = new StringBuffer();
            cond.append(weatherResult.getCondition().getDisplay() + "\n");
            cond.append("Temperature: " + weatherResult.getTemp() + " F " + " (wind chill " + weatherResult.getWindChill()
                + " F)\n");
            cond.append("Barometer: " + weatherResult.getPressure() + " and " + weatherResult.getPressureState() + "\n");
            cond.append("Humidity: " + weatherResult.getHumidity() + "% - Wind: " + weatherResult.getWindDirection() + " "
                + weatherResult.getWindSpeed() + "mph\n");
            cond.append("Sunrise: " + weatherResult.getSunrise() + " - Sunset:  " + weatherResult.getSunset());
            condition.setText(cond.toString());

            StringBuilder fore = new StringBuilder();
            for (int i = 0; i < weatherResult.getForecasts().length; i++) {
                WeatherForecast fc = weatherResult.getForecasts()[i];
                Log.d("forecast", weatherResult.getForecasts()[i].toString());
                fore.append(fc.getDay() + ":\n");
                fore.append(fc.getCondition().getDisplay() + " High:" + fc.getHigh() + " F - Low:" + fc.getLow()
                    + " F");
                if (i == 0) {
                    fore.append("\n\n");
                }
            }
            
            forecast.setText(fore.toString());
            String resPath = "com.emtmm.weatherexample:drawable/" + "cond" + weatherResult.getCondition().getId();
            int resId = getResources().getIdentifier(resPath, null, null);
            conditionImage.setImageDrawable(getResources().getDrawable(resId));
			super.onPostExecute(result);
		}

		
	}
	

	@Override
	public void onStart() {
		super.onStart();
		Log.v(Constants.LOGTAG, " " + WeatherDetailActivity.TAG + " onStart");
		dbHelper = new DBHelper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.weather_detail, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Log.d("item", String.valueOf(item.getItemId()));
		switch (item.getItemId()) {
		case R.id.add_to_favorites:

			Location loc = new Location();
			loc.zip = zip;
			loc.city = city;
			loc.region = region;
			loc.woeid = sel_woeid;
			this.dbHelper.insert(loc);

			break;
		case android.R.id.home: 
			onBackPressed()
			;break;
		}

		return true;
	}

}
