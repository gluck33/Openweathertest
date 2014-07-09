package ru.openitr.openweathertest;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ru.openitr.openweathertest.sqlite.LocationsProvider;
import ru.openitr.openweathertest.sqlite.WeatherProvider;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends ListFragment {
    public static final String ARG_ITEM_ID = "item_id";
    private int cityId;
    private String mItem;
    private String mItemDetail;
    protected int itemId;
    private ContentResolver contentResolver;
    private SimpleAdapter adapter;
    static public ContentObserver observer;
    public Cursor cursor;
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            itemId = Integer.valueOf(getArguments().getString(ARG_ITEM_ID));
            contentResolver = getActivity().getContentResolver();
            cursor = contentResolver.query(LocationsProvider.URI,null,LocationsProvider.Columns._ID + " = ?",new String[]{String.valueOf(itemId)}, null);
            if (cursor.moveToNext()) {
                cityId = cursor.getInt(cursor.getColumnIndex(LocationsProvider.Columns.CITY_ID));
                mItem = cursor.getString(cursor.getColumnIndex(LocationsProvider.Columns.CITY_NAME));
                String lon = cursor.getString(cursor.getColumnIndex(LocationsProvider.Columns.CITY_LON));
                String lat = cursor.getString(cursor.getColumnIndex(LocationsProvider.Columns.CITY_LAT));
                Calendar sunrise = Calendar.getInstance();
                sunrise.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LocationsProvider.Columns.CITY_SUNRISE))*1000);
                Calendar sunset = Calendar.getInstance();
                sunset.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LocationsProvider.Columns.CITY_SUNSET))*1000);
                mItemDetail = "Longitude: " + lon +
                        " Latitude: " + lat +
                        " Sunrise: " + (sunrise.get(Calendar.HOUR_OF_DAY)) + ":" +sunrise.get(Calendar.MINUTE) +
                        " Sunset: " +sunset.get(Calendar.HOUR_OF_DAY) + ":"+sunset.get(Calendar.MINUTE);
            }
            cursor.close();
            setAdapter();
            observer = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    if (cursor !=null){
                        cursor.requery();
                        setAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }
            };

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem);
            ((TextView) rootView.findViewById(R.id.item_detail_1)).setText(mItemDetail);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        contentResolver.registerContentObserver(WeatherProvider.URI,true, observer);
    }

    @Override
    public void onPause() {
        super.onPause();
        contentResolver.unregisterContentObserver(observer);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setAdapter(){
        ArrayList<HashMap<String, String>> myArrList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        cursor = contentResolver.query(WeatherProvider.URI,
                null,
                WeatherProvider.Columns.CITY_ID + " = ?",
                new String[]{String.valueOf(cityId)},null);
        while (cursor.moveToNext()){
            Date fDate = new Date(cursor.getLong(cursor.getColumnIndex(WeatherProvider.Columns.DATE))*1000);
            String maxTemp = cursor.getString(cursor.getColumnIndex(WeatherProvider.Columns.TEMP_MAX));
            String minTemp = cursor.getString(cursor.getColumnIndex(WeatherProvider.Columns.TEMP_MIN));
            String symbol = cursor.getString(cursor.getColumnIndex(WeatherProvider.Columns.SYMBOL_NAME));
            String pressure = cursor.getString(cursor.getColumnIndex(WeatherProvider.Columns.PRESSURE));
            String hum = cursor.getString(cursor.getColumnIndex(WeatherProvider.Columns.HUMIDITY));
            String descString = "T max: " + maxTemp + "\u2103" + ", " +
                    " T min: " + minTemp + "\u2103" + ", " +
                    " " +symbol + " ," +
                    " Pressure: " + pressure + ", " +
                    " Humidity: " + hum +"%";

            map = new HashMap<String, String>();
            map.put("Date", fDate.toLocaleString());
            map.put("Weather", descString);
            myArrList.add(map);
        }
        adapter = new SimpleAdapter(this.getActivity(),
                myArrList,
                android.R.layout.simple_list_item_activated_2,
                new String[]{"Date", "Weather"}, new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
    }

}
