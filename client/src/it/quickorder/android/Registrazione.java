package it.quickorder.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import it.quickorder.domain.Cliente;
import it.quickorder.helpers.ControlloDati;
import it.qwerty.android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Registrazione extends Base implements OnClickListener
{
	private String imei;
	private EditText nomeForm;
	private EditText cognomeForm;
	private EditText luogoForm;
	private EditText cfForm;
	private EditText mailForm;
	private RadioButton sessoForm;
	private DatePicker dataForm;
	private Button registra;
	
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.registrazione);
       init(this);
       nomeForm = (EditText) findViewById(R.id.nome);
       cognomeForm = (EditText) findViewById(R.id.cognome);
       luogoForm = (EditText) findViewById(R.id.luogo);
       mailForm = (EditText) findViewById(R.id.mail);
       cfForm = (EditText) findViewById(R.id.cf);
       sessoForm = (RadioButton) findViewById(R.id.m);
       if (!sessoForm.isChecked())
    	   sessoForm = (RadioButton) findViewById(R.id.f);
       dataForm = (DatePicker) findViewById(R.id.data);
       registra = (Button) findViewById(R.id.registra);
       registra.setOnClickListener(this);
       TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
       imei = telephonyManager.getDeviceId();
       if (imei.equals(null))
       {
    	   imei = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
       }
    }
	
	@Override
	public void onClick(View v)
	{
		final Cliente nuovoCliente = creaBeanCliente();
		boolean[] check = ControlloDati.checkBeanCliente(nuovoCliente);
		
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
					ProgressDialog comm = new ProgressDialog(getApplicationContext());
					comm.setCancelable(false);
					comm.setMessage("Registrazione col server in corso..");
					comm.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					comm.show();
					Socket socket = null;
					String response = null;
					try
					{
						socket = new Socket("192.168.1.2", 4446);
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
						ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
						output.writeObject(nuovoCliente);
						response = (String) input.readObject();						
					}
					catch (Exception ex)
					{
						Toast t = Toast.makeText(getApplicationContext(), "Errore nel completamento della registrazione.", Toast.LENGTH_SHORT);
						t.show();
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
					
					if (response == null || response.equalsIgnoreCase("FAILED"))
					{
						Toast t = Toast.makeText(getApplicationContext(), "Errore nel completamento della registrazione.", Toast.LENGTH_SHORT);
						t.show();
					}
					ContentValues values = new ContentValues();	
					values.put("imei", nuovoCliente.getIMEI());
					values.put("nome", nuovoCliente.getNome());
					values.put("cognome", nuovoCliente.getCognome());
					values.put("cf", nuovoCliente.getCodiceFiscale());
					values.put("email", nuovoCliente.getEmail());
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					values.put("dataNascita", dateFormat.format(nuovoCliente.getDataNascita()));
					values.put("luogoNascita", nuovoCliente.getLuogoNascita());
					values.put("sesso", "" + nuovoCliente.getSesso());
					values.put("abilitato",1);
					
					long i = db.insert("user", null, values);
					if (i == -1)
					{
						Toast t = Toast.makeText(getApplicationContext(), "Errore nel completamento della registrazione.", Toast.LENGTH_SHORT);
						t.show();
					}
					else
					{	
						Toast t = Toast.makeText(getApplicationContext(), "Registrazione completata con successo.", Toast.LENGTH_SHORT);
						t.show();
						main();
			        	finish();
					}	
		        	db.close();
		        	
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
	
	public void main()
	{
		Intent i = new Intent(this, Main.class);
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
		c.setSesso(sessoForm.getText().charAt(0));
		c.setIMEI(randomImei());
		Date dataNascita = new Date();
		dataNascita.setDate(dataForm.getDayOfMonth());
		dataNascita.setMonth(dataForm.getMonth());
		dataNascita.setYear(dataForm.getYear());
		c.setDataNascita(dataNascita);
		return c;
	}
	
	private String randomImei()
	{
		Random rand=new Random();
		String res="";
		for (int i=0; i<17;i++)
		{
			res += String.valueOf(rand.nextInt(9));	
		}
			
		return res;
	}
}
