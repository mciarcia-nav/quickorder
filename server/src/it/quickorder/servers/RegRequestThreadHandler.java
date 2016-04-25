package it.quickorder.servers;

import it.quickorder.control.CodaNotifiche;
import it.quickorder.domain.Cliente;
import it.quickorder.domain.Notifica;
import it.quickorder.helpers.CodiceFiscaleUtil;
import it.quickorder.helpers.HibernateUtil;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

public class RegRequestThreadHandler implements Runnable
{
	private Socket socket;
	private CodaNotifiche coda;
	private int abilitato;

	public RegRequestThreadHandler(Socket socket, CodaNotifiche coda)
	{
		this.socket = socket;
		this.coda = coda;
		abilitato = -1;
	}
	
	public synchronized void setAbilitato(int valore)
	{
		abilitato = valore;
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
				
				boolean check = true;/*CodiceFiscaleUtil.isValid(nuovoCliente.getCodiceFiscale(), nuovoCliente.getNome(), 
						nuovoCliente.getCognome(), nuovoCliente.getDataNascita(), nuovoCliente.getSesso(), nuovoCliente.getLuogoNascita());
				*/ 
				if (check)
					try
					{
						session.beginTransaction();
						@SuppressWarnings("rawtypes")
						List risultati = session.createQuery("FROM Cliente c WHERE c.codiceFiscale = :cf").setString("cf", nuovoCliente.getCodiceFiscale()).list();
						if (risultati.size() > 0)
							result = "DUP_CF";
						else
						{
							risultati = session.createQuery("FROM Cliente c WHERE c.email = :mail").setString("mail", nuovoCliente.getEmail()).list();
							if (risultati.size() > 0)
								result = "DUP_EMAIL";
							else
							{
								result = "OK";
							}
						}
					}
					catch (HibernateException ex2)
					{
						result = "FAILED";
						ex2.printStackTrace();
					}
				else
				{
					result = "WRONG_CF";
				}
				
				// Invio l'esito dell'operazione al client.
				output.writeObject(result);
				output.flush();	
				
				if (result.equalsIgnoreCase("OK"))
				{
					Notifica nuova = new Notifica();
					nuova.setCliente(nuovoCliente);
					nuova.setThreadPartenza(this);
					coda.aggiungiNotifica(nuova);
					while (abilitato == -1)
						try 
						{
							Thread.sleep(5000);
						} catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					output.writeInt(abilitato);
					output.flush();	
					nuovoCliente.setAbilitato(abilitato == 1);
					Transaction t = session.beginTransaction();
					session.save(nuovoCliente);
					t.commit();
				}
				
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

