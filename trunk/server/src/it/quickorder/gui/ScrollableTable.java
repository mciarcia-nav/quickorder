/*
 * ScrollableTable.java
 *
 * 1.0
 *
 * 21/05/2007
 *
 * � 2007 eTour Project - Copyright by SE@SA Lab - DMI � University of Salerno
 */
package it.quickorder.gui;

import java.awt.*;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.table.*;

/**
 * Crea una JTable personalizzata che pu&ograve; essere visualizzata tramite
 * componenti che consentono lo scrolling.
 * 
 * @see javax.swing.JTable
 * @see javax.swing.Scrollable
 * @author Mario Gallo
 * @version 1.0
 * 
 */
public class ScrollableTable extends JTable implements Scrollable
{
	private static final long serialVersionUID = 1L;
	private static final int maxUnitIncrement = 20;
	
	public ScrollableTable()
	{
		super();
	}
	
	public ScrollableTable(TableModel tm)
	{
		super(tm);
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction)
	{

		int posCorrente = 0;
		if (orientation == SwingConstants.HORIZONTAL)
		{
			posCorrente = visibleRect.x;
		}
		else
		{
			posCorrente = visibleRect.y;
		}

		if (direction < 0)
		{
			/*
			 *	La nuova posizione � data dalla corrente meno il numero di
			 *	posizioni per l'incremento unitario.
			 * 
			 */
			int newPos = posCorrente - (posCorrente / maxUnitIncrement)
					* maxUnitIncrement;
			return (newPos == 0) ? maxUnitIncrement : newPos;
		}
		else
		{
			return ((posCorrente / maxUnitIncrement) + 1) * maxUnitIncrement
					- posCorrente;
		}
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction)
	{
		if (orientation == SwingConstants.HORIZONTAL)
		{
			return visibleRect.width - maxUnitIncrement;
		}
		else
		{
			return visibleRect.height - maxUnitIncrement;
		}
	}

	public boolean getScrollableTracksViewportWidth()
	{
		Component parent = getParent();
		if ( parent instanceof JViewport) 
		{
			return parent.getWidth() > getPreferredSize().width;
		}
		return false;
	}

	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}
}