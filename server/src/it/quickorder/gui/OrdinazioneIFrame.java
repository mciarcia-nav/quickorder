package it.quickorder.gui;

import it.quickorder.control.StackOrdinazioni;
import it.quickorder.domain.Ordinazione;
import it.quickorder.gui.table.OrdinazioneModel;
import it.quickorder.gui.table.PrezzoEuroCellRenderer;
import it.quickorder.gui.table.ScrollableTable;
import it.quickorder.gui.table.TipologiaTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;


@SuppressWarnings("serial")
public class OrdinazioneIFrame extends JInternalFrame implements InternalFrameListener, ActionListener
{
	private JPanel jContentPane;
	private ScrollableTable prodotti;
	private JLabel labelTotale;
	private boolean emessoScontrino;
	private JButton emettiScontrino, eliminaOrdinazione;
	private StackOrdinazioni stackOrdinazioni;
	private Ordinazione ordinazione;
	private JDesktopPane desktop;
	
	public OrdinazioneIFrame (StackOrdinazioni stackOrdinazioni, Ordinazione ordinazione)
	{
		super("Ordinazione #" + ordinazione.getId());
		setFrameIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "ordinazione24.png")));
		setClosable(true);
		
		// Oggetti
		emessoScontrino = false;
		this.ordinazione = ordinazione;
		this.stackOrdinazioni = stackOrdinazioni;
		
		addInternalFrameListener(this);
		
		costruisciInterfaccia();
		
	}

	public JDesktopPane getDesktop() 
	{
		return desktop;
	}

	public void setDesktop(JDesktopPane desktop) 
	{
		this.desktop = desktop;
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
			desktop.getDesktopManager().iconifyFrame(this);
		}
		else
		{
			desktop.getDesktopManager().closeFrame(this);
			stackOrdinazioni.rimuoviOrdinazione(ordinazione);
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

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource().equals(eliminaOrdinazione))
		{
			int choice = JOptionPane.showInternalConfirmDialog(this, "Sei sicuro di voler eliminare l'ordinazione corrente?", 
					"Conferma Eliminazione Ordinazione", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION)
			{
				getDesktopPane().getDesktopManager().closeFrame(this);
				stackOrdinazioni.rimuoviOrdinazione(ordinazione);
			}
		}
		else if (e.getSource().equals(emettiScontrino))
		{
			emessoScontrino = true;
			emettiScontrino.setEnabled(false);
			eliminaOrdinazione.setEnabled(false);
		}
		
	}
	

	private void costruisciInterfaccia()
	{
		
		// Content Pane
		setLayout(new BorderLayout());
		jContentPane = new JPanel();
		jContentPane.setLayout(new GridBagLayout());
		
		// Pannello dati
		JPanel dati = new JPanel(new GridLayout(1,3));
		dati.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		JLabel nomeCliente = new JLabel("   " + ordinazione.getCliente().getNome() + " " + ordinazione.getCliente().getCognome());
		nomeCliente.setFont(Main.plainFont);
		ImageIcon icoCliente = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "customers48.png"));
		nomeCliente.setIcon(icoCliente);
		JLabel numeroTavolo = new JLabel("   " + ordinazione.getNumeroTavolo());
		numeroTavolo.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "table.png")));
		numeroTavolo.setFont(Main.bigFont);
		
		JLabel tempoArrivo = new JLabel("   " + DateFormat.getTimeInstance(DateFormat.SHORT).format(ordinazione.getArrivo()));
		ImageIcon icotempo = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "clock48.png"));
		tempoArrivo.setIcon(icotempo);
		tempoArrivo.setFont(Main.plainFont);
		JLabel numeroProdotti = new JLabel("Numero Prodotti: " + ordinazione.getNumeroProdotti());
		
		dati.add(nomeCliente);
		dati.add(numeroTavolo);
		dati.add(tempoArrivo);
		
		// Pannello pulsanti
		JPanel pulsanti = new JPanel(new GridBagLayout());
		emettiScontrino = new JButton("Emetti scontrino");
		emettiScontrino.setBackground(Color.GREEN);
		emettiScontrino.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "receipt.png")));
		emettiScontrino.addActionListener(this);
		
		eliminaOrdinazione = new JButton("Elimina Ordinazione");
		eliminaOrdinazione.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "delete.png")));
		eliminaOrdinazione.setBackground(Color.RED);
		eliminaOrdinazione.setForeground(Color.WHITE);
		eliminaOrdinazione.addActionListener(this);
		
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
		tableModel.caricaProdotti(ordinazione);
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
		
		// Impostazione Renderer
		prodotti.getColumnModel().getColumn(1).setCellRenderer(new TipologiaTableCellRenderer());
		prodotti.getColumnModel().getColumn(2).setCellRenderer(new PrezzoEuroCellRenderer());
		prodotti.getColumnModel().getColumn(4).setCellRenderer(new PrezzoEuroCellRenderer());
		
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
		labelTotale = new JLabel("Totale:" + ordinazione.getTotale());
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
}
