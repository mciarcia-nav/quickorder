package it.quickorder.helpers;

import it.quickorder.domain.Cliente;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControlloDati 
{
	public static boolean[] checkBeanCliente(Cliente cliente)
	{
		int len;
		Pattern pat;
		Matcher match;
		boolean[] risultato = new boolean[7];
		
		// Nome
		if (cliente.getNome() == null)
			risultato[1] = false;
		else
		{
			len = cliente.getNome().length();
			if (len < 3 || len > 30)
				risultato[1] = false;
			else
			{
				pat = Pattern.compile("[a-z|A-Z|\\s]{3,30}");
				match = pat.matcher(cliente.getNome());
				risultato[1] = match.matches();		
			}
		}
				
		// Cognome
		if (cliente.getCognome() == null)
		{
			risultato[2] = false;
		}
		else
		{
			len = cliente.getCognome().length();
			if (len < 3 || len > 30)
				risultato[2] = false;
			else
			{
				pat = Pattern.compile("[a-z|A-Z|\\s]{3,30}");
				match = pat.matcher(cliente.getCognome());
				risultato[2] = match.matches();
			}
		}
		
		//Data di Nascita
		if (cliente.getDataNascita() == null)
			risultato[3] = false;
		else
		{
			risultato[3] = cliente.getDataNascita().before(new Date(97, 9, 1));
		}
		
		
		//Luogo di Nascita
		if (cliente.getLuogoNascita() == null)
			risultato[4] = false;
		else
		{
			len = cliente.getLuogoNascita().length();
			if (len < 3 || len > 30)
				risultato[4] = false;
			else
			{
				pat = Pattern.compile("[a-z|A-Z|\\s]{3,30}");
				match = pat.matcher(cliente.getLuogoNascita());
				risultato[4] = match.matches();	
			}
		}
		
		
		//E-Mail
		if (cliente.getEmail() == null)
			risultato[5] = false;
		else
		{
			len = cliente.getEmail().length();
			if (len < 5 || len > 35)
				risultato[5] = false;
			else
			{
				pat = Pattern.compile("^(([A-Za-z0-9]+_+)|([A-Za-z0-9]+\\-+)|([A-Za-z0-9]+\\."
						+ "+)|([A-Za-z0-9]+\\++))*[A-Za-z0-9]+@((\\w+\\-+)|(\\w+\\.))*\\w{1,63}\\." + 
						"[a-zA-Z]{2,6}$");
				match = pat.matcher(cliente.getEmail());
				risultato[5] = match.matches();	
			}
		}
				
		// CodiceFiscale
		if (cliente.getCodiceFiscale() == null)
			risultato[6] = false;
		else
		{
			if (cliente.getCodiceFiscale().length() != 16)
				risultato[6] = false;
			else
			{
				// TODO: checkCodiceFiscale
				risultato[6] = true;
			}
		}
		risultato[0] = true;
		for (int i=1; i < 7; i++)
			risultato[0] = risultato[0] & risultato[i];

		return risultato;		
	}
}
