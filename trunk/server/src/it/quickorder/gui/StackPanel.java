package it.quickorder.gui;

import it.quickorder.domain.Ordinazione;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class StackPanel extends JPanel 
{
	
	private Rectangle layoutArea;
	private Ordinazione ordinazione;
	private ImageIcon icoOrdinazione;
	private JButton btn;
	private DeskManager deskManager;
	
	
	public StackPanel(DeskManager deskManager)
	{
		this.deskManager = deskManager;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (hasOrdinazione())
		{
			btn.repaint();
			repaint();
			return;
		}
		
		Graphics2D gg = (Graphics2D) g;
		if (layoutArea == null)
		{
			
			Rectangle rect = gg.getClipBounds();
			Rectangle rect2 = (Rectangle) rect.clone();
		
			rect2.width = (int) (0.70 * rect2.width);
			rect2.height = (int) (0.70 * rect2.height);
			if (rect2.height < 48)
				rect2.height = 48;
			rect2.x += (int) ((rect.width - rect2.width) / 2);
			rect2.y += (int) ((rect.height - rect2.height) / 2);
			layoutArea = rect2;
		}
		
		gg.setColor(Color.BLACK);
		gg.drawRect(layoutArea.x-1, layoutArea.y-1, layoutArea.width, layoutArea.height);
		gg.setColor(Color.WHITE);
		gg.fill(layoutArea);
			
	}
	
	public void setOrdinazione(Ordinazione ordinazione)
	{
		this.ordinazione = ordinazione;
		btn = new JButton();
		btn.setVerticalAlignment(SwingConstants.TOP);
		btn.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn.setHorizontalTextPosition(SwingConstants.CENTER);		
		add(btn);
		if (layoutArea.height >= 56)
			icoOrdinazione = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "ordinazione.png"));
		else
			icoOrdinazione = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "ordinazione24.png"));
		btn.setIcon(icoOrdinazione);
		btn.setBorder(BorderFactory.createLoweredBevelBorder());
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(layoutArea.width, layoutArea.height));
		btn.setText("< 1 min");
		btn.setLocation(layoutArea.x, layoutArea.y);
		btn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				deskManager.openOrdinazione(getOrdinazione());
				
			}
			
		});
	}
	
	public Ordinazione getOrdinazione()
	{
		return ordinazione;
	}
	
	public boolean hasOrdinazione()
	{
		return ordinazione != null;
	}

	public JButton getBtn() 
	{
		return btn;
	}

	public void setBtn(JButton btn) 
	{
		this.btn = btn;
	}
	
	
}
