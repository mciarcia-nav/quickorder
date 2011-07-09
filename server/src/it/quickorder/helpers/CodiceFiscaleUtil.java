package it.quickorder.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CodiceFiscaleUtil
{
	public final static char[] alfabeto = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	public final static char[] numeri = {'0','1','2','3','4','5','6','7','8','9'};
	public final static char[] vocali = {'a','e','i','o','u'};
	public final static char[] mesi = {'a','b','c','d','e','h','l','m','p','r','s','t'};
	public final static int[] pari = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};
	public final static int[] dispari = {1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23};
	
	@SuppressWarnings("deprecation")
	public static boolean isValid(String codiceFiscale, String nome, String cognome, Date dataNascita, char sesso, String luogoNascita) throws FileNotFoundException
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
			System.out.println("CF-Validator: Errore nelle consonanti del cognome.");
			System.out.println(check + " <> " + codiceFiscale.substring(0,3));
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
			System.out.println("CF-Validator: Errore nelle consonanti del nome.");
			System.out.println(check + " <> " + codiceFiscale.substring(3,6));
			return false;
		}
		
		int gg = dataNascita.getDate();
		if (sesso == 'F' || sesso == 'f')
			gg += 40;
		check = "" + dataNascita.getYear() + mesi[dataNascita.getMonth()] + ((gg >= 10)?gg:"0" + gg);
		if (!check.equalsIgnoreCase(codiceFiscale.substring(6, 11)))
		{
			System.out.println("CF-Validator: Errore nella data di nascita.");
			System.out.println(check + " <> " + codiceFiscale.substring(6,11));
			return false;
		}
		check = cercaComune(luogoNascita);
		if (check == null || !check.equalsIgnoreCase(codiceFiscale.substring(11,15)))
		{
			System.out.println("CF-Validator: Errore nel comune di nascita.");
			if (check == null)
				System.out.println("Codice Comune non trovato.");
			else
				System.out.println(check + " <> " + codiceFiscale.substring(11, 15));
			return false;
		}
		int somma = 0;
		for (int i = 0; i < 15; i++)
		{
			char cc = codiceFiscale.charAt(i);
			int index = Arrays.binarySearch(alfabeto, cc);
			if (index == -1)
			{
				index = Arrays.binarySearch(numeri, cc);
			}
			if (i % 2 != 0)				// PARI
			{
				somma += pari[index];
			}
			else
			{
				somma += dispari[index];
			}
		}
		if (codiceFiscale.charAt(15) == alfabeto[somma % 26])
			return true;
		else 
		{
			System.out.println("CF-Validator: Errore nel carattere di controllo.");
			System.out.println(alfabeto[somma % 26] + " <> " + codiceFiscale.charAt(15));
			return false;
		}
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
	
	private static String cercaComune(String comune) throws FileNotFoundException
	{

		File f = new File(".\\ITALIA.CSV");
		try
		{
			Scanner sc = new Scanner(f);
			StringTokenizer st;
			while(sc.hasNext())
			{
				st = new StringTokenizer(sc.nextLine(), ";");
				String codice = st.nextToken();
				String ccomune = st.nextToken();
				if (ccomune.equalsIgnoreCase(comune))
				{
					return codice;
				}
			}
			sc.close();
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
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

}
