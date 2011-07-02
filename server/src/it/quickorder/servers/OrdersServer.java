package it.quickorder.servers;

import it.quickorder.domain.Ordinazione;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class OrdersServer implements Runnable 
{
	private ServerSocket srvSocket;
	private int port;
	private SimpleDateFormat formato;
	private List<Ordinazione> ordinazioni;
	
	public OrdersServer(int port)
	{
		this.port = port;
		ordinazioni = new ArrayList<Ordinazione>(100);
		formato = new SimpleDateFormat("hh:mm.ss");
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
				Date corrente = new Date(System.currentTimeMillis());
				System.out.println("[" + formato.format(corrente) + "] - Richiesta di ordinazione da client: " + socketClient.getInetAddress().getHostAddress());
				Runnable runnable = new OrderRequestThreadHandler(socketClient, ordinazioni);
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
	private List<Ordinazione> ordinazioni;
	
	public OrderRequestThreadHandler(Socket socket, List<Ordinazione> ordinazioni)
	{
		this.socket = socket;
		this.ordinazioni = ordinazioni;
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
				
				// Invio l'esito dell'operazione al client.
				output.writeInt(0);
				output.flush();	
				
			}
			finally
			{
				socket.close();
			}
			if (nuova == null)
				return;
			nuova.setArrivo(new Date(System.currentTimeMillis()));
			
			synchronized (ordinazioni) 
			{	
				ordinazioni.add(nuova);
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
