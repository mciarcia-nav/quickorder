package it.quickorder.gui;

import it.quickorder.control.OrdinazioniListener;
import it.quickorder.domain.Ordinazione;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class StackIFrame extends JInternalFrame implements OrdinazioniListener
{
	
	public static final int LEFT = 0, RIGHT = 1;
	private static final long serialVersionUID = 7374022043737716673L;
	private Dimension size;
	private Point location;
	private ArrayList<StackPanel> pannelli;
	public static Color verde_scuro = new Color(51,170,102);
	public static Color giallo_scuro = new Color(255,161,67);
	private JPanel contentPane;	
	private JDesktopPane desktop;
	private int dimY, numeroPannelli;
	
	public StackIFrame(JDesktopPane desktop, int numeroPannelli)
	{
		setTitle("Ordinazioni");
		setFrameIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "ordinazione24.png")));
		this.desktop = desktop;
		this.numeroPannelli = numeroPannelli;
		pannelli = new ArrayList<StackPanel>(numeroPannelli);
		contentPane = new JPanel(new GridLayout(numeroPannelli,1));
		setContentPane(contentPane);
		
	}
	
	public void costruisciInterfaccia()
	{
		Dimension deskSize = desktop.getSize();
		costruisciGUI(deskSize);
		posiziona(deskSize, RIGHT);

	}
	
	private void costruisciGUI(Dimension deskSize)
	{
		
		dimY = (int) Math.floor(deskSize.height / (numeroPannelli + 2));
		DeskManager desk = (DeskManager) desktop.getDesktopManager();
		for (int i = 0; i < numeroPannelli; i++)
		{
			StackPanel nuovo = new StackPanel(desk);
			nuovo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			contentPane.add(nuovo);
			pannelli.add(nuovo);
		}
		size = new Dimension();
		size.height = dimY * numeroPannelli + 13;
		size.width = 140;
		setResizable(false);
		setSize(size);
		startThreadTempi();
	}
	
	private void posiziona(Dimension deskSize, int type)
	{
		location = new Point();
		location.y = (int) (deskSize.height - size.height) / 2;
		if (type == LEFT)
		{			
			location.x = 30;	
		}
		else if (type == RIGHT)
		{
			location.x = deskSize.width - size.width - 30;
		}
		setLocation(location);
	}
	
	public Point getLocation(int type)
	{
		Point toReturn = new Point();
		if (type == LEFT)
		{			
			toReturn.x = 30;	
		}
		else if (type == RIGHT)
		{
			toReturn.x = desktop.getWidth() - size.width - 30;
		}
		toReturn.y = location.y;
		return toReturn;
	}
	
	public void aggiungiOrdinazione(Ordinazione ordinazione)
	{
		for (int index = 0; index < pannelli.size(); index++)
		{
			if (! (pannelli.get(index).hasOrdinazione()))
			{
				pannelli.get(index).setOrdinazione(ordinazione);
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
					int index = 0;
					StackPanel corrente = pannelli.get(index);
					while (index < pannelli.size() && corrente.hasOrdinazione())
					{
						Ordinazione ord = corrente.getOrdinazione();
						Date d = ord.getArrivo();
						int minuti = (int) (System.currentTimeMillis() - d.getTime()) / 60000;
						if (minuti == 0)
						{
							corrente.getBtn().setForeground(verde_scuro);
							corrente.getBtn().setText("< 1 min");
						}
						else
						{
							if (minuti < 10)
							{
								corrente.getBtn().setForeground(verde_scuro);
							}
							else if (minuti >= 10 && minuti < 15)
							{
								corrente.getBtn().setForeground(giallo_scuro);
							}
							else
							{
								corrente.getBtn().setForeground(Color.RED);
							}
							corrente.getBtn().setText("" + minuti + " min");
						}
						
						index++;
						if (index < pannelli.size())
							corrente = pannelli.get(index);
					}
					try 
					{
						Thread.sleep(10000);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			}		
		});
		tempi.start();
	}
	
	@Override
	public void ordinazioneRicevuta(Event evt) 
	{
		for (int index = 0; index < pannelli.size(); index++)
		{
			if (! (pannelli.get(index).hasOrdinazione()))
			{
				pannelli.get(index).setOrdinazione((Ordinazione)evt.arg);
				try {
					playSound();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}	
	}
	
	private void playSound() throws Throwable
	{
		File file = new File(".\\nuovo_cliente_sound.wav");
		AudioInputStream in = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(in);
		clip.start();
	}

	@Override
	public void ordinazioneRimossa(Event event) 
	{
		Ordinazione rimossa = (Ordinazione) event.arg;
		boolean trovato = false;
		int index = 0, size = pannelli.size();
		StackPanel corrente = null;
		while (!trovato && index < size)
		{
			corrente = pannelli.get(index);
			if (corrente.hasOrdinazione() && corrente.getOrdinazione().equals(rimossa))
			{
				trovato = true;
			}
			index++;
		}
		
		if (trovato)
		{
			contentPane.remove(corrente);
			contentPane.add(corrente);
			contentPane.doLayout();
			corrente.rimuoviOrdinazione();
			pannelli.remove(corrente);
			pannelli.add(size - 1, corrente);
		}	
	}
}
