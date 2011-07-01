package it.quickorder.servers;

import it.quickorder.domain.Cliente;
import it.quickorder.helpers.HibernateUtil;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.exception.ConstraintViolationException;

public class SignupServer implements Runnable 
{
	private ServerSocket srvSocket;
	private int port;
	
	public SignupServer(int port)
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
				System.out.println("Richiesta di registrazione da client: " + socketClient.getInetAddress().getHostAddress());
				Runnable runnable = new RegRequestThreadHandler(socketClient);
				Thread nuovoThread = new Thread(runnable);
				nuovoThread.start();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		}
	}

}
class RegRequestThreadHandler implements Runnable
{
	private Socket socket;
	
	public RegRequestThreadHandler(Socket socket)
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
				
				// Ricevo il bean del nuovo cliente da registrare.
				Cliente nuovoCliente = (Cliente) input.readObject();
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				String result;
				try
				{
					Transaction t = session.beginTransaction();
					session.save(nuovoCliente);
					t.commit();
					result = "OK";
				}
				catch (ConstraintViolationException ex)
				{
					result = "DUP_EMAIL";
					
				}
				catch (HibernateException ex2)
				{
					result = "FAILED";
				}
				// Invio l'esito dell'operazione al client.
				output.writeObject(result);
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
		catch (ClassNotFoundException ex2)
		{
			ex2.printStackTrace();
		}
	}
}

