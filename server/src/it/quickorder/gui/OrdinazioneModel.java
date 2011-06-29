package it.quickorder.gui;

import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;


public class OrdinazioneModel extends AbstractTableModel
{
	private static final String[] headers = { "Prodotto", "Quantità", "Prezzo","Subtotale"};
	private static final Class[] columnClasses = { String.class, Integer.class, Double.class,Double.class};
	private ArrayList<Object[]> data;
	
	public OrdinazioneModel()
	{
		data = new ArrayList<Object[]>();
	}
	
	@Override
	public int getColumnCount() 
	{
		return headers.length;
	}

	@Override
	public int getRowCount() 
	{
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) 
	{
		return data.get(row)[col];
	}

	public String getColumnName(int pColumn) throws IllegalArgumentException
	{
		return headers[pColumn];
	}
	
	public boolean isCellEditable(int pRow, int pColumn)
	{
		return false;
	}
	
	public static String[] getHeaders() 
	{
		return headers;
	}
	
	public void caricaProdotti(Ordinazione ord) throws IllegalArgumentException
	{
		if (ord == null)
		{
			throw new IllegalArgumentException("Il bean fornito non può essere null!");
		}
	/*	Iterator<Prodotto> pp = ord.getProdotti().iterator();
		while(pp.hasNext())
		{
			Prodotto p = pp.next();
			Object[] aRow = new Object[headers.length];
			aRow[0] = p.getNome();
			aRow[1] = p.getQuantita();
			aRow[2] = p.getPrezzo();
			aRow[3] = p.getSubtotale();
			data.add(aRow);
		}*/
		
	}
	
}
