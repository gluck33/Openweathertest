package ru.openitr.openweathertest;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;

import java.util.ArrayList;

import ru.openitr.openweathertest.sqlite.LocationsProvider;
import ru.openitr.openweathertest.sqlite.WeatherProvider;
import ru.openitr.openweathertest.weather.JSONWeatherParser;
import ru.openitr.openweathertest.weather.WeatherHttpClient;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ItemListActivity extends Activity
        implements ItemListFragment.Callbacks, SelectionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public final static int ACTION_GET_WEATHER = 1;
    private boolean mTwoPane;
    public ContentResolver lcr;
    private ArrayList<ContentValues> cityListContentVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }
        lcr = getContentResolver();
        new GetWeatherTask().execute(ACTION_GET_WEATHER);
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    public void clickAddButton(View view){
        EditText searchText = (EditText) findViewById(R.id.searchEditText);
        new AddCityTask().execute(searchText.getText().toString());
    }



    void showChoiceCityDialog(ArrayList<ContentValues> cityList){
        ArrayList<String> items = new ArrayList<String>();
        for (ContentValues item : cityList) {
            items.add(item.getAsString(LocationsProvider.Columns.CITY_NAME)
                    + ", " + item.getAsString(LocationsProvider.Columns.CITY_COUNTRY));
        }
        FragmentManager manager = getFragmentManager();
        ChoiceCityDialog dialog = new ChoiceCityDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ChoiceCityDialog.DATA, items);
        bundle.putInt(ChoiceCityDialog.SELECTED, 0);
        dialog.setArguments(bundle);
        dialog.show(manager, "Dialog");
    }

    @Override
    public void selectItem(int position) {
        addCity(cityListContentVal.get(position));
    }

    public void addCity(ContentValues contentValues){
        lcr.insert(LocationsProvider.URI, contentValues);
        lcr.notifyChange(LocationsProvider.URI,null);
        new GetWeatherTask().execute(0);
    }

    public class GetWeatherTask extends AsyncTask<Integer, Integer, String>{

        @Override
        protected String doInBackground(Integer... params) {
            refreshForecastWeather(params[0]);
            lcr.notifyChange(LocationsProvider.URI, null);
            return null;
        }

        private void refreshForecastWeather(int param) {
            Cursor locationsCursor = lcr.query(LocationsProvider.URI,
                    new String[] {LocationsProvider.Columns.CITY_ID},null,null, null);
            lcr.delete(WeatherProvider.URI,null,null);
                while (locationsCursor.moveToNext()){
                    int cityId = locationsCursor.getInt(0);
                    String StrCity_Id = String.valueOf(cityId);
                    String locData = (new WeatherHttpClient().getWeatherData(StrCity_Id));

                    //locations.
                    try {
                        ContentValues lcv = JSONWeatherParser.getLocationInfo(locData);
                        lcr.update(LocationsProvider.URI,
                                lcv,
                                LocationsProvider.Columns.CITY_ID + " = ?",
                                new String[]{StrCity_Id});
                        String data = (new WeatherHttpClient()).getForecastWeatherData(StrCity_Id, "3");

                        //forecast weathers.
                        try {
                            ArrayList<ContentValues> wcv = JSONWeatherParser.getForecastWeather(data);
                            for (ContentValues aWcv : wcv) {
                                lcr.insert(WeatherProvider.URI, aWcv);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                locationsCursor.close();
        }

    }

    private class AddCityTask extends AsyncTask<String, Integer, Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            String findData = new WeatherHttpClient().findLocation(params[0]);
            cityListContentVal = JSONWeatherParser.getLocationsInfo(findData);
            switch (cityListContentVal.size()){
                case 0: return null;
                case 1: addCity(cityListContentVal.get(0));
                        break;
                default: showChoiceCityDialog(cityListContentVal);
                        break;
            }
            lcr.notifyChange(LocationsProvider.URI,null);
            return null;
        }
    }

}
