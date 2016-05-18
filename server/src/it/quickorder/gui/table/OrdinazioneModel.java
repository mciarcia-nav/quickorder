package it.quickorder.gui.table;

import it.quickorder.domain.Articolo;
import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class OrdinazioneModel extends AbstractTableModel
{
	private static final String[] headers = { "Tipologia", "Prodotto", "Quantità","Prezzo","Subtotale", "Note"};
	@SuppressWarnings("rawtypes")
	private static final Class[] columnClasses = { Integer.class, String.class, Integer.class, Double.class, Double.class, String.class};
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
	
	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		return columnClasses[columnIndex];
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
		Iterator<Articolo> aa = ord.getArticoli().iterator();
		ArrayList<Object[]> bevande = new ArrayList<Object[]>();
		while (aa.hasNext())
		{
			Articolo a = aa.next();
			Object[] aRow = new Object[headers.length];
			aRow[1] = a.getProdotto().getNome();
			aRow[0] = a.getProdotto().getTipologia();
			aRow[3] = a.getProdotto().getPrezzo();
			aRow[2] = a.getQuantita();
			aRow[4] = a.getSubTotale();
			aRow[5] = a.getNote();
			if (a.getProdotto().getTipologia() == Prodotto.Primi ||
					a.getProdotto().getTipologia() == Prodotto.Antipasto ||
					a.getProdotto().getTipologia() == Prodotto.Secondi ||
					a.getProdotto().getTipologia() == Prodotto.Dessert)
			{
				data.add(aRow);
			}
			else
			{
				bevande.add(aRow);
			}
		}
		data.addAll(bevande);
	}
	
}
