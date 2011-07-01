package it.quickorder.android;

import it.quickorder.domain.Ordinazione;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Base extends Activity 
{
	public static final String SRV_ADDRESS = "10.0.2.2";
	public static final int UPD_PORT = 4445;
	public static final int SIGNUP_PORT = 4446;
	
	
	public DatabaseHelper dbhelper;
	public SQLiteDatabase db;
	
	public void init(Context c, String mode)
	{
		Log.i("DB","CREO DATABASE");
		if (dbhelper == null)
			dbhelper = new DatabaseHelper(c);
		if (mode.equals("write"))
			db = dbhelper.getWritableDatabase();
		else
			db = dbhelper.getReadableDatabase();
		Log.i("DB","CREATO DATABASE");
	}
	
	public void close()
	{
		Log.i("DB","CHIUDO DATABASE");
		db.close();
		dbhelper.close();
		Log.i("DB","DATABASE CHIUSO");
		
	}
}
