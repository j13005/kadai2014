package jp.massan.massan_dayo_plus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME ="myOkozukai.db";
	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_NAME_1 ="kaimono_table";
	private static final String TABLE_NAME_2 ="yosan_table";
	private static final String TABLE_NAME_3 ="photo_table";
	private static final String ID_yosan="id_yosan";
	private static final String ID_kaimono ="id_kaimono";
	private static final String ID_photo = "id_photo";
	private static final String YOSAN ="yosan";
	private static final String HI ="hi";
	private static final String SHINAMONO = "shinamono";
	private static final String BASYO ="basyo";
	private static final String KINGAKU ="kingaku";
	private static final String COLUMN_FILE_NAME = "fname";
	private static final String COLUMN_PHOTO_BINARY_DATA = "bdata";


	DatabaseHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		//買い物テーブル作成
		db.execSQL("create table "+TABLE_NAME_1 + "(" +ID_kaimono + " INTEGER PRIMARY KEY AUTOINCREMENT," + SHINAMONO +" TEXT,"+BASYO+" TEXT,"+KINGAKU+" INTEGER,"+HI+" TEXT);");
		//予算テーブル作成
		db.execSQL("create table "+TABLE_NAME_2 + "("+ ID_yosan + " INTEGER PRIMARY KEY AUTOINCREMENT,"+YOSAN+" INTEGER,"+HI+" TEXT);");
		//写真テーブル作成
		//db.execSQL("create table" +TABLE_NAME_3 + "("+ ID_photo + "INTEGER PRIMARY KEY AUTOINCREMENT,"+ COLUMN_FILE_NAME+" TEXT,"+COLUMN_PHOTO_BINARY_DATA+" BLOB);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists "+TABLE_NAME_1);
		db.execSQL("drop table if exists " + TABLE_NAME_2);
		db.execSQL("drop table if exists" + TABLE_NAME_3);
		onCreate(db);
	}
}
