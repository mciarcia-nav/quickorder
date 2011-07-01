package it.quickorder.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.quickorder.domain.Cliente;
import it.quickorder.domain.Prodotto;
import it.qwerty.android.R;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Base implements OnClickListener
{
	private Cliente cliente;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	if (controllaWiFi())
    	{	
	        init(this);
	        String query = "SELECT * from user";
	        Cursor cursor = db.rawQuery(query, null);
	        if (cursor.getCount() != 0) //UTENTE REGISTRATO
	        {	
	        	setContentView(R.layout.main);
	        	cursor.moveToFirst();
	        	cliente = new Cliente();
	        	cliente.setNome(cursor.getString(cursor.getColumnIndex("nome")));
	        	cliente.setIMEI(cursor.getString(cursor.getColumnIndex("imei")));
	        	cliente.setCognome(cursor.getString(cursor.getColumnIndex("cognome")));
	        	cliente.setCodiceFiscale(cursor.getString(cursor.getColumnIndex("cf")));
	        	cliente.setEmail(cursor.getString(cursor.getColumnIndex("email")));
	        	cliente.setLuogoNascita(cursor.getString(cursor.getColumnIndex("luogoNascita")));
	        	cliente.setSesso(cursor.getString(cursor.getColumnIndex("sesso")).charAt(0));
	        	cliente.setAbilitato(cursor.getInt(cursor.getColumnIndex("abilitato")) == 1);
	        	String data = cursor.getString(cursor.getColumnIndex("dataNascita"));
	        	Date dataNascita = new Date();
	        	dataNascita.setYear(Integer.parseInt(data.substring(0,4)) - 1900);
	        	dataNascita.setMonth(Integer.parseInt(data.substring(5,7)));
	        	dataNascita.setDate(Integer.parseInt(data.substring(8,10)));
	        	cliente.setDataNascita(dataNascita);
	        	Log.i("data", cliente.getDataNascita().toString());
	        	TextView w = (TextView) findViewById(R.id.welcome);
	        	w.setText("Benvenuto: "+ cliente.getNome());
	        	Button nuovaOrdinazione = (Button) findViewById (R.idButtons.nuovaOrdinazione);
	        	Button esci = (Button) findViewById(R.idButtons.esci);
	        	nuovaOrdinazione.setOnClickListener(this);
	            esci.setOnClickListener(this);
	            db.close();
	        }
	        else  //REGISTRAZIONE
	        {
	        	db.close();
	        	this.launchActivity(Registrazione.class);
	        }           	
    	}
    	else
    	{
    		Log.i("conn", "NESSUNA");
    		setContentView(R.layout.vuoto);
    		Toast t = Toast.makeText(getApplicationContext(), "Nessuna connessione disponibile", Toast.LENGTH_SHORT);
			t.show();
    	}
    }

	private boolean controllaWiFi() 
	{
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.idButtons.nuovaOrdinazione)
			this.launchActivity(NuovaOrdinazione.class);
		
		if (v.getId() == R.idButtons.esci)
			this.finish();
	}

	private void launchActivity(Class<?> c)
	{
		Intent i = new Intent(this, c);
		startActivity(i);
		finish();
	}
	
	private void upgradeDB()
	{
		String query = "SELECT MAX(versione) FROM prodotti";
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		int max_versione = cursor.getInt(0);
		Log.i("versione", Integer.toString(max_versione));
		try 
		{
			Socket socket = new Socket("192.168.1.2", 4445);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeInt(max_versione);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			List<Prodotto> risultati = (List<Prodotto>) in.readObject();
			Log.i("dimensione", Integer.toString(risultati.size()));
			socket.close();
			ContentValues values = new ContentValues();
			for(Prodotto p : risultati)
			{
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
			}
			
			
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}