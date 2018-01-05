package com.budget.abudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper  extends SQLiteOpenHelper{

    private static final String TAG = "DBHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "budgetDb";
    private static final String TABLE_BUDGET = "budget";

    private static final String KEY_ID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_COST = "cost";
    static final String KEY_QUANTITY = "quantity";
    static final String KEY_P_DATE = "p_date";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_BUDGET + "(" + KEY_ID
                + " integer primary key," + KEY_NAME + " text," + KEY_COST + " real," +
                KEY_QUANTITY + " real," + KEY_P_DATE + " DATETIME DEFAULT CURRENT_DATE" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_BUDGET);
        onCreate(db);
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET;
        return db.rawQuery(query, null);
    }

    Cursor getDataById(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT *" + " FROM " + TABLE_BUDGET +
                " WHERE " + KEY_ID + " = '" + id + "'";
        Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        return data;
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_BUDGET +
                " WHERE " + KEY_NAME + " = '" + name + "'";
        return db.rawQuery(query, null);
    }

    Cursor getAnalysis(String from, String to){
        SQLiteDatabase db = this.getWritableDatabase();
        if (from.equals("")) {
            from = "1800-01-01";
        }
        if (to.equals("")){
            to = "2200-01-01";
        }
        String query = "SELECT " + DBHelper.KEY_NAME + ", Sum(" + DBHelper.KEY_COST + "*" +
                DBHelper.KEY_QUANTITY + ") as [money]" + " FROM " + DBHelper.TABLE_BUDGET +
                " where strftime('%Y-%m-%d'," + DBHelper.KEY_P_DATE + ") >= " + "'" + from + "'"+
                " and strftime('%Y-%m-%d'," + DBHelper.KEY_P_DATE + ") <= " + "'" + to + "'"+
                " group by " + DBHelper.KEY_NAME + " order by money desc";
        Log.d(TAG, query);
        return db.rawQuery(query, null);
    }

    boolean addData(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_BUDGET, null, contentValues);
        if (result == -1) {
            Log.d(TAG, "addData: Ошибка добавления " + contentValues + " в " + TABLE_BUDGET);
            return false;
        } else {
            Log.d(TAG, "addData: Добавлено " + contentValues + " в " + TABLE_BUDGET);
            return true;
        }
    }

    boolean updateData(ContentValues contentValues, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.update(DBHelper.TABLE_BUDGET, contentValues,
                DBHelper.KEY_ID + "=" + String.valueOf(id), null);
        if (result == -1) {
            Log.d(TAG, "updateData: Ошибка изменения записи с id " + String.valueOf(id));
            return false;
        } else {
            Log.d(TAG, "updateData: Изменена запись с id " + String.valueOf(id));
            return true;
        }
    }

    boolean deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(DBHelper.TABLE_BUDGET, "_id = ?",
                new String[]{String.valueOf(id)});
        if (result == -1) {
            Log.d(TAG, "deleteData: Ошибка удаления записи с id " + String.valueOf(id));
            return false;
        } else {
            Log.d(TAG, "deleteData: Удалена запись с id " + String.valueOf(id));
            return true;
        }
    }

}


