package it.quickorder.android;

import java.util.List;
import it.quickorder.domain.Articolo;
import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SceltaBibita extends Base implements OnClickListener
{
	private int posizione;
	private List<Prodotto> listaBibite;
	private ImageButton nextBibita;
	private ImageButton prevBibita;
	private ImageButton quantitaMinusBibita;
	private ImageButton quantitaPlusBibita;
	private ImageButton aggiungiBibita;
	private TextView prezzoBibita;
	private TextView nomeBibita;
	private TextView descrizioneBibita;
	private TextView quantitaBibita;
	private ImageView imageBibita;
	private TextView labelTotale;
	private Ordinazione ordinazione;
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutsceltabibita);
		posizione = 0;
		ordinazione = ((NuovaOrdinazione)this.getParent()).getOrdinazione();
		nextBibita = (ImageButton) findViewById(R.id.nextBibita);
        prevBibita = (ImageButton) findViewById(R.id.prevBibita);
        aggiungiBibita = (ImageButton) findViewById(R.id.aggiungiBibita);
        quantitaMinusBibita = (ImageButton) findViewById(R.id.minusBibita);
        quantitaMinusBibita.setEnabled(false);
        labelTotale = (TextView) findViewById(R.id.labelTotale);
        quantitaPlusBibita = (ImageButton) findViewById(R.id.plusBibita);
        prezzoBibita = (TextView) findViewById(R.id.prezzoBibita);
        nomeBibita = (TextView) findViewById(R.id.nomeBibita);
        descrizioneBibita = (TextView) findViewById(R.id.descrizioneBibita);
        quantitaBibita = (TextView) findViewById(R.id.labelQuantitaBibita);
        imageBibita = (ImageView) findViewById(R.id.immagineBibita);
        
        nextBibita.setOnClickListener(this);
        prevBibita.setOnClickListener(this);
        quantitaMinusBibita.setOnClickListener(this);
        quantitaPlusBibita.setOnClickListener(this);
        aggiungiBibita.setOnClickListener(this);
        ordinazione = ((NuovaOrdinazione)this.getParent()).getOrdinazione();
        listaBibite = dbAdapter.caricaDatiProdotti(Prodotto.BEVANDA);
        posizione = 0;
        aggiornaInformazioniBibita(posizione);
        labelTotale.setText("€ " + Double.toString(ordinazione.getTotale()));
	}
	
	private void aggiornaInformazioniBibita(int posizioneLista)
	{
		Prodotto corrente = listaBibite.get(posizioneLista);
		nomeBibita.setText(corrente.getNome());
		descrizioneBibita.setText(corrente.getDescrizione());
		prezzoBibita.setText("Prezzo: € "+Double.toString(corrente.getPrezzo()));
		if (ordinazione.containsProdotto(corrente))
			quantitaBibita.setText("" + ordinazione.getArticolo(corrente).getQuantita());
		else
			quantitaBibita.setText("0");
		//String app = corrente.getNome().toLowerCase();
		//int idImage = getResources().getIdentifier(app, "drawable", "it.qwerty.android");
		//imageBibita.setImageResource(idImage);
	}

	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.nextBibita) // PANINO SUCCESSIVO
		{
			posizione = (posizione + 1) % listaBibite.size();
			aggiornaInformazioniBibita(posizione);
		}
		else if (v.getId() == R.id.prev) // PANINO PRECEDENTE
		{			
			posizione = (posizione + listaBibite.size() - 1) % listaBibite.size();
			aggiornaInformazioniBibita(posizione);
		}
		else if (v.getId() == R.id.plus) // AGGIUNGI PANINO
		{
			int q = Integer.parseInt(quantitaBibita.getText().toString()) +  1;
			quantitaBibita.setText("" + q);
			if (q == 1)
				quantitaMinusBibita.setEnabled(true);
		}
		else if (v.getId() == R.id.minus) // RIMOUVI PANINO
		{
			int q = Integer.parseInt(quantitaBibita.getText().toString()) - 1;
			quantitaBibita.setText("" + q);
			if (q == 0)
				quantitaMinusBibita.setEnabled(false);		
		}
		else if (v.getId() == R.id.aggiungi)
		{
			Articolo nuovo = new Articolo();
			int q = Integer.parseInt(quantitaBibita.getText().toString());
			Prodotto selezionato = listaBibite.get(posizione);
			nuovo.setSubTotale(q * selezionato.getPrezzo());
			nuovo.setProdotto(selezionato);
			nuovo.setQuantita(q);
			ordinazione.aggiungiArticolo(nuovo);
			labelTotale.setText("€ " + Double.toString(ordinazione.getTotale()));		
		}
		
	}
}
