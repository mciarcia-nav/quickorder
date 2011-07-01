package it.quickorder.android;

import it.quickorder.domain.Ordinazione;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Base extends Activity 
{
	public static final String SRV_ADDRESS = "10.0.2.2";
	public static final int UPD_PORT = 4445;
	public static final int SIGNUP_PORT = 4446;
	
	
	public DatabaseHelper dbhelper;
	public SQLiteDatabase db;
	public Ordinazione ordinazione;
	
	public void init(Context c)
	{
		dbhelper = new DatabaseHelper(c);
        db = dbhelper.getWritableDatabase();
	}
	
	public void nuovaOrdinazione()
	{
		ordinazione = new Ordinazione();
	}
}
