package it.quickorder.android;

import java.util.Random;

import it.qwerty.android.R;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
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
		String control = controllaCampi();
		if (control.equals("OK"))
		{
			final AlertDialog alert = new AlertDialog.Builder(Registrazione.this).create();
			alert.setTitle("Conferma");
			alert.setMessage("Sicuro di confermare i dati?");
			alert.setButton("Conferma", new DialogInterface.OnClickListener() 
			{		
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					ContentValues values = new ContentValues();	
					values.put("imei", randomImei());
					values.put("nome", nomeForm.getText().toString());
					values.put("cognome", cognomeForm.getText().toString());
					values.put("cf", cfForm.getText().toString());
					values.put("email", mailForm.getText().toString());
					values.put("dataNascita", getData());
					values.put("luogoNascita", luogoForm.getText().toString());
					values.put("sesso", sessoForm.getText().toString());
					values.put("abilitato",1);
					
					long i = db.insert("user", null, values);
					if (i == -1)
					{
						Toast t = Toast.makeText(getApplicationContext(), "Errore inserimento", Toast.LENGTH_SHORT);
						t.show();
					}
					else
					{	
						Toast t = Toast.makeText(getApplicationContext(), "Inserito con successo", Toast.LENGTH_SHORT);
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
		else
		{
			Toast t = Toast.makeText(this.getApplicationContext(), control, Toast.LENGTH_SHORT);
        	t.show();
		}
		
		
	}
	
	public String randomImei()
	{
		Random rand=new Random();
		String res="";
		for (int i=0; i<17;i++)
		{
			res += String.valueOf(rand.nextInt(9));	
		}
			
		return res;
	}
	
	public String getData()
	{
		String data="";
		int gg = dataForm.getDayOfMonth();
		int mm = dataForm.getMonth();
		int yy = dataForm.getYear();
		data = Integer.toString(gg) + "-" + Integer.toString(mm) + "-" + Integer.toString(yy);
		return data;
	}
	
	public String controllaCampi()
	{
		String campi="Inserisci i campi: ";
			if (nomeForm.getText().toString().equals(""))
				campi = campi + "Nome, ";
			if (cognomeForm.getText().toString().equals(""))
				campi = campi + "Cognome, ";
			if (cfForm.getText().toString().equals(""))
				campi = campi + "Cofice Fiscale, ";
			if (mailForm.getText().toString().equals(""))
				campi = campi + "E-Mail, ";
			if (luogoForm.getText().toString().equals(""))
				campi = campi + "Luogo di Nascita, ";
			if (campi.equals("Inserisci i campi: "))
				campi = "OK  ";
		return campi.substring(0, campi.length()-2);
	}
	
	public void main()
	{
		Intent i = new Intent(this, Main.class);
		startActivity(i);
	}
}
