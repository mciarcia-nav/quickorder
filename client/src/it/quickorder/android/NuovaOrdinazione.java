package it.quickorder.android;

import it.qwerty.android.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class NuovaOrdinazione extends TabActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordinazione);
		TabHost tabhost = getTabHost();
		Log.i("dopo tab", "si");
		TabSpec sceltaPanino = tabhost.newTabSpec("panino");
		TabSpec sceltaBibita = tabhost.newTabSpec("bibita");
		TabSpec riepilogo = tabhost.newTabSpec("riepilogo");
		sceltaPanino.setIndicator("PANINI").setContent(new Intent(this,SceltaPanino.class));
		sceltaBibita.setIndicator("BIBITE").setContent(new Intent(this,SceltaBibita.class));
		riepilogo.setIndicator("RIEPILOGO").setContent(new Intent(this,Riepilogo.class));
		tabhost.addTab(sceltaPanino);
		tabhost.addTab(sceltaBibita);
		tabhost.addTab(riepilogo);
		tabhost.setCurrentTab(0);
	}
}
