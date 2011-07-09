package it.quickorder.repository;

import it.quickorder.domain.Cliente;

import java.util.List;

public interface DataRecovery 
{
	public List<Cliente> getClienti();
	
	public void abilitaCliente(Cliente cliente);
	
	public void disabilitaCliente(Cliente cliente);
	
	public void eliminaCliente(Cliente cliente);
}

