package it.quickorder.gui;

import it.quickorder.domain.Ordinazione;
import it.quickorder.domain.Prodotto;
import it.quickorder.helpers.HibernateUtil;
import it.quickorder.servers.SignupServer;
import it.quickorder.servers.UpdateServer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.FontPeer;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultDesktopManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.hibernate.SessionFactory;


public class Main extends JFrame 
{
	// Percorso per le immagini
	public static String URL_IMAGES = "/quickorder/gui/images/";
	
	protected Dimension SIZE;
	private JPanel jContentPane;
	private JDesktopPane jDesktopPane;
	private JLabel sfondo;
	private StackIFrame stackFrame;
	protected static Font plainFont, bigFont;
	private int count;
	
	{
		inizializzaFonts();
	}
	
	
	public Main()
	{
		super("QuickOrder");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		
		this.setUndecorated(true);
		
		// Setta l'icona per il frame.
		//setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(Home.URL_IMAGES + "etouricon.png")));
		
		// Setta la grandezza del frame e la grandezza minima.
		setSize(screenSize);
		setMinimumSize(screenSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Disegna l'interfaccia a partire dal content pane.
		setContentPane(getJContentPane());
		
		JInternalFrame a = new JInternalFrame("Prova");
		a.setLayout(new BorderLayout());
		a.setContentPane(new JPanel());
		JButton prova = new JButton("PROVA");
		a.getContentPane().add(prova);
		count = 1;
		prova.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Ordinazione ord = new Ordinazione();
				Date nuovo = new Date(System.currentTimeMillis());
/*				ord.setArrivo(nuovo);
				ord.setId(count++);
				ord.setCliente("Mario Gallo");
				ord.setNumeroTavolo(5);
				stackFrame.aggiungiOrdinazione(ord);
				Prodotto p = new Prodotto();
				p.setNome("Panino con salsiccia e patatine");
				p.setPrezzo(3.50);
				p.setQuantita(2);
				p.setSubtotale(7);
				ord.aggiungiProdotto(p);
				p = new Prodotto();
				p.setNome("Coca-Cola");
				p.setPrezzo(1.50);
				p.setQuantita(2);
				p.setSubtotale(3);
				ord.aggiungiProdotto(p);*/
				
			}
			
		});
		a.setSize(200,200);
		a.setLocation(10, 10);
		jDesktopPane.add(a,Integer.MAX_VALUE);
		a.setVisible(true);
		
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

	private JPanel getJContentPane()
	{
		if (jContentPane == null)
		{
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJDesktopPane());
		}
		return jContentPane;
	}

	private JDesktopPane getJDesktopPane()
	{
		if (jDesktopPane == null)
		{
			sfondo = new JLabel();
			setBackgroundImage();
			jDesktopPane = new JDesktopPane();		
			jDesktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
			jDesktopPane.setDesktopManager(new DeskManager(jDesktopPane));
			jDesktopPane.add(sfondo, Integer.MIN_VALUE);
			
			
		}
		return jDesktopPane;
	}

	private void setBackgroundImage()
	{
		ImageIcon nuova = new ImageIcon(getClass().getResource(URL_IMAGES + "sfondo.png"));
		sfondo.setIcon(nuova);
		sfondo.setSize(nuova.getIconWidth(), nuova.getIconHeight());
		if (jDesktopPane == null)
		{
			sfondo.setLocation(0, 0);
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
		stackFrame = new StackIFrame(jDesktopPane);
		jDesktopPane.add(stackFrame, Integer.MAX_VALUE);
		stackFrame.setVisible(true);
		
	}
	
	public static void main(String[] args)
	{
		try 
		{
			
			//String classe = "javax.swing.plaf.mac.MacLookAndFeel"; //MAC
			String classe = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; //WIN
			//String classe = "com.sun.java.swing.plaf.motif.MotifLookAndFeel"; //MOTIF
			UIManager.setLookAndFeel(classe);
			
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		new Thread(new UpdateServer(4445)).start();
		new Thread(new SignupServer(4446)).start();
		
			
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				new Main().setVisible(true);
//				
//			}
//		});
	}
	
	
}
