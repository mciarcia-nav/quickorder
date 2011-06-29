package it.quickorder.servers;

import it.quickorder.domain.Prodotto;
import it.quickorder.helpers.HibernateUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		try {
			srvSocket = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true)
		{
			try {
				Socket socketClient = srvSocket.accept();
				System.out.println("Connesso client: " + socketClient.getInetAddress().getHostAddress());
				ObjectInputStream objInput = new ObjectInputStream(socketClient.getInputStream());
				ObjectOutputStream objOutput = new ObjectOutputStream(socketClient.getOutputStream());
				int versione = objInput.readInt();
				System.out.println("La versione del client è " + versione);
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				Query query = session.createQuery("from Prodotto where versione > :var");
				query.setInteger("var", versione);
				List risultati = query.list();
				objOutput.writeObject(risultati);
				objOutput.flush();
				socketClient.close();
				System.out.println("Finito con questo client.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
	
	
}
