package it.quickorder.gui;

import it.quickorder.domain.Cliente;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.hibernate.type.descriptor.sql.BitTypeDescriptor;

import antlr.collections.impl.BitSet;

public class ClienteModel extends AbstractTableModel 
{
	private static final String[] headers = {"Codice Fiscale", "Indirizzo e-mail", "Nome", "Cognome", "Sesso", "Data di Nascita", "Luogo di nascita", "IMEI", "Stato Abilitazione"};
	private static final Class[] columnClasses = { String.class, String.class, String.class, String.class, Character.class, Date.class, String.class, String.class, Boolean.class};
	private ArrayList<Object[]> data;
	
	public ClienteModel()
	{
		data = new ArrayList<Object[]>();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		return columnClasses[columnIndex];
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
	
	public void caricaClienti(List<Cliente> clienti)
	{
		for(Cliente c : clienti)
        {
        	Object[] aRow = new Object[headers.length];
        	aRow[0] = c.getCodiceFiscale();
        	aRow[1] = c.getEmail();
        	aRow[2] = c.getNome();
        	aRow[3] = c.getCognome();
        	aRow[4] = c.getSesso();
        	aRow[5] = c.getDataNascita();
        	aRow[6] = c.getLuogoNascita();
        	aRow[7] = c.getIMEI();
        	aRow[8] = c.isAbilitato();
        	data.add(aRow);
        }
	}
}
