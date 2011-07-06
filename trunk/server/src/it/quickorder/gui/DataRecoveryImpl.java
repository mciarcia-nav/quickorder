package it.quickorder.gui;

import it.quickorder.domain.Cliente;
import it.quickorder.helpers.HibernateUtil;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.TypedValue;

public class DataRecoveryImpl implements DataRecovery
{
	private Session session;
	public DataRecoveryImpl()
	{
		session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
	}
	
	@Override
	public List<Cliente> getClienti() 
	{
		Query query = session.createQuery("select distinct cliente from Cliente cliente");
        return query.list();
	}

	@Override
	public void abilitaCliente(Cliente cliente) 
	{
		cliente.setAbilitato(true);
		Cliente result = (Cliente) session.createQuery("from cliente c where c.codice_fiscale=?").setString(0, cliente.getCodiceFiscale()).uniqueResult();
	}
	
	public void disabilitaCliente(Cliente cliente) 
	{
		cliente.setAbilitato(false);
		Cliente result = (Cliente) session.createQuery("from cliente c where c.codice_fiscale=?").setString(0, cliente.getCodiceFiscale()).uniqueResult();
	}
}
