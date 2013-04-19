package com.emtmm.weatherexample.fragments;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.actionbarsherlock.app.SherlockListFragment;
import com.emtmm.weatherexample.Constants;
import com.emtmm.weatherexample.R;
import com.emtmm.weatherexample.data.DBHelper;
import com.emtmm.weatherexample.data.DBHelper.Location;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SavedLocationsFragment extends SherlockListFragment {
	public static final String TAG = SavedLocationsFragment.class
			.getSimpleName();

	private DBHelper dbHelper;
	private ProgressDialog progressDialog;
	private TextView empty;
	private List<Location> locations;
	private ListAdapter adapter;

	public SavedLocationsFragment() {

	}

	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			Log.v(Constants.LOGTAG, " " + SavedLocationsFragment.TAG
					+ " worker thread done, setup list");
			progressDialog.dismiss();
			if ((locations == null) || (locations.size() == 0)) {
				empty.setText("No Data");
			} else {
				// adapter = new
				// ArrayAdapter<Location>(SavedLocationsFragment.this,
				// R.layout.locations_list_item, locations);
				setListAdapter(adapter);
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getActivity().setContentView(R.layout.saved_location_list);
		getListView().setEmptyView(
				getActivity().findViewById(R.id.view_saved_locations_empty));
		this.empty = (TextView) getActivity().findViewById(
				R.id.view_saved_locations_empty);
		this.dbHelper = new DBHelper(getActivity());
		loadLocations();
	}

	private void loadLocations() {
		Log.v(Constants.LOGTAG, " " + SavedLocationsFragment.TAG
				+ " loadLocations");
		this.progressDialog = ProgressDialog.show(getActivity(), " Working...",
				" Retrieving saved locations", true, false);
		new Thread() {

			@Override
			public void run() {
				locations = dbHelper.getAll();
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onPause() {
		super.onResume();
		this.dbHelper.cleanup();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

}
