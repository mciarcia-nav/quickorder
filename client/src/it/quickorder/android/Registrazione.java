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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.registrazione);
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
       //TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
       //imei = telephonyManager.getDeviceId();
       /*if (imei.equals(null))
       {
    	   imei = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
       }*/
    }
	
	@Override
	public void onClick(View v)
	{
		nuovoCliente = creaBeanCliente();
		boolean[] check = ControlloDati.checkBeanCliente(nuovoCliente);
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
			// Dati Corretti
			if (check[0])
			{
				final AlertDialog alert = new AlertDialog.Builder(Registrazione.this).create();
				alert.setTitle("Conferma");
				alert.setMessage("Sei sicuro di voler confermare i dati?");
				alert.setButton("Conferma", new DialogInterface.OnClickListener() 
				{		
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						progress = ProgressDialog.show(Registrazione.this, "", "Invio dei dati al server in corso..",true,true);
						progress.show();
						
						new Thread(Registrazione.this).start();
			        }
				});
				alert.setButton2("Annulla", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						alert.dismiss();
					}
				});
				alert.show();
			}
			// Dati non corretti
			else
			{
				String messaggio = "Campi non validi! Correggi i campi:";
				if (!check[1])
					messaggio += "Nome, ";
				if (!check[2])
					messaggio += "Cognome, ";
				if (!check[3])
					messaggio += "Data di Nascita, ";
				if (!check[4])
					messaggio += "Luogo di Nascita, ";
				if (!check[5])
					messaggio += "E-Mail, ";
				if (!check[6])
					messaggio += "Codice Fiscale, ";
				
				messaggio = messaggio.substring(0, messaggio.length() - 2);
				messaggio += ".";
				Toast t = Toast.makeText(this.getApplicationContext(), messaggio, Toast.LENGTH_SHORT);
	        	t.show();
			}
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
		dataNascita.setMonth(dataForm.getMonth()-1);
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
                if (message.equalsIgnoreCase("DUP_EMAIL"))
				{
                	progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "L'email selezionata è stata registrata nel database.", Toast.LENGTH_SHORT);
					t.show();
					return;
				}
                else if (message.equalsIgnoreCase("DUP_CF"))
				{
                	progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "Un utente col medesimo codice fiscale è già registrato al sistema.", Toast.LENGTH_SHORT);
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
					//dbAdapter.registraCliente(nuovoCliente);
					progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "Registrazione completata con successo.", Toast.LENGTH_SHORT);
					t.show();
					main(nuovoCliente);
		        	finish();
				}
				else if (message.equalsIgnoreCase("DISABILITATO"))
				{
					progress.dismiss();
					Toast t = Toast.makeText(getApplicationContext(), "Il cliente non è stato abilitato. Correggere i dati o parlare col cassiere.", Toast.LENGTH_SHORT);
					t.show();
					return;
				}
        }
};


	@Override
	public void run() 
	{
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
			Message msg = handler.obtainMessage();
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
