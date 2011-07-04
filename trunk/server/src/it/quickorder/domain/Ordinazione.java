package it.quickorder.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Ordinazione implements Serializable 
{
	private static final long serialVersionUID = 4936994990728437519L;
	private int id, numeroTavolo, numeroProdotti;
	private Cliente cliente;
	private Date arrivo;
	private ArrayList<Articolo> articoli;
	private double totale;
	
	public Ordinazione()
	{
		totale = 0;
		articoli = new ArrayList<Articolo>();
		numeroProdotti = 0;
	}
	
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public int getNumeroTavolo() 
	{
		return numeroTavolo;
	}
	
	public void setNumeroTavolo(int numeroTavolo) 
	{
		this.numeroTavolo = numeroTavolo;
	}
	
	public Cliente getCliente() 
	{
		return cliente;
	}
	
	public void setCliente(Cliente cliente) 
	{
		this.cliente = cliente;
	}
	
	public Date getArrivo() 
	{
		return arrivo;
	}
	
	public void setArrivo(Date arrivo) 
	{
		this.arrivo = arrivo;
	}
	
	public void aggiungiArticolo(Articolo a)
	{
		totale += a.getSubTotale();
		articoli.add(a);
		numeroProdotti += a.getQuantita();
	}
	
	public void rimuoviArticolo(Prodotto p)
	{
		Iterator<Articolo> pp = articoli.iterator();
		boolean trovato = false;
		Articolo a = null;
		while (!trovato && pp.hasNext())
		{
			a = pp.next();
			if (a.getProdotto().equals(p))
			{
				totale -= a.getSubTotale();
				numeroProdotti -= a.getQuantita();
				trovato = true;
			}
		}
		if (a != null)
			articoli.remove(a);
	}
	
	public int getNumeroProdotti()
	{
		return numeroProdotti;
	}
	
	public double getTotale()
	{
		return totale;
	}
	
	public void setTotale(double totale)
	{
		this.totale = totale;
	}
	
	public ArrayList<Articolo> getArticoli()
	{
		return articoli;
	}
	
	public void setArticoli(ArrayList<Articolo> articoli)
	{
		this.articoli = articoli;
	}
	
	public boolean containsProdotto(Prodotto p)
	{
		Iterator<Articolo> pp = articoli.iterator();
		Articolo a = null;
		while (pp.hasNext())
		{
			a = pp.next();
			if (a.getProdotto().equals(p))
				return true;
		}
		return false;
	}
	
	public Articolo getArticolo(Prodotto p)
	{
		Iterator<Articolo> pp = articoli.iterator();
		Articolo a = null;
		while (pp.hasNext())
		{
			a = pp.next();
			if (a.getProdotto().equals(p))
				return a;
		}
		return null;
	}
}