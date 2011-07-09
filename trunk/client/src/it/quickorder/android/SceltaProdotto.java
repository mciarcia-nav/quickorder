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
			nuovo.setNote(new String());
			ordinazione.aggiungiArticolo(nuovo);
			labelTotale.setText("€ " + formatoPrezzo.format(ordinazione.getTotale()));
			aggiungi.setImageResource(R.drawable.aggiornaprodottoicon);
			nota.setEnabled(true);
		}
		else if (v.getId() == R.id.nota)
		{
			Prodotto selezionato = listaProdotti.get(posizione);
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Aggiungi nota per " + selezionato.getNome());
			final EditText editNota = new EditText(this);
			final Articolo esistente = ordinazione.getArticolo(selezionato);
			editNota.setText(esistente.getNote());
			editNota.setHint("Scrivi qui eventuali modifiche da apportare..");
			alert.setView(editNota);
			if (esistente.getNote().equalsIgnoreCase(""))
				alert.setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
					  esistente.setNote(editNota.getText().toString());
					  return;
					}
				});
			else
				alert.setPositiveButton("Modifica", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						esistente.setNote(editNota.getText().toString());
					  return;
					}
				});
			alert.setNegativeButton("Cancella", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					esistente.setNote("");
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
			aggiungi.setImageResource(R.drawable.aggiornaprodottoicon);
			quantita.setText("" + ordinazione.getArticolo(corrente).getQuantita());
			aggiungi.setEnabled(true);
			quantitaMinus.setEnabled(true);
			nota.setEnabled(true);
		}
		else
		{
			aggiungi.setImageResource(R.drawable.aggiungiprodotto);
			quantita.setText("0");
			aggiungi.setEnabled(false);
			quantitaMinus.setEnabled(false);
			nota.setEnabled(false);
		}
		
		Bitmap bm = BitmapFactory.decodeFile(imagesPath + corrente.getCodice() + ".jpg");
		image.setImageBitmap(bm);
	}
}
	 
	

