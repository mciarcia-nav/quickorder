package it.quickorder.helpers;

import it.quickorder.domain.Cliente;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
			{
				risultato[6] = false;
			}
			else
			{
				risultato[6] = ControlloParzialeCodiceFiscale.isValid(cliente.getCodiceFiscale(), cliente.getNome(), cliente.getCognome(), cliente.getDataNascita(), cliente.getSesso());
			}
		}
		
		risultato[0] = true;
		for (int i=1; i < 7; i++)
			risultato[0] = risultato[0] & risultato[i];

		return risultato;		
	}
}


class ControlloParzialeCodiceFiscale 
{
	public final static char[] alfabeto = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	public final static char[] numeri = {'0','1','2','3','4','5','6','7','8','9'};
	public final static char[] vocali = {'a','e','i','o','u'};
	public final static char[] mesi = {'a','b','c','d','e','h','l','m','p','r','s','t'};
	public final static int[] pari = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};
	public final static int[] dispari = {1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23};
	
	public static boolean isValid(String codiceFiscale, String nome, String cognome, Date dataNascita, char sesso)
	{
		codiceFiscale = codiceFiscale.toLowerCase();
		nome = normalize(nome);
		cognome = normalize(cognome);
		List<Character> consonantiCognome = extractConsonanti(cognome);
		if (consonantiCognome.size() < 3)
		{
			List<Character> vocali = extractVocali(cognome);
			while (consonantiCognome.size() < 3 && vocali.size() > 0)
			{ 
				consonantiCognome.add(vocali.get(0));
				vocali.remove(0);
			}
			for (int j = consonantiCognome.size(); j < 3; j++)
				consonantiCognome.add('x');
		}
		String check = regroup(consonantiCognome);
		if (!codiceFiscale.substring(0, 3).equalsIgnoreCase(check))
		{
			return false;
		}
		List<Character> consonantiNome = extractConsonanti(nome);
		if (consonantiNome.size() >= 4)
			consonantiNome.remove(1);
		if (consonantiNome.size() < 3)
		{
			List<Character> vocali = extractVocali(nome);
			while (consonantiNome.size() < 3 && vocali.size() > 0)
			{ 
				consonantiNome.add(vocali.get(0));
				vocali.remove(0);
			}
			for (int j = consonantiNome.size(); j < 3; j++)
				consonantiNome.add('x');
		}
		check = regroup(consonantiNome);
		if (!codiceFiscale.substring(3, 6).equalsIgnoreCase(check))
		{
			return false;
		}
		
		int gg = dataNascita.getDate();
		if (sesso == 'F' || sesso == 'f')
			gg += 40;
		check = "" + dataNascita.getYear() + mesi[dataNascita.getMonth()] + ((gg >= 10)?gg:"0" + gg);
		if (!check.equalsIgnoreCase(codiceFiscale.substring(6, 11)))
		{
			return false;
		}
		return true;
	}
	
	private static String normalize(String stringa)
	{
		stringa = stringa.trim();
		stringa = stringa.toLowerCase();
		stringa = stringa.replace(" ", "");
		stringa = stringa.replace("'", "");
		return stringa;
	}
	
	private static String regroup(List<Character> collection)
	{
		return "" + collection.get(0) + collection.get(1) + collection.get(2);
	}
	
	protected static List<Character> extractConsonanti(String stringa)
	{
		List<Character> consonanti = new ArrayList<Character>();
		stringa = stringa.toLowerCase();
		for (int i = 0; i < stringa.length(); i++)
		{
			int j;
			for (j = 0; j < vocali.length; j++)
			if (stringa.charAt(i) == vocali[j])
			{
				break;
			}
			if (j == vocali.length)
				consonanti.add(stringa.charAt(i));
		}
		return consonanti;
	}
	
	protected static List<Character> extractVocali(String stringa)
	{
		List<Character> vocalii = new ArrayList<Character>();
		stringa = stringa.toLowerCase();
		for (int i = 0; i < stringa.length(); i++)
		{
			int j;
			for (j = 0; j < vocali.length; j++)
			if (stringa.charAt(i) == vocali[j])
			{
				vocalii.add(stringa.charAt(i));
				break;
			}
		}
		return vocalii;
	}
}
