package it.quickorder.gui;

import it.quickorder.control.StackOrdinazioni;
import it.quickorder.domain.Ordinazione;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class DeskManager extends DefaultDesktopManager 
{
	private static final long serialVersionUID = 6182103251037333895L;
	private JDesktopPane jDesktop;
	private int ordinazioniMassime;
	private Map<Long, OrdinazioneIFrame> ordinazioni;
	private List<OrdinazioneIFrame> ordinazioniAttive;
	private StackOrdinazioni stackOrdinazioni;
	private static final int SCOSTAMENTO_ORIZZONTALE = 15, SCOSTAMENTO_VERTICALE = 30;
	private final static int MAX_WIDTH = 1100;
	private final static int MAX_HEIGHT = 600;
	private StackIFrame stackFrame;
	private Point draggingPoint;
	private JInternalFrame chiusura;
	
	public DeskManager(JDesktopPane jDesktop, StackOrdinazioni stack,int numeroOrdinazioni)
	{
		this.stackOrdinazioni = stack;
		this.jDesktop = jDesktop;
		ordinazioniMassime = numeroOrdinazioni;
		ordinazioni = new HashMap<Long, OrdinazioneIFrame>(ordinazioniMassime);
		ordinazioniAttive = new ArrayList<OrdinazioneIFrame>();
	}
	
	public void activateFrame(JInternalFrame frame)
	{
		try
		{
			if (frame.isIcon())
			{
				frame.setIcon(false);
			}
			frame.setSelected(true);
			frame.setVisible(true);
			super.activateFrame(frame);
		}
		catch (PropertyVetoException e)
		{
			e.printStackTrace();
		}

	}
	
	@Override
	public void iconifyFrame(JInternalFrame f) 
	{
		jDesktop.remove(f);

	}
	
	@Override
	public void closeFrame(JInternalFrame f) 
	{
		super.closeFrame(f);
		ordinazioniAttive.remove(f);
		jDesktop.repaint();
	}

	@Override
	public void endDraggingFrame(JComponent f) 
	{
		if (f instanceof StackIFrame)
		{
			Point endDragging = f.getLocation();
			if (endDragging.equals(draggingPoint))
				return;
			int stackWidth = f.getWidth();
			JInternalFrame[] jj = jDesktop.getAllFrames();
			boolean sottrai = (f.getX() == 30);
			for (int i = 0; i < jj.length; i++)
				if (jj[i] instanceof OrdinazioneIFrame || jj[i] instanceof ClientiIFrame)
				{
					Point loc = jj[i].getLocation();
					loc.x = sottrai ? loc.x - 30 - stackWidth : loc.x + 30 + stackWidth;
					jj[i].setLocation(loc);
				}
		}
		super.endDraggingFrame(f);
	}
	
	public void dragFrame(JComponent pComponent, int pX, int pY)
	{
		if (!(pComponent instanceof JInternalFrame))
			return;
		if (pComponent instanceof StackIFrame)
		{
			StackIFrame stack = (StackIFrame) pComponent;
			stackFrame = stack;
			Point corrente = stack.getLocation();
			cercaChiusura();
			Point chiusuraLoc = chiusura.getLocation();
			if (pX < corrente.x)
			{
				Point left = stack.getLocation(StackIFrame.LEFT);
				draggingPoint = left;
				super.dragFrame(pComponent, left.x, left.y);
				chiusuraLoc.x = left.x;
				
			}
			else
			{
				Point right = stack.getLocation(StackIFrame.RIGHT);
				draggingPoint = right;
				super.dragFrame(pComponent, right.x, right.y);
				chiusuraLoc.x = right.x;
			}
			chiusura.setLocation(chiusuraLoc);
		}
		else if (pComponent instanceof OrdinazioneIFrame || pComponent instanceof ClientiIFrame)
		{
			cercaStackFrame();
			Point stackLoc = stackFrame.getLocation();
			Dimension stackSize = stackFrame.getSize();
			Dimension deskSize = jDesktop.getSize();
			Dimension frameSize = pComponent.getSize();
			
			if (pX < 0)
				pX = 0;
			else if (pX > deskSize.width - frameSize.width)
				pX = deskSize.width - frameSize.width;
			if (pY < 0)
				pY = 0;
			else if (pY > stackLoc.y + stackSize.height - frameSize.height)
				pY = stackLoc.y + stackSize.height - frameSize.height;
			
			if (stackLoc.x == 30)
			{
				if (pX < 30 + stackSize.width + 1)
				{
					pX = 30 + stackSize.width + 1;
				}	
			}
			else
			{
				if (pX > stackLoc.x - 1 - frameSize.width)
					pX = stackLoc.x - 1 - frameSize.width;
			}
			super.dragFrame(pComponent, pX, pY);
		}
	}

	private void cercaChiusura() 
	{
		if (chiusura == null)
		{
			
			JInternalFrame[] jj = jDesktop.getAllFrames();
			boolean trovato = false;
			int index = 0;
			while (!trovato && index < jj.length)
			{
				if (jj[index].getX() == stackFrame.getX() && jj[index].getY() < stackFrame.getY())
					trovato = true;
				else
					index++;
			}
			chiusura = jj[index];
		}
		
	}

	public void openOrdinazione(Ordinazione ordinazione) 
	{
		long id = ordinazione.getId();
		// Il frame dell'ordinazione corrente è stato già creato.
		if (ordinazioni.containsKey(id))
		{
			
			OrdinazioneIFrame nuova = ordinazioni.get(id);
			// Il frame dell'ordinazione corrente non è correntemente visualizzato.
			if (!ordinazioniAttive.contains(nuova))
			{
				jDesktop.add(nuova, Integer.MAX_VALUE - 1);
				ordinazioniAttive.add(nuova);
			}
			activateFrame(nuova);
			return;
		}
		
		// Deve essere creato un nuovo frame per l'ordinazione corrente.
		cercaStackFrame();
		Point stackLoc = stackFrame.getLocation();
		Dimension stackSize = stackFrame.getSize();
		Dimension deskSize = jDesktop.getSize();
		Dimension frameSize = new Dimension();
		frameSize.width = deskSize.width - stackSize.width - 80 - SCOSTAMENTO_ORIZZONTALE * ordinazioniMassime - (int) (deskSize.width * 0.1);
		frameSize.height = (int) (stackSize.height * 0.9) - SCOSTAMENTO_VERTICALE * ordinazioniMassime; 
		
		if (frameSize.width > MAX_WIDTH)
			frameSize.width = MAX_WIDTH;
		if (frameSize.height > MAX_HEIGHT)
			frameSize.height = MAX_HEIGHT;
		
		Point frameLoc = new Point();
		if (stackFrame.getLocation().x == 30)
		{
			int spazioDisponibile = deskSize.width - stackLoc.x -  stackSize.width;
			frameLoc.x = 30 + stackSize.width + (int) ((spazioDisponibile - frameSize.width) / 2) + 15 * ordinazioniAttive.size();
		}
		else
		{
			int spazioDisponibile = deskSize.width -  stackSize.width - 30;
			frameLoc.x = 30 + (int) ((spazioDisponibile - frameSize.width) / 2) + 15 * ordinazioniAttive.size();
		}
		frameLoc.y = stackLoc.y + (int) ((stackSize.height - frameSize.height) / 4) + 30 * ordinazioniAttive.size();
		
		OrdinazioneIFrame o = new OrdinazioneIFrame(stackOrdinazioni, ordinazione);
		ordinazioni.put(id, o);
		ordinazioniAttive.add(o);
		o.setDesktop(jDesktop);
		jDesktop.add(o, Integer.MAX_VALUE - 1);
		o.setSize(frameSize);
		o.setLocation(frameLoc);
		o.setVisible(true);
	}
	
	public void rimuoviOrdinazione(Ordinazione ordinazione)
	{
		if (ordinazioni.containsKey(ordinazione.getId()))
			ordinazioni.remove(ordinazione.getId());
	}
	
	private void cercaStackFrame()
	{
		if (stackFrame == null)
		{
			JInternalFrame[] jj = jDesktop.getAllFrames();
			boolean trovato = false;
			int index = 0;
			while (!trovato && index < jj.length)
			{
				if (jj[index] instanceof StackIFrame)
					trovato = true;
				else
					index++;
			}
			stackFrame = (StackIFrame) jj[index];
		}
	}
}
