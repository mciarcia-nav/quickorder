package it.quickorder.gui;

import it.quickorder.domain.Ordinazione;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DateFormat;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;


public class OrdinazioneIFrame extends JInternalFrame implements InternalFrameListener
{
	private JPanel jContentPane;
	private ScrollableTable prodotti;
	private JLabel labelTotale;
	private boolean emessoScontrino;
	
	public OrdinazioneIFrame (Ordinazione ord)
	{
		super("Ordinazione #" + ord.getId());
		setClosable(true);
		emessoScontrino = false;
		
		// Content Pane
		setLayout(new BorderLayout());
		jContentPane = new JPanel();
		jContentPane.setLayout(new GridBagLayout());
		
		
		// Pannello dati
		JPanel dati = new JPanel(new GridLayout(1,3));
		dati.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		JLabel nomeCliente = new JLabel("   " + ord.getCliente().getNome() + " " + ord.getCliente().getCognome());
		nomeCliente.setFont(Main.plainFont);
		ImageIcon icoCliente = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "customers48.png"));
		nomeCliente.setIcon(icoCliente);
		JLabel numeroTavolo = new JLabel("   " + ord.getNumeroTavolo());
		numeroTavolo.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "table.png")));
		numeroTavolo.setFont(Main.bigFont);
		
		JLabel tempoArrivo = new JLabel("   " + DateFormat.getTimeInstance(DateFormat.SHORT).format(ord.getArrivo()));
		ImageIcon icotempo = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "clock48.png"));
		tempoArrivo.setIcon(icotempo);
		tempoArrivo.setFont(Main.plainFont);
		JLabel numeroProdotti = new JLabel("Numero Prodotti: " + ord.getNumeroProdotti());
		
		dati.add(nomeCliente);
		dati.add(numeroTavolo);
		dati.add(tempoArrivo);
		
		// Pannello pulsanti
		JPanel pulsanti = new JPanel(new GridBagLayout());
		JButton emettiScontrino = new JButton("Emetti scontrino");
		emettiScontrino.setBackground(Color.GREEN);
		emettiScontrino.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "receipt.png")));
		
		JButton eliminaOrdinazione = new JButton("Elimina Ordinazione");
		eliminaOrdinazione.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "delete.png")));
		eliminaOrdinazione.setBackground(Color.RED);
		eliminaOrdinazione.setForeground(Color.WHITE);
		
		GridBagConstraints gd = new GridBagConstraints();
		gd.gridx = 0;
		gd.gridy = 0;
		gd.insets = new Insets(10,5,10,5);
		gd.ipadx = 36;
		gd.ipady = 30;
		pulsanti.add(emettiScontrino, gd);
		gd = new GridBagConstraints();
		gd.gridx = 0;
		gd.gridy = 1;
		gd.insets = new Insets(10,5,10,5);
		gd.ipadx = 20;
		gd.ipady = 30;
		pulsanti.add(eliminaOrdinazione, gd);
		
		// Pannello tabellaProdotti
		JPanel pannelloProdotti = new JPanel(new BorderLayout());
		OrdinazioneModel tableModel = new OrdinazioneModel();
		tableModel.caricaProdotti(ord);
		prodotti = new ScrollableTable(tableModel);
		prodotti.setRowHeight(32);
		prodotti.setAutoCreateColumnsFromModel(true);
		prodotti.setColumnSelectionAllowed(true);
		prodotti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		prodotti.setSelectionForeground(Color.RED);
		prodotti.setSelectionBackground(Color.white);
		prodotti.setShowVerticalLines(false);
		JScrollPane jScrollPane = new JScrollPane(prodotti);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		prodotti.getColumnModel().getColumn(0).setPreferredWidth(180);
		prodotti.getColumnModel().getColumn(1).setPreferredWidth(30);
		prodotti.getColumnModel().getColumn(2).setPreferredWidth(30);
		prodotti.getColumnModel().getColumn(3).setPreferredWidth(30);
		
		pannelloProdotti.add(jScrollPane, BorderLayout.CENTER);
		
		
		
		// Pannello Totale
		JPanel totale = new JPanel(new GridLayout(1,4));
		totale.add(new JLabel());
		totale.add(new JLabel());
		totale.add(new JLabel());
		labelTotale = new JLabel("Totale:" + ord.getTotale());
		labelTotale.setHorizontalTextPosition(SwingConstants.TRAILING);
		labelTotale.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		totale.add(labelTotale);
		
		
		
		// Impostazione layout content pane
		GridBagConstraints gb = new GridBagConstraints();
		gb.gridx = 0;
		gb.gridy = 0;
		gb.weightx = 0.8;
		gb.weighty = 0.1;
		gb.insets = new Insets(10,10,0,10);
		gb.fill = GridBagConstraints.BOTH;
		gb.ipady = 10;
		jContentPane.add(dati,gb);
		gb = new GridBagConstraints();
		gb.gridx = 1;
		gb.gridy = 0;
		gb.gridheight = 3;
		gb.weightx = 0.2;
		gb.weighty = 0.8;
		gb.fill = GridBagConstraints.BOTH;
		jContentPane.add(pulsanti,gb);
		gb = new GridBagConstraints();
		gb.gridx = 0;
		gb.gridy = 1;
		gb.insets = new Insets(10,10,0,10);
		gb.weightx = 0.8;
		gb.weighty = 0.8;
		gb.fill = GridBagConstraints.BOTH;
		jContentPane.add(pannelloProdotti, gb);
		gb = new GridBagConstraints();
		gb.gridx = 0;
		gb.gridy = 2;
		gb.weightx = 0.8;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.BOTH;
		jContentPane.add(totale, gb);
		
		
		jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(jContentPane, BorderLayout.CENTER);
		
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) 
	{
		if (!emessoScontrino)
		{
			DeskManager desk = (DeskManager) e.getInternalFrame().getDesktopPane().getDesktopManager();
			desk.iconifyFrame(e.getInternalFrame());
		}
		else
		{
			super.setVisible(false);
			super.dispose();
		}
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}
}
