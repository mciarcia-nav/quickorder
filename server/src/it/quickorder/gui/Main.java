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

import javax.swing.*;

public class Main extends JFrame 
{
	private static final long serialVersionUID = -571519118389440334L;

	// Percorso per le immagini
	public static String URL_IMAGES = "/it/quickorder/gui/images/";
	private static final int NUMERO_PANNELLI = 10;
	
	// Porte di ascolto per i server.
	private static final int ORDERS_PORT = 4444;
	private static final int UPD_PORT = 4445;
	private static final int SIGNUP_PORT = 4446;
	
	protected Dimension SIZE;
	private JPanel jContentPane;
	private JDesktopPane jDesktopPane;
	private JLabel sfondo;
	private StackIFrame stackFrame;
	private ClientiIFrame clientiFrame;
	protected static Font plainFont, bigFont;
	private StackOrdinazioni stack;
	private CodaNotifiche codaNotifiche;
	private OrdersServer orderServer;
	private SignupServer signupServer;
	private UpdateServer updateServer;
	private JInternalFrame notificaRegistrazione;
	private JInternalFrame gestioneClienti;
	private JButton btnGestioneClienti;
	private NotificaButton btnNotificheRegistrazione;
	
	{
		inizializzaFonts();
	}
	
	
	public Main() throws IOException
	{
		super("QuickOrder");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
		clientiFrame = new ClientiIFrame();
			
		// Disegna l'interfaccia a partire dal content pane.
		jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		sfondo = new JLabel();
		setBackgroundImage();
		
		// Creazione del desktop pane.
		jDesktopPane = new JDesktopPane();		
		jDesktopPane.setBackground(new Color(165,18,64));
		jDesktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		jDesktopPane.setDesktopManager(new DeskManager(jDesktopPane, stack, NUMERO_PANNELLI));
		jDesktopPane.add(sfondo, Integer.MIN_VALUE);
		jContentPane.add(jDesktopPane);
		
		// Creazione dello stack frame.
		stackFrame = new StackIFrame(jDesktopPane, NUMERO_PANNELLI);

		// Creazione del bottone per la gestione delle notifiche di registrazione.
		notificaRegistrazione = new TransparentJInternalFrame();
		btnNotificheRegistrazione = new NotificaButton(jDesktopPane,8);
		btnNotificheRegistrazione.setText("Richieste di Registrazione");
		btnNotificheRegistrazione.setIcon(new ImageIcon(getClass().getResource(URL_IMAGES + "notifica32.png")));
		btnNotificheRegistrazione.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNotificheRegistrazione.setBackground(Color.WHITE);
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
				if(!clientiFrame.isVisible())
				{
					clientiFrame.setVisible(true);
					clientiFrame.moveToFront();
				}
				else
				{
					clientiFrame.setVisible(false);
				}
			}
		});

		jDesktopPane.add(clientiFrame);
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
		
		if (isFontPresente("Tahoma", fonts))
		{
			plainFont = new Font("Tahoma", Font.BOLD, 14);
			bigFont = new Font("Tahoma", Font.BOLD, 20);
		}	
		else
		{
			plainFont = new Font("Sans-Serif", Font.BOLD, 14);
			bigFont = new Font("Sans-Serif", Font.BOLD, 20);
		}
		
	}
	
	private static boolean isFontPresente(String nome, String[] listaFonts)
	{
		for (String s : listaFonts)
			if (s.equalsIgnoreCase(nome))
				return true;
		return false;
	}

	private void setBackgroundImage()
	{
		ImageIcon nuova = new ImageIcon(getClass().getResource(URL_IMAGES + "logo.png"));
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
					(jDesktopPane.getWidth() - imageSfondo.width) / 2,
					(jDesktopPane.getHeight() - imageSfondo.height) / 2);
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
					System.err.append("Impossibile avviare uno o più server. L'applicazione non può essere avviata.");
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
		gestioneClienti.pack();
		notificaRegistrazione.pack();
		
		Point stackLoc = stackFrame.getLocation();
		Point gestioneClientiLoc = new Point();
		gestioneClientiLoc.x = 30;
		int space = jDesktopPane.getHeight() - stackLoc.y - stackFrame.getHeight() - gestioneClienti.getHeight();
		gestioneClientiLoc.y = stackLoc.y + stackFrame.getHeight() + (int) space/2;
		gestioneClienti.setLocation(gestioneClientiLoc);
		Point notificheLoc = new Point();
		notificheLoc.x = 30 + gestioneClienti.getWidth() + 20;
		notificheLoc.y = gestioneClientiLoc.y;
		notificaRegistrazione.setLocation(notificheLoc);
		
		
	}
	
}
