package it.quickorder.android;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import it.quickorder.domain.Ordinazione;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Riepilogo extends Base implements OnClickListener
{
	private Button inviaOrdinazione;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutriepilogo);
		inviaOrdinazione = (Button) findViewById(R.id.inviaOrdinazione);
		inviaOrdinazione.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.inviaOrdinazione)
		{
			try
			{
				Socket socket = new Socket(SRV_ADDRESS, ORDERS_PORT);
				Ordinazione ord = ((NuovaOrdinazione)getParent()).getOrdinazione();
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				
				output.writeObject(ord);
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
		
	}
	
}
