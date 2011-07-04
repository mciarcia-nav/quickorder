package it.quickorder.android;

import it.quickorder.domain.Articolo;
import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;
import java.util.List;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
			
			setContentView(R.layout.layoutsceltapanino);
            next = (ImageButton) findViewById(R.id.next);
            prev = (ImageButton) findViewById(R.id.prev);
            aggiungi = (ImageButton) findViewById(R.id.aggiungi);
            aggiungi.setEnabled(false);
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
            listaPanini = dbAdapter.caricaDatiProdotti(Prodotto.PANINO);
            posizione = 0;
            aggiornaInformazioniPanino(posizione);
            labelTotale.setText("€ " + Double.toString(ordinazione.getTotale()));
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
			{
				quantitaMinus.setEnabled(true);
				aggiungi.setEnabled(true);
			}
		}
		else if (v.getId() == R.id.minus) // RIMOUVI PANINO
		{
			int q = Integer.parseInt(quantita.getText().toString()) - 1;
			quantita.setText("" + q);
			if (q == 0)
			{
				quantitaMinus.setEnabled(false);
				aggiungi.setEnabled(false);
			}
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
			labelTotale.setText("€ " + Double.toString(ordinazione.getTotale()));
			aggiungi.setImageResource(R.drawable.refresh);
		}
	}
		
	private void aggiornaInformazioniPanino(int posizioneLista)
	{
		Prodotto corrente = listaPanini.get(posizioneLista);
		nomePanino.setText(corrente.getNome());
		descrizionePanino.setText(corrente.getDescrizione());
		prezzoPanino.setText("Prezzo: € "+Double.toString(corrente.getPrezzo()));
		if (ordinazione.containsProdotto(corrente))
		{
			aggiungi.setImageResource(R.drawable.refresh);
			quantita.setText("" + ordinazione.getArticolo(corrente).getQuantita());
			aggiungi.setEnabled(true);
			quantitaMinus.setEnabled(true);
		}
		else
		{
			aggiungi.setImageResource(R.drawable.shopping_cart_add);
			quantita.setText("0");
			aggiungi.setEnabled(false);
			quantitaMinus.setEnabled(false);
		}
		String app = corrente.getNome().toLowerCase();
		int idImage = getResources().getIdentifier(app, "drawable", "it.qwerty.android");
		Log.i("IDPANINO",Integer.toString(idImage));
		image.setImageResource(idImage);
	}
}
	 
	

