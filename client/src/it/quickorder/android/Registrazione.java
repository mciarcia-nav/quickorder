package it.quickorder.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import it.quickorder.domain.Cliente;
import it.quickorder.helpers.ControlloDati;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Registrazione extends Base implements OnClickListener, Runnable
{
	private EditText nomeForm;
	private EditText cognomeForm;
	private EditText luogoForm;
	private EditText cfForm;
	private EditText mailForm;
	private RadioButton sessoFormM;
	private RadioButton sessoFormF;
	private DatePicker dataForm;
	private Button registra;
	private Cliente nuovoCliente;
	private ProgressDialog progress;
	private boolean[] check;
	private boolean confirmed;
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.registrazione);
       TextView welcome = (TextView) findViewById(R.id.welcomereg);
       String messaggio = "<html>Per utilizzare il servizio � <b>necessario</b> registrarsi<br>al sistema. Per favore, compila tutti i campi qui<br>sotto e quindi clicca su <i>Registrati</i>.</html>"; 
       welcome.setTextSize(14);
       welcome.setText(Html.fromHtml(messaggio));
       welcome.setGravity(Gravity.CENTER);
       nomeForm = (EditText) findViewById(R.id.nome);
       cognomeForm = (EditText) findViewById(R.id.cognome);
       luogoForm = (EditText) findViewById(R.id.luogo);
       mailForm = (EditText) findViewById(R.id.mail);
       cfForm = (EditText) findViewById(R.id.cf);
       sessoFormM = (RadioButton) findViewById(R.id.m);
       sessoFormF = (RadioButton) findViewById(R.id.f);
       dataForm = (DatePicker) findViewById(R.id.data);
       registra = (Button) findViewById(R.id.registra);
       registra.setOnClickListener(this);
       sessoFormM.setOnClickListener(this);
       sessoFormF.setOnClickListener(this);
       
    }
	
	@Override
	public void onClick(View v)
	{
		if (v.getId() == sessoFormM.getId())
		{	
			sessoFormM.setChecked(true);
			sessoFormF.setChecked(false);
		}
		else if (v.getId() == sessoFormF.getId())
		{	
			sessoFormM.setChecked(false);
			sessoFormF.setChecked(true);
		}
		else if (v.getId() == registra.getId())
		{	
			progress = ProgressDialog.show(Registrazione.this, "", "Controllo dei dati in corso..",true,true);
			progress.show();
			new Thread(this).start();
		}
	}
	
	private void main(Cliente cliente)
	{
		Intent i = new Intent(this, NuovaOrdinazione.class);
		String pkg = getPackageName();
		i.putExtra(pkg + ".cliente", cliente);
		startActivity(i);
	}
	
	private Cliente creaBeanCliente()
	{
		Cliente c = new Cliente();
		c.setNome(nomeForm.getText().toString());
		c.setCognome(cognomeForm.getText().toString());
		c.setCodiceFiscale(cfForm.getText().toString());
		c.setEmail(mailForm.getText().toString());
		c.setLuogoNascita(luogoForm.getText().toString());
		if (sessoFormM.isChecked())
			c.setSesso(sessoFormM.getText().charAt(0));
		else if (sessoFormF.isChecked())
			c.setSesso(sessoFormF.getText().charAt(0));
		c.setIMEI(randomImei());
		Date dataNascita = new Date();
		dataNascita.setDate(dataForm.getDayOfMonth());
		dataNascita.setMonth(dataForm.getMonth());
		dataNascita.setYear(dataForm.getYear()-1900);
		Log.i("date",dataNascita.toString());
		c.setDataNascita(dataNascita);
		return c;
	}
	
	private String randomImei()
	{
		Random rand = new Random(System.currentTimeMillis());
		String res="";
		for (int i=1; i<=18;i++)
		{
			if (i == 7 || i == 10 || i == 17)
				res += "-";
			else
				res += "" + rand.nextInt(9);	
		}
			
		return res;
	}
	
	private Handler handler = new Handler() 
	{
        @Override
        public void handleMessage(Message msg) 
        {
                String message = (String) msg.obj;
                if (message.equalsIgnoreCase("DATI_CORRETTI"))
				{
                	progress.dismiss();
                	final AlertDialog alert = new AlertDialog.Builder(Registrazione.this).create();
    				alert.setTitle("Conferma Registrazione");
    				alert.setMessage("Dati corretti. Procedere con la registrazione?");
    				alert.setIcon(R.drawable.questionicon);
    				alert.setButton("Conferma", new DialogInterface.OnClickListener() 
    				{		
    					@Override
    					public void onClick(DialogInterface dialog, int which) 
    					{
    						confirmed = true;
    						alert.dismiss();
    						synchronized (Registrazione.this) 
    						{
    							Registrazione.this.notify();
							}
    						
    			        }
    				});
    				alert.setButton2("Annulla", new DialogInterface.OnClickListener()
    				{
    					@Override
    					public void onClick(DialogInterface dialog, int which) 
    					{
    						confirmed = false;
    						alert.dismiss();
    						synchronized (Registrazione.this) 
    						{
    							Registrazione.this.notify();
							}
    					}
    				});
    				alert.show();
				}
                else if (message.equalsIgnoreCase("DATI_NON_CORRETTI"))
                {
                	progress.dismiss();
                	String messaggio = "<html>Alcuni dati non sono corretti!<br>Per favore correggi i dati nei campi seguenti:<br>";
    				if (!check[1])
    					messaggio += "+ Nome<br>";
    				if (!check[2])
    					messaggio += "+ Cognome<br>";
    				if (!check[3])
    					messaggio += "+ Data di Nascita<br>";
    				if (!check[4])
    					messaggio += "+ Luogo di Nascita<br>";
    				if (!check[5])
    					messaggio += "+ E-Mail<br>";
    				if (!check[6])
    					messaggio += "+ Codice Fiscale<br>";
    				messaggio = messaggio.substring(0, messaggio.length() - 2);
    				messaggio += ".</html>";
    				final AlertDialog alert = new AlertDialog.Builder(Registrazione.this).create();
    				alert.setTitle("Errore");
    				alert.setMessage(Html.fromHtml(messaggio));
    				alert.setButton("OK", new DialogInterface.OnClickListener() 
    				{		
    					@Override
    					public void onClick(DialogInterface dialog, int which) 
    					{
    						alert.dismiss();
    			        }
    				});
    				alert.show();
                }
                else if (message.equalsIgnoreCase("INVIO_DATI"))
                {
                	progress = ProgressDialog.show(Registrazione.this, "", "Invio dei dati al server..",true,true);
        			progress.show();
                }
                else if (message.equalsIgnoreCase("DUP_EMAIL"))
				{
                	progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "L'email selezionata � stata registrata nel database.", Toast.LENGTH_SHORT);
					t.show();
					return;
				}
                else if (message.equalsIgnoreCase("DUP_CF"))
				{
                	progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "Un utente col medesimo codice fiscale � gi� registrato al sistema.", Toast.LENGTH_SHORT);
					t.show();
					return;
				}
                else if (message.equalsIgnoreCase("WRONG_CF"))
				{
                	progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "Il codice fiscale inserito non � corretto per i dati forniti.", Toast.LENGTH_SHORT);
					t.show();
					return;
				}
				else if (message.equalsIgnoreCase("FAILED"))
				{
					progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "Errore nel completamento della registrazione.", Toast.LENGTH_SHORT);
					t.show();
					return;
				}
				else if (message.equalsIgnoreCase("OK"))
				{
					progress.setMessage("In attesa dell'abilitazione...");
				}
				else if (message.equalsIgnoreCase("ABILITATO"))
				{
					dbAdapter.registraCliente(nuovoCliente);
					progress.dismiss();
					final AlertDialog alert = new AlertDialog.Builder(Registrazione.this).create();
					alert.setTitle("Registrazione completata!");
					alert.setIcon(R.drawable.okicon);
					String messaggio = "<html>La registrazione � stata completata con successo.<br><br>Ora puoi effettuare le tue ordinazioni direttamente seduto al tavolo.<br></html>";
					TextView text = new TextView(Registrazione.this);
					text.setGravity(Gravity.CENTER_HORIZONTAL);
					text.setText(Html.fromHtml(messaggio));
					alert.setView(text);
					alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							alert.dismiss();
							main(nuovoCliente);
							finish();
						}					
					});
					alert.show();
					
				}
				else if (message.equalsIgnoreCase("DISABILITATO"))
				{
					progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "Il cliente non � stato abilitato. Correggere i dati o parlare col cassiere.", Toast.LENGTH_SHORT);
					t.show();
					return;
				}
        }
};


	@Override
	public void run() 
	{
		nuovoCliente = creaBeanCliente();
		check = ControlloDati.checkBeanCliente(nuovoCliente);
		
		Message msg = handler.obtainMessage();
		if (check[0])
		{
			msg.obj = "DATI_CORRETTI";
			handler.sendMessage(msg);
		}
		else
		{
			//msg.obj = "DATI_NON_CORRETTI";
			msg.obj = "DATI_CORRETTI";
			handler.sendMessage(msg);
			//return;
		}
		
		try 
		{
			synchronized (this) 
			{
				wait();
			}
			
		} catch (InterruptedException e1) 
		{
			e1.printStackTrace();
		}
		
		if (!confirmed)
			return;
		msg = handler.obtainMessage();
		msg.obj = "INVIO_DATI";
		handler.sendMessage(msg);
		try 
		{
			Thread.sleep(1000);
		} catch (InterruptedException e1) 
		{
			e1.printStackTrace();
		}
		
		Socket socket = null;
		String response = null;
		try
		{
			socket = new Socket(SRV_ADDRESS, SIGNUP_PORT);
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			output.writeObject(nuovoCliente);
			output.flush();
			response = (String) input.readObject();
			msg = handler.obtainMessage();
			msg.obj = response;
			handler.sendMessage(msg);
			if (!response.equalsIgnoreCase("OK"))
				return;
			Thread.sleep(10000);
			int abilitazione = input.readInt();
			msg = handler.obtainMessage();
			if (abilitazione == 0)
			{
				msg.obj = new String("DISABILITATO");
			}
			else if (abilitazione == 1)
			{
				msg.obj = new String("ABILITATO");
			}
			Thread.sleep(1000);
			handler.sendMessage(msg);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (socket != null)
				try 
				{
					socket.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
		}
		
	}
}
