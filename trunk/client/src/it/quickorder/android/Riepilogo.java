package it.quickorder.android;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import it.quickorder.domain.Articolo;
import it.quickorder.domain.Ordinazione;
import it.quickorder.android.R;
import android.R.attr;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Riepilogo extends Base implements OnClickListener, OnItemSelectedListener
{
	private Button inviaOrdinazione;
	private ImageButton cancella;
	private Ordinazione ordinazione;
	private ScrollView scroll;
	private ArrayList<Articolo> listaArticoli;
	private TableLayout tl;
	private TableRow row;
	private TextView text;
	private int numeroTavolo;
	private Spinner spinner;
	private String[] tavoli;
	
	@Override
	public void onResume()
	{
		super.onResume();
		scroll = (ScrollView) findViewById(R.id.riepilogoSroll);
		scroll.removeAllViews();
		if (ordinazione.getArticoli().size()==0)
		{
			Log.i("dove","if");
			vuoto();
		}
		else
		{
			Log.i("dove","else");
			creaTabella();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.riepilogo);	
		scroll = (ScrollView) findViewById(R.id.riepilogoSroll);
		ordinazione = ((NuovaOrdinazione)this.getParent()).getOrdinazione();
		Log.i("ordinazione",Integer.toString(ordinazione.getArticoli().size()));
		if (ordinazione.getArticoli().size()==0)
		{		
			vuoto();
		}
		else
		{
			creaTabella();
		}
	}
	
	@Override
	public void onClick(View v) 
	{
		
		if (v.getId() == 100)
		{
			try
			{
				
				ordinazione.setNumeroTavolo(numeroTavolo);
				Socket socket = new Socket(SRV_ADDRESS, ORDERS_PORT);
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				output.writeObject(ordinazione);
				output.flush();
				int response = input.readInt();
				
				if (response == 0)
				{
					Toast t = Toast.makeText(getApplicationContext(), "Ordinazione inviata con successo.", Toast.LENGTH_SHORT);
					t.show();
				}
				else
				{
					Toast t = Toast.makeText(getApplicationContext(), "Invio dell'ordinazione fallito.", Toast.LENGTH_SHORT);
					t.show();
				}
				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
		else //BOTTONE CANCELLA
		{
			int posizione = v.getId() - 200;
			ArrayList<Articolo> listaArticoli = ordinazione.getArticoli();
			Articolo a = listaArticoli.get(posizione);
			double totale = ordinazione.getTotale();
			double prezzoSingolo = a.getProdotto().getPrezzo();
			double subTotale = a.getSubTotale();
			int quantita = a.getQuantita();
			Log.i("quantita", Integer.toString(quantita));
			if (quantita == 1)
			{
				Log.i("rimouvi", "tutto");
				ordinazione.rimuoviArticolo(a.getProdotto());
			}	
			else
			{
				Log.i("rimuovi","singolo");
				ordinazione.getArticoli().get(posizione).setQuantita(quantita-1);
				ordinazione.getArticoli().get(posizione).setSubTotale(subTotale - prezzoSingolo);
				ordinazione.setTotale(totale-prezzoSingolo);
				ordinazione.rimuoviSingoloProdotto();
			}
			if (ordinazione.getArticoli().size()==0)
			{
				vuoto();
			}
			else
			{
				creaTabella();
			}
		}
		
	}
	
	private void creaTabella()
	{
		scroll.removeAllViews();
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tl = new TableLayout(this);
		listaArticoli = ordinazione.getArticoli();
		
		//TITOLO
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		text = new TextView(this);
		text.setText("Riepilogo Ordinazione");
		text.setTextAppearance(this, android.R.attr.textAppearanceLarge);
		row.addView(text);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//INTESTAZIONE TABELLA
		row = new TableRow(this);
		text = new TextView(this);
		text.setText("Nome Articolo");
		text.setTextAppearance(this, android.R.attr.textAppearanceMedium);
		//text.setLayoutParams(lp);
		row.addView(text);
		text = new TextView(this);
		text.setText("Quantità");
		text.setTextAppearance(this, android.R.attr.textAppearanceMedium);
		//text.setLayoutParams(lp);
		row.addView(text);
		text = new TextView(this);
		text.setText("Prezzo");
		text.setTextAppearance(this, android.R.attr.textAppearanceMedium);
		//text.setLayoutParams(lp);
		row.addView(text);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//CARICAMENTO ARTICOLI
		for (int i = 0; i<listaArticoli.size(); i++)
		{
			row = new TableRow(this);
			text = new TextView(this);
			text.setText(listaArticoli.get(i).getProdotto().getNome());
			//text.setLayoutParams(lp);
			row.addView(text);
			text = new TextView(this);
			text.setText(Integer.toString(listaArticoli.get(i).getQuantita()));
			text.setGravity(Gravity.CENTER);
			//text.setLayoutParams(lp);
			row.addView(text);
			text = new TextView(this);
			text.setText(Double.toString(listaArticoli.get(i).getSubTotale())+"€");
			//text.setLayoutParams(lp);
			row.addView(text);
			cancella = new ImageButton(this);
			cancella.setImageResource(R.drawable.delete);
			cancella.setOnClickListener(this);
			cancella.setId(200+i);
			row.addView(cancella);
			tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		//PREZZO FINALE
		row = new TableRow(this);
		text = new TextView(this);
		text.setText("Totale da pagare: ");
		//text.setLayoutParams(lp);
		row.addView(text);
		text = new TextView(this);
		text.setText(Double.toString(ordinazione.getTotale())+"€");
		//text.setLayoutParams(lp);
		row.addView(text);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//SPINNER NUMERO TAVOLI
		row = new TableRow(this);
		text = new TextView(this);
		text.setText("Numero tavolo: ");
		row.addView(text);
		spinner = new Spinner(this);
		spinner.setOnItemSelectedListener(this);
		caricaSpinner();
		row.addView(spinner);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//PULSANTE INVIA ORDINE
		inviaOrdinazione = new Button(this);
		inviaOrdinazione.setId(100);
		inviaOrdinazione.setText("Invia Ordinazione");
		inviaOrdinazione.setOnClickListener(this);
		inviaOrdinazione.setGravity(Gravity.CENTER);
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		row.addView(inviaOrdinazione);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//ADD TABLE TO SCROLL
		scroll.addView(tl);
	}
	
	private void caricaSpinner()
	{
		tavoli = new String[10];
		for (int i=0;i<10;i++)
			tavoli[i] = "No. "+Integer.toString(i+1);
		ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,tavoli);
		spinner.setAdapter(adapter);
		
	}
	
	private void vuoto()
	{
		scroll.removeAllViews();
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tl = new TableLayout(this);
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		text = new TextView(this);
		text.setText("Nessun prodotto selezionato");
		row.addView(text);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		scroll.addView(tl);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) 
	{
		numeroTavolo = spinner.getSelectedItemPosition()+1;
		Log.i("tavolo", Integer.toString(numeroTavolo));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) 
	{
		
	}
	
}
