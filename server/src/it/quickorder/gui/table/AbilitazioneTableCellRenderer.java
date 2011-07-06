package it.quickorder.gui.table;

import it.quickorder.gui.Main;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class AbilitazioneTableCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if (!(value instanceof Boolean))
			throw new IllegalArgumentException("Impossibile renderizzare il contenuto selezionato.");
		Boolean bool = (Boolean) value;
		JLabel componente = new JLabel();
		componente.setHorizontalAlignment(SwingConstants.CENTER);
		componente.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + (bool ? "abilitato.png" : "disabilitato.png")))); 
		
		return componente;
	}
}
