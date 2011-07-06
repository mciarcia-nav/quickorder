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
			case Prodotto.PANINO:
				nuova.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "panino24.png")));
				break;
			case Prodotto.BEVANDA:
				nuova.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "panino24.png")));
				break;
			default:
				nuova.setText("" + valore);
				break;
		}
		return nuova;
	}
}
