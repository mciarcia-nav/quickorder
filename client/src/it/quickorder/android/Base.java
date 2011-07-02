package it.quickorder.android;

import it.quickorder.helpers.DBAdapter;
import android.app.Activity;
import android.os.Bundle;

public class Base extends Activity 
{
	public static final String SRV_ADDRESS = "10.0.2.2";
	public static final int UPD_PORT = 4445;
	public static final int SIGNUP_PORT = 4446;
	protected DBAdapter dbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		dbAdapter = new DBAdapter(this);
		dbAdapter.open();
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		dbAdapter.close();
	}
}
