package it.quickorder.gui;

import it.quickorder.control.StackOrdinazioni;
import it.quickorder.domain.Ordinazione;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.HashMap;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class DeskManager extends DefaultDesktopManager 
{
	private static final long serialVersionUID = 6182103251037333895L;
	private JDesktopPane jDesktop;
	private int ordinazioniAttive, ordinazioniMassime;
	private HashMap<Integer, OrdinazioneIFrame> ordinazioni;
	private StackOrdinazioni stackOrdinazioni;
	private static final int SCOSTAMENTO_ORIZZONTALE = 15, SCOSTAMENTO_VERTICALE = 30;
	
	public DeskManager(JDesktopPane jDesktop, StackOrdinazioni stack,int numeroOrdinazioni)
	{
		this.stackOrdinazioni = stack;
		this.jDesktop = jDesktop;
		ordinazioniAttive = 0;
		ordinazioniMassime = numeroOrdinazioni;
		ordinazioni = new HashMap<Integer, OrdinazioneIFrame>(ordinazioniMassime);
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
		jDesktop.repaint();
	}

	public void dragFrame(JComponent pComponent, int pX, int pY)
	{
		if (!(pComponent instanceof JInternalFrame))
			return;
		if (pComponent instanceof StackIFrame)
		{
			StackIFrame stack = (StackIFrame) pComponent;
			Point corrente = stack.getLocation();
			if (pX < corrente.x)
			{
				Point left = stack.getLocation(StackIFrame.LEFT);
				super.dragFrame(pComponent, left.x, left.y);
			}
			else
			{
				Point right = stack.getLocation(StackIFrame.RIGHT);
				super.dragFrame(pComponent, right.x, right.y);
				
			}
		}
	}

	public void openOrdinazione(Ordinazione ordinazione) 
	{
		StackIFrame stack = (StackIFrame) jDesktop.getComponent(0);
		Point stackLoc = stack.getLocation();
		Dimension stackSize = stack.getSize();
		Dimension deskSize = jDesktop.getSize();
		Dimension frameSize = new Dimension();
		
		frameSize.width = deskSize.width - stackSize.width - 80 - SCOSTAMENTO_ORIZZONTALE * 5;
		frameSize.height = (int) (stackSize.height * 0.75) - SCOSTAMENTO_VERTICALE * 5;
		
		Point frameLoc = new Point();
		if (stack.getLocation().x == 30)
		{
			frameLoc.x = 50 + stack.getWidth() + (15 * ordinazioniAttive);
		}
		else
		{
			frameLoc.x = 30 + 15 * ordinazioniAttive;
		}
		frameLoc.y = stackLoc.y + (int) ((stackSize.height - frameSize.height) / 4) + 30 * ordinazioniAttive;
		
		
		ordinazioniAttive++;
		
		int id = ordinazione.getId();
		if (ordinazioni.containsKey(id))
		{
			
			OrdinazioneIFrame nuova = ordinazioni.get(id);
			jDesktop.add(nuova, Integer.MAX_VALUE - 1);
			activateFrame(nuova);
			System.out.println("ECCOMI");
		}
		else
		{
			OrdinazioneIFrame o = new OrdinazioneIFrame(stackOrdinazioni, ordinazione);
			ordinazioni.put(id, o);
			o.setDesktop(jDesktop);
			jDesktop.add(o, Integer.MAX_VALUE - 1);
			o.setSize(frameSize);
			o.setLocation(frameLoc);
			o.setVisible(true);
		}
	}
	
	public void rimuoviOrdinazione(Ordinazione ordinazione)
	{
		if (ordinazioni.containsKey(ordinazione.getId()))
			ordinazioni.remove(ordinazione.getId());
	}
}
