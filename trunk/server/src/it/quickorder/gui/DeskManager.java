package it.quickorder.gui;

import it.quickorder.domain.Ordinazione;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class DeskManager extends DefaultDesktopManager 
{
	private static final long serialVersionUID = 6182103251037333895L;
	private JDesktopPane jDesktop;
	private JInternalFrame attivo;
	private int ordinazioniAttive, ordinazioniMassime;
	private HashMap<Integer, OrdinazioneIFrame> ordinazioni;
	
	private static final int SCOSTAMENTO_ORIZZONTALE = 15, SCOSTAMENTO_VERTICALE = 30;
	
	public DeskManager(JDesktopPane jDesktop, int numeroOrdinazioni)
	{
		this.jDesktop = jDesktop;
		ordinazioniAttive = 0;
		ordinazioniMassime = numeroOrdinazioni;
		ordinazioni = new HashMap<Integer, OrdinazioneIFrame>(ordinazioniMassime);
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
		if (ordinazioni.isEmpty())
		{
			OrdinazioneIFrame o = new OrdinazioneIFrame(ordinazione);
			ordinazioni.put(id, o);
			jDesktop.add(o, Integer.MAX_VALUE);
			o.setSize(frameSize);
			o.setLocation(frameLoc);
			o.setVisible(true);
		}
		else
		{
			if (ordinazioni.containsKey(id))
			{
				
				OrdinazioneIFrame nuova = ordinazioni.get(id);
				activateFrame(nuova);
			}
			else
			{
				OrdinazioneIFrame o = new OrdinazioneIFrame(ordinazione);
				ordinazioni.put(id, o);
				jDesktop.add(o, Integer.MAX_VALUE);
				o.setSize(frameSize);
				o.setLocation(frameLoc);
				o.setVisible(true);
			}
		}
	}
}
