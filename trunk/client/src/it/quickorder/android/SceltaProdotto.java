package it.quickorder.android;

import it.quickorder.domain.Articolo;
import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;

import java.text.DecimalFormat;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SceltaProdotto extends Base implements OnClickListener
{	
	private static final String imagesPath = "/data/data/it.quickorder.android/files/";
	private int posizione;
	private List<Prodotto> listaProdotti;
	private ImageButton next;
	private ImageButton prev;
	private ImageButton quantitaMinus;
	private ImageButton quantitaPlus;
	private ImageButton aggiungi;
	private TextView prezzoProdotto;
	private TextView nomeProdotto;
	private TextView descrizioneProdotto;
	private TextView quantita;
	private ImageView image;
	private TextView labelTotale;
	private Ordinazione ordinazione;
	private int tipologia;
	private DecimalFormat formatoPrezzo;
	private ImageButton nota;
	private String notaDaScrivere ="";
	
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
		labelTotale.setText("€ " + formatoPrezzo.format(ordinazione.getTotale()));
	}
	
	@Override
	  public void onCreate(Bundle savedInstanceState)  
	  {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.layoutsceltaprodotto);
			String pkg = getPackageName();
			tipologia = getIntent().getIntExtra(pkg + ".tipologia", -1);
			formatoPrezzo = new DecimalFormat("#0.00");
            next = (ImageButton) findViewById(R.id.next);
            prev = (ImageButton) findViewById(R.id.prev);
            aggiungi = (ImageButton) findViewById(R.id.aggiungi);
            aggiungi.setEnabled(false);
            quantitaMinus = (ImageButton) findViewById(R.id.minus);
            quantitaMinus.setEnabled(false);
            labelTotale = (TextView) findViewById(R.id.labelTotale);
            quantitaPlus = (ImageButton) findViewById(R.id.plus);
            prezzoProdotto = (TextView) findViewById(R.id.prezzo);
            nomeProdotto = (TextView) findViewById(R.id.nome);
            descrizioneProdotto = (TextView) findViewById(R.id.descrizione);
            quantita = (TextView) findViewById(R.id.labelQuantita);
            image = (ImageView) findViewById(R.id.immagine);
            nota = (ImageButton) findViewById(R.id.nota);
            nota.setEnabled(false);
            // Impostazione Listeners
            next.setOnClickListener(this);
            prev.setOnClickListener(this);
            quantitaMinus.setOnClickListener(this);
            quantitaPlus.setOnClickListener(this);
            aggiungi.setOnClickListener(this);
            nota.setOnClickListener(this);
            ordinazione = ((NuovaOrdinazione)this.getParent()).getOrdinazione();
            listaProdotti = dbAdapter.caricaDatiProdotti(tipologia);
            if (tipologia != 0)
            	nota.setVisibility(4);
            posizione = 0;
            aggiornaInformazioniProdotto(posizione);
            labelTotale.setText("€ " + formatoPrezzo.format(ordinazione.getTotale()));
    }


	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.next) // Prodotto SUCCESSIVO
		{
			posizione = (posizione + 1) % listaProdotti.size();
			aggiornaInformazioniProdotto(posizione);
		}
		else if (v.getId() == R.id.prev) // Prodotto PRECEDENTE
		{			
			posizione = (posizione + listaProdotti.size() - 1) % listaProdotti.size();
			aggiornaInformazioniProdotto(posizione);
		}
		else if (v.getId() == R.id.plus) // Aggiungi Prodotto
		{
			int q = Integer.parseInt(quantita.getText().toString()) +  1;
			quantita.setText("" + q);
			if (q == 1)
			{
				quantitaMinus.setEnabled(true);
				aggiungi.setEnabled(true);
				nota.setEnabled(true);
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
				nota.setEnabled(false);
			}
		}
		else if (v.getId() == R.id.aggiungi)
		{
			Prodotto selezionato = listaProdotti.get(posizione);
			Articolo esistente = ordinazione.getArticolo(selezionato);
			if (esistente != null)
			{
				ordinazione.rimuoviArticolo(selezionato);
			}
					
			Articolo nuovo = new Articolo();
			int q = Integer.parseInt(quantita.getText().toString());				
			nuovo.setSubTotale(((double) q) * selezionato.getPrezzo());
			nuovo.setProdotto(selezionato);
			nuovo.setQuantita(q);
			nuovo.setNote(notaDaScrivere);
			ordinazione.aggiungiArticolo(nuovo);
			labelTotale.setText("€ " + formatoPrezzo.format(ordinazione.getTotale()));
			aggiungi.setImageResource(R.drawable.refresh);
		}
		else if (v.getId() == R.id.nota)
		{
			notaDaScrivere = "";
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Note");
			final EditText editNota = new EditText(this);
			editNota.setHint("Inserisci eventuali note per il panino");
			alert.setView(editNota);
			alert.setPositiveButton("Aggiungi Nota", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
				  notaDaScrivere = editNota.getText().toString();
				  return;
				}
			});

			alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					return;
				}
			});
			alert.show();
		}
	}
		
	private void aggiornaInformazioniProdotto(int posizioneLista)
	{
		Prodotto corrente = listaProdotti.get(posizioneLista);
		nomeProdotto.setText(corrente.getNome());
		descrizioneProdotto.setText(corrente.getDescrizione());
		prezzoProdotto.setText("Prezzo: € "+ formatoPrezzo.format(corrente.getPrezzo()));
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
		
		Bitmap bm = BitmapFactory.decodeFile(imagesPath + corrente.getCodice() + ".jpg");
		image.setImageBitmap(bm);
	}
}
	 
	

