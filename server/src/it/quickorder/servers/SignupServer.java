package it.quickorder.servers;

import it.quickorder.control.CodaNotifiche;
import it.quickorder.gui.Main;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignupServer implements Runnable 
{
	private ServerSocket srvSocket;
	private SimpleDateFormat formato;
	private CodaNotifiche coda;
	
	public SignupServer(int port, CodaNotifiche coda) throws IOException
	{
		srvSocket = new ServerSocket(port ,50, InetAddress.getByName(Main.ip_address));
		formato = new SimpleDateFormat("hh:mm.ss");
		this.coda = coda;
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