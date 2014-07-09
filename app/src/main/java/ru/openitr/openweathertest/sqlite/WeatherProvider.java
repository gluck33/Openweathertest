package ru.openitr.openweathertest.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Oleg Balditsyn on 01.07.14.
 */
public class WeatherProvider extends SQLiteTableProvider{

    public static final String TABLE_NAME = "weathers";
    public static final Uri URI = Uri.parse("content://ru.openitr.openweather/" + TABLE_NAME);


    public static String getSymbolName(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.SYMBOL_NAME));
    }

    public String getSymbolIcon(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.SYMBOL_ICON));
    }

    public String getWindspeed(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.WIND_SPEED));
    }

    public String getWindDeg(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.WIND_DEG));
    }


    public String getTempDay(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.TEMP_DAY));
    }

    public String getTempMin(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.TEMP_MIN));
    }

    public String getTempMax(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.TEMP_MAX));
    }

    public String getTempNight(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.TEMP_NIGHT));
    }

    public String getTempEve(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.TEMP_EVE));
    }

    public String getTempMorn(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.TEMP_MORN));
    }

    public String getPressure(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.PRESSURE));
    }

    public String getHumidity(Cursor c) {
        return c.getString(c.getColumnIndex(Columns.HUMIDITY));
    }

    public WeatherProvider() {
        super(TABLE_NAME);
    }


    @Override
    public Uri getBaseUri() {
        return URI;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +
                "(" + Columns._ID + " integer primary key on conflict replace, "
                + Columns.CITY_ID + " integer, "
                + Columns.DATE + " long, "
                + Columns.SYMBOL_NAME + " text, "
                + Columns.SYMBOL_ICON + " text, "
                + Columns.WIND_SPEED + " text, "
                + Columns.WIND_DEG + " text, "
                + Columns.TEMP_DAY + " text, "
                + Columns.TEMP_MAX + " text, "
                + Columns.TEMP_MIN + " text, "
                + Columns.TEMP_NIGHT + " text, "
                + Columns.TEMP_EVE + " text, "
                + Columns.TEMP_MORN + " text, "
                + Columns.PRESSURE + " text, "
                + Columns.HUMIDITY + " text )");
    }

    public interface Columns extends BaseColumns{
        String CITY_ID              = "city_id";
        String DATE                 = "date";
        String SYMBOL_NAME          = "symbol_name";
        String SYMBOL_ICON          = "symbol_icon";
        String WIND_SPEED = "wind_speed";
        String WIND_DEG = "wind_deg";
        String TEMP_DAY             = "t_day";
        String TEMP_MIN             = "t_min";
        String TEMP_MAX             = "t_max";
        String TEMP_NIGHT           = "t_night";
        String TEMP_EVE             = "t_eve";
        String TEMP_MORN            = "t_morn";
        String PRESSURE             = "pressure";
        String HUMIDITY             = "humidity";
        }

}
