package it.quickorder.control;

import it.quickorder.domain.Ordinazione;

import java.awt.Event;
import java.util.*;

public class StackOrdinazioni 
{
	private List<Ordinazione> ordinazioni;
	private List<OrdinazioniListener> listeners;
	private int counter;
	
	public StackOrdinazioni()
	{
		counter = 0;
		ordinazioni = new ArrayList<Ordinazione>(100);
		listeners = new ArrayList<OrdinazioniListener>();
	}
	
	public synchronized void aggiungiOrdinazione(Ordinazione ord)
	{
		synchronized (ordinazioni) 
		{
			counter++;
			ord.setId(counter);
			ord.setArrivo(new Date(System.currentTimeMillis()));
			ordinazioni.add(ord);
		}
		for (OrdinazioniListener o : listeners)
			o.ordinazioneRicevuta(new Event(null, 0, ord));
	}
	
	public synchronized void rimuoviOrdinazione(Ordinazione ord)
	{
		synchronized (ordinazioni) 
		{
			Iterator<Ordinazione> oo = ordinazioni.iterator();
			boolean trovato = false;
			Ordinazione o = null;
			while (!trovato && oo.hasNext())
			{
				o = oo.next();
				if (o.equals(ord))
					trovato = true;
			}
			if (o != null)
				ordinazioni.remove(o);
		}
	}
	
	public void aggiungiListener(OrdinazioniListener listener)
	{
		listeners.add(listener);
	}
	
	public void rimuoviListener(OrdinazioniListener listener)
	{
		if (listeners.contains(listener))
			listeners.remove(listener);
	}
}
