package it.quickorder.android;

import java.util.List;

import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;
import it.qwerty.android.R;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SceltaBibita extends Base
{
	private List<Prodotto> listaBibite;
	private int posizione;
	private Ordinazione ordinazione;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutsceltabibita);
		posizione = 0;
		ordinazione = ((NuovaOrdinazione)this.getParent()).getOrdinazione();
		
	}
}
