package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
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
	
	@SuppressWarnings("unchecked")
	public List<Municipio> listarTodos(){
		Criteria criteria = getCriteria(Municipio.class);
		criteria.addOrder(Order.asc("nomeMunicipio"));
		return criteria.list();
	}
}
