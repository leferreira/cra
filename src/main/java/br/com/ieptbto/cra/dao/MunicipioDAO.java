package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Municipio;

@Repository
public class MunicipioDAO extends AbstractBaseDAO{

	
	@Transactional(readOnly = true)
	public Municipio salvar(Municipio municipio) {
		Municipio novoMunicipio = new Municipio();
		Transaction transaction = getBeginTransation();
		try {
			novoMunicipio = save(municipio);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novoMunicipio;
	}

	@Transactional(readOnly = true)
	public Municipio alterar(Municipio municipio) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(municipio);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return municipio;
	}
	
	public Municipio buscarMunicipioPorNome(String nomeMunicipio) {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.add(Restrictions.like("nomeMunicipio", nomeMunicipio, MatchMode.EXACT));

		return Municipio.class.cast(criteria.uniqueResult());
	}
	
	@SuppressWarnings("unchecked")
	public List<Municipio> listarTodos(){
		Criteria criteria = getCriteria(Municipio.class);
		criteria.addOrder(Order.asc("nomeMunicipio"));
		return criteria.list();
	}
}
