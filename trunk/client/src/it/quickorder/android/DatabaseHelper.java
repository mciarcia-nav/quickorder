package it.quickorder.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{

	private static final String DB_NAME = "db";
	private static final int DB_VERSION = 1;
	
	public DatabaseHelper(Context context) 
	{
		super(context, DB_NAME, null, DB_VERSION);
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
		popolaDB(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		
	}

	public void popolaDB(SQLiteDatabase db)
	{
		ContentValues values = new ContentValues();
		values.put("codice", "P002");
		values.put("nome", "Crudino");
		values.put("tipologia", "0");
		values.put("prezzo", 3.00);
		values.put("versione", 0);
		values.put("descrizione", "Un gustoso panino con prosciutto crudo e formaggio. Adatto per tutti i palati!");
		db.insert("prodotti", null, values);
		values.put("codice", "P003");
		values.put("nome", "Caprese");
		values.put("tipologia", "0");
		values.put("prezzo", 3.50);
		values.put("versione", 0);
		values.put("descrizione", "Un gustoso panino con pomodorini e mozzarella. Ideale per i mediterranei!");
		db.insert("prodotti", null, values);
		values.put("codice", "B004");
		values.put("nome", "Acqua Naturale 0,5L");
		values.put("tipologia", "1");
		values.put("prezzo", 0.80);
		values.put("versione", 0);
		values.put("descrizione", "E' acqua!");
		db.insert("prodotti", null, values);
	}
	
}
