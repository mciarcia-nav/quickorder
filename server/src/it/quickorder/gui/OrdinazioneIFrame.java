package it.quickorder.gui;

import it.quickorder.control.StackOrdinazioni;
import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;
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
	private JDialog eliminaDialog;
	private JButton[] options;
	private JOptionPane optionPane;
	
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
	public void internalFrameActivated(InternalFrameEvent e) {}

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
	public void internalFrameClosing(InternalFrameEvent e){}
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {}
	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {}
	@Override
	public void internalFrameIconified(InternalFrameEvent e) {}
	@Override
	public void internalFrameOpened(InternalFrameEvent e) {}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource().equals(eliminaOrdinazione))
		{
			confermaEliminazione();
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
	
	private void confermaEliminazione()
	{
		if (eliminaDialog == null)
		{
			optionPane = new JOptionPane();
			eliminaDialog = optionPane.createDialog(this, "QuickOrder");
			JPanel nuovo = new JPanel(new GridLayout(2,1));
			JLabel message = new JLabel("Vuoi davvero eliminare l'ordinazione corrente?");
			message.setFont(new Font("Dialog", Font.BOLD, 14));
			JLabel avviso = new JLabel("Il cliente non potr� essere avvertito dell'eliminazione dell'ordinazione.");
			avviso.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "warning16.png")));
			nuovo.add(message);
			nuovo.add(avviso);
			options = new JButton[2];
			options[0] = new JButton("Conferma");
			options[0].setPreferredSize(new Dimension(140, 36));
			options[0].setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "delete32.png")));
			options[0].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			options[0].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					optionPane.setValue(options[0]);
				}
			});
			options[1] = new JButton("Indietro");
			options[1].setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "back.png")));
			options[1].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			options[1].setPreferredSize(new Dimension(140, 36));
			options[1].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					optionPane.setValue(options[1]);
				}
			});
			optionPane.setMessage(nuovo);
			optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
			optionPane.setOptions(options);
			optionPane.setInitialValue(options[1]);
			optionPane.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "help48.png")));
			eliminaDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			eliminaDialog.pack();
		}
		eliminaDialog.setVisible(true);
		Point loc = new Point();
		loc.x = (desktop.getWidth() - eliminaDialog.getWidth()) / 2;
		loc.y = (desktop.getHeight() - eliminaDialog.getHeight()) / 2;
		eliminaDialog.setLocation(loc);
		if (optionPane.getValue().equals(options[0]))
		{
			eliminaDialog.setVisible(false);
			getDesktopPane().getDesktopManager().closeFrame(this);
			stackOrdinazioni.rimuoviOrdinazione(ordinazione);
		}
		else if (optionPane.getValue().equals(options[1]))
		{
			eliminaDialog.setVisible(false);
		}
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
		labelCliente.setFont(Main.boldFont12);
		ImageIcon icoCliente = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "customers48.png"));
		labelCliente.setIcon(icoCliente);
		labelCliente.setHorizontalAlignment(SwingConstants.CENTER);
		labelCliente.setHorizontalTextPosition(SwingConstants.CENTER);
		labelCliente.setVerticalTextPosition(SwingConstants.BOTTOM);
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.gridheight = 3;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(labelCliente,g);
		JLabel nomeCliente = new JLabel(ordinazione.getCliente().getNome() + " " + ordinazione.getCliente().getCognome());
		nomeCliente.setHorizontalAlignment(SwingConstants.CENTER);
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 0;
		g.gridheight = 3;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(nomeCliente,g);
		JLabel labelTavolo = new JLabel("<html><p align=\"center\">Numero<br>Tavolo</html>");
		labelTavolo.setFont(Main.boldFont12);
		ImageIcon icoTavolo = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "table.png"));
		labelTavolo.setIcon(icoTavolo);
		labelTavolo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTavolo.setHorizontalTextPosition(SwingConstants.CENTER);
		labelTavolo.setVerticalTextPosition(SwingConstants.BOTTOM);
		g = new GridBagConstraints();
		g.gridx = 2;
		g.gridy = 0;
		g.gridheight = 3;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(labelTavolo,g);
		JLabel numeroTavolo = new JLabel();
		int numTavolo = ordinazione.getNumeroTavolo();
		numeroTavolo.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "glass_numbers_" + numTavolo + ".png")));
		numeroTavolo.setHorizontalAlignment(SwingConstants.LEFT);
		g = new GridBagConstraints();
		g.gridx = 3;
		g.gridy = 0;
		g.gridheight = 3;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(numeroTavolo,g);
		JLabel labelNumero = new JLabel("<html><p align=\"center\">Numero<br>Prodotti</html>");
		labelNumero.setFont(Main.boldFont12);
		ImageIcon ico = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "numeroProdotti48.png"));
		labelNumero.setIcon(ico);
		labelNumero.setHorizontalAlignment(SwingConstants.CENTER);
		labelNumero.setHorizontalTextPosition(SwingConstants.CENTER);
		labelNumero.setVerticalTextPosition(SwingConstants.BOTTOM);
		g = new GridBagConstraints();
		g.gridx = 4;
		g.gridy = 0;
		g.gridheight = 3;
		g.weightx = 0.2;
		//g.insets = new Insets(10,10,10,10);
		dati.add(labelNumero,g);
		JLabel numero = new JLabel();
		int numeroProdotti = ordinazione.getNumeroProdotti();
		if (numeroProdotti == 1)
		{
			numero.setText("Un prodotto di cui:");
		}
		else
		{
			numero.setText("" + numeroProdotti + " prodotti di cui:");
		}
		g = new GridBagConstraints();
		g.gridx = 5;
		g.gridy = 0;
		g.gridheight = 1;
		g.weightx = 0.2;
		g.insets = new Insets(15,5,5,5);
		dati.add(numero,g);
		JLabel numeroOrdini = new JLabel("x " + ordinazione.getNumeroProdotto(Prodotto.Primi)
				+ ordinazione.getNumeroProdotto(Prodotto.Antipasto) 
				+ ordinazione.getNumeroProdotto(Prodotto.Secondi
				+ ordinazione.getNumeroProdotto(Prodotto.Dessert)));
		numeroOrdini.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "panino24.png")));
		g = new GridBagConstraints();
		g.gridx = 5;
		g.gridy = 1;
		g.anchor = GridBagConstraints.CENTER;
		g.gridheight = 1;
		g.weightx = 0.2;
		//g.insets = new Insets(10,10,10,10);
		dati.add(numeroOrdini,g);
		JLabel numeroBibite = new JLabel("x " + ordinazione.getNumeroProdotto(Prodotto.Bevande));
		numeroBibite.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "bevanda24.png")));
		g = new GridBagConstraints();
		g.gridx = 5;
		g.gridy = 2;
		g.anchor = GridBagConstraints.CENTER;
		g.gridheight = 1;
		g.weightx = 0.2;
		//g.insets = new Insets(10,10,10,10);
		dati.add(numeroBibite,g);
		
		JLabel labelTempo = new JLabel("<html><p align=\"center\">Orario di<br>Arrivo</html>");
		labelTempo.setFont(Main.boldFont12);
		ImageIcon icotempo = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "clock48.png"));
		labelTempo.setIcon(icotempo);
		labelTempo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTempo.setHorizontalTextPosition(SwingConstants.CENTER);
		labelTempo.setVerticalTextPosition(SwingConstants.BOTTOM);
		g = new GridBagConstraints();
		g.gridx = 6;
		g.gridy = 0;
		g.weightx = 0.2;
		g.gridheight = 3;
		g.insets = new Insets(10,20,10,10);
		dati.add(labelTempo,g);
		JLabel tempoArrivo = new JLabel(DateFormat.getTimeInstance(DateFormat.SHORT).format(ordinazione.getArrivo()));
		tempoArrivo.setFont(Main.boldFont12);
		g = new GridBagConstraints();
		g.gridx = 7;
		g.gridy = 0;
		g.gridheight = 3;
		g.weightx = 0.2;
		g.insets = new Insets(10,10,10,10);
		dati.add(tempoArrivo,g);	

		
		// Pannello pulsanti
		JPanel pulsanti = new JPanel(new GridBagLayout());
		emettiScontrino = new JButton("Emetti scontrino");
		emettiScontrino.setBackground(new Color(18,180,18));
		emettiScontrino.setForeground(Color.WHITE);
		emettiScontrino.setFont(Main.boldFont14);
		emettiScontrino.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "receipt.png")));
		emettiScontrino.addActionListener(this);
		
		eliminaOrdinazione = new JButton("Elimina Ordinazione");
		eliminaOrdinazione.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "delete.png")));
		eliminaOrdinazione.setBackground(new Color(224,0,5));
		eliminaOrdinazione.setFont(Main.boldFont14);
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
		prodotti.setColumnSelectionAllowed(false);
		prodotti.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		prodotti.setSelectionForeground(Color.BLACK);
		prodotti.setSelectionBackground(Color.white);
		prodotti.setShowVerticalLines(false);
		JScrollPane jScrollPane = new JScrollPane(prodotti);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		// Impostazione Renderer
		prodotti.getColumnModel().getColumn(0).setCellRenderer(new TipologiaTableCellRenderer());
		prodotti.getColumnModel().getColumn(3).setCellRenderer(new PrezzoEuroCellRenderer());
		prodotti.getColumnModel().getColumn(4).setCellRenderer(new PrezzoEuroCellRenderer());
		
		prodotti.getColumnModel().getColumn(0).setPreferredWidth(30);
		prodotti.getColumnModel().getColumn(1).setPreferredWidth(90);
		prodotti.getColumnModel().getColumn(2).setPreferredWidth(30);
		prodotti.getColumnModel().getColumn(3).setPreferredWidth(30);
		prodotti.getColumnModel().getColumn(4).setPreferredWidth(30);
		prodotti.getColumnModel().getColumn(5).setPreferredWidth(100);
		
		pannelloProdotti.add(jScrollPane, BorderLayout.CENTER);
		
		
		
		// Pannello Totale
		JPanel totale = new JPanel(new GridLayout(1,2,10,0));
		JLabel labelDesc = new JLabel("Totale");
		labelDesc.setFont(Main.boldFont14);
		ImageIcon icot = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "totale48.png"));
		labelDesc.setIcon(icot);
		labelDesc.setHorizontalAlignment(SwingConstants.CENTER);
		labelDesc.setHorizontalTextPosition(SwingConstants.CENTER);
		labelDesc.setVerticalTextPosition(SwingConstants.BOTTOM);
		DecimalFormat formato = new DecimalFormat("##.00");
		labelTotale = new JLabel("� " + formato.format(ordinazione.getTotale()));
		labelTotale.setHorizontalTextPosition(SwingConstants.TRAILING);	
		labelTotale.setFont(Main.boldFont16);
		totale.add(labelDesc);
		totale.add(labelTotale);	
		
		// Impostazione layout content pane
		GridBagConstraints gb = new GridBagConstraints();
		gb.gridx = 0;
		gb.gridy = 0;
		gb.gridwidth = 3;
		gb.weightx = 0.8;
		gb.weighty = 0;
		gb.insets = new Insets(0,5,0,5);
		gb.fill = GridBagConstraints.HORIZONTAL;
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
		gb.insets = new Insets(0,5,0,5);
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
