package it.quickorder.gui;

import it.quickorder.domain.Cliente;
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

public class DataRecoveryImpl implements DataRecovery
{
	private Session session;
	public DataRecoveryImpl()
	{
		session = HibernateUtil.getSessionFactory().getCurrentSession();
        
	}
	
	@Override
	public List<Cliente> getClienti() 
	{
		if(!session.isOpen())
		{
			System.out.println("La sessione è chiusa");
			session = HibernateUtil.getSessionFactory().getCurrentSession();
		}
		session.beginTransaction();
		Query query = session.createQuery("select distinct cliente from Cliente cliente");
        return query.list();
	}

	@Override
	public void abilitaCliente(Cliente cliente) 
	{
		if(!session.isOpen())
		{
			System.out.println("La sessione è chiusa");
			session = HibernateUtil.getSessionFactory().getCurrentSession();
		}
		Transaction tr = session.beginTransaction();
		Cliente toUpdate = (Cliente) session.get(Cliente.class, new String(cliente.getCodiceFiscale()));
		toUpdate.setAbilitato(true);
		session.saveOrUpdate(toUpdate);
		tr.commit();
	}
	
	public void disabilitaCliente(Cliente cliente) 
	{
		if(!session.isOpen())
		{
			System.out.println("La sessione è chiusa");
			session = HibernateUtil.getSessionFactory().getCurrentSession();
		}
		Transaction tr = session.beginTransaction();
		Cliente toUpdate = (Cliente) session.get(Cliente.class, new String(cliente.getCodiceFiscale()));
		toUpdate.setAbilitato(false);
		session.saveOrUpdate(toUpdate);
		tr.commit();
		
	}
	
	public void eliminaCliente(Cliente cliente)
	{
		if(!session.isOpen())
		{
			System.out.println("La sessione è chiusa");
			session = HibernateUtil.getSessionFactory().getCurrentSession();
		}
		Transaction tr = session.beginTransaction();
		Cliente toDelete = (Cliente) session.get(Cliente.class, new String(cliente.getCodiceFiscale()));
		session.delete(toDelete);
		tr.commit();
		
	}
}
