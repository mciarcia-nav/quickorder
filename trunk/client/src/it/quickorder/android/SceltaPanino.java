package it.quickorder.android;

import it.quickorder.domain.Articolo;
import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;
import it.qwerty.android.R;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SceltaPanino extends Base implements OnClickListener
{	
	private int posizione;
	private List<Prodotto> listaPanini;
	private ImageButton next;
	private ImageButton prev;
	private ImageButton quantitaMinus;
	private ImageButton quantitaPlus;
	private ImageButton aggiungi;
	private TextView prezzoPanino;
	private TextView nomePanino;
	private TextView descrizionePanino;
	private TextView quantita;
	private ImageView image;
	private TextView labelTotale;
	private Ordinazione ordinazione;
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		close();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		close();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		close();
	}
	
	@Override
	  public void onCreate(Bundle savedInstanceState)  
	  {
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.layoutsceltapanino);
            next = (ImageButton) findViewById(R.id.next);
            prev = (ImageButton) findViewById(R.id.prev);
            aggiungi = (ImageButton) findViewById(R.id.aggiungi);
            quantitaMinus = (ImageButton) findViewById(R.id.minus);
            quantitaMinus.setEnabled(false);
            labelTotale = (TextView) findViewById(R.id.labelTotale);
            quantitaPlus = (ImageButton) findViewById(R.id.plus);
            prezzoPanino = (TextView) findViewById(R.id.prezzo);
            nomePanino = (TextView) findViewById(R.id.nome);
            descrizionePanino = (TextView) findViewById(R.id.descrizione);
            quantita = (TextView) findViewById(R.id.labelQuantita);
            image = (ImageView) findViewById(R.id.immagine);
            image.setImageResource(R.drawable.panino);
            image.setAdjustViewBounds(true);
            
            // Impostazione Listeners
            next.setOnClickListener(this);
            prev.setOnClickListener(this);
            quantitaMinus.setOnClickListener(this);
            quantitaPlus.setOnClickListener(this);
            aggiungi.setOnClickListener(this);
            ordinazione = ((NuovaOrdinazione)this.getParent()).getOrdinazione();
            init(this,"read");
            caricaDatiPanini();
            posizione = 0;
            aggiornaInformazioniPanino(posizione);
            labelTotale.setText("€ " + Double.toString(ordinazione.getTotale()));
            close();
    }


	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.next) // PANINO SUCCESSIVO
		{
			posizione = (posizione + 1) % listaPanini.size();
			aggiornaInformazioniPanino(posizione);
		}
		else if (v.getId() == R.id.prev) // PANINO PRECEDENTE
		{			
			posizione = (posizione + listaPanini.size() - 1) % listaPanini.size();
			aggiornaInformazioniPanino(posizione);
		}
		else if (v.getId() == R.id.plus) // AGGIUNGI PANINO
		{
			int q = Integer.parseInt(quantita.getText().toString()) +  1;
			quantita.setText("" + q);
			if (q == 1)
				quantitaMinus.setEnabled(true);
		}
		else if (v.getId() == R.id.minus) // RIMOUVI PANINO
		{
			int q = Integer.parseInt(quantita.getText().toString()) - 1;
			quantita.setText("" + q);
			if (q == 0)
				quantitaMinus.setEnabled(false);		
		}
		else if (v.getId() == R.id.aggiungi)
		{
			Articolo nuovo = new Articolo();
			int q = Integer.parseInt(quantita.getText().toString());
			Prodotto selezionato = listaPanini.get(posizione);
			nuovo.setSubTotale(q * selezionato.getPrezzo());
			nuovo.setProdotto(selezionato);
			nuovo.setQuantita(q);
			ordinazione.aggiungiArticolo(nuovo);
			labelTotale.setText("€ " + Double.toString(ordinazione.getTotale()));		}
	}
	
	private void caricaDatiPanini()
	{
		listaPanini = new ArrayList<Prodotto>();
		String selection = "tipologia = ?";
		String[] selectionArgs = { "0" };
		Cursor cursor = db.query("prodotti", null, selection, selectionArgs, null, null, null);
		while(cursor.moveToNext())
		{
			Prodotto p = new Prodotto();
			p.setCodice(cursor.getString(0));
			p.setNome(cursor.getString(1));
			p.setTipologia(0);
			p.setPrezzo(Double.parseDouble(cursor.getString(3)));
			p.setDescrizione(cursor.getString(cursor.getColumnIndex("descrizione")));
			listaPanini.add(p);
		}
	}
	
	private void aggiornaInformazioniPanino(int posizioneLista)
	{
		Prodotto corrente = listaPanini.get(posizioneLista);
		nomePanino.setText(corrente.getNome());
		descrizionePanino.setText(corrente.getDescrizione());
		prezzoPanino.setText("Prezzo: € "+Double.toString(corrente.getPrezzo()));
		if (ordinazione.containsProdotto(corrente))
			quantita.setText("" + ordinazione.getArticolo(corrente).getQuantita());
		else
			quantita.setText("0");
		String app = corrente.getNome().toLowerCase();
		int idImage = getResources().getIdentifier(app, "drawable", "it.qwerty.android");
		Log.i("IDPANINO",Integer.toString(idImage));
		image.setImageResource(idImage);
	}
}
	 
	

