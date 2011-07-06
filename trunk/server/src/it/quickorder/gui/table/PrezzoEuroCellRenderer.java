package it.quickorder.gui.table;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class PrezzoEuroCellRenderer extends DefaultTableCellRenderer 
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{

		if (!(value instanceof Double))
			throw new IllegalArgumentException("Il renderer non può essere applicato sul dato selezionato.");
		Double valore = (Double) value;
		JLabel nuova = new JLabel();
		nuova.setHorizontalAlignment(SwingConstants.CENTER);
		DecimalFormat f = new DecimalFormat("##.00");
		nuova.setText("€ " + f.format(valore));
		return nuova;
	}
}
