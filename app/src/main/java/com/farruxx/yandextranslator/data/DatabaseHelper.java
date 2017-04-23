package com.farruxx.yandextranslator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.farruxx.yandextranslator.model.Translate;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "base.db";
    private static final int DATABASE_VERSION = 1;
    Context context;
    private TranslateDao addressDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Translate.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    public TranslateDao getTranslateDao() {
        if(addressDao == null){
            try {
                addressDao = new TranslateDao(connectionSource);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addressDao;
    }


}
