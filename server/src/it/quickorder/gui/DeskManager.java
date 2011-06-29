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
	private JDesktopPane jDesktop;
	private JInternalFrame attivo;
	private HashMap<Integer, OrdinazioneIFrame> ordinazioni;
	
	public DeskManager(JDesktopPane jDesktop)
	{
		this.jDesktop = jDesktop;
		ordinazioni = new HashMap<Integer, OrdinazioneIFrame>(10);
	}
	
	public void dragFrame(JComponent pComponent, int pX, int pY)
	{
		if (!(pComponent instanceof JInternalFrame))
			return;
		if (pComponent instanceof StackIFrame)
		{
			Point corrente = pComponent.getLocation();
			if (pX < corrente.x)
			{
				super.dragFrame(pComponent, 20, corrente.y);
			}
			else
			{
				super.dragFrame(pComponent, jDesktop.getSize().width - 20 - pComponent.getSize().width, corrente.y);
				
			}
		}
	}

	public void openOrdinazione(Ordinazione ordinazione) 
	{
		JComponent stack = (JComponent) jDesktop.getComponent(0);
		Point stackLoc = stack.getLocation();
		Dimension stackSize = stack.getSize();
		Dimension deskSize = jDesktop.getSize();
		
		Dimension frameSize = new Dimension();
		frameSize.width = deskSize.width - stackSize.width - 20 - ((int)(deskSize.width * 0.1));
		frameSize.height = (int) (stackSize.height * 0.8);
		
		Point frameLoc = new Point();
		frameLoc.y = stackLoc.y + (int) ((stackSize.height - frameSize.height) / 2);
		frameLoc.x = 20 + ((int)(deskSize.width * 0.05));
		
		int id = ordinazione.getId();
		if (ordinazioni.isEmpty())
		{
			OrdinazioneIFrame o = new OrdinazioneIFrame(ordinazione);
			ordinazioni.put(id, o);
			jDesktop.add(o, Integer.MAX_VALUE);
			o.setSize(frameSize);
			o.setLocation(frameLoc);
			o.setVisible(true);
			attivo = o;
		}
		else
		{
			if (ordinazioni.containsKey(id))
			{
				iconifyFrame(attivo);
				attivo = ordinazioni.get(id);
				activateFrame(attivo);
			}
			else
			{
				if (attivo != null)
				{
					iconifyFrame(attivo);
				}
				OrdinazioneIFrame o = new OrdinazioneIFrame(ordinazione);
				ordinazioni.put(id, o);
				jDesktop.add(o, Integer.MAX_VALUE);
				o.setSize(frameSize);
				o.setLocation(frameLoc);
				o.setVisible(true);
				attivo = o;
			}
		}
	}
}
