package it.quickorder.gui.table;

import it.quickorder.domain.Prodotto;
import it.quickorder.gui.Main;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class TipologiaTableCellRenderer extends DefaultTableCellRenderer 
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if (!(value instanceof Integer))
			throw new IllegalArgumentException("Il renderer non può essere applicato sul dato selezionato.");
		Integer valore = (Integer) value;
		JLabel nuova = new JLabel();
		nuova.setHorizontalAlignment(SwingConstants.CENTER);
		switch(valore)
		{
			case Prodotto.Primi:
				nuova.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "primi.png")));
				break;
			case Prodotto.Antipasto:
				nuova.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "antipasti.png")));
				break;
			case Prodotto.Secondi:
				nuova.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "secondi.png")));
				break;
			case Prodotto.Dessert:
				nuova.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "dolci.png")));
				break;
			case Prodotto.Bevande:
				nuova.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "bevanda24.png")));
				break;
			default:
				nuova.setText("" + valore);
				break;
		}
		return nuova;
	}
}
