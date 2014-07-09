package ru.openitr.openweathertest.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Oleg Balditsyn on 03.07.14.
 */
public class WeatherHttpClient {
    private final static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?id=";
    private final static String OPTIONS = "&lang=ru&units=metric";
    private static final String FIND_OPTIONS = "&type=like";
    private static String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&id=";
    private static String BASE_FIND_URL = "http://api.openweathermap.org/data/2.5/find?q=";

    public String getWeatherData(URL url){
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) (url).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            StringBuilder stringBuffer = new StringBuilder();
            inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ( (line = br.readLine())!=null ){
                stringBuffer.append(line).append("\r\n");
            }
            inputStream.close();
            connection.disconnect();
            return stringBuffer.toString();
        } catch (Throwable t){
            t.printStackTrace();
        }
        finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.disconnect();
            } catch (Throwable ignored){}
        }
        return null;
    }



    public String getWeatherData(String location){
        String stringUrl = BASE_URL + location + OPTIONS;
        try {
            return getWeatherData(new URL(stringUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String findLocation (String location){
        String stringUrl = BASE_FIND_URL + location + FIND_OPTIONS;
        try {
            return getWeatherData(new URL(stringUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getForecastWeatherData(String location, String sForecastDayNum) {

        int forecastDayNum = Integer.parseInt(sForecastDayNum);
        String stringUrl = BASE_FORECAST_URL + location + OPTIONS;
        stringUrl = stringUrl + "&cnt=" + forecastDayNum;
        try {
            return getWeatherData(new URL(stringUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
