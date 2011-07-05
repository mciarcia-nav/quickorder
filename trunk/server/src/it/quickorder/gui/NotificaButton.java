package it.quickorder.gui;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class NotificaButton extends JButton 
{
	int numeroNotifiche, raggio;
	
	public NotificaButton(int raggio)
	{
		numeroNotifiche = 0;
		this.raggio = raggio;
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
	
	public void aggiungiNotifica()
	{
		numeroNotifiche++;
		repaint();
	}
}
