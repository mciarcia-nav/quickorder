package it.quickorder.control;

import it.quickorder.domain.Notifica;

import java.awt.Event;
import java.util.*;

public class CodaNotifiche 
{
	private List<NotificheListener> listeners;
	
	public CodaNotifiche()
	{
		listeners = new ArrayList<NotificheListener>();
	}
	
	public synchronized void aggiungiNotifica(Notifica nuovaNotifica)
	{
		nuovaNotifica.setArrivo(new Date(System.currentTimeMillis()));

		for (NotificheListener corrente : listeners)
		{
			corrente.notificaRicevuta(new Event(null, 0, nuovaNotifica));
		}
	}
	
	public void aggiungiListener(NotificheListener l)
	{
		listeners.add(l);
	}
}
