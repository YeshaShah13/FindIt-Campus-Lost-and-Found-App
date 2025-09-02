package com.example.findit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FindItDB.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_ITEMS = "items";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_IMAGE_PATH = "imagePath";
    private static final String COLUMN_CONTACT_INFO = "contactInfo";

    private static final String COLUMN_USER_ID = "userId"; // Add this


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT, " +
                "name TEXT, " +
                "description TEXT, " +
                "location TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "imagePath TEXT, " +
                "userId TEXT, " +
                "contactInfo TEXT)"); // ✅ must be here
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE items ADD COLUMN contactInfo TEXT");
        }
    }




    public void insertItem(ItemModel item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d("DB", "Inserting item: " + item.getName());


        values.put(COLUMN_TYPE, item.getType());
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_LOCATION, item.getLocation());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_TIME, item.getTime());
        values.put(COLUMN_IMAGE_PATH, item.getImagePath());
        values.put(COLUMN_CONTACT_INFO, item.getContactInfo());
        values.put("userId", item.getUserId()); // ✅ Add this

        db.insert(TABLE_ITEMS, null, values);

    }

    public void uploadToFirebase(ItemModel item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("type", item.getType());
        itemMap.put("name", item.getName());
        itemMap.put("description", item.getDescription());
        itemMap.put("location", item.getLocation());
        itemMap.put("date", item.getDate());
        itemMap.put("time", item.getTime());
        itemMap.put("imagePath", item.getImagePath());
        itemMap.put("contactInfo", item.getContactInfo());
        itemMap.put("userId", item.getUserId());

        db.collection("items")
                .add(itemMap)
                .addOnSuccessListener(documentReference ->
                        Log.d("FirebaseUpload", "Item uploaded: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e("FirebaseUpload", "Upload failed", e));
    }

    public List<ItemModel> getAllItems() {
        List<ItemModel> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                );
                item.setContactInfo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_INFO)));
                item.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));

                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    public List<ItemModel> getItemsByType(String type) {
        List<ItemModel> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_TYPE + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{type});

        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                );
                item.setContactInfo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_INFO)));
                item.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId"))); // ✅ Add this

                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    // ✅ New method for searching items
    public List<ItemModel> searchItems(String keyword) {
        List<ItemModel> itemList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_ITEMS + " WHERE "
                + COLUMN_NAME + " LIKE ? OR "
                + COLUMN_DESCRIPTION + " LIKE ? OR "
                + COLUMN_LOCATION + " LIKE ?";

        SQLiteDatabase db = this.getReadableDatabase();
        String wildcard = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{wildcard, wildcard, wildcard});

        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                );
                item.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    // ✅ Optional: sort list by name (asc/desc)
    public List<ItemModel> sortItems(List<ItemModel> items, boolean ascending) {
        Collections.sort(items, new Comparator<ItemModel>() {
            @Override
            public int compare(ItemModel o1, ItemModel o2) {
                return ascending ? o1.getName().compareToIgnoreCase(o2.getName())
                        : o2.getName().compareToIgnoreCase(o1.getName());
            }
        });
        return items;
    }
    public boolean itemExists(ItemModel item) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ITEMS + " WHERE " +
                COLUMN_NAME + " = ? AND " +
                COLUMN_DATE + " = ? AND " +
                COLUMN_TIME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[] {
                item.getName(), item.getDate(), item.getTime()
        });

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("items", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }


}
