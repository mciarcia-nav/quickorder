package it.quickorder.android;


import it.quickorder.domain.Cliente;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Fine extends Base implements OnClickListener
{
	private Button nuova,esci;
	private Cliente cliente;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fine);
		String pkg = getPackageName();
		cliente = (Cliente) getIntent().getSerializableExtra(pkg + ".cliente");
		Log.i("nome",cliente.getNome());
		nuova = (Button) findViewById(R.id.nuovaOrdinazione);
		esci = (Button) findViewById(R.id.exit);
		nuova.setOnClickListener(this);
		esci.setOnClickListener(this);
		
	}	

	@Override
	public void onClick(View v) 
	{
		if (v.getId() == nuova.getId())
		{
			Intent i = new Intent(this,NuovaOrdinazione.class);
			String pkg = getPackageName();
			i.putExtra(pkg + ".cliente", cliente);
			startActivity(i);
			finish();
			
		}
		else
			finish();
	}
}