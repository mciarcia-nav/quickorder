package it.quickorder.gui.table;

import it.quickorder.domain.Cliente;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class ClienteModel extends AbstractTableModel 
{
	private static final String[] headers = {"Codice Fiscale", "Indirizzo E-Mail", "Nome", "Cognome", "Sesso", "Data di Nascita", "Luogo di nascita", "IMEI", "Stato"};
	@SuppressWarnings("rawtypes")
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

	@Override
	public String getColumnName(int pColumn) throws IllegalArgumentException
	{
		return headers[pColumn];
	}
	
	@Override
	public boolean isCellEditable(int pRow, int pColumn)
	{
		return false;
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
		
	public void aggiornaClienti(List<Cliente> clienti)
	{
		data.clear();
		caricaClienti(clienti);
		fireTableRowsUpdated(0, clienti.size());
	}
	
	public void eliminaCliente(int row)
	{
		data.remove(row);
		fireTableRowsDeleted(row, row);
	}

	public boolean isAttivato(int selected) 
	{
		return (Boolean) getValueAt(selected, 8);
	}

	public void abilitaCliente(int rowSelected) 
	{
		data.get(rowSelected)[8] = true;
		fireTableCellUpdated(rowSelected, 8);
		
	}

	public void disabilitaCliente(int rowSelected) 
	{
		data.get(rowSelected)[8] = false;
		fireTableCellUpdated(rowSelected, 8);	
	}
	
	public Cliente getClienteAtRow(int rowSelected)
	{
		Cliente c = new Cliente();
		c.setCodiceFiscale((String) getValueAt(rowSelected, 0));
		return c;
	}
	
}
