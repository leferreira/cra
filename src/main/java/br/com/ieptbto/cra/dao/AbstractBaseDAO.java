package br.com.ieptbto.cra.dao;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.AbstractEntidade;

/**
 * 
 * 
 */
@SuppressWarnings({ "unchecked" })
public class AbstractBaseDAO {

	protected static final Logger logger = Logger.getLogger(AbstractBaseDAO.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Iniciar o bean com o session factory.
	 * 
	 * @param session
	 */
	public void setSessionFactory(SessionFactory sf) {
		this.sessionFactory = sf;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public <T extends AbstractEntidade<T>> T merge(T obj) {
		T t = (T) getSession().get(obj.getClass(), getSession().save(obj));
		flush();
		return t;
	}

	/**
	 * Remove todos os objetos passados na lista.
	 * 
	 * @param <T>
	 * @param lista
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public <T> void deleteAll(List<T> lista) {
		for (T t : lista) {
			delete(t);
		}
	}

	/**
	 * Salvar um objeto sem realizar flush e sem transacao.
	 * 
	 * @param entidade
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveOnly(Object entidade) {
		getSession().save(entidade);
	}

	/**
	 * 
	 * @param obj
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public <T extends AbstractEntidade<T>> T save(T obj) {
		T t = (T) getSession().get(obj.getClass(), getSession().save(obj));
		return t;
	}

	/**
	 * 
	 * @param obj
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public <T extends AbstractEntidade<T>> void persist(T obj) {
		getSession().persist(obj);
	}

	/**
	 * Buscar uma entidade pelo seu identificador.
	 * 
	 * @param <T>
	 * @param entidade
	 * @return
	 */
	public <T extends AbstractEntidade<T>> T buscarPorPK(T entidade) {
		Class<T> clazz = (Class<T>) entidade.getClass();
		return buscarPorPK(entidade, clazz);
	}

	/**
	 * Buscar uma entidade pelo seu identificador.
	 * 
	 * @param <T>
	 * @param entidade
	 * @param clazz
	 * @return
	 */
	public <T extends AbstractEntidade<T>> T buscarPorPK(T entidade, Class<T> clazz) {
		return (T) getSession().get(clazz, entidade.getId());
	}

	/**
	 * Criar uma query com a querystring passada.
	 * 
	 * @param queryString
	 * @return
	 */
	public Query createQuery(String queryString) {
		return getSession().createQuery(queryString);
	}
	
	public Query createSQLQuery(String queryString) {
		return getSession().createSQLQuery(queryString);
	}

	public <T extends AbstractEntidade<T>> Criteria getCriteria(Class<T> class1) {
		return getSession().createCriteria(class1);
	}

	/**
	 * Retorna todos os objetos do tipo clazz
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getAll(Class<T> clazz) {
		Query query = getSession().createQuery("from " + clazz.getName());
		return Collections.checkedList(query.list(), clazz);
	}

	public Transaction getBeginTransation() {
		return getSession().beginTransaction();
	}

	/**
	 * Salvar uma entidade na sessão sem realizar o flush.
	 * 
	 * @see org.hibernate.Session#saveOrUpdate(Object)
	 * 
	 * @param entidade
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveOrUpdateOnly(Object entidade) {
		getSession().saveOrUpdate(entidade);
	}

	/**
	 * Atualiza o objeto.
	 * 
	 * @param object
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Object object) {
		getSession().update(object);
		flush();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public <T extends AbstractEntidade<T>> T update(T object) {
		getSession().update(object);
		T t = (T) getSession().get(object.getClass(), object.getId());
		flush();
		return t;
	}

	/**
	 * desassocia obj da sessao
	 * 
	 * @param obj
	 */
	public void evict(Object obj) {
		getSession().evict(obj);
	}

	/**
	 * Faz delete no obj
	 * 
	 * @param obj
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void delete(Object obj) {
		getSession().delete(obj);
		flush();
	}

	/**
	 * @see org.hibernate.Session#flush()
	 */
	public void flush() {
		getSession().flush();
	}

	public <t> void inserirLista(List<t> lista) {
	}

}
