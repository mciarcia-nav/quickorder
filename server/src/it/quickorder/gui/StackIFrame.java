package it.quickorder.gui;

import it.quickorder.domain.Ordinazione;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

public class StackIFrame extends JInternalFrame 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7374022043737716673L;
	private Dimension size;
	private Point location;
	private ArrayList<StackPanel> pannelli;
	public static Color verde_scuro = new Color(51,153,102);
	public static Color giallo_scuro = new Color(255,204,51);
	private JPanel contentPane;
	
	public StackIFrame(JDesktopPane jDesktop)
	{
		Dimension deskSize = jDesktop.getSize();
		int dimY = (int) Math.floor(deskSize.height / 12);
		size = new Dimension();
		size.height = dimY * 10 + 13;
		size.width = 140;
		setResizable(false);
		setSize(size);
		location = new Point();
		location.x = deskSize.width - size.width - 30;
		location.y = (int) (deskSize.height - size.height) / 2;
		setLocation(location);
		contentPane = new JPanel(new GridLayout(10,1));
		pannelli = new ArrayList<StackPanel>(10);
		DeskManager desk = (DeskManager) jDesktop.getDesktopManager();
		for (int i = 0; i < 10; i++)
		{
			StackPanel nuovo = new StackPanel(desk);
			nuovo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			contentPane.add(nuovo);
			pannelli.add(nuovo);
		}
		setContentPane(contentPane);	
	}
	
	public void aggiungiOrdinazione(Ordinazione ordinazione)
	{
		for (StackPanel o : pannelli)
		{
			if (!o.hasOrdinazione())
			{
				o.setOrdinazione(ordinazione);
				startThreadTempi();
				return;
			}
		}
	}
	
	private void startThreadTempi()
	{
		Thread tempi = new Thread(new Runnable()
		{

			@Override
			public void run() 
			{
				while(true)
				{
					for (StackPanel o : pannelli)
					{
						if (o.hasOrdinazione())
						{
							Ordinazione ord = o.getOrdinazione();
							Date d = ord.getArrivo();
							int minuti = (int) (System.currentTimeMillis() - d.getTime()) / 60000;
							if (minuti == 0)
							{
								o.getBtn().setForeground(verde_scuro);
								o.getBtn().setText("< 1 min");
							}
							else
							{
								if (minuti < 10)
								{
									o.getBtn().setForeground(verde_scuro);
								}
								else if (minuti >= 10 && minuti < 15)
								{
									o.getBtn().setForeground(giallo_scuro);
								}
								else
								{
									o.getBtn().setForeground(Color.RED);
								}
								o.getBtn().setText("" + minuti + " min");
							}
						}
						else
						{
							break;
						}
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				
			}
			
		});
		tempi.start();
	}
}
