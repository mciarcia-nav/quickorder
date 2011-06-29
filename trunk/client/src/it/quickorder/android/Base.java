package it.quickorder.android;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Base extends Activity 
{
	public DatabaseHelper dbhelper;
	public SQLiteDatabase db;
	
	public void init(Context c)
	{
		dbhelper = new DatabaseHelper(c);
        db = dbhelper.getWritableDatabase();
	}
}
