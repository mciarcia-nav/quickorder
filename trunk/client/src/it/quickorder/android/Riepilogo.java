package it.quickorder.android;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import it.quickorder.domain.Articolo;
import it.quickorder.domain.Cliente;
import it.quickorder.domain.Ordinazione;
import it.quickorder.android.R;
import android.R.attr;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Riepilogo extends Base implements OnClickListener, OnItemSelectedListener
{
	private ImageButton inviaOrdinazione;
	private ImageButton cancella;
	private Ordinazione ordinazione;
	private ScrollView scroll;
	private Set<Articolo> listaArticoli;
	private TableLayout tl;
	private TableRow row;
	private TextView text;
	private int numeroTavolo;
	private Spinner spinner;
	private String[] tavoli;
	private DecimalFormat formatoPrezzo;
	
	@Override
	public void onResume()
	{
		super.onResume();
		scroll = (ScrollView) findViewById(R.id.riepilogoSroll);
		if (ordinazione.getArticoli().size()==0)
			vuoto();
		else
			creaTabella();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.riepilogo);	
		formatoPrezzo = new DecimalFormat("#0.00");
		scroll = (ScrollView) findViewById(R.id.riepilogoSroll);
		ordinazione = ((NuovaOrdinazione)this.getParent()).getOrdinazione();
		if (ordinazione.getArticoli().size()==0)
			vuoto();
		else
			creaTabella();
	}
	
	@Override
	public void onClick(View v) 
	{
		
		if (v.getId() == 100)
		{
			// TODO Auto-generated method stub
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
					final AlertDialog alert = new AlertDialog.Builder(Riepilogo.this).create();
					alert.setTitle("Ordinazione");
					alert.setMessage("Ordinazione inviata con successo! Cosa vuoi fare?");
					alert.setButton("Nuova Ordinazione", new DialogInterface.OnClickListener() 
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							alert.dismiss();
							nuovaOrdinazione();
							finish();
						}

						
					});
					alert.setButton2("Esci",new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							alert.dismiss();
							finish();
						}
					});
					alert.show();
				}
				else
				{
					Toast t = Toast.makeText(getApplicationContext(), "Invio dell'ordinazione fallito.", Toast.LENGTH_SHORT);
					t.show();
					return;
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
			Articolo[] articoli = new Articolo[ordinazione.getArticoli().size()];
			ordinazione.getArticoli().toArray(articoli);
			Articolo a = articoli[posizione];
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
				Articolo aa = ordinazione.getArticolo(a.getProdotto());
				aa.setQuantita(quantita-1);
				aa.setSubTotale(subTotale - prezzoSingolo);
				ordinazione.setTotale(totale - prezzoSingolo);
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
	
	private void nuovaOrdinazione() 
	{
		Cliente cliente = ordinazione.getCliente();
		Intent intent = new Intent(this, NuovaOrdinazione.class);
		String pkg = getPackageName();
		intent.putExtra(pkg + ".cliente", cliente);
		startActivity(intent);
		finish();
	}
	
	private void creaTabella()
	{	
		scroll.removeAllViews();
		tl = new TableLayout(this);
		listaArticoli = ordinazione.getArticoli();
		//TITOLO
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		text = new TextView(this);
		text.setText("Riepilogo Ordinazione");
		text.setGravity(Gravity.CENTER);
		//text.setTextColor(android.R.color.);
		text.setPadding(0,0,0,30);
		text.setTextAppearance(this, android.R.style.TextAppearance_Large);
		row.addView(text);
		tl.addView(row);
		
		//INTESTAZIONE TABELLA
		row = new TableRow(this);
		row.setGravity(Gravity.LEFT);
		text = new TextView(this);
		text.setText("Q.  Prodotto");
		text.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		row.addView(text);
		text = new TextView(this);
		text.setText("Prezzo");
		text.setGravity(Gravity.LEFT);
		text.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		row.addView(text);
		tl.addView(row);
		
		//CARICAMENTO ARTICOLI
		Iterator<Articolo> articoli = listaArticoli.iterator();
		int index = 0;
		while (articoli.hasNext())
		{
			Articolo corrente = articoli.next();
			row = new TableRow(this);
			row.setGravity(Gravity.LEFT);
			text = new TextView(this);
			text.setText(Integer.toString(corrente.getQuantita()) + "     "+corrente.getProdotto().getNome());
			row.addView(text);
			text = new TextView(this);
			text.setText(formatoPrezzo.format(corrente.getSubTotale()) + " €");
			row.addView(text);
			
			cancella = new ImageButton(this);
			cancella.setImageResource(R.drawable.deleteicon);
			cancella.setPadding(0, 0, 3, 10);
			cancella.setOnClickListener(this);
			cancella.setId(200 + index);
			cancella.setBackgroundColor(android.R.color.transparent);
			row.addView(cancella);
			tl.addView(row);
			index++;
		}
		
		//PREZZO FINALE
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		text = new TextView(this);
		String html = "<html>Importo totale da pagare:<br>" + formatoPrezzo.format(ordinazione.getTotale()) + " €</html>";
		text.setText(Html.fromHtml(html));
		text.setGravity(Gravity.CENTER);
		text.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		text.setPadding(0, 15, 0, 15);
		row.addView(text);
		tl.addView(row);
		
		//SPINNER NUMERO TAVOLI
		row = new TableRow(this);
		row.setPadding(0, 0, 0, 5);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		text = new TextView(this);
		text.setGravity(Gravity.CENTER);
		text.setText(Html.fromHtml("<html>Seleziona il numero del tavolo<br>al quale sei seduto.</html>"));
		text.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		row.addView(text);
		tl.addView(row);
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		spinner = new Spinner(this);
		spinner.setOnItemSelectedListener(this);
		caricaSpinner();
		row.addView(spinner);
		tl.addView(row);
		
		//PULSANTE INVIA ORDINE
		inviaOrdinazione = new ImageButton(this);
		inviaOrdinazione.setId(100);
		inviaOrdinazione.setImageResource(R.drawable.send);
		inviaOrdinazione.setBackgroundColor(android.R.color.transparent);
		inviaOrdinazione.setOnClickListener(this);
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		row.addView(inviaOrdinazione);
		row.setPadding(0, 15, 0, 0);
		tl.addView(row);
	
		//ADD TABLE TO SCROLL
		scroll.addView(tl);
	}
	
	private void caricaSpinner()
	{
		tavoli = new String[10];
		for (int i=0;i<10;i++)
			tavoli[i] = "Tavolo "+Integer.toString(i+1);
		ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,tavoli);
		spinner.setAdapter(adapter);
		
	}
	
	private void vuoto()
	{
		scroll.removeAllViews();
		tl = new TableLayout(this);
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER);
		text = new TextView(this);
		text.setTextAppearance(this, android.R.style.TextAppearance_Large);
		text.setText("Nessun prodotto selezionato");
		row.addView(text);
		tl.addView(row);
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
