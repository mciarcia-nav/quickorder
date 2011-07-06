package it.quickorder.gui;

import it.quickorder.domain.Cliente;
import it.quickorder.gui.table.AbilitazioneTableCellRenderer;
import it.quickorder.gui.table.ClienteModel;
import it.quickorder.gui.table.ScrollableTable;
import it.quickorder.gui.table.SessoTableCellRenderer;
import it.quickorder.helpers.HibernateUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class ClientiIFrame extends JInternalFrame
{
	private ScrollableTable clienti;
	private JPanel jContentPane;
	private DataRecovery dataRecovery;
	private JToolBar toolbar;
	public static String URL_IMAGES = "/it/quickorder/gui/images/";
	private JButton abilitaButton, disabilitaButton, eliminaButton;
	private ClienteModel clientiTableModel;
	private Cliente clienteSelezionato;
	
	public ClientiIFrame() 
	{
        super("Gestione Clienti");
        setClosable(true);
        setResizable(true);
		setMaximizable(true);
        setLocation(100, 100);
        setSize(1000, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        toolbar = new JToolBar();
        toolbar.setLayout(new GridLayout(1, 2));
        dataRecovery = new DataRecoveryImpl();
       
        jContentPane = new JPanel();
        jContentPane.setLayout(new BorderLayout());
        clientiTableModel = new ClienteModel();
        clientiTableModel.caricaClienti(dataRecovery.getClienti());
        
        clienti = new ScrollableTable(clientiTableModel);
		clienti.setRowHeight(32);
		clienti.setAutoCreateColumnsFromModel(true);
		
		// Impostazione dei Renderer
		clienti.setDefaultRenderer(Object.class, new ClienteCellRenderer());
		clienti.setDefaultRenderer(Character.class, new SessoTableCellRenderer());
		clienti.setDefaultRenderer(Boolean.class, new AbilitazioneTableCellRenderer());
		//Selezione della riga e non della singola cella
		clienti.setColumnSelectionAllowed(false);
		clienti.setRowSelectionAllowed(true);
		clienti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clienti.setSelectionForeground(Color.RED);
		clienti.setSelectionBackground(Color.white);
		clienti.setAutoscrolls(true);
		clienti.setShowVerticalLines(false);
        jContentPane.add(clienti.getTableHeader(), BorderLayout.NORTH);
		jContentPane.add(clienti, BorderLayout.CENTER);
		jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(jContentPane, BorderLayout.CENTER);
		abilitaButton = new JButton("Abilita", new ImageIcon(URL_IMAGES+"yes.png"));
	    disabilitaButton = new JButton("Disabilita", new ImageIcon(URL_IMAGES+"no.png"));
	    eliminaButton = new JButton("Elimina", new ImageIcon(URL_IMAGES+"delete.png"));
	    
	    toolbar.add(eliminaButton, 0);
		clienti.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int selectedRow = clienti.getSelectedRow();
	    		 clienteSelezionato = clientiTableModel.recuperaCliente(selectedRow);
	    		 if(clienteSelezionato.isAbilitato())
	    		 {
	    			 disabilitaButton.addActionListener(new DisabilitaListener(clienteSelezionato));
	    			 toolbar.add(disabilitaButton, 1);
	    		 }
	    		 else
	    		 {
	    			 abilitaButton.addActionListener(new AbilitaListener(clienteSelezionato));
	    			 toolbar.add(abilitaButton, 1);
	    		 }
			}
		});
		
		abilitaButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				
			}
		});
		
		getContentPane().add(toolbar, BorderLayout.SOUTH);
	    setVisible(false);
		
	}
	
}

class ClienteCellRenderer extends DefaultTableCellRenderer
{ 
	public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{ 
		Component pComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
		if (pComponent instanceof JLabel)
			((JLabel) pComponent).setHorizontalAlignment(SwingConstants.CENTER);
		return pComponent;
	} 
}

class AbilitaListener implements ActionListener
{
	private Cliente clienteSelezionato;
	private DataRecovery dataRecovery;
	
	public AbilitaListener(Cliente clienteSelezionato)
	{
		this.clienteSelezionato = clienteSelezionato;
		dataRecovery = new DataRecoveryImpl();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		dataRecovery.abilitaCliente(clienteSelezionato);
	}
	
}

class DisabilitaListener implements ActionListener
{
	private Cliente clienteSelezionato;
	private DataRecovery dataRecovery;
	
	public DisabilitaListener(Cliente clienteSelezionato)
	{
		this.clienteSelezionato = clienteSelezionato;
		dataRecovery = new DataRecoveryImpl();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		dataRecovery.disabilitaCliente(clienteSelezionato);
	}
	
}
