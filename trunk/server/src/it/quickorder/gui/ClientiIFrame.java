package it.quickorder.gui;

import it.quickorder.domain.Cliente;
import it.quickorder.helpers.HibernateUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import org.hibernate.*;

@SuppressWarnings("serial")
public class ClientiIFrame extends JInternalFrame
{
	private ScrollableTable clienti;
	private JPanel jContentPane;
	
	public ClientiIFrame() 
	{
        super("Gestione Clienti");
        setClosable(true);
        setResizable(true);
		setMaximizable(true);
        setLocation(100, 100);
        setSize(1000, 750);
        setLayout(new BorderLayout());
       
        jContentPane = new JPanel();
        jContentPane.setLayout(new BorderLayout());
        ClienteModel clientiTableModel = new ClienteModel();
        clientiTableModel.caricaClienti(getClienti());
        clienti = new ScrollableTable(clientiTableModel);
		clienti.setRowHeight(32);
		clienti.setAutoCreateColumnsFromModel(true);
		
		//Selezione della riga e non della singola cella
		clienti.setColumnSelectionAllowed(false);
		clienti.setRowSelectionAllowed(true);
		clienti.setSelectionForeground(Color.RED);
		clienti.setSelectionBackground(Color.white);
		clienti.setAutoscrolls(true);
		clienti.setShowVerticalLines(false);
        jContentPane.add(clienti.getTableHeader(), BorderLayout.NORTH);
		jContentPane.add(clienti, BorderLayout.CENTER);
		jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(jContentPane, BorderLayout.CENTER);
		setVisible(false);
		
		clienti.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				
			}
		});
	}
	
	private List<Cliente> getClienti()
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("select distinct cliente from Cliente cliente");
        return query.list();
	}
	
}
