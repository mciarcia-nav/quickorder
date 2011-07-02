package it.quickorder.android;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter 
{
	private static final String DATABASE_NAME = "quickorder.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase db;
	private final Context context;
	private DbHelper dbHelper;
	
	public DBAdapter(Context context) 
	{
		this.context = context;
		dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DBAdapter open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() 
	{
		db.close();
	}
	
	private static class DbHelper extends SQLiteOpenHelper 
	{
		public DbHelper(Context context, String name, CursorFactory factory, int version) 
		{
			super(context, name, factory, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			String sql = "";
			sql = sql + "CREATE TABLE user (";
			sql = sql + " imei TEXT PRIMARY KEY,";
			sql = sql + " nome TEXT NOT NULL,";
			sql = sql + " cognome TEXT NOT NULL,";
			sql = sql + " cf TEXT NOT NULL,";
			sql = sql + " email TEXT NOT NULL,";
			sql = sql + " dataNascita DATETIME NOT NULL,";
			sql = sql + " luogoNascita TEXT NOT NULL,";
			sql = sql + " sesso TEXT NOT NULL,";
			sql = sql + " abilitato INTEGER NOT NULL";
			sql = sql + ")";
			db.execSQL(sql);
			String sql2 = "";
			sql2 += "CREATE TABLE prodotti (";
			sql2 += "codice TEXT PRIMARY KEY,";
			sql2 += "nome TEXT NOT NULL,";
			sql2 += "tipologia INTEGER NOT NULL,";
			sql2 += "prezzo DOUBLE NOT NULL,";
			sql2 += "versione INTEGER NOT NULL,";
			sql2 += "descrizione TEXT NOT NULL";
			sql2 += ")";
			db.execSQL(sql2);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS prodotti");
			db.execSQL("DROP TABLE IF EXISTS user");
			onCreate(db);
		}
	}
}


