package it.quickorder.gui.table;

import it.quickorder.domain.Cliente;
import it.quickorder.repository.GetDataFromDB;
import it.quickorder.repository.GetDataFromDBImpl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.hibernate.type.descriptor.sql.BitTypeDescriptor;

import antlr.collections.impl.BitSet;

public class ClienteModel extends AbstractTableModel 
{
	private static final String[] headers = {"Codice Fiscale", "Indirizzo e-mail", "Nome", "Cognome", "Sesso", "Data di Nascita", "Luogo di nascita", "IMEI", "Stato Abilitazione"};
	private static final Class[] columnClasses = { String.class, String.class, String.class, String.class, Character.class, Timestamp.class, String.class, String.class, Boolean.class};
	private ArrayList<Object[]> data;
	private GetDataFromDB dataRecovery;
	
	public ClienteModel()
	{
		data = new ArrayList<Object[]>();
		dataRecovery = new GetDataFromDBImpl();
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
	
	public Cliente recuperaCliente(int row)
	{
		Cliente cliente = new Cliente();
		cliente.setCodiceFiscale((String)data.get(row)[0]);
		cliente.setEmail((String)data.get(row)[1]);
		cliente.setNome((String)data.get(row)[2]);
		cliente.setCognome((String)data.get(row)[3]);
		cliente.setSesso((Character)data.get(row)[4]);
		cliente.setDataNascita((Timestamp)data.get(row)[5]);
		cliente.setLuogoNascita((String)data.get(row)[6]);
		cliente.setIMEI((String)data.get(row)[7]);
		cliente.setAbilitato((Boolean)data.get(row)[8]);
		
		return cliente;
	}
	
	public List<Cliente> aggiornaClienti()
	{
		//Per far funzionare l'aggiornamento, l'arcano è che devi prima disabilitare(abilitare un altro cliente e poi premere Aggiorna.
		//Hai detto di fare la commit e la faccio.
		List<Cliente> nuoviClienti = dataRecovery.getClienti();
		data=new ArrayList<Object[]>();
		caricaClienti(nuoviClienti);
		return nuoviClienti;
	}
	
	public void modificaAbilitazioneCliente(int row, boolean abilitazione)
	{
		Object[] riga = data.get(row);
		riga[8] = abilitazione;
		data.set(row, riga);
	}
	
	public void eliminaCliente(int row)
	{
		data.remove(row);
	}
	
}
