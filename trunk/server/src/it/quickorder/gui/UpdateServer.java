package it.quickorder.gui;

import it.quickorder.domain.Prodotto;
import it.quickorder.helpers.HibernateUtil;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.classic.Session;

public class UpdateServer implements Runnable 
{
	private ServerSocket srvSocket;
	private int port;
	
	public UpdateServer(int port)
	{
		this.port = port;
	}
	
	
	@Override
	public void run() 
	{
		try 
		{
			srvSocket = new ServerSocket(port);
		} catch (IOException e1) 
		{
			e1.printStackTrace();
			return;
		}
		while(true)
		{
			Socket socketClient = null;
			try 
			{
				socketClient = srvSocket.accept();
				System.out.println("Richiesta di aggiornamento da client: " + socketClient.getInetAddress().getHostAddress());
				Runnable runnable = new UpdateRequestThreadHandler(socketClient);
				Thread nuovoThread = new Thread(runnable);
				nuovoThread.start();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		}
	}

}
class UpdateRequestThreadHandler implements Runnable
{
	private Socket socket;
	
	public UpdateRequestThreadHandler(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	public void run() 
	{
		try
		{
			try
			{
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				
				// Ricevo la versione del database del client.
				int versione = input.readInt();
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				// Recupero i prodotti che sono stati aggiornati.
				Query query = session.createQuery("from Prodotto where versione > :var");
				query.setInteger("var", versione);
				@SuppressWarnings("unchecked")
				List<Prodotto> risultati = (List<Prodotto>) query.list();
				// Invio dei prodotti al client.
				output.writeObject(risultati);
				output.flush();
			}
			finally
			{
				socket.close();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}

