package it.quickorder.gui;

import it.quickorder.control.CodaNotifiche;
import it.quickorder.control.StackOrdinazioni;
import it.quickorder.servers.OrdersServer;
import it.quickorder.servers.SignupServer;
import it.quickorder.servers.UpdateServer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class Main extends JFrame implements ActionListener 
{
	private static final long serialVersionUID = -571519118389440334L;

	// Percorso per le immagini
	public static String URL_IMAGES = "/it/quickorder/gui/images/";
	private static final int NUMERO_PANNELLI = 10;
	
	// Porte di ascolto per i server.
	private static final int ORDERS_PORT = 24444;
	private static final int UPD_PORT = 24445;
	private static final int SIGNUP_PORT = 24446;
	private static final int SCOSTAMENTO_ORIZZONTALE = 15, SCOSTAMENTO_VERTICALE = 30;
	private final static int MAX_WIDTH = 1100;
	private final static int MAX_HEIGHT = 600;
	protected Dimension SIZE;
	private JPanel jContentPane;
	private JDesktopPane jDesktopPane;
	private JLabel sfondo;
	private StackIFrame stackFrame;
	private GestioneClientiIFrame clientiFrame;
	protected static Font boldFont14, boldFont12, plainFont12, plainFont14, bigFont, boldFont16;
	private StackOrdinazioni stack;
	private CodaNotifiche codaNotifiche;
	private OrdersServer orderServer;
	private SignupServer signupServer;
	private UpdateServer updateServer;
	private JInternalFrame notificaRegistrazione, gestioneClienti,chiusura;
	private PannelloDataOra pannelloDataOra;
	private JButton btnGestioneClienti, btnChiusura;
	private NotificaButton btnNotificheRegistrazione;
	private JDialog exitDialog;
	private JOptionPane optionPane;
	private JButton[] options;
	
	{
		inizializzaFonts();
	}
	
	
	public Main()throws IOException
	{
		super("QuickOrder");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.setSize(screenSize.width, screenSize.height -200);
		setUndecorated(true);
		setSize(screenSize);
		setMinimumSize(screenSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Creazione oggetti di gestione.
		stack = new StackOrdinazioni();
		codaNotifiche = new CodaNotifiche();
		orderServer = new OrdersServer(ORDERS_PORT, stack);
		signupServer = new SignupServer(SIGNUP_PORT, codaNotifiche);
		updateServer = new UpdateServer(UPD_PORT);
			
		// Disegna l'interfaccia a partire dal content pane.
		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		sfondo = new JLabel();
		setBackgroundImage();
		
		// Creazione del desktop pane.
		jDesktopPane = new JDesktopPane();		
		jDesktopPane.setBackground(new Color(84,127,172));
		jDesktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		jDesktopPane.setDesktopManager(new DeskManager(jDesktopPane, stack, NUMERO_PANNELLI));
		jDesktopPane.add(sfondo, Integer.MIN_VALUE);
		jContentPane.add(jDesktopPane);
		
		// Creazione dello stack frame.
		stackFrame = new StackIFrame(jDesktopPane, NUMERO_PANNELLI);

		// Creazione del pannello data e ora
		pannelloDataOra = new PannelloDataOra();
		new Thread(pannelloDataOra).start();
		jDesktopPane.add(pannelloDataOra, Integer.MAX_VALUE - 1);
		pannelloDataOra.setVisible(true);

		
		
		// Creazione del bottone per la gestione delle notifiche di registrazione.
		notificaRegistrazione = new TransparentJInternalFrame();
		btnNotificheRegistrazione = new NotificaButton(jDesktopPane,8);
		btnNotificheRegistrazione.setText("Richieste di Registrazione");
		btnNotificheRegistrazione.setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "notifica32.png")));
		btnNotificheRegistrazione.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNotificheRegistrazione.setBackground(Color.WHITE);
		btnNotificheRegistrazione.setFont(boldFont14);
		codaNotifiche.aggiungiListener(btnNotificheRegistrazione);
		notificaRegistrazione.add(btnNotificheRegistrazione);
		notificaRegistrazione.pack();
		jDesktopPane.add(notificaRegistrazione, Integer.MAX_VALUE - 1);
		notificaRegistrazione.setVisible(true);
		
		// Creazione del bottone per la gestione clienti.
		gestioneClienti = new TransparentJInternalFrame();
		btnGestioneClienti = new JButton("Gestione Clienti");
		btnGestioneClienti.setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "customers.png")));
		btnGestioneClienti.setBackground(Color.WHITE);
		btnGestioneClienti.setFont(boldFont14);
		btnGestioneClienti.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		gestioneClienti.add(btnGestioneClienti);
		gestioneClienti.pack();
		jDesktopPane.add(gestioneClienti, Integer.MAX_VALUE - 1);
		gestioneClienti.setVisible(true);

		btnGestioneClienti.addActionListener(new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (clientiFrame == null)
				{
					clientiFrame = new GestioneClientiIFrame();
					jDesktopPane.add(clientiFrame, Integer.MAX_VALUE-1);
				}
				if(!clientiFrame.isVisible())
				{
					Point stackLoc = stackFrame.getLocation();
					Dimension stackSize = stackFrame.getSize();
					Dimension deskSize = jDesktopPane.getSize();
					Dimension frameSize = new Dimension();
					frameSize.width = deskSize.width - stackSize.width - 80 - SCOSTAMENTO_ORIZZONTALE - (int) (deskSize.width * 0.1);
					frameSize.height = (int) (stackSize.height * 0.9) - SCOSTAMENTO_VERTICALE; 
					
					if (frameSize.width > MAX_WIDTH)
						frameSize.width = MAX_WIDTH;
					if (frameSize.height > MAX_HEIGHT)
						frameSize.height = MAX_HEIGHT;
					
					Point frameLoc = new Point();
					if (stackFrame.getLocation().x == 30)
					{
						int spazioDisponibile = deskSize.width - stackLoc.x -  stackSize.width;
						frameLoc.x = 30 + stackSize.width + (int) ((spazioDisponibile - frameSize.width) / 2) + 15 ;
					}
					else
					{
						int spazioDisponibile = deskSize.width -  stackSize.width - 30;
						frameLoc.x = 30 + (int) ((spazioDisponibile - frameSize.width) / 2) + 15 ;
					}
					frameLoc.y = stackLoc.y + (int) ((stackSize.height - frameSize.height) / 4) + 30 ;
					
					clientiFrame.setBounds(frameLoc.x, frameLoc.y, frameSize.width, frameSize.height);
					clientiFrame.setVisible(true);
					clientiFrame.moveToFront();
				}
				else
				{
					clientiFrame.setVisible(false);
				}
			}
		});

		// Creazione del bottone per la chiusura.
		chiusura = new TransparentJInternalFrame();
		btnChiusura = new JButton("Esci");
		btnChiusura.setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "close.png")));
		btnChiusura.setBackground(Color.WHITE);
		btnChiusura.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnChiusura.addActionListener(this);
		btnChiusura.setFont(boldFont14);
		chiusura.add(btnChiusura);
		chiusura.pack();
		jDesktopPane.add(chiusura, Integer.MAX_VALUE - 1);
		chiusura.setVisible(true);
		setContentPane(jContentPane);		
	}
	
	public void avviaServer()
	{
		new Thread(orderServer).start();
		new Thread(signupServer).start();
		new Thread(updateServer).start();
	}
	
	private static void inizializzaFonts() 
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = ge.getAvailableFontFamilyNames();
		
		String family;
		if (isFontPresente("Tahoma", fonts))
		{
			family = "Tahoma";
		}	
		else
		{
			family = "Sans-Serif";
		}

		plainFont12 = new Font(family, Font.PLAIN, 12);
		plainFont14 = new Font(family, Font.PLAIN, 12);
		boldFont12 = new Font(family, Font.BOLD, 12);
		boldFont14 = new Font(family, Font.BOLD, 14);
		boldFont16 = new Font(family, Font.BOLD, 16);
		bigFont = new Font(family, Font.BOLD, 20);
		
	}
	
	private static boolean isFontPresente(String nome, String[] listaFonts)
	{
		for (String s : listaFonts)
			if (s.equalsIgnoreCase(nome))
				return true;
		return false;
	}

	protected void setBackgroundImage()
	{
		ImageIcon nuova = new ImageIcon(getClass().getResource(URL_IMAGES + "logoQuickOrder.png"));
		sfondo.setIcon(nuova);
		sfondo.setSize(nuova.getIconWidth(), nuova.getIconHeight());
		if (jDesktopPane == null)
		{
			sfondo.setLocation(100, 100);
		}
		else
		{
			Dimension imageSfondo = sfondo.getSize();
			sfondo.setLocation(
					(int) ((jDesktopPane.getWidth() - imageSfondo.width) / 3),
					(int) ((jDesktopPane.getHeight() - imageSfondo.height) / 2));
			jDesktopPane.repaint();
		}
	}
	
	public void setVisible(boolean bool)
	{
		super.setVisible(true);	
		jDesktopPane.add(stackFrame, Integer.MAX_VALUE - 1);
		stack.aggiungiListener(stackFrame);
		stackFrame.costruisciInterfaccia();
		stackFrame.setVisible(true);
		posizionaFrames();
		setBackgroundImage();
		
	}
		
	public static void main(String[] args)
	{
		/*
		try 
		{
			
			//String classe = "javax.swing.plaf.mac.MacLookAndFeel"; //MAC
			//String classe = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; //WIN
			//String classe = "com.sun.java.swing.plaf.motif.MotifLookAndFeel"; //MOTIF
			UIManager.setLookAndFeel(classe);
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		} */
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Main main = null;
				try 
				{
					main = new Main();
				} 
				catch (IOException e) 
				{
					System.err.append("Impossibile avviare uno o più server. L'applicazione non pu� essere avviata.");
					System.exit(-1);
				}
				main.setVisible(true);
				main.avviaServer();
			}
		});
	}
	
	private void posizionaFrames()
	{
		Dimension size1, size2, max;
		size1 = btnGestioneClienti.getSize();
		size2 = btnNotificheRegistrazione.getSize();
		max = new Dimension(Math.max(size1.width, size2.width + 10), Math.max(size2.height,size1.height));
		btnGestioneClienti.setPreferredSize(max);
		btnNotificheRegistrazione.setPreferredSize(max);
		pannelloDataOra.setDataSize(max);
		gestioneClienti.pack();
		notificaRegistrazione.pack();
		
		Point stackLoc = stackFrame.getLocation();
		Point dataLoc = new Point();
		dataLoc.x = 30;
		int space = jDesktopPane.getHeight() - stackLoc.y - stackFrame.getHeight() - gestioneClienti.getHeight();
		dataLoc.y = stackLoc.y + stackFrame.getHeight() + (int) space/2;
		pannelloDataOra.setLocation(dataLoc);
		Point clientiLoc = new Point();
		clientiLoc.x = 30 + pannelloDataOra.getWidth() + 20;
		clientiLoc.y = dataLoc.y;
		gestioneClienti.setLocation(clientiLoc);
		
		Point notificheLoc = new Point();
		notificheLoc.x = 30 + gestioneClienti.getWidth() + pannelloDataOra.getWidth() + 40;
		notificheLoc.y = dataLoc.y;
		notificaRegistrazione.setLocation(notificheLoc);
		
		Dimension chiusuraSize = new Dimension();
		chiusuraSize.width = stackFrame.getWidth();
		chiusuraSize.height = size1.height;
		
		Point chiusuraLoc = new Point();
		chiusuraLoc.x = stackLoc.x;
		chiusuraLoc.y = 0 + (int) ((stackLoc.y - chiusuraSize.height) / 2);
		
		chiusura.setSize(chiusuraSize);
		chiusura.setLocation(chiusuraLoc);
	}

	@Override
	public void actionPerformed(ActionEvent a) 
	{
		confermaChiusura();
	}
	
	private void confermaChiusura()
	{
		if (exitDialog == null)
		{
			optionPane = new JOptionPane();
			exitDialog = optionPane.createDialog(this, "QuickOrder");
			JPanel nuovo = new JPanel(new GridLayout(2,1));
			JLabel message = new JLabel("Vuoi davvero chiudere la sessione di lavoro corrente?");
			message.setFont(new Font("Dialog", Font.BOLD, 14));
			JLabel avviso = new JLabel("I clienti del fast-food non potranno accedere pi� al servizio!");
			avviso.setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "warning16.png")));
			nuovo.add(message);
			nuovo.add(avviso);
			options = new JButton[2];
			options[0] = new JButton("Conferma");
			options[0].setPreferredSize(new Dimension(140, 36));
			options[0].setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "close.png")));
			options[0]
					.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			options[0].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					optionPane.setValue(options[0]);

				}
			});
			options[1] = new JButton("Indietro");
			options[1].setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "back.png")));
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
			optionPane.setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "help48.png")));
			exitDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			exitDialog.pack();

		}
		exitDialog.setVisible(true);
		Point loc = new Point();
		loc.x = (jDesktopPane.getWidth() - exitDialog.getWidth()) / 2;
		loc.y = (jDesktopPane.getHeight() - exitDialog.getHeight()) / 2;
		exitDialog.setLocation(loc);
		if (optionPane.getValue().equals(options[0]))
		{
			System.exit(0);
		}
		else if (optionPane.getValue().equals(options[1]))
		{
			exitDialog.setVisible(false);
		}
	}
	
	@SuppressWarnings("serial")
	class PannelloDataOra extends TransparentJInternalFrame implements Runnable
	{
		private SimpleDateFormat dataFormat, orarioFormat;
		private JLabel data;
		
		public PannelloDataOra()
		{
			dataFormat = new SimpleDateFormat("dd MMMMM yyyy");
			orarioFormat = new SimpleDateFormat("HH:mm");
			Date corrente = new Date(System.currentTimeMillis());
			data = new JLabel("  " + dataFormat.format(corrente) + "  " + orarioFormat.format(corrente));
			data.setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "date32.png")));
			data.setHorizontalAlignment(SwingConstants.CENTER);
			data.setBackground(Color.WHITE);
			data.setFont(boldFont14);	
			getContentPane().add(data);
			getContentPane().setBackground(Color.WHITE);
			pack();
		}

		public void setDataSize(Dimension size)
		{
			data.setPreferredSize(size);
			pack();
		}
		
		@Override
		public void run() 
		{
			while(true)
			{
				Date corrente = new Date(System.currentTimeMillis());
				data.setText("  " + dataFormat.format(corrente) + "  " + orarioFormat.format(corrente));
				try 
				{
					Thread.sleep(60000);
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			
		}
		
		
	}
	
}
