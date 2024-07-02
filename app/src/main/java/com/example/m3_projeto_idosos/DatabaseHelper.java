package com.example.m3_projeto_idosos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "photoAlbum.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PHOTOS = "photos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_URI = "uri";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CATEGORY = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PHOTOS_TABLE = "CREATE TABLE " + TABLE_PHOTOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_URI + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CATEGORY + " TEXT" +
                ")";
        db.execSQL(CREATE_PHOTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    // Métodos para inserir, atualizar e recuperar dados
    public void insertPhoto(PhotoData photoData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_URI, photoData.getPhotoUri().toString());
        values.put(COLUMN_LATITUDE, photoData.getLatitude());
        values.put(COLUMN_LONGITUDE, photoData.getLongitude());
        values.put(COLUMN_TIMESTAMP, photoData.getTimestamp());
        values.put(COLUMN_DESCRIPTION, photoData.getDescription());
        values.put(COLUMN_CATEGORY, photoData.getCategory());

        long id = db.insert(TABLE_PHOTOS, null, values);
        photoData.setId(id);
        db.close();
    }

    public void updatePhoto(PhotoData photoData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, photoData.getDescription());
        values.put(COLUMN_CATEGORY, photoData.getCategory());

        db.update(TABLE_PHOTOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(photoData.getId())});
        db.close();
    }

    public List<PhotoData> getAllPhotos() {
        List<PhotoData> photos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTOS,
                new String[]{COLUMN_ID, COLUMN_URI, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_TIMESTAMP, COLUMN_DESCRIPTION, COLUMN_CATEGORY},
                null, null, null, null, COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                PhotoData photo = new PhotoData();
                photo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                photo.setPhotoUri(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URI))));
                photo.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)));
                photo.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)));
                photo.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                photo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                photo.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));

                photos.add(photo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return photos;
    }
    public void clearAlbum() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PHOTOS);
        db.close();
    }


    // Método para filtrar fotos por categoria
    @SuppressLint("Range")
    public List<PhotoData> getPhotosByCategory(String category) {
        List<PhotoData> photoDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_ID, COLUMN_URI, COLUMN_LATITUDE, COLUMN_LONGITUDE,
                COLUMN_TIMESTAMP, COLUMN_DESCRIPTION, COLUMN_CATEGORY
        };
        String selection = COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = {category};
        Cursor cursor = db.query(TABLE_PHOTOS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PhotoData photoData = new PhotoData();
                photoData.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                photoData.setPhotoUri(Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_URI))));
                photoData.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                photoData.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                photoData.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                photoData.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                photoData.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));

                photoDataList.add(photoData);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return photoDataList;
    }
    public void updatePhotoDescription(PhotoData photoData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, photoData.getDescription());

        db.update(TABLE_PHOTOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(photoData.getId())});
        db.close();
    }
}