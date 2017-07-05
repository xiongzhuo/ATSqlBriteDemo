package com.asiatravel.atsqlbritedemo.db;

import android.content.Context;

import com.asiatravel.atsqlbritedemo.model.Person;
import com.asiatravel.atsqlbritedemo.tools.Logger;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jsion on 16/5/18.
 */
public class ATDbManager {
    private static final String TAG = "ATDbManager";
    // rx响应式数据库,
    private BriteDatabase briteDatabase;

    public ATDbManager(Context context) {
        ATDbOpenHelper dbOpenHelper;
        // sqlbrite 初始化,构造出响应式数据库,添加log
        SqlBrite sqlBrite;
        sqlBrite = SqlBrite.create(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                Logger.wtf(TAG, "log: >>>>" + message);
            }
        });
        // 原生的sqllitehelper 用来建立数据库和数据表,以及构造,rx响应式数据库
        dbOpenHelper = new ATDbOpenHelper(context);
        // 执行slqbirte 构造数据库的语句
        briteDatabase = sqlBrite.wrapDatabaseHelper(dbOpenHelper, Schedulers.io());
        briteDatabase.setLoggingEnabled(true);
    }

    public Observable<List<Person>> queryPerson() {
        return briteDatabase
                .createQuery(ATDb.PersonTable.TABLE_NAME, "SELECT * FROM " + ATDb.PersonTable.TABLE_NAME)
                .mapToList(ATDb.PersonTable.PERSON_MAPPER);

    }

    public Observable<List<Person>> queryPersonByName(String name) {
        return briteDatabase.createQuery(ATDb.PersonTable.TABLE_NAME, "SELECT * FROM "
                        + ATDb.PersonTable.TABLE_NAME
                        + " WHERE "
                        + ATDb.PersonTable.COLUMN_NAME
                        + " = ?"
                , name)
                .mapToList(ATDb.PersonTable.PERSON_MAPPER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public long addPerson(Person person) {
        return briteDatabase.insert(ATDb.PersonTable.TABLE_NAME, ATDb.PersonTable.toContentValues(person));
    }

    public int deletePersonByName(final String name) {
        return briteDatabase.delete(ATDb.PersonTable.TABLE_NAME, ATDb.PersonTable.COLUMN_NAME + "=?", name);
    }

}
