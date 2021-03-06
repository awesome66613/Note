package com.example.note;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TakeMeAwayDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "takemeaway.db";
    public static final String TABLE_NAME = "tmaTravelPost";
    public static final String COL_PostID = "postID";
    public static final String COL_PostTitle = "postTitle";
    public static final String COL_PostContent = "postContent";
    public static final String COL_PostDate = "postDate";
    public static final String COL_PostTime = "postTime";
    public static final String COL_PostImage = "postImage";
    public static final String COL_PostLocationName = "postLocationName";
    public static final String COL_PostLocationAddr = "postLocationAddress";
    public static final String COL_PostLocationLat = "postLocationLat";
    public static final String COL_PostLocationLng = "postLocationLng";
    public static final String COL_IsDelete = "isDelete";

    public static final int ROW_COUNT_SMALL = 15;

    public static final int DELETE_FLG_Y = 1;
    public static final int DELETE_FLG_N = 0;

    public static final String  DESC_ORDER = "DESC";
    public static final String  ASC_ORDER = "ASC";

    SQLiteDatabase tmaDB;
    public static final String DB_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    "(" +
                    COL_PostID + " INTEGER NOT NULL CONSTRAINT tmaTravelPost_pk PRIMARY KEY AUTOINCREMENT, " +
                    COL_PostTitle + " VARCHAR(128), " +
                    COL_PostContent + " TEXT, " +
                    COL_PostDate + " TEXT, " +
                    COL_PostTime + " TEXT, " +
                    COL_PostImage + " BLOB, " +
                    COL_PostLocationName + " VARCHAR(60), " +
                    COL_PostLocationAddr + " VARCHAR(80), " +
                    COL_PostLocationLat + " TEXT, " +
                    COL_PostLocationLng + " TEXT ," +
                    COL_IsDelete + " BOOLEAN NOT NULL DEFAULT 0 " +
                    ");";
    public static final String DUMP_DATA =
            "INSERT INTO " + TABLE_NAME +" (" + COL_PostTitle +"," +COL_PostContent+"," +COL_PostDate+"," +COL_PostTime
                    +"," +COL_PostLocationName+"," +COL_IsDelete
                    +") VALUES ('FIRST NOTE','sample content','Mar 20, 2020', '03:22','Singapore',0)";
    public static final String DUMP_DATA2 =
            "INSERT INTO " + TABLE_NAME +" (" + COL_PostTitle +"," +COL_PostContent+"," +COL_PostDate+"," +COL_PostTime
                    +"," +COL_PostLocationName+"," +COL_IsDelete
                    +") VALUES ('SECOND NOTE','sample content','Mar 20, 2020', '05:22','Singapore',0)";
    //TODO: change the single table to multiple table (e.g. PostImageTable, PostLocationTable, etc. as necessary)


    public TakeMeAwayDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(DB_CREATE);
            db.execSQL(DUMP_DATA);
            db.execSQL(DUMP_DATA2);
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public SQLiteDatabase open() throws SQLException{
        tmaDB = getWritableDatabase();
        tmaDB.beginTransaction();
        return tmaDB;
    }


    public void commit(){
        tmaDB.setTransactionSuccessful();
    }

    public void close(){
        tmaDB.endTransaction();
        tmaDB.close();
    }

    public long insertNote(TMANote note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cvs = new ContentValues();

        cvs.put(COL_PostTitle, note.getTitle());
        cvs.put(COL_PostContent, note.getContent());
        cvs.put(COL_PostLocationName, note.getLocation());
        cvs.put(COL_PostDate, note.getDate());
        cvs.put(COL_PostTime, note.getTime());
        cvs.put(COL_PostImage,note.getImageByte());
        cvs.put(COL_PostLocationAddr, "");
        cvs.put(COL_PostLocationLat, "");
        cvs.put(COL_PostLocationLng, "");
        cvs.put(COL_IsDelete, DELETE_FLG_N);

        return db.insert(TABLE_NAME, null, cvs);
    }
    public int updateNote(int postID,TMANote note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cvs = new ContentValues();

        cvs.put(COL_PostTitle, note.getTitle());
        cvs.put(COL_PostContent, note.getContent());
        cvs.put(COL_PostLocationName, note.getLocation());
        cvs.put(COL_PostDate, note.getDate());
        cvs.put(COL_PostTime, note.getTime());
        cvs.put(COL_PostImage,note.getImageByte());
        return db.update(TABLE_NAME, cvs, COL_PostID + " = ?" , new String[]{String.valueOf(postID)});
    }


    public TMANote getNoteById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(
                TABLE_NAME, null, COL_PostID + " = "+id,
                null, null,
                null, null, null );
        if (res.getCount() > 0)  res.moveToFirst();
        TMANote note = new TMANote();
        note.setId(res.getInt(res.getColumnIndex(COL_PostID)));
        note.setTitle(res.getString(res.getColumnIndex(COL_PostTitle)));
        note.setContent(res.getString(res.getColumnIndex(COL_PostContent)));
        note.setLocation(res.getString(res.getColumnIndex(COL_PostLocationName)));
        note.setDate(res.getString(res.getColumnIndex(COL_PostDate)));
        note.setTime(res.getString(res.getColumnIndex(COL_PostTime)));
        byte[] imgByte  = res.getBlob(res.getColumnIndex(COL_PostImage));
        if(imgByte!=null) {
            Bitmap img = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            note.setImage(img);
        }
        return note;
    }
    public ArrayList<TMANote> getNoteList(int rowCount, String sortOrder){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(
                TABLE_NAME, null, COL_IsDelete + " = 0",
                null, null,
                null, COL_PostDate + ", "+COL_PostTime+" " + sortOrder, rowCount+"" );
        ArrayList<TMANote> note_list = new ArrayList<TMANote>();
        if (res.getCount() > 0) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                TMANote note = new TMANote();
                note.setId(res.getInt(res.getColumnIndex(COL_PostID)));
                note.setTitle(res.getString(res.getColumnIndex(COL_PostTitle)));
                note.setContent(res.getString(res.getColumnIndex(COL_PostContent)));
                note.setLocation(res.getString(res.getColumnIndex(COL_PostLocationName)));
                note.setDate(res.getString(res.getColumnIndex(COL_PostDate)));
                note.setTime(res.getString(res.getColumnIndex(COL_PostTime)));
                byte[] imgByte  = res.getBlob(res.getColumnIndex(COL_PostImage));
                if(imgByte!=null) {
                    Bitmap img = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    note.setImage(img);
                }
                note_list.add(note);
                res.moveToNext();
            }

        }
        return note_list;

    }
    public Cursor QueryRowsNotFlagDelete(int rowCount, String sortOrder){

        String rowCountStr = null;
        if(rowCount > 0){
            rowCountStr = String.valueOf(rowCount);
        }


        return tmaDB.query(
                TABLE_NAME, null, COL_IsDelete + " = 0",
                null, null,
                null, COL_PostDate + " " + sortOrder, rowCountStr );
    }



    public int FlagDeleteRow(int postID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cvs = new ContentValues();

        cvs.put(COL_IsDelete, 1);

        return db.update(TABLE_NAME, cvs, COL_PostID + " = ?" , new String[]{String.valueOf(postID)});
    }

    public int DeleteRow(int postID){
        return tmaDB.delete(TABLE_NAME, COL_PostID + " = ?" , new String[]{String.valueOf(postID)});
    }

}
