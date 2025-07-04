package com.example.findit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FindItDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ITEMS = "items";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_IMAGE_PATH = "imagePath";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_IMAGE_PATH + " TEXT)";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    // Drop old table on upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Insert a new item
    public void insertItem(ItemModel item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE, item.getType());
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_LOCATION, item.getLocation());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_TIME, item.getTime());
        values.put(COLUMN_IMAGE_PATH, item.getImagePath());

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    // Get all items
    public List<ItemModel> getAllItems() {
        List<ItemModel> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }
}