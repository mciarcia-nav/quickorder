package it.quickorder.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

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
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	//prova
    	super.onCreate(savedInstanceState);
    	if (controllaWiFi())
    	{	
	        init(this);
	        String query = "Select nome from user";
	        Cursor cursor = db.rawQuery(query, null);
	        if (cursor.getCount() != 0) //UTENTE REGISTRATO
	        {	
	        	setContentView(R.layout.main);
	        	cursor.moveToFirst();
	        	String nome = cursor.getString(0);
	        	TextView w = (TextView) findViewById(R.id.welcome);
	        	w.setText("Benvenuto: "+nome);
	        	Button nuovaOrdinazione = (Button) findViewById (R.idButtons.nuovaOrdinazione);
	        	Button esci = (Button) findViewById(R.idButtons.esci);
	        	nuovaOrdinazione.setOnClickListener(this);
	            esci.setOnClickListener(this);
	        }
	        else  //REGISTRAZIONE
	        {
	        	this.launchActivity(Registrazione.class);
	        }
	            upgradeDB();
	            
	            //STAMPA PRODOTTI
	            String query2 = "SELECT nome FROM prodotti";
	            Cursor cursor2 = db.rawQuery(query2, null);
	    		
	            int i=1;
	    		while(cursor2.moveToNext())
	    		{
	    			Log.i("Nome "+i+"° panino", cursor2.getString(0));
	    			i++;
	    		}
	            
	            
	        	db.close();
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
			this.launchActivity(SceltaPanino.class);
		
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