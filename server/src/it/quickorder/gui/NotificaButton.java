package it.quickorder.gui;

import it.quickorder.control.NotificheListener;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import it.quickorder.domain.Notifica;

@SuppressWarnings("serial")
public class NotificaButton extends JButton implements NotificheListener, ActionListener
{
	private int numeroNotifiche, raggio;
	private java.util.List<Notifica> notifiche;
	private JDesktopPane desktop;
	private NotificheIFrame notificheFrame;
	
	public NotificaButton(JDesktopPane desktop, int raggio)
	{
		numeroNotifiche = 0;
		this.raggio = raggio;
		this.desktop = desktop;
		notifiche = new ArrayList<Notifica>();
		notificheFrame = null;
		addActionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		Graphics2D gg = (Graphics2D) g;
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Rectangle area = gg.getClipBounds();
		Rectangle oval;
		if (numeroNotifiche < 10)
		{
			oval = new Rectangle(area.x + area.width - raggio * 2 - 5, area.y + 5, raggio * 2, raggio * 2);
		}
		else
		{
			oval = new Rectangle(area.x + area.width - (int) (raggio * 2.5) - 5, area.y + 5, (int) (raggio * 2.5), raggio * 2);
		}
		gg.setColor(Color.BLACK);
		gg.setStroke(new BasicStroke(2f));
		gg.drawOval(oval.x, oval.y, oval.width, oval.height);
		gg.setColor(Color.RED);
		gg.fillOval(oval.x+1, oval.y+1, oval.width-1, oval.height-1);
		gg.setColor(Color.WHITE);
		if (numeroNotifiche < 10)
			gg.drawString("" + numeroNotifiche, oval.x + oval.width / 3,  oval.y + oval.height - 2);
		else
			gg.drawString("" + numeroNotifiche, oval.x + 3,  oval.y + oval.height - 2);
		
	}

	@Override
	public void notificaRicevuta(Event event) 
	{
		synchronized (notifiche) 
		{
			
			try {
				numeroNotifiche++;
				playSound();
				notifiche.add((Notifica) event.arg);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		repaint();
		
	}
	
	private void playSound() throws Throwable
	{
		File file = new File(".\\nuova_ordinazione_sound.wav");
		AudioInputStream in = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(in);
		clip.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if (notificheFrame != null)
		{
			desktop.getDesktopManager().closeFrame(notificheFrame);
			notificheFrame.dispose();
			notificheFrame = null;
			return;
		}
		synchronized (notifiche)
		{
			if (numeroNotifiche > 0)
			{
				NotificheIFrame nuovo = new NotificheIFrame(notifiche);
				desktop.add(nuovo, Integer.MAX_VALUE);
				nuovo.addNotificheListener(this);
				nuovo.setVisible(true);
				Dimension size = nuovo.getSize();
				Dimension sizeButton = getSize();
				Point location = getLocationOnScreen();
				Point p = new Point();
				p.x = location.x + (int) (sizeButton.width / 2) - (int)(size.width / 2);
				p.y = location.y - 20 - size.height;	
				nuovo.setLocation(p);
				notificheFrame = nuovo;
			}
		}
	}

	@Override
	public void notificaGestita(Event event) 
	{
		synchronized (notifiche) 
		{
			notifiche.remove((Notifica)event.arg);
			numeroNotifiche--;
			if (event.id == -1)
				notificheFrame = null;
		}
		repaint();
	}
}
