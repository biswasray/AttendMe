package com.sbr.attendme;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBName.db";

    public static final String TEACHER_TABLE_NAME = "teacher_db";
    public static final String TEACHER_ID = "id";
    public static final String TEACHER_NAME = "name";

    public static final String STUDENT_TABLE_NAME = "student_db";
    public static final String STUDENT_ID = "id";
    public static final String STUDENT_NAME = "name";
    public static final String STUDENT_ROLL_NO = "roll";
    public static final String STUDENT_REG_NO = "regn";

    public static final String CLASS_TABLE_NAME = "class_db";
    public static final String CLASS_ID = "id";
    public static final String CLASS_STREAM = "stream";
    public static final String CLASS_BRANCH = "branch";
    public static final String CLASS_SUBJECT = "subject";
    public static final String CLASS_SESSION = "session";
    public static final String CLASS_DATE_TABLE = "date_tables";

    public static final String DATE_TABLE = "date_db";
    public static final String DATE_DATA = "date";
    public static final String DATE_STUDENT_ID = "student_id";

    private SQLiteDatabase sqLiteDatabase;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase=sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public boolean tableExists(String table){
        if (getReadableDatabase() == null || !getReadableDatabase().isOpen() || table == null){
            return false;
        }
        int count = 0;
        String[] args = {"table",table};
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type=? AND name=?",args,null);
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }
    public void createTeacher() {
        String CREATE_TABLE = "CREATE TABLE " + TEACHER_TABLE_NAME + "("
                + TEACHER_ID + " TEXT PRIMARY KEY," + TEACHER_NAME + " TEXT" + ")";

        getWritableDatabase().execSQL(CREATE_TABLE);
    }
    public void insertTeacher(String name,String id){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBHelper.TEACHER_NAME,name);
        contentValues.put(DBHelper.TEACHER_ID,id);
        getWritableDatabase().insert(DBHelper.TEACHER_TABLE_NAME,null,contentValues);
    }
    @SuppressLint("Range")
    public ArrayList<Teacher> getTeachers() {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("SELECT * FROM " + TEACHER_TABLE_NAME, null);
        ArrayList<Teacher> res=new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                res.add(new Teacher(
                        cursor.getString(cursor.getColumnIndex(DBHelper.TEACHER_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.TEACHER_ID))
                ));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public void createStudent() {
        String CREATE_TABLE = "CREATE TABLE " + STUDENT_TABLE_NAME + "("
                + STUDENT_ID + " TEXT PRIMARY KEY," + STUDENT_NAME + " TEXT ," +STUDENT_ROLL_NO+" INTEGER"+ ")";

        getWritableDatabase().execSQL(CREATE_TABLE);
    }
    public void insertStudent(String name,String id,int roll){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBHelper.STUDENT_NAME,name);
        contentValues.put(DBHelper.STUDENT_ID,id);
        contentValues.put(DBHelper.STUDENT_ROLL_NO,roll);
        getWritableDatabase().insert(DBHelper.STUDENT_TABLE_NAME,null,contentValues);
    }
    @SuppressLint("Range")
    public ArrayList<Student> getStudents() {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("SELECT * FROM " + STUDENT_TABLE_NAME, null);
        ArrayList<Student> res=new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                res.add(new Student(
                        cursor.getString(cursor.getColumnIndex(DBHelper.STUDENT_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.STUDENT_ID)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.STUDENT_ROLL_NO))
                ));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public void createDateTable(String tableName) {
        String CREATE_TABLE = "CREATE TABLE " + tableName + "("
                + DATE_DATA + " TEXT," + DATE_STUDENT_ID + " TEXT " + ")";

        getWritableDatabase().execSQL(CREATE_TABLE);
    }
    public void insertDate(String tableName,String date,String id){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBHelper.DATE_DATA,date);
        contentValues.put(DBHelper.DATE_STUDENT_ID,id);
        getWritableDatabase().insert(tableName,null,contentValues);
    }
    public void createClass() {
        String CREATE_TABLE = "CREATE TABLE " + CLASS_TABLE_NAME + "("
                + CLASS_ID + " INTEGER PRIMARY KEY," + CLASS_SUBJECT + " TEXT ," + CLASS_BRANCH + " TEXT ," + CLASS_STREAM + " TEXT ," + CLASS_DATE_TABLE + " TEXT ," +CLASS_SESSION+" INTEGER"+ ")";

        getWritableDatabase().execSQL(CREATE_TABLE);
    }
    public void insertClass(Classs classs) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBHelper.CLASS_ID,classs.getId());
        contentValues.put(DBHelper.CLASS_SUBJECT,classs.getSubject());
        contentValues.put(DBHelper.CLASS_BRANCH,classs.getBranch());
        contentValues.put(DBHelper.CLASS_STREAM,classs.getStream());
        contentValues.put(DBHelper.CLASS_DATE_TABLE,classs.getDateTable());
        contentValues.put(DBHelper.CLASS_SESSION,classs.getSession());
        getWritableDatabase().insert(DBHelper.CLASS_TABLE_NAME,null,contentValues);
    }
    @SuppressLint("Range")
    public ArrayList<Classs> getClasses() {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("SELECT * FROM " + CLASS_TABLE_NAME, null);
        ArrayList<Classs> res=new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                res.add(new Classs(cursor.getInt(cursor.getColumnIndex(DBHelper.CLASS_ID)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.CLASS_SUBJECT)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.CLASS_BRANCH)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.CLASS_STREAM)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.CLASS_DATE_TABLE)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.CLASS_SESSION))));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public void createTable(String tableName,String[] columnName) {
        String CREATE_TABLE= "CREATE TABLE " + CLASS_TABLE_NAME + " ("+columnName[0]+" TEXT,";
        int i=1;
        while(i<columnName.length) {
            CREATE_TABLE+=columnName[i]+" INTEGER";
            i++;
            if(i<columnName.length)
                CREATE_TABLE+=",";
        }
        CREATE_TABLE+=")";
        getWritableDatabase().execSQL(CREATE_TABLE);
    }
    public void upgradeTable(String tableName,String columnName[]) {
        String CREATE_TABLE= "ALTER TABLE " + tableName + " ADD ";
        int i=0;
        while(i<columnName.length) {
            CREATE_TABLE+=columnName[i]+" INTEGER DEFAULT 0 ";
            i++;
            if(i<columnName.length)
                CREATE_TABLE+=",";
        }
        getWritableDatabase().execSQL(CREATE_TABLE);
    }
    public void insertTable(String tableName,ContentValues contentValues) {
        getWritableDatabase().insert(tableName,null,contentValues);
    }
}
