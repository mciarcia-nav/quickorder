package it.quickorder.domain;

import java.io.Serializable;

public class Prodotto implements Serializable
{
	private String nome, codice, descrizione;
	private int tipologia, versione;
	private double prezzo;
	public static final int PANINO = 0, BEVANDA = 1;

	public String getDescrizione() 
	{
		return descrizione;
	}

	public int getVersione()
	{
		return versione;
	}
	
	public void setVersione(int versione)
	{
		this.versione = versione;
	}
	
	public void setDescrizione(String descrizione) 
	{
		this.descrizione = descrizione;
	}
	
	public String getCodice()
	{
		return codice;
	}
	
	public void setCodice(String codice)
	{
		this.codice = codice;
	}
	
	public String getNome() 
	{
		return nome;
	}
	
	public void setNome(String nome) 
	{
		this.nome = nome;
	}
	
	public int getTipologia()
	{
		return tipologia;
	}
	
	public void setTipologia(int tipologia)
	{
		this.tipologia = tipologia;
	}
	
	public double getPrezzo() 
	{
		return prezzo;
	}
	
	public void setPrezzo(double prezzo) 
	{
		this.prezzo = prezzo;
	}	
	
}
