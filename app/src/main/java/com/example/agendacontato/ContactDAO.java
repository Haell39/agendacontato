package com.example.agendacontato;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ContactDAO extends SQLiteOpenHelper {
    private static final String TAG = "ContactDAO";

    private static final String DATABASE_NAME = "agendacontato.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone";
    public static final String COL_EMAIL = "email";
    public static final String COL_ADDRESS = "address";
    public static final String COL_NOTES = "notes";

    private SQLiteDatabase db;

    public ContactDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLiteException {
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            Log.e(TAG, "Erro ao abrir DB", e);
            throw e;
        }
    }

    @Override
    public synchronized void close() {
        try {
            if (db != null && db.isOpen()) db.close();
        } catch (Exception e) {
            Log.w(TAG, "Erro ao fechar DB", e);
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_PHONE + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_NOTES + " TEXT" + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
            onCreate(sqLiteDatabase);
        } catch (SQLException e) {
            Log.e(TAG, "Erro no onUpgrade", e);
        }
    }

    public long insert(Contact contact) {
        if (contact == null) return -1;
        long id = -1;
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_PHONE, contact.getPhone());
        values.put(COL_EMAIL, contact.getEmail());
        values.put(COL_ADDRESS, contact.getAddress());
        values.put(COL_NOTES, contact.getNotes());
        try {
            db.beginTransaction();
            id = db.insertOrThrow(TABLE_CONTACTS, null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(TAG, "Erro ao inserir", e);
        } finally {
            try { db.endTransaction(); } catch (Exception ignored) {}
        }
        return id;
    }

    public int update(Contact contact) {
        if (contact == null) return 0;
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_PHONE, contact.getPhone());
        values.put(COL_EMAIL, contact.getEmail());
        values.put(COL_ADDRESS, contact.getAddress());
        values.put(COL_NOTES, contact.getNotes());
        int rows = 0;
        try {
            rows = db.update(TABLE_CONTACTS, values, COL_ID + "=?", new String[]{String.valueOf(contact.getId())});
        } catch (SQLiteException e) {
            Log.e(TAG, "Erro ao atualizar", e);
        }
        return rows;
    }

    public int delete(long id) {
        int rows = 0;
        try {
            rows = db.delete(TABLE_CONTACTS, COL_ID + "=?", new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Erro ao deletar", e);
        }
        return rows;
    }

    public int delete(Contact contact) {
        if (contact == null) return 0;
        return delete(contact.getId());
    }

    public List<Contact> getAll() {
        List<Contact> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] cols = {COL_ID, COL_NAME, COL_PHONE, COL_EMAIL, COL_ADDRESS, COL_NOTES};
            cursor = db.query(TABLE_CONTACTS, cols, null, null, null, null, COL_NAME + " COLLATE NOCASE ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS));
                    String notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES));
                    list.add(new Contact(id, name, phone, email, address, notes));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao getAll", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public Contact getById(long id) {
        Cursor cursor = null;
        Contact contact = null;
        try {
            String[] cols = {COL_ID, COL_NAME, COL_PHONE, COL_EMAIL, COL_ADDRESS, COL_NOTES};
            cursor = db.query(TABLE_CONTACTS, cols, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                long cid = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES));
                contact = new Contact(cid, name, phone, email, address, notes);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao getById", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return contact;
    }
}
