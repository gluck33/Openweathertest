package ru.openitr.openweathertest;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.openitr.openweathertest.sqlite.LocationsProvider;
import ru.openitr.openweathertest.sqlite.WeatherProvider;
import ru.openitr.openweathertest.weather.Weather;

/**
 * Created by Oleg Balditsyn on 03.07.14.
 */
public class JSONWeatherParser {

    public static ContentValues getLocationInfo (String data) throws JSONException{
        ContentValues result = new ContentValues();
        JSONObject jsonObject = new JSONObject(data);

        JSONObject coordObj = getObject("coord", jsonObject);

        result.put(LocationsProvider.Columns.CITY_ID, getInt("id", jsonObject));
        result.put(LocationsProvider.Columns.CITY_NAME, getString("name", jsonObject));
        result.put(LocationsProvider.Columns.CITY_LAT, getFloat("lat", coordObj));
        result.put(LocationsProvider.Columns.CITY_LON, getFloat("lon", coordObj));

        JSONObject sysObj = getObject("sys", jsonObject);

        result.put(LocationsProvider.Columns.CITY_SUNRISE, getInt("sunrise", sysObj));
        result.put(LocationsProvider.Columns.CITY_SUNSET, getInt("sunset", sysObj));

        JSONObject mainObj = getObject("main", jsonObject);

        result.put(LocationsProvider.Columns.CITY_TEMP, getFloat("temp", mainObj));

        return result;
    }

    public static ArrayList<ContentValues> getForecastWeather (String data) throws JSONException{
        ArrayList<ContentValues> result = new ArrayList<ContentValues>();
        ContentValues resultItem = new ContentValues();
        JSONObject jsonObject = new JSONObject(data);
        JSONObject jsonCityObj = jsonObject.getJSONObject("city");
        int cityId = jsonCityObj.getInt("id");
        JSONArray jsonArray = jsonObject.getJSONArray("list");

        for (int i = 0; i < jsonArray.length(); i++){
            resultItem.clear();
            resultItem.put(WeatherProvider.Columns.CITY_ID, cityId);

            JSONObject jsForecastItem = jsonArray.getJSONObject(i);
            JSONArray jsWeather = jsForecastItem.getJSONArray("weather");
            JSONObject jsTemp = jsForecastItem.getJSONObject("temp");

            resultItem.put(WeatherProvider.Columns.DATE, jsForecastItem.getLong("dt")); //Дата
            resultItem.put(WeatherProvider.Columns.SYMBOL_NAME, jsWeather.getJSONObject(0).getString("description")); //Облачность
            resultItem.put(WeatherProvider.Columns.SYMBOL_ICON, jsWeather.getJSONObject(0).getString("icon"));//Имя иконки неба
            resultItem.put(WeatherProvider.Columns.WIND_SPEED, jsForecastItem.getString("speed"));//Скорость ветра
            resultItem.put(WeatherProvider.Columns.WIND_DEG, jsForecastItem.getString("deg"));//Направление ветра
            resultItem.put(WeatherProvider.Columns.TEMP_DAY, jsTemp .getString("day"));//Температура
            resultItem.put(WeatherProvider.Columns.TEMP_MAX, jsTemp.getString("max"));//Минимальная суточная температура
            resultItem.put(WeatherProvider.Columns.TEMP_MIN, jsTemp.getString("min"));//Минимальная суточная температура
            resultItem.put(WeatherProvider.Columns.TEMP_NIGHT, jsTemp.getString("night"));//Ночная температура
            resultItem.put(WeatherProvider.Columns.TEMP_EVE, jsTemp.getString("eve"));//Вечер
            resultItem.put(WeatherProvider.Columns.TEMP_MORN, jsTemp.getString("morn"));//Утро
            resultItem.put(WeatherProvider.Columns.PRESSURE, jsForecastItem.getString("pressure"));//Давление
            resultItem.put(WeatherProvider.Columns.HUMIDITY, jsForecastItem.getString("humidity"));//Влажность
            result.add(new ContentValues(resultItem));
        }
        return result;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}
