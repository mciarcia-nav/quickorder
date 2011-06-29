package it.quickorder.domain;

import java.io.Serializable;
import java.util.Date;

public class Cliente implements Serializable
{
	private String nome, cognome, IMEI, codiceFiscale, luogoNascita, email;
	private char sesso;
	private Date dataNascita;
	private boolean abilitato;
	
	public String getNome() 
	{
		return nome;
	}
	public void setNome(String nome) 
	{
		this.nome = nome;
	}
	public String getCognome() 
	{
		return cognome;
	}
	
	public char getSesso()
	{
		return sesso;
	}
	
	public void setSesso(char sesso)
	{
		this.sesso = sesso;
	}
	
	public void setCognome(String cognome) 
	{
		this.cognome = cognome;
	}
	public String getIMEI() 
	{
		return IMEI;
	}
	public void setIMEI(String iMEI) 
	{
		IMEI = iMEI;
	}
	public String getCodiceFiscale() 
	{
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) 
	{
		this.codiceFiscale = codiceFiscale;
	}
	public String getLuogoNascita()
	{
		return luogoNascita;
	}
	public void setLuogoNascita(String luogoNascita)
	{
		this.luogoNascita = luogoNascita;
	}
	public String getEmail() 
	{
		return email;
	}
	public void setEmail(String email) 
	{
		this.email = email;
	}
	public Date getDataNascita() 
	{
		return dataNascita;
	}
	public void setDataNascita(Date dataNascita) 
	{
		this.dataNascita = dataNascita;
	}
	
	public boolean isAbilitato()
	{
		return abilitato;
	}
	
	public void setAbilitato(boolean abilitato)
	{
		this.abilitato = abilitato;
	}
	
}
