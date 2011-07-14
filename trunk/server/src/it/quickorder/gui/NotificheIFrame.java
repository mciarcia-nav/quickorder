package it.quickorder.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DesktopManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import it.quickorder.control.NotificheListener;
import it.quickorder.domain.*;
import it.quickorder.servers.RegRequestThreadHandler;

@SuppressWarnings("serial")
public class NotificheIFrame extends TransparentJInternalFrame 
{
	private List<Notifica> notifiche;
	private List<NotificheListener> listeners;
	private List<JPanel> pannelliNotifiche;
	private int corrente;
	private JPanel contentPane;
	
	public NotificheIFrame(List<Notifica> listaNotifiche)
	{
		this.notifiche = listaNotifiche;
		listeners = new ArrayList<NotificheListener>();
		pannelliNotifiche = new ArrayList<JPanel>(notifiche.size());
		contentPane = new JPanel(new GridBagLayout());
		
		costruisciPannelli();
		
		JButton precedente = new JButton();
		precedente.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "arrow_left_double.png")));
		precedente.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.remove(pannelliNotifiche.get(corrente));
				corrente = (corrente - 1 + pannelliNotifiche.size()) % pannelliNotifiche.size();
				GridBagConstraints g = new GridBagConstraints();
				g.gridx = 1;
				g.gridy = 0;
				g.fill = GridBagConstraints.BOTH;
				g.weightx = 1;
				g.weighty = 1;
				contentPane.add(pannelliNotifiche.get(corrente), g);
				contentPane.validate();
				contentPane.repaint();
				pannelliNotifiche.get(corrente).repaint();
			}
		});
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.insets = new Insets(5, 5, 5, 5);
		g.gridheight = 2;
		contentPane.add(precedente, g);
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 0;
		g.fill = GridBagConstraints.BOTH;
		g.weightx = 1;
		g.weighty = 1;
		contentPane.add(pannelliNotifiche.get(0), g);
		JButton successivo = new JButton();
		successivo.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "arrow_right_double.png")));
		successivo.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				contentPane.remove(pannelliNotifiche.get(corrente));
				corrente = (corrente + 1) % pannelliNotifiche.size();
				GridBagConstraints g = new GridBagConstraints();
				g.gridx = 1;
				g.gridy = 0;
				g.fill = GridBagConstraints.BOTH;
				g.weightx = 1;
				g.weighty = 1;
				contentPane.add(pannelliNotifiche.get(corrente), g);
				contentPane.validate();
				contentPane.repaint();
				pannelliNotifiche.get(corrente).repaint();
			}
		});
		g = new GridBagConstraints();
		g.gridx = 2;
		g.gridy = 0;
		g.gridheight = 2;
		g.insets = new Insets(5, 5, 5, 5);
		contentPane.add(successivo, g);
		JPanel pulsanti = new JPanel(new GridLayout(1,2,10,0));
		JButton abilita = new JButton("Abilita");
		abilita.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Notifica notifica = notifiche.get(corrente);
				((RegRequestThreadHandler) notifica.getThreadPartenza()).setAbilitato(1);
				pannelliNotifiche.remove(corrente);
				if (pannelliNotifiche.size() == 0)
					close();
				for (NotificheListener l : listeners)
					l.notificaGestita(new Event(null,pannelliNotifiche.size() == 0 ? -1 : 0,notifica));		
			}
			
		});
		abilita.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "confirmAbilitazione.gif")));
		JButton disabilita = new JButton("Non effettuare Modifiche");
		disabilita.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Notifica notifica = notifiche.get(corrente);
				((RegRequestThreadHandler) notifica.getThreadPartenza()).setAbilitato(0);
				pannelliNotifiche.remove(corrente);
				if (pannelliNotifiche.size() == 0)
					close();
				for (NotificheListener l : listeners)
					l.notificaGestita(new Event(null,pannelliNotifiche.size() == 0 ? -1 : 0,notifica));		
			}
			
		});
		disabilita.setIcon(new ImageIcon(getClass().getResource(Main.URL_IMAGES + "denyAbilitazione.png")));
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 1;
		g.fill = GridBagConstraints.BOTH;
		g.insets = new Insets(5,5,5,5);
		pulsanti.add(abilita);
		pulsanti.add(disabilita);
		contentPane.add(pulsanti, g);

		precedente.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		successivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		abilita.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		disabilita.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		
		corrente = 0;
		setContentPane(contentPane);
		pack();
		
	}
	
	private void close() 
	{
		DesktopManager manager = getDesktopPane().getDesktopManager();
		manager.closeFrame(this);
	}

	private void costruisciPannelli()
	{
		SimpleDateFormat dataNascitaFormato = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat dataRegFormato = new SimpleDateFormat("hh:mm");
		ImageIcon male = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "male.png"));
		ImageIcon female = new ImageIcon(getClass().getResource(Main.URL_IMAGES + "female.png"));
		int index = 0;
		for (Notifica n : notifiche)
		{
			JPanel nuovo = new JPanel();
			nuovo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), 
					"Notifica " + ++index + " di " + notifiche.size()));
			nuovo.setLayout(new GridBagLayout());
			Cliente cliente = n.getCliente();
			JLabel labelNome = new JLabel("Nome :");
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = 0;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.insets = new Insets(5,5,5,5);
			nuovo.add(labelNome, g);
			JTextField nome = new JTextField(cliente.getNome());
			nome.setEditable(false);
			nome.setBackground(Color.WHITE);
			g = new GridBagConstraints();
			g.gridx = 1;
			g.gridy = 0;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.HORIZONTAL;
			nuovo.add(nome, g);
			JLabel labelCognome = new JLabel("Cognome :");
			g = new GridBagConstraints();
			g.gridx = 2;
			g.gridy = 0;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.insets = new Insets(5,5,5,5);
			nuovo.add(labelCognome, g);
			JTextField cognome = new JTextField(cliente.getCognome());
			cognome.setEditable(false);
			cognome.setBackground(Color.WHITE);
			g = new GridBagConstraints();
			g.gridx = 3;
			g.gridy = 0;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.HORIZONTAL;
			nuovo.add(cognome, g);
			JLabel labelData = new JLabel("Data di Nascita :");
			g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = 1;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.insets = new Insets(5,5,5,5);
			nuovo.add(labelData, g);
			JTextField dataNascita = new JTextField(dataNascitaFormato.format(cliente.getDataNascita()));
			dataNascita.setEditable(false);
			dataNascita.setBackground(Color.WHITE);
			g = new GridBagConstraints();
			g.gridx = 1;
			g.gridy = 1;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.HORIZONTAL;
			nuovo.add(dataNascita, g);
			JLabel labelLuogo = new JLabel("Luogo di Nascita :");
			g = new GridBagConstraints();
			g.gridx = 2;
			g.gridy = 1;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.insets = new Insets(5,5,5,5);
			nuovo.add(labelLuogo,g);
			JTextField luogo = new JTextField(cliente.getLuogoNascita());
			luogo.setEditable(false);
			luogo.setBackground(Color.WHITE);
			g = new GridBagConstraints();
			g.gridx = 3;
			g.gridy = 1;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.HORIZONTAL;
			nuovo.add(luogo, g);
			JLabel labelCF = new JLabel("Codice Fiscale :");
			g = new GridBagConstraints();
			g.gridx = 0;
			g.gridwidth = 1;
			g.gridy = 2;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.insets = new Insets(5,5,5,5);
			nuovo.add(labelCF, g);
			JTextField CF = new JTextField(cliente.getCodiceFiscale());
			CF.setEditable(false);
			CF.setBackground(Color.WHITE);
			g = new GridBagConstraints();
			g.gridwidth = 2;
			g.gridx = 1;
			g.gridy = 2;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.HORIZONTAL;
			nuovo.add(CF, g);
			JLabel labelIMEI = new JLabel("Codice IMEI :");
			g = new GridBagConstraints();
			g.gridx = 0;
			g.gridwidth = 1;
			g.gridy = 3;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.insets = new Insets(5,5,5,5);
			nuovo.add(labelIMEI, g);
			JTextField IMEI = new JTextField(cliente.getIMEI());
			IMEI.setEditable(false);
			IMEI.setBackground(Color.WHITE);
			g = new GridBagConstraints();
			g.gridwidth = 2;
			g.gridx = 1;
			g.gridy = 3;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.HORIZONTAL;
			nuovo.add(IMEI, g);
			JLabel sesso = new JLabel();
			sesso.setIcon(cliente.getSesso() == 'm' || cliente.getSesso() == 'M' ? male : female);
			g = new GridBagConstraints();
			g.gridheight = 2;
			g.gridx = 3;
			g.gridy = 3;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.BOTH;
			nuovo.add(sesso,g);
			JLabel labelRegistrazione = new JLabel("Ricezione Richiesta :");
			g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = 4;
			g.insets = new Insets(5,5,5,5);
			nuovo.add(labelRegistrazione, g);
			JLabel dataRegistrazione = new JLabel(dataRegFormato.format(n.getArrivo()));
			g = new GridBagConstraints();
			g.gridx = 1;
			g.gridy = 4;
			g.gridwidth = 2;
			g.insets = new Insets(5,5,5,5);
			g.fill = GridBagConstraints.HORIZONTAL;
			nuovo.add(dataRegistrazione,g);
			
			pannelliNotifiche.add(nuovo);
		}
	}

	public void addNotificheListener(NotificheListener l)
	{
		listeners.add(l);
	}
}
