package it.quickorder.gui;

import it.quickorder.control.StackOrdinazioni;
import it.quickorder.domain.Ordinazione;
import it.quickorder.gui.table.OrdinazioneModel;
import it.quickorder.gui.table.PrezzoEuroCellRenderer;
import it.quickorder.gui.table.ScrollableTable;
import it.quickorder.gui.table.TipologiaTableCellRenderer;
import it.quickorder.helpers.HibernateUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.hibernate.classic.Session;


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
			salvaOrdinazione();
		}
		
	}
	

	private void salvaOrdinazione() 
	{
		Session sessione = HibernateUtil.getSessionFactory().getCurrentSession();
		sessione.beginTransaction();
		sessione.save(ordinazione);
		sessione.getTransaction().commit();
		
	}

	private void costruisciInterfaccia()
	{
		
		// Content Pane
		setLayout(new BorderLayout());
		jContentPane = new JPanel();
		jContentPane.setLayout(new GridBagLayout());
		
		// Pannello dati
		JPanel dati = new JPanel(new GridBagLayout());
		dati.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		JLabel labelCliente = new JLabel("Cliente");
		labelCliente.setFont(Main.plainFont);
		ImageIcon icoCliente = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "customers48.png"));
		labelCliente.setIcon(icoCliente);
		labelCliente.setHorizontalAlignment(SwingConstants.CENTER);
		labelCliente.setHorizontalTextPosition(SwingConstants.CENTER);
		labelCliente.setVerticalTextPosition(SwingConstants.BOTTOM);
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.weightx = 0.2;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.insets = new Insets(10,10,10,10);
		dati.add(labelCliente,g);
		JLabel nomeCliente = new JLabel(ordinazione.getCliente().getNome() + " " + ordinazione.getCliente().getCognome());
		nomeCliente.setHorizontalAlignment(SwingConstants.CENTER);
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 0;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(nomeCliente,g);
		JLabel labelTavolo = new JLabel("<html><p align=\"center\">Numero<br>Tavolo</html>");
		labelTavolo.setFont(Main.plainFont);
		ImageIcon icoTavolo = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "table.png"));
		labelTavolo.setIcon(icoTavolo);
		labelTavolo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTavolo.setHorizontalTextPosition(SwingConstants.CENTER);
		labelTavolo.setVerticalTextPosition(SwingConstants.BOTTOM);
		g = new GridBagConstraints();
		g.gridx = 2;
		g.gridy = 0;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(labelTavolo,g);
		JLabel numeroTavolo = new JLabel("" + ordinazione.getNumeroTavolo());
		numeroTavolo.setFont(Main.bigFont);
		g = new GridBagConstraints();
		g.gridx = 3;
		g.gridy = 0;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(numeroTavolo,g);
		JLabel labelNumero = new JLabel("<html><p align=\"center\">Numero<br>Prodotti</html>");
		labelNumero.setFont(Main.plainFont);
		ImageIcon ico = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "numeroProdotti48.png"));
		labelNumero.setIcon(ico);
		labelNumero.setHorizontalAlignment(SwingConstants.CENTER);
		labelNumero.setHorizontalTextPosition(SwingConstants.CENTER);
		labelNumero.setVerticalTextPosition(SwingConstants.BOTTOM);
		g = new GridBagConstraints();
		g.gridx = 4;
		g.gridy = 0;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(labelNumero,g);
		JLabel numero = new JLabel("" + ordinazione.getNumeroProdotti() + " prodotti" );
		g = new GridBagConstraints();
		g.gridx = 5;
		g.gridy = 0;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(numero,g);
		JLabel labelTempo = new JLabel("<html><p align=\"center\">Orario di<br>Arrivo</html>");
		labelTempo.setFont(Main.plainFont);
		ImageIcon icotempo = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "clock48.png"));
		labelTempo.setIcon(icotempo);
		labelTempo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTempo.setHorizontalTextPosition(SwingConstants.CENTER);
		labelTempo.setVerticalTextPosition(SwingConstants.BOTTOM);
		g = new GridBagConstraints();
		g.gridx = 6;
		g.gridy = 0;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(labelTempo,g);
		JLabel tempoArrivo = new JLabel(DateFormat.getTimeInstance(DateFormat.SHORT).format(ordinazione.getArrivo()));
		tempoArrivo.setFont(Main.plainFont);
		g = new GridBagConstraints();
		g.gridx = 7;
		g.gridy = 0;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(tempoArrivo,g);	

		
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
		JPanel totale = new JPanel(new GridLayout(1,2,10,0));
		JLabel labelDesc = new JLabel("Totale");
		labelDesc.setFont(Main.plainFont);
		ImageIcon icot = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "totale48.png"));
		labelDesc.setIcon(icot);
		labelDesc.setHorizontalAlignment(SwingConstants.CENTER);
		labelDesc.setHorizontalTextPosition(SwingConstants.CENTER);
		labelDesc.setVerticalTextPosition(SwingConstants.BOTTOM);
		DecimalFormat formato = new DecimalFormat("##.00");
		labelTotale = new JLabel("€ " + formato.format(ordinazione.getTotale()));
		labelTotale.setHorizontalTextPosition(SwingConstants.TRAILING);
		
		labelTotale.setFont(Main.plainFont);
		totale.add(labelDesc);
		totale.add(labelTotale);
		totale.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		
		
		// Impostazione layout content pane
		GridBagConstraints gb = new GridBagConstraints();
		gb.gridx = 0;
		gb.gridy = 0;
		gb.gridwidth = 3;
		gb.weightx = 0.8;
		gb.weighty = 0.1;
		gb.insets = new Insets(10,10,0,10);
		gb.fill = GridBagConstraints.BOTH;
		gb.ipady = 10;
		jContentPane.add(dati,gb);
		gb = new GridBagConstraints();
		gb.gridx = 3;
		gb.gridy = 0;
		gb.gridheight = 3;
		gb.weightx = 0.2;
		gb.weighty = 0.8;
		gb.fill = GridBagConstraints.BOTH;
		jContentPane.add(pulsanti,gb);
		gb = new GridBagConstraints();
		gb.gridx = 0;
		gb.gridwidth = 3;
		gb.gridy = 1;
		gb.insets = new Insets(10,10,0,10);
		gb.weightx = 0.8;
		gb.weighty = 0.8;
		gb.fill = GridBagConstraints.BOTH;
		jContentPane.add(pannelloProdotti, gb);
		gb = new GridBagConstraints();
		gb.gridx = 2;
		gb.gridy = 2;
		gb.gridwidth = 1;
		gb.weightx = 0.8;
		gb.weighty = 0;
		gb.ipadx = 10;
		gb.insets = new Insets(10,10,0,10);
		gb.fill = GridBagConstraints.VERTICAL;
		gb.anchor = GridBagConstraints.EAST;
		jContentPane.add(totale, gb);
		
		
		jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(jContentPane, BorderLayout.CENTER);
		
	}
}
