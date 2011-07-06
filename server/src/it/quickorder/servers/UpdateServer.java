package it.quickorder.servers;

import it.quickorder.domain.Prodotto;
import it.quickorder.helpers.HibernateUtil;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.classic.Session;

public class UpdateServer implements Runnable 
{
	private ServerSocket srvSocket;
	private SimpleDateFormat formato;
	
	public UpdateServer(int port) throws IOException
	{
		formato = new SimpleDateFormat("hh:mm.ss");
		srvSocket = new ServerSocket(port);
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			Socket socketClient = null;
			try 
			{
				socketClient = srvSocket.accept();
				Date corrente = new Date(System.currentTimeMillis());
				System.out.println("[" + formato.format(corrente) + "] - Richiesta di aggiornamento da client: " + socketClient.getInetAddress().getHostAddress());
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

