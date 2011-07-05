package it.quickorder.domain;

import java.util.Date;

public class Notifica 
{
	private Cliente cliente;
	private Date arrivo;
	private Runnable threadPartenza;
	
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
	
	public Runnable getThreadPartenza()
	{
		return threadPartenza;
	}
	
	public void setThreadPartenza(Runnable threadPartenza)
	{
		this.threadPartenza = threadPartenza;
	}
	
}
