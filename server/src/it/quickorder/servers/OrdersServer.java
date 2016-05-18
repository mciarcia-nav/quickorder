package it.quickorder.servers;

import it.quickorder.control.StackOrdinazioni;
import it.quickorder.domain.Ordinazione;
import it.quickorder.gui.Main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdersServer implements Runnable 
{
	private ServerSocket srvSocket;
	private SimpleDateFormat formato;
	private StackOrdinazioni stack;
		
	public OrdersServer(int port, StackOrdinazioni stack) throws IOException 
	{
		this.stack = stack;
		formato = new SimpleDateFormat("hh:mm.ss");
		srvSocket = new ServerSocket(port, 50, InetAddress.getByName(Main.ip_address));
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
				System.out.println("[" + formato.format(corrente) + "] - Richiesta di ordinazione da client: " + socketClient.getInetAddress().getHostAddress());
				Runnable runnable = new OrderRequestThreadHandler(socketClient, stack);
				Thread nuovoThread = new Thread(runnable);
				nuovoThread.start();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		}
	}

}
class OrderRequestThreadHandler implements Runnable
{
	private Socket socket;
	private StackOrdinazioni stack;
	
	public OrderRequestThreadHandler(Socket socket, StackOrdinazioni stack)
	{
		this.socket = socket;
		this.stack = stack;
	}

	@Override
	public void run() 
	{
		try
		{
			Ordinazione nuova = null;
			try
			{
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				
				// Ricevo il bean della nuova ordinazione effettuata dal client.
				nuova = (Ordinazione) input.readObject();
				if (nuova == null)
					return;
						
				stack.aggiungiOrdinazione(nuova);
				// Invio l'esito dell'operazione al client.
				output.writeInt((int) nuova.getId());
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
