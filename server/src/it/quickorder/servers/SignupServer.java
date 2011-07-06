package it.quickorder.servers;

import it.quickorder.control.CodaNotifiche;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignupServer implements Runnable 
{
	private ServerSocket srvSocket;
	private int port;
	private SimpleDateFormat formato;
	private CodaNotifiche coda;
	
	public SignupServer(int port, CodaNotifiche coda) throws IOException
	{
		srvSocket = new ServerSocket(port);
		formato = new SimpleDateFormat("hh:mm.ss");
		this.coda = coda;
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
				System.out.println("[" + formato.format(corrente) + "] - Richiesta di registrazione da client: " + socketClient.getInetAddress().getHostAddress());
				Runnable runnable = new RegRequestThreadHandler(socketClient, coda);
				Thread nuovoThread = new Thread(runnable);
				nuovoThread.start();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		}
	}

}