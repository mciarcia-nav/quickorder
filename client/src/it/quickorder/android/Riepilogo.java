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
	private Button inviaOrdinazione;
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
		formatoPrezzo = new DecimalFormat("#0.00");
		//scroll = (ScrollView) findViewById(R.id.riepilogoSroll);
		tl = (TableLayout) findViewById(R.id.tableRiepilogo);
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
		Intent i = new Intent(this,NuovaOrdinazione.class);
		String pkg = getPackageName();
		Cliente cliente = ordinazione.getCliente();
		i.putExtra(pkg + ".cliente", cliente);
		startActivity(i);
	}
	
	private void creaTabella()
	{
		tl.removeAllViews();
		//tl.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listaArticoli = ordinazione.getArticoli();
		
		//TITOLO
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		text = new TextView(this);
		text.setText("Riepilogo Ordinazione");
		text.setPadding(0,0,0,30);
		text.setTextAppearance(this, android.R.style.TextAppearance_Large);
		row.addView(text);
		//tl.addView(row);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//INTESTAZIONE TABELLA
		row = new TableRow(this);
		text = new TextView(this);
		text.setText("Nome Articolo");
		//text.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		//text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		row.addView(text);
		text = new TextView(this);
		text.setText("Quantità");
		//text.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		//text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		row.addView(text);
		text = new TextView(this);
		text.setText("Prezzo");
		//text.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		//text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		row.addView(text);
		//tl.addView(row);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//CARICAMENTO ARTICOLI
		Iterator<Articolo> articoli = listaArticoli.iterator();
		int index = 0;
		while (articoli.hasNext())
		{
			Articolo corrente = articoli.next();
			row = new TableRow(this);
			text = new TextView(this);
			text.setText(corrente.getProdotto().getNome());
			//text.setLayoutParams(lp);
			row.addView(text);
			text = new TextView(this);
			text.setText(Integer.toString(corrente.getQuantita()));
			text.setGravity(Gravity.CENTER);
			//text.setLayoutParams(lp);
			row.addView(text);
			text = new TextView(this);
			text.setText(formatoPrezzo.format(corrente.getSubTotale()) + " €");
			//text.setLayoutParams(lp);
			row.addView(text);
			cancella = new ImageButton(this);
			cancella.setImageResource(R.drawable.delete);
			cancella.setOnClickListener(this);
			cancella.setId(200 + index);
			cancella.setBackgroundColor(android.R.color.transparent);
			row.addView(cancella);
			tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			//tl.addView(row);
			index++;
		}
		
		//PREZZO FINALE
		row = new TableRow(this);
		text = new TextView(this);
		text.setText("Totale da pagare: ");
		text.setPadding(0, 30, 0, 30);
		//text.setLayoutParams(lp);
		row.addView(text);
		text = new TextView(this);
		text.setText(formatoPrezzo.format(ordinazione.getTotale()) + " €");
		text.setPadding(0, 30, 0, 30);
		//text.setLayoutParams(lp);
		row.addView(text);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//tl.addView(row);
		
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
		//tl.addView(row);
		
		//PULSANTE INVIA ORDINE
		inviaOrdinazione = new Button(this);
		inviaOrdinazione.setId(100);
		inviaOrdinazione.setText("Invia Ordinazione");
		inviaOrdinazione.setOnClickListener(this);
		inviaOrdinazione.setGravity(Gravity.CENTER);
		row = new TableRow(this);
		row.setGravity(Gravity.CENTER_HORIZONTAL);
		row.addView(inviaOrdinazione);
		row.setPadding(0, 30, 0, 0);
		tl.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//tl.addView(row);
		
		//ADD TABLE TO SCROLL
		//scroll.addView(tl);
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
		int id=1;
		tl.removeAllViews();
		row = new TableRow(this);
		row.setId(id);
		row.setGravity(Gravity.CENTER);
		text = new TextView(this);
		text.setGravity(Gravity.CENTER);
		text.setId(id++);
		text.setTextAppearance(this, android.R.style.TextAppearance_Large);
		text.setText("Nessun prodotto selezionato");
		//text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		row.addView(text);
		//row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tl.addView(row);
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
