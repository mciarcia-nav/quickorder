package it.quickorder.domain;

import java.io.Serializable;

public class Articolo implements Serializable 
{
	private int id, quantita;
	private double subTotale;
	private Prodotto prodotto;
	private String note;
	
	public int getId() 
	{
		return id;
	}
	public void setId(int id) 
	{
		this.id = id;
	}
	public int getQuantita() 
	{
		return quantita;
	}
	public void setQuantita(int quantita) 
	{
		this.quantita = quantita;
	}
	public double getSubTotale() 
	{
		return subTotale;
	}
	public void setSubTotale(double subTotale) 
	{
		this.subTotale = subTotale;
	}
	public Prodotto getProdotto() 
	{
		return prodotto;
	}
	public void setProdotto(Prodotto prodotto) 
	{
		this.prodotto = prodotto;
	}
	
	public String getNote()
	{
		return note;
	}
	
	public void setNote(String note)
	{
		this.note = note;
	}
	
}
