package it.quickorder.android;

import it.quickorder.domain.Articolo;
import it.quickorder.domain.Prodotto;
import it.qwerty.android.R;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SceltaPanino extends Base implements OnClickListener
{	
	private int posizione=0;
	private List<Prodotto> listaPanini;
	private Button next;
	private Button prev;
	private Button paninoMinus;
	private Button paninoPlus;
	private TextView prezzoPanino;
	private TextView nomePanino;
	private TextView descrizionePanino;
	private TextView quantita;
	private ImageView image;
	private int[] selezionati;
	
	@Override
	  public void onCreate(Bundle savedInstanceState)  
	  {
			super.onCreate(savedInstanceState);
            setContentView(R.layout.panino5);
            settaLista();
            next = (Button) findViewById(R.id.successivo);
            prev = (Button) findViewById(R.id.precedente);
            paninoMinus = (Button) findViewById(R.id.paninoMinus);
            paninoPlus = (Button) findViewById(R.id.paninoPlus);
            prezzoPanino = (TextView) findViewById(R.id.prezzoPanino);
            nomePanino = (TextView) findViewById(R.id.nomePanino);
            descrizionePanino = (TextView) findViewById(R.id.descrizione);
            quantita = (TextView) findViewById(R.id.quantita);
            image = (ImageView) findViewById(R.id.imagePanino);
            next.setOnClickListener(this);
            prev.setOnClickListener(this);
            paninoMinus.setEnabled(false);
            if (listaPanini.size()==1)
            {
            	next.setEnabled(false);
            	prev.setEnabled(false);
            }
            this.nuovaOrdinazione();
    }


	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.next) // PANINO SUCCESSIVO
		{
			if (posizione == listaPanini.size()-1)
				posizione = 0;
			else
				posizione = posizione + 1;
		}
		else if (v.getId() == R.id.prev) // PANINO PRECEDENTE
		{
			if (posizione == 0)
				posizione = listaPanini.size()-1;
			else
				posizione = posizione -1;
		}
		else if (v.getId() == R.id.paninoPlus) // AGGIUNGI PANINO
		{
			selezionati[posizione] = selezionati[posizione] + 1; 
			paninoMinus.setEnabled(true);
			Articolo a = new Articolo();
			
			
		}
		else if (v.getId() == R.id.paninoMinus) // RIMOUVI PANINO
		{
			selezionati[posizione] = selezionati[posizione] - 1; 
			if (selezionati[posizione] == 0)
				paninoMinus.setEnabled(false);
		}
		aggiungiInfo(posizione);
		
	}
	
	public void settaLista()
	{
		listaPanini = new ArrayList<Prodotto>();
		String selection = "tipologia = ?";
		String[] selectionArgs = { "0" };
		db = dbhelper.getReadableDatabase();
		Cursor cursor = db.query("prodotti", null, selection, selectionArgs, null, null, null);
		while(cursor.moveToNext())
		{
			Prodotto p = new Prodotto();
			p.setCodice(cursor.getString(0));
			p.setNome(cursor.getString(1));
			p.setTipologia(0);
			p.setPrezzo(Double.parseDouble(cursor.getString(3)));
			listaPanini.add(p);
		}
		selezionati = new int[listaPanini.size()];
		for (int i=0;i<selezionati.length;i++)
			selezionati[i] = 0;
		aggiungiInfo(posizione);
	}
	
	public void aggiungiInfo(int pos)
	{
		nomePanino.setText(listaPanini.get(pos).getNome());
		descrizionePanino.setText(listaPanini.get(pos).getDescrizione());
		prezzoPanino.setText("Prezzo: "+Double.toString(listaPanini.get(pos).getPrezzo()));
		quantita.setText(Integer.toString(selezionati[pos]));
	}
}
	 
	

