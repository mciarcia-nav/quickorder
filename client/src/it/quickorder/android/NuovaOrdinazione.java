package it.quickorder.android;

import it.quickorder.domain.Cliente;
import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;
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
		TabSpec sceltaAntipasto = tabhost.newTabSpec("antipasto");
		TabSpec sceltaPrimo = tabhost.newTabSpec("primo");
		TabSpec sceltaSecondo = tabhost.newTabSpec("secondo");
		TabSpec sceltaDessert = tabhost.newTabSpec("dessert");
		TabSpec sceltaBibita = tabhost.newTabSpec("bibita");
		TabSpec riepilogo = tabhost.newTabSpec("riepilogo");
		sceltaAntipasto.setIndicator("Antipasto", getResources().getDrawable(R.drawable.antipasto));
		sceltaPrimo.setIndicator("Primo", getResources().getDrawable(R.drawable.primo));
		sceltaSecondo.setIndicator("Secondo", getResources().getDrawable(R.drawable.secondo));
		sceltaDessert.setIndicator("Dessert", getResources().getDrawable(R.drawable.dessert));

		sceltaBibita.setIndicator("Bevande", getResources().getDrawable(R.drawable.tabbevande));
		riepilogo.setIndicator("Riepilogo", getResources().getDrawable(R.drawable.tabriepilogo));		
		
		// Creazione degli Intent per panini e bevande.
		Intent intentSceltaAntipasto = new Intent(this, SceltaProdotto.class);
		intentSceltaAntipasto.putExtra(pkg + ".tipologia", Prodotto.Antipasto);
		
		Intent intentSceltaPrimi = new Intent(this, SceltaProdotto.class);
		intentSceltaPrimi.putExtra(pkg + ".tipologia", Prodotto.Primi);
		
		Intent intentSceltaSecondo = new Intent(this, SceltaProdotto.class);
		intentSceltaSecondo.putExtra(pkg + ".tipologia", Prodotto.Secondi);
		
		Intent intentSceltaDessert = new Intent(this, SceltaProdotto.class);
		intentSceltaDessert.putExtra(pkg + ".tipologia", Prodotto.Dessert);
		
		Intent intentSceltaBibita = new Intent(this, SceltaProdotto.class);
		intentSceltaBibita.putExtra(pkg + ".tipologia", Prodotto.Bevande);
		
		sceltaAntipasto.setContent(intentSceltaAntipasto);
		sceltaPrimo.setContent(intentSceltaPrimi);
		sceltaSecondo.setContent(intentSceltaSecondo);	
		sceltaDessert.setContent(intentSceltaDessert);	
		sceltaBibita.setContent(intentSceltaBibita);
		riepilogo.setContent(new Intent(this,Riepilogo.class));
		
		tabhost.addTab(sceltaAntipasto);
		tabhost.addTab(sceltaPrimo);
		tabhost.addTab(sceltaSecondo);
		tabhost.addTab(sceltaDessert);
		
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
