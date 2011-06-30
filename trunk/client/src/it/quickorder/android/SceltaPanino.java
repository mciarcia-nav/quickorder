package it.quickorder.android;

import it.quickorder.domain.Prodotto;
import it.qwerty.android.R;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SceltaPanino extends Base implements OnClickListener
{	
	private int posizione=0;
	private List<Prodotto> listaPanini;
	
	@Override
	  public void onCreate(Bundle savedInstanceState)  
	  {
			init(this);
			super.onCreate(savedInstanceState);
            setContentView(R.layout.panino4);
            settaLista();
            Button next = (Button) findViewById(R.id.next);
            Button prev = (Button) findViewById(R.id.prev);
            next.setOnClickListener(this);
            prev.setOnClickListener(this);
            db.close();		
    }


	@Override
	public void onClick(View v) 
	{
		Log.i("Posizione",Integer.toString(posizione));
		if (v.getId() == R.id.next)
		{
			if (posizione == listaPanini.size()-1)
				posizione = 0;
			else
				posizione = posizione + 1;
		}
		else if (v.getId() == R.id.prev)
		{
			if (posizione == 0)
				posizione = listaPanini.size()-1;
			else
				posizione = posizione -1;
		}
		String app = listaPanini.get(posizione).getNome();
		TextView t = (TextView) findViewById(R.id.twnomePanino);
		t.setText(app);
		
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
	}
}
	 
	

