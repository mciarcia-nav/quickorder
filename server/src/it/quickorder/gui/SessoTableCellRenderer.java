package it.quickorder.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class SessoTableCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if (!(value instanceof Character))
			throw new IllegalArgumentException("Impossibile renderizzare il contenuto selezionato.");
		Character c = (Character) value;
		JLabel componente = new JLabel();
		componente.setHorizontalAlignment(SwingConstants.CENTER);
		componente.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + (c == 'M' || c == 'm' ? "male24.png" : "female24.png")))); 
		
		return componente;
	}
}
