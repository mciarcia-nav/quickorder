package it.quickorder.domain;

import java.io.Serializable;

public class Aggiornamento implements Serializable
{
	private static final long serialVersionUID = 7365170795915547616L;
	
	private Prodotto prodotto;
	private byte[] image;
	
	public Prodotto getProdotto() 
	{
		return prodotto;
	}
	
	public void setProdotto(Prodotto prodotto) 
	{
		this.prodotto = prodotto;
	}
	
	public byte[] getImage() 
	{
		return image;
	}
	
	public void setImage(byte[] image) 
	{
		this.image = image;
	}
	
	
}
