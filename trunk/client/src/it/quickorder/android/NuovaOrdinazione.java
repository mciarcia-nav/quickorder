package it.quickorder.android;

import it.quickorder.domain.Cliente;
import it.quickorder.domain.Ordinazione;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class NuovaOrdinazione extends TabActivity
{
	private Cliente cliente;
	private Ordinazione ordinazione;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		String pkg = getPackageName();
		cliente = (Cliente) getIntent().getSerializableExtra(pkg + ".cliente");
		ordinazione = new Ordinazione();
		ordinazione.setCliente(cliente);
		
		setContentView(R.layout.ordinazione);
		TabHost tabhost = getTabHost();
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

	public Ordinazione getOrdinazione() 
	{
		return ordinazione;
	}

	public void setOrdinazione(Ordinazione ordinazione) 
	{
		this.ordinazione = ordinazione;
	}
	
	
}
