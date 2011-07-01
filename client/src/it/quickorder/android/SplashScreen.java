package it.quickorder.android;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import it.quickorder.domain.Prodotto;
import it.qwerty.android.*;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreen extends Base 
{
	private ProgressBar progressBar1;
	private TextView textView1, textView2;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		ImageView i = (ImageView) findViewById(R.id.imageView1);
//		i.setImageResource(R.drawable.loading);
		i.setAdjustViewBounds(true);
		textView1 = (TextView) findViewById(R.id.download);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBarIniziale);
		textView2 = (TextView) findViewById(R.id.information);
		textView1.setText("Sto cercando aggiornamenti del menù...");
		textView2.setText("In downloading..");
		upgradeDB();
		final Intent intent = new Intent(this, Main.class);
		AlertDialog alert = new AlertDialog.Builder(SplashScreen.this).create();
		alert.setTitle("Aggiornamento Completato");
		alert.setMessage(textView2.getText());
		alert.setButton("Entra nell'app!", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				startActivity(intent);
				finish();
			}
		});
		alert.show();		
	}
	
	private void upgradeDB()
	{
		init(this);
		String query = "SELECT MAX(versione) FROM prodotti";
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		int max_versione = 0;
		if(cursor.getCount()!=0)
		{
			max_versione = cursor.getInt(0);
		}
		Log.i("versione", Integer.toString(max_versione));
		try 
		{
			Socket socket = new Socket(SRV_ADDRESS, UPD_PORT);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeInt(max_versione);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			List<Prodotto> risultati = (List<Prodotto>) in.readObject();
			Log.i("dimensione", Integer.toString(risultati.size()));
			socket.close();
			ContentValues values = new ContentValues();
			if (risultati.size() !=0)
			{
				textView1.setText("Sto scaricando l'aggiornamento...");
				int progresso = 100/risultati.size();
				int prodottiAggiornati = risultati.size();
				int i = 1;
				for(Prodotto p : risultati)
				{
					textView2.setText("Sto scaricando il prodotto "+i+" di "+prodottiAggiornati+".");
					values.put("codice", p.getCodice());
					values.put("nome", p.getNome());
					values.put("descrizione", p.getDescrizione());
					values.put("prezzo", p.getPrezzo());
					values.put("versione", p.getVersione());
					values.put("tipologia", p.getTipologia()); 
					String q = "Select * from prodotti where codice='"+p.getCodice()+"'";
					Cursor c = db.rawQuery(q, null);
					if (c.getCount() == 0)					
						db.insert("prodotti", null, values);
					else
						db.update("prodotti", values, "codice='" + p.getCodice() + "'",null);
					i++;
					progressBar1.setProgress(progresso);
				}
				
				textView2.setText("Download Completato! "+i+" prodotti aggiornati.");
				progressBar1.setProgress(100);
			}
			else
			{
				textView2.setText("Nessun aggiornamento disponibile. Il Menù è già aggiornato.");
				progressBar1.setProgress(100);
			}
			
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
