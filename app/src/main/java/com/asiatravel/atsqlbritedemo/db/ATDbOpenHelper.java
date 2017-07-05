package com.asiatravel.atsqlbritedemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.asiatravel.atsqlbritedemo.model.Person;

/**
 * Created by jsion on 16/5/18.
 */
public class ATDbOpenHelper extends SQLiteOpenHelper {

    public ATDbOpenHelper(Context context) {
        super(context, ATDbConstant.AT_DB_NAME, null, ATDbConstant.AT_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ATDb.PersonTable.CREATE);
        addSamePerson(db);
    }

    private void addSamePerson(SQLiteDatabase db) {
        Person person = new Person();
        person.setAge(35);
        person.setName("陈冠希");

        db.insert(ATDb.PersonTable.TABLE_NAME, null, ATDb.PersonTable.toContentValues(person));

        person = new Person();
        person.setAge(28);
        person.setName("张柏芝");
        db.insert(ATDb.PersonTable.TABLE_NAME, null, ATDb.PersonTable.toContentValues(person));


        person = new Person();
        person.setAge(23);
        person.setName("蔡依林");
        db.insert(ATDb.PersonTable.TABLE_NAME, null, ATDb.PersonTable.toContentValues(person));


        person = new Person();
        person.setAge(26);
        person.setName("小S");
        db.insert(ATDb.PersonTable.TABLE_NAME, null, ATDb.PersonTable.toContentValues(person));


        person = new Person();
        person.setAge(27);
        person.setName("洪文安");
        db.insert(ATDb.PersonTable.TABLE_NAME, null, ATDb.PersonTable.toContentValues(person));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
