package it.quickorder.android;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import it.quickorder.domain.Aggiornamento;
import it.quickorder.domain.Cliente;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreen extends Base 
{
	private ProgressBar progressBar1;
	private TextView labelStato;
	private int downloadCounter, downloadTotal;
	
	Handler progressHandler = new Handler()
	{
		private Cliente cliente;
		
		public void handleMessage(Message msg)
		{
			switch (msg.arg1)
			{
				case 1:
					cliente = (Cliente) msg.obj;
					progressBar1.incrementProgressBy(15);
					break;
				case 2:
					downloadCounter = msg.arg2;
					int progresso = (int) Math.ceil(80 / downloadTotal);
					labelStato.setText("Aggiornamento prodotto " + downloadCounter + " di " + downloadTotal + ".");
					progressBar1.incrementProgressBy(progresso);
					break;
				case 3:
					labelStato.setText("Aggiornamento completato.");
					progressBar1.setProgress(100);
					break;
				case 4:
					labelStato.setText("Nessun aggiornamento disponibile.");
					progressBar1.setProgress(100);
					break;
				case 5:
					// Se il cliente è già registrato si passa a nuova Ordinazione.
					if (cliente != null)
					{
						Intent intent = new Intent(SplashScreen.this, NuovaOrdinazione.class);
						String pkg = getPackageName();
						intent.putExtra(pkg + ".cliente", cliente);
						startActivity(intent);
						finish();
					}
					// Viceversa, il cliente deve registrarsi.
					else
					{
						Intent intent = new Intent(SplashScreen.this, Registrazione.class);
						startActivity(intent);
						finish();
					}
					break;
				case 6:
					downloadTotal = msg.arg2;
					break;
					
			}			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
//		ImageView i = (ImageView) findViewById(R.id.imageView1);
//		i.setAdjustViewBounds(true);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBarIniziale);
		labelStato = (TextView) findViewById(R.id.download);
		labelStato.setText("Caricamento dati applicazione in corso...");
		labelStato.setTextSize(14);
		labelStato.setTextColor(Color.rgb(13,51,147));
		progressBar1.setProgress(0);
		progressBar1.setMax(100);
		Thread background = new Thread (new Runnable() 
		{
	        @SuppressWarnings("unchecked")
			public void run() 
	           {
	               try 
	               {
	            	   Thread.sleep(1000);
	            	   Cliente cliente = dbAdapter.recuperaDatiCliente();
	            	   Message msg = progressHandler.obtainMessage();
		           	   msg.obj = cliente;
		           	   msg.arg1 = 1;
		           	   progressHandler.sendMessage(msg);
		           	   
		           	   Thread.sleep(500);
		           	   int versione = dbAdapter.getMaxVersioneProdotti();
		           	   List<Aggiornamento> risultati = null;
		           	   Socket socket = null;
		           	   try
		           	   {
			           	   socket = new Socket(SRV_ADDRESS, UPD_PORT);
			           	   ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			           	   out.writeInt(versione);
			           	   out.flush();
			           	   ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			           	   risultati = (List<Aggiornamento>) in.readObject();
			           	   socket.close();
		           	   }
		           	   catch(Exception ex)
		           	   {
		           		   ex.printStackTrace();
		           	   }
					   if (risultati != null && risultati.size() > 0)
					   {
						   progressHandler.sendMessage(progressHandler.obtainMessage(0,6,risultati.size()));
						   int i = 1;
						   for(Aggiornamento p : risultati)
						   {
								dbAdapter.aggiungiProdotto(p.getProdotto());
								try
								{
									FileOutputStream fos = openFileOutput(p.getProdotto().getCodice() + ".jpg", Context.MODE_PRIVATE);
									fos.write(p.getImage());
									fos.close();
								}
								catch (IOException ex)
								{
									ex.printStackTrace();
								}
								progressHandler.sendMessage(progressHandler.obtainMessage(0,2,i));
								i++;
								Thread.sleep(500);
							}
							progressHandler.sendMessage(progressHandler.obtainMessage(0,3,0));

					   }
					   else
					   {
						   progressHandler.sendMessage(progressHandler.obtainMessage(0,4,0));
					   }
			           Thread.sleep(1500);
			           progressHandler.sendMessage(progressHandler.obtainMessage(0,5,0));
	            	   
	               }
	               catch (java.lang.InterruptedException e) 
	               {
	                  e.printStackTrace();
	               }
	           }
	        });

		background.start();
	}
}


