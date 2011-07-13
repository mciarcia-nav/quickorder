package it.quickorder.gui;

import it.quickorder.domain.Cliente;
import it.quickorder.gui.table.AbilitazioneTableCellRenderer;
import it.quickorder.gui.table.ClienteModel;
import it.quickorder.gui.table.ScrollableTable;
import it.quickorder.gui.table.SessoTableCellRenderer;
import it.quickorder.repository.GetDataFromDB;
import it.quickorder.repository.GetDataFromDBImpl;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class GestioneClientiIFrame extends JInternalFrame implements ActionListener
{
	private ScrollableTable clienti;
	private JPanel jContentPane;
	private GetDataFromDB dataRecovery;
	private JToolBar toolbar;
	public static String URL_IMAGES = "/it/quickorder/gui/images/";
	private JButton abilitaButton, disabilitaButton, eliminaButton, aggiornaButton;
	private ClienteModel clientiTableModel;
	private ListSelectionModel listSelection;
	
	public GestioneClientiIFrame() 
	{
        super("Gestione Clienti");
        setClosable(true);
        setResizable(true);
		setMaximizable(false);
		
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setFrameIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "customers24.png")));
        toolbar = new JToolBar();
        toolbar.setLayout(new GridLayout(1, 2));
        dataRecovery = new GetDataFromDBImpl();
       
        jContentPane = new JPanel();
        jContentPane.setLayout(new BorderLayout());
       
        clientiTableModel = new ClienteModel();
        clientiTableModel.caricaClienti(dataRecovery.getClienti());
        
        clienti = new ScrollableTable(clientiTableModel);
		clienti.setRowHeight(32);
		clienti.setAutoCreateColumnsFromModel(true);
		
		// Impostazione dei Renderer
		clienti.setDefaultRenderer(Character.class, new SessoTableCellRenderer());
		clienti.setDefaultRenderer(Boolean.class, new AbilitazioneTableCellRenderer());
		
		//Selezione della riga e non della singola cella
		listSelection = clienti.getSelectionModel();
		listSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clienti.setSelectionForeground(Color.RED);
		clienti.setSelectionBackground(Color.white);
		clienti.setAutoscrolls(true);
		clienti.setShowVerticalLines(false);
		
		clienti.getColumnModel().getColumn(0).setPreferredWidth(130);
		clienti.getColumnModel().getColumn(1).setPreferredWidth(130);
		clienti.getColumnModel().getColumn(2).setPreferredWidth(80);
		clienti.getColumnModel().getColumn(3).setPreferredWidth(80);
		clienti.getColumnModel().getColumn(4).setPreferredWidth(32);
		clienti.getColumnModel().getColumn(5).setPreferredWidth(80);
		clienti.getColumnModel().getColumn(6).setPreferredWidth(80);
		clienti.getColumnModel().getColumn(7).setPreferredWidth(120);
		clienti.getColumnModel().getColumn(8).setPreferredWidth(32);
		
		listSelection.addListSelectionListener(new ListSelectionListener() 
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				int selected = clienti.getSelectedRow();
				if (selected >= 0)
				{
					boolean attivato = clientiTableModel.isAttivato(selected);
					abilitaButton.setEnabled(!attivato);
					disabilitaButton.setEnabled(attivato);
				}
				else
				{
					abilitaButton.setEnabled(false);
					disabilitaButton.setEnabled(false);
				}
				eliminaButton.setEnabled(selected != -1);
			}
		});
		
        jContentPane.add(clienti.getTableHeader(), BorderLayout.NORTH);
		jContentPane.add(clienti, BorderLayout.CENTER);
		jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(jContentPane, BorderLayout.CENTER);
		abilitaButton = new JButton("Abilita", new ImageIcon(getClass().getResource(URL_IMAGES+"yesIcon.gif")));
		abilitaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		abilitaButton.addActionListener(this);
	    disabilitaButton = new JButton("Disabilita", new ImageIcon(getClass().getResource(URL_IMAGES+"noIcon.gif")));
	    disabilitaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    disabilitaButton.addActionListener(this);
	    eliminaButton = new JButton("Elimina", new ImageIcon(getClass().getResource(URL_IMAGES+"delete.png")));
	    eliminaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    eliminaButton.addActionListener(this);
	    aggiornaButton = new JButton("Aggiorna", new ImageIcon(getClass().getResource(URL_IMAGES+"refresh.png")));
	    aggiornaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    aggiornaButton.addActionListener(this);
	    abilitaButton.setEnabled(false);
	    disabilitaButton.setEnabled(false);
	    eliminaButton.setEnabled(false);
	    aggiornaButton.setEnabled(true);
	    toolbar.add(abilitaButton);
	    toolbar.add(disabilitaButton);
	    toolbar.add(eliminaButton);
	    toolbar.add(aggiornaButton);
	    getContentPane().add(toolbar, BorderLayout.NORTH);
		
	}

	@Override
	public void actionPerformed(ActionEvent evt) 
	{
		if(evt.getSource().equals(aggiornaButton))
		{
			List<Cliente> listaAggiornata = dataRecovery.getClienti();
			clientiTableModel.aggiornaClienti(listaAggiornata);
			listSelection.setSelectionInterval(-1, 0);
			abilitaButton.setEnabled(false);
			disabilitaButton.setEnabled(false);
			eliminaButton.setEnabled(false);
			return;
		}
		
		int rowSelected = clienti.getSelectedRow();
		if (rowSelected >= 0)
		{
			if (evt.getSource().equals(abilitaButton))
			{
				clientiTableModel.abilitaCliente(rowSelected);
				Cliente c = clientiTableModel.getClienteAtRow(rowSelected);
				dataRecovery.abilitaCliente(c);
				abilitaButton.setEnabled(false);
				disabilitaButton.setEnabled(true);
			}
			else if (evt.getSource().equals(disabilitaButton))
			{
				clientiTableModel.disabilitaCliente(rowSelected);
				Cliente c = clientiTableModel.getClienteAtRow(rowSelected);
				dataRecovery.disabilitaCliente(c);
				abilitaButton.setEnabled(true);
				disabilitaButton.setEnabled(false);
			}
			else if(evt.getSource().equals(eliminaButton))
			{
				Cliente c = clientiTableModel.getClienteAtRow(rowSelected);
				clientiTableModel.eliminaCliente(rowSelected);
				dataRecovery.eliminaCliente(c);
				abilitaButton.setEnabled(false);
				disabilitaButton.setEnabled(false);
				eliminaButton.setEnabled(false);
			}
		}
		
		
	}
}


