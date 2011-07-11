package it.quickorder.repository;

import it.quickorder.domain.Cliente;
import it.quickorder.domain.Ordinazione;
import it.quickorder.helpers.HibernateUtil;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.TypedValue;

public class GetDataFromDBImpl implements GetDataFromDB
{
	private Session session;
	public GetDataFromDBImpl()
	{
		session = HibernateUtil.getSessionFactory().getCurrentSession();
        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> getClienti() 
	{
		if(!session.isOpen())
			session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		Query query = session.createQuery("from Cliente");
        List<Cliente> clienti = (List<Cliente>) query.list();
        session.getTransaction().rollback();
        return clienti;
	}

	@Override
	public void abilitaCliente(Cliente cliente) 
	{
		if(!session.isOpen())
			session = HibernateUtil.getSessionFactory().getCurrentSession();

		Transaction tr = session.beginTransaction();
		Cliente toUpdate = (Cliente) session.get(Cliente.class, new String(cliente.getCodiceFiscale()));
		toUpdate.setAbilitato(true);
		session.saveOrUpdate(toUpdate);
		tr.commit();
	}
	
	public void disabilitaCliente(Cliente cliente) 
	{
		if(!session.isOpen())
			session = HibernateUtil.getSessionFactory().getCurrentSession();

		Transaction tr = session.beginTransaction();
		Cliente toUpdate = (Cliente) session.get(Cliente.class, new String(cliente.getCodiceFiscale()));
		toUpdate.setAbilitato(false);
		session.saveOrUpdate(toUpdate);
		tr.commit();
		
	}
	
	public void eliminaCliente(Cliente cliente)
	{
		if(!session.isOpen())
			session = HibernateUtil.getSessionFactory().getCurrentSession();

		Transaction tr = session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Ordinazione> daEliminare = (List<Ordinazione>)session.createQuery("from Ordinazione WHERE cliente.codiceFiscale = :var").setString("var", cliente.getCodiceFiscale()).list();
		for (Ordinazione o : daEliminare)
			session.delete(o);
		Cliente toDelete = (Cliente) session.get(Cliente.class, new String(cliente.getCodiceFiscale()));
		session.delete(toDelete);
		tr.commit();
		
	}
}
