package com.dzh.filemanagement.dao.db;

import android.database.sqlite.SQLiteDatabase;

public interface IDBHelper {
    
    public SQLiteDatabase openDatabase();
    public void closeDatabase();

}
