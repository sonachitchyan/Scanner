package com.example.asus.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class DataBaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "data";
    private static final String TABlE_NAME = "import";
    private static final String KEY_ARTICLE = "article";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_CODE = "code";
    private static final String KEY_COUNT = "count";
    private static final String KEY_COUNT_DB = "count_db";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABlE_NAME + "("
                 + KEY_NAME + " TEXT NOT NULL,"
                + KEY_ARTICLE + " INTEGER NOT NULL, " +
                KEY_BARCODE + " TEXT NOT NULL, " +
                KEY_CODE + " TEXT NOT NULL, " +
                KEY_COUNT + " INTEGER NOT NULL, " +
                KEY_COUNT_DB + " INTEGER NOT NULL, " +
                KEY_PRICE + "INTEGER NOT NULL" + ");";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    public void addInfo(Data data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, data.getName());
        contentValues.put(KEY_ARTICLE, data.getArticle());
        contentValues.put(KEY_BARCODE, data.getBarcode());
        contentValues.put(KEY_CODE, data.getCode());
        contentValues.put(KEY_COUNT_DB, data.getCount_db());
        contentValues.put(KEY_COUNT, data.getCount());
        contentValues.put(KEY_PRICE, data.getPrice());


        db.insert(TABlE_NAME, null, contentValues);

    }

    public Data getInfoByBarcode(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABlE_NAME, new String[] {
                        KEY_NAME,
                KEY_ARTICLE,
                KEY_BARCODE,
                KEY_CODE,
                KEY_COUNT,
                KEY_COUNT_DB,
                KEY_PRICE
        }, KEY_BARCODE + "=?", new String[]{ barcode}, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
        }

        else {
            return null;
        }
        Data data = new Data(cursor.getString(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getInt(5),
               cursor.getInt(6));
        return data;
    }

    public Data getInfoByCode(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABlE_NAME, new String[] {
                KEY_NAME,
                KEY_ARTICLE,
                KEY_BARCODE,
                KEY_CODE,
                KEY_COUNT,
                KEY_COUNT_DB,
                KEY_PRICE
        }, KEY_CODE + "=?", new String[]{ code}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        else if (cursor.getCount()==0){
            return null;
        }
        else {
            return null;
        }

        Data data = new Data(cursor.getString(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6));
        return data;
    }
    public Data getInfoByArticle(int article) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABlE_NAME, new String[] {
                KEY_NAME,
                KEY_ARTICLE,
                KEY_BARCODE,
                KEY_CODE,
                KEY_COUNT,
                KEY_COUNT_DB,
                KEY_PRICE
        }, KEY_ARTICLE + "=?", new String[]{ String.valueOf(article)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        else if (cursor.getCount()==0){
            return null;
        }
        else {
            return null;
        }

        Data data = new Data(cursor.getString(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6));
        return data;
    }
    public int updateInfoByBarcode(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, data.getCount());


        return db.update(TABlE_NAME, values, KEY_BARCODE + " = ?",
                new String[] { data.getBarcode() });
    }

    public int updateInfoByCode(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, data.getCount());


        return db.update(TABlE_NAME, values, KEY_CODE + " = ?",
                new String[] { data.getCode() });
    }
    public int updateInfoByArticle(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, data.getCount());


        return db.update(TABlE_NAME, values, KEY_ARTICLE + " = ?",
                new String[] { String.valueOf(data.getArticle()) });
    }
    public boolean isEmpty(){
        String selectQuery = "SELECT  * FROM " + TABlE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount()>0){
            return false;
        }
        return true;
    }
    public List<Data> getAllInfo() {
        List<Data> dataList = new ArrayList<Data>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABlE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Data data = new Data();
                data.setName(cursor.getString(0));
                data.setArticle(cursor.getInt(1));
                data.setBarcode(cursor.getString(2));
                data.setCode(cursor.getString(3));
                data.setCount(cursor.getInt(4));
                data.setCount_db(cursor.getInt(5));
                data.setPrice(cursor.getInt(6));

                dataList.add(data);
            } while (cursor.moveToNext());
        }
        return dataList;

    }
    public void deletAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABlE_NAME, null, null);
    }

    public void makeAllZero() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABlE_NAME + " SET " + KEY_COUNT + "=0";
        db.execSQL(query);
    }
    public void changeCountForBarcode(String barcode, int count){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABlE_NAME + " SET " + KEY_COUNT + "=" + count +" WHERE " +
                KEY_BARCODE + "='" + barcode + "';";
        db.execSQL(query);
    }
    public void changeCountForCode(String code, int count){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABlE_NAME + " SET " + KEY_COUNT + "=" + count +" WHERE " +
                KEY_CODE + "='" + code + "';";
        db.execSQL(query);
    }
    public void changeCountForArticle(int article, int count){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABlE_NAME + " SET " + KEY_COUNT + "= " + count + " WHERE " +
                KEY_ARTICLE + "=" + article + ";";
        db.execSQL(query);
    }
}
