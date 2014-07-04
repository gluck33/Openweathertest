package ru.openitr.openweathertest.sqlite;

/**
 * Created by Oleg Balditsyn on 01.07.14.
 */
public interface SQLiteOperation {
    int INSERT = 1;

    int UPDATE = 2;

    int DELETE = 3;

    String KEY_LAST_ID = "ru.openitr.openweathertest.sqlite.KEY_LAST_ID";

    String KEY_AFFECTED_ROWS = "ru.openitr.openweathertest.sqlite.KEY_AFFECTED_ROWS";
}
