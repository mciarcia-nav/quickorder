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
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class ClientiIFrame extends JInternalFrame implements ActionListener
{
	private ScrollableTable clienti;
	private JPanel jContentPane;
	private DataRecovery dataRecovery;
	private JToolBar toolbar;
	public static String URL_IMAGES = "/it/quickorder/gui/images/";
	private JButton abilitaButton, disabilitaButton, eliminaButton, aggiornaButton;
	private ClienteModel clientiTableModel;
	private Cliente clienteSelezionato;
	private int rigaSelezionata;
	
	public ClientiIFrame() 
	{
        super("Gestione Clienti");
        setClosable(true);
        setResizable(true);
		setMaximizable(false);
		
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setFrameIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "customers.png")));
        toolbar = new JToolBar();
        toolbar.setLayout(new GridLayout(1, 2));
        dataRecovery = new DataRecoveryImpl();
        rigaSelezionata = -1;
       
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
		
		clienti.getColumnModel().getColumn(0).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(1).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(2).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(3).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(4).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(5).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(6).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(7).setPreferredWidth(180);
		clienti.getColumnModel().getColumn(8).setPreferredWidth(180);
		
        jContentPane.add(clienti.getTableHeader(), BorderLayout.NORTH);
		jContentPane.add(clienti, BorderLayout.CENTER);
		jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(jContentPane, BorderLayout.CENTER);
		abilitaButton = new JButton("Abilita", new ImageIcon(getClass().getResource(URL_IMAGES+"yesIcon.png")));
		abilitaButton.addActionListener(this);
	    disabilitaButton = new JButton("Disabilita", new ImageIcon(getClass().getResource(URL_IMAGES+"noIcon.png")));
	    disabilitaButton.addActionListener(this);
	    eliminaButton = new JButton("Elimina", new ImageIcon(getClass().getResource(URL_IMAGES+"delete.png")));
	    eliminaButton.addActionListener(this);
	    abilitaButton.setEnabled(false);
	    disabilitaButton.setEnabled(false);
	    eliminaButton.setEnabled(false);
	    toolbar.add(abilitaButton);
	    toolbar.add(disabilitaButton);
	    toolbar.add(eliminaButton);
	    clientiTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) 
			{
				int row = e.getFirstRow();
				System.out.println("riga "+row);
		        int col = e.getColumn();
		        System.out.println("colonna "+col);
		        if(col!=-1)
		        {
		        	boolean abilitazione = (Boolean)clientiTableModel.getValueAt(row, col);
		        	if( abilitazione == true)
		        	{
		        		clientiTableModel.modificaAbilitazioneCliente(row, false);
		        		disabilitaButton.setEnabled(false);
		        		abilitaButton.setEnabled(true);
		        	}
		        	else
		        	{
		        		clientiTableModel.modificaAbilitazioneCliente(row, true);
		        		disabilitaButton.setEnabled(true);
		        		abilitaButton.setEnabled(false);
		        	}
		        }
		        else
		        {
		        	clientiTableModel.eliminaCliente(row);
		        	if(clientiTableModel.getRowCount()==0)
		        	{
		        		abilitaButton.setEnabled(false);
		        		disabilitaButton.setEnabled(false);
		        		eliminaButton.setEnabled(false);
		        	}
		        }
			}
		});
		clienti.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int selectedRow = clienti.getSelectedRow();
				if(selectedRow != -1)
				{
					clienteSelezionato = clientiTableModel.recuperaCliente(selectedRow);
					setRigaSelezionata(selectedRow);
					if(clienteSelezionato.isAbilitato())
					{
						disabilitaButton.setEnabled(true);
						abilitaButton.setEnabled(false);
						eliminaButton.setEnabled(true);
					}
					else
					{
						disabilitaButton.setEnabled(false);
						abilitaButton.setEnabled(true);
						eliminaButton.setEnabled(true);
					}
				}
				
			}
		});
		
		getContentPane().add(toolbar, BorderLayout.SOUTH);
	    setVisible(false);
		
	}

	@Override
	public void actionPerformed(ActionEvent evt) 
	{
		if (evt.getSource().equals(abilitaButton))
		{
			dataRecovery.abilitaCliente(clienteSelezionato);
			clientiTableModel.fireTableCellUpdated(getRigaSelezionata(), 8);
		}
		else if (evt.getSource().equals(disabilitaButton))
		{
			dataRecovery.disabilitaCliente(clienteSelezionato);
			clientiTableModel.fireTableCellUpdated(getRigaSelezionata(), 8);
		}
		else if(evt.getSource().equals(eliminaButton))
		{
			dataRecovery.eliminaCliente(clienteSelezionato);
			clientiTableModel.fireTableRowsDeleted(rigaSelezionata, rigaSelezionata+1);
		}
		
	}
	
	private void setRigaSelezionata(int riga)
	{
		rigaSelezionata = riga;
	}
	
	private int getRigaSelezionata()
	{
		return rigaSelezionata;
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


