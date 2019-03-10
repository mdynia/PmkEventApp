package org.cat.pmk.events.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.cat.pmk.events.R;
import org.cat.pmk.events.api.ApiResponseHandler;
import org.cat.pmk.events.api.PmkEventsRestApi;
import org.cat.pmk.events.datamodel.AdapterEvent;
import org.cat.pmk.events.datamodel.Event;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class AbstractFragmentByDistance extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final ArrayList<Event> EVENTS = new ArrayList<>();

    ListView listView;

    public Context ctx;

    private static AdapterEvent ibAdapter;

    public static boolean spowiedzFilter;

    private float geoLat = 51.755828f;
    private float geoLon = 8.777529f;
    private int distanceIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fgmt_events_bydistance, container, false);
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);


        listView = view.findViewById(R.id.list_ib);


        //populate spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.distance_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx,
                R.array.distance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // set spinner to selection stores in shared prefs
        SharedPreferences prefs = view.getContext().getSharedPreferences(getString(R.string.slectedDistanceRestriction), Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.slectedDistanceRestriction))) {
            distanceIndex  = prefs.getInt(getString(R.string.slectedDistanceRestriction),0);
            spinner.setSelection(distanceIndex);
        }


        // fetch events from server
        PmkEventsRestApi.getEventsByGeo(geoLat, geoLon, distanceIndex, new ApiResponseHandler() {
            @Override
            public void handleResponse(JSONObject response) {
                populateEventList(ctx, listView, response);
            }
        });

    }


    private static void populateEventList(Context cntx, ListView listView, JSONObject response) {
        EVENTS.clear();

        // parse results

        try {
            if (response != null) {
                JSONArray records = response.getJSONArray("records");
                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = (JSONObject) records.get(i);
                    Event e = new Event(record);
                    if (spowiedzFilter) {
                        if (e.getTitle() != null && e.getTitle().toLowerCase().contains("spowiedź")) {
                            EVENTS.add(e);
                        }
                    } else {
                        if (e.getTitle() != null && !e.getTitle().toLowerCase().contains("spowiedź")) {
                            EVENTS.add(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        ibAdapter = new AdapterEvent(EVENTS, cntx);
        listView.setAdapter(ibAdapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        // store spinner selected possition in share
        SharedPreferences.Editor editor = view.getContext().getSharedPreferences(getString(R.string.slectedDistanceRestriction), Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.slectedDistanceRestriction), pos);
        editor.apply();

        distanceIndex  = pos;

        // fetch events from server again
        PmkEventsRestApi.getEventsByGeo(geoLat, geoLon, distanceIndex, new ApiResponseHandler() {
            @Override
            public void handleResponse(JSONObject response) {
                populateEventList(ctx, listView, response);
            }
        });
    }

    public void onNothingSelected(AdapterView<?> parent) {

        // Another interface callback
    }

}
