package ru.openitr.openweathertest.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by oleg on 01.07.14.
 */
public class LocationsProvider extends SQLiteTableProvider{

    public static final String TABLE_NAME = "locations";
    public static final Uri URI = Uri.parse("content://ru.openitr.openweather/" + TABLE_NAME);

    public LocationsProvider() {
        super(TABLE_NAME);
    }

    @Override
    public Uri getBaseUri() {
        return URI;
    }

    public int getCityId(Cursor c){
        return c.getInt(c.getColumnIndex(Columns.CITY_ID));
    }

    public static String getCityName(Cursor c){
        return c.getString(c.getColumnIndex(Columns.CITY_NAME));
    }

    public long getCityLat(Cursor c){
        return c.getLong(c.getColumnIndex(Columns.CITY_LAT));
    }

    public  long getCityLon(Cursor c){
        return c.getLong(c.getColumnIndex(Columns.CITY_LON));
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +
                "(" + Columns._ID + " integer primary key on conflict replace, "
                + Columns.CITY_ID + " integer, "
                + Columns.CITY_NAME + " text, "
                + Columns.CITY_LAT + " real, "
                + Columns.CITY_LON + " real, "
                + Columns.CITY_SUNRISE + " integer, "
                + Columns.CITY_SUNSET + " integer, "
                + Columns.CITY_TEMP + " real);");
        db.execSQL("insert into " +TABLE_NAME + " values (0, 524901, 'Moscow', 37.615555, 55.75222, null, null, null);");
        db.execSQL("insert into " +TABLE_NAME + " values (1, 498817, 'Saint Petersburg', 30.306, 59.9332, null, null, null);");
    }

    public interface Columns extends BaseColumns{
        String CITY_ID = "city_id";
        String CITY_NAME = "city_name";
        String CITY_LON = "city_lon";
        String CITY_LAT = "city_lat";
        String CITY_TEMP = "city_temp";
        String CITY_SUNRISE = "city_sunrise";
        String CITY_SUNSET = "city_sunset";
        String[] ALL_COLUMNS = {_ID, CITY_ID,CITY_NAME,CITY_LON,CITY_LAT,CITY_TEMP,CITY_SUNRISE,CITY_SUNSET};

    }

}
