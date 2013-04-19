package com.emtmm.weatherexample.fragments;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.emtmm.weatherexample.Constants;
import com.emtmm.weatherexample.R;
import com.emtmm.weatherexample.WeatherDetailActivity;
import com.emtmm.weatherexample.data.DBHelper;

public class NewLocationFragment extends SherlockFragment {

	public static final String TAG = NewLocationFragment.class.getSimpleName();

	private DBHelper dbHelper;
	private ProgressDialog progressDialog;
	private EditText location;
	private Button button;
	private ListView listviewWOEID;
	final String yahooPlaceApisBase = "http://where.yahooapis.com/v1/places.q('";
	String yahooPlaceAPIsQuery;

	public NewLocationFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getActivity().setContentView(R.layout.new_location_fragment);
		location = (EditText) getActivity().findViewById(R.id.location);
		button = (Button) getActivity().findViewById(
				R.id.specify_location_button);
		listviewWOEID = (ListView) getActivity().findViewById(R.id.woeidlist);

		location.addTextChangedListener(new TextWatcher() {
			// @Override
			public void onTextChanged(final CharSequence s, final int start,
					final int before, final int count) {
				if (location.getText().toString().length() > 5) {
					Toast.makeText(getActivity(),
							"Please enter no more than 5 digits",
							Toast.LENGTH_SHORT).show();
					location.setText(location.getText().toString()
							.substring(0, 5));
				}
			}

			// @Override
			public void afterTextChanged(final Editable e) {
			}

			// @Override
			public void beforeTextChanged(final CharSequence s,
					final int start, final int count, final int after) {
			}
		});

		this.button.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				handleLoadLocation();
			}
		});
	}

	protected void handleLoadLocation() {
		Intent intent = null;
		if (validate()) {
			new MyQueryYahooPlaceTask().execute();
		}

	}

	public class MyQueryYahooPlaceTask extends AsyncTask<Void, Void, Void> {
		ArrayList<String> l;

		@Override
		protected Void doInBackground(Void... arg0) {
			l = QueryYahooPlaceAPIs();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			/*ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, l);
			listviewWOEID.setAdapter(aa);

			listviewWOEID.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String selWoeid = l.get(position).toString();
					Log.d("woeid", selWoeid);
					
					 * Toast.makeText(getApplicationContext(), selWoeid,
					 * Toast.LENGTH_LONG).show();
					 

					Intent intent = new Intent();
					intent.setClass(getActivity(), WeatherDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putCharSequence("SEL_WOEID", selWoeid);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});*/
			String selWoeid = l.get(0).toString();
			Intent intent = new Intent();
			intent.setClass(getActivity(), WeatherDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putCharSequence("SEL_WOEID", selWoeid);
			bundle.putCharSequence("zip", location.getText().toString());
			intent.putExtras(bundle);
			startActivity(intent);
			super.onPostExecute(result);
		}

		private ArrayList<String> QueryYahooPlaceAPIs() {
			String uriPlace = Uri.encode(location.getText().toString());

			yahooPlaceAPIsQuery = yahooPlaceApisBase
					+ uriPlace
					+ "')?appid=euyWoO3V34HOEKoHiBz8maLM244BCvf28QAOBfEmrJUyzg8xbHTDTsD.8.rkiRdGECxfLzH_";

			String woeidString = QueryYahooWeather(yahooPlaceAPIsQuery);
			Document woeidDoc = convertStringToDocument(woeidString);
			return parseWOEID(woeidDoc);
		}

	}

	private ArrayList<String> parseWOEID(Document srcDoc) {
		ArrayList<String> listWOEID = new ArrayList<String>();

		
		NodeList nodeListDescription = srcDoc.getElementsByTagName("woeid");
		if (nodeListDescription.getLength() >= 0) {
			for (int i = 0; i < nodeListDescription.getLength(); i++) {
				listWOEID.add(nodeListDescription.item(i).getTextContent());
				
			}
		} else {
			listWOEID.clear();
		}

		return listWOEID;
	}

	private Document convertStringToDocument(String src) {
		Document dest = null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
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

	private String QueryYahooWeather(String queryString) {
		String qResult = "";

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
			;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return qResult;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(Constants.LOGTAG, " " + NewLocationFragment.TAG + " onResume");
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private boolean validate() {
		boolean valid = true;
		StringBuffer validationText = new StringBuffer();
		if ((this.location.getText() == null)
				|| this.location.getText().toString().equals("")) {
			validationText.append(getResources().getString(
					R.string.message_no_location));
			valid = false;
		} else if (!isNumeric(this.location.getText().toString())
				|| (this.location.getText().toString().length() != 5)) {
			validationText.append(getResources().getString(
					R.string.message_invalid_location));
			valid = false;
		}
		if (!valid) {
			new AlertDialog.Builder(getActivity())
					.setTitle(getResources().getString(R.string.alert_label))
					.setMessage(validationText.toString())
					.setPositiveButton(
							"Continue",
							new android.content.DialogInterface.OnClickListener() {

								// @Override
								public void onClick(
										final DialogInterface dialog,
										final int arg1) {
									getActivity().setResult(Activity.RESULT_OK);
								}
							}).show();
			validationText = null;
		}
		return valid;
	}

	private boolean isNumeric(final String s) {
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
