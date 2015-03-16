package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

@Repository
public class TipoArquivoDAO extends AbstractBaseDAO {

	@Transactional(readOnly = true)
	public TipoArquivo salvar(TipoArquivo tipoArquivo) {
		TipoArquivo novo = new TipoArquivo();
		Transaction transaction = getBeginTransation();
		try {
			novo = save(tipoArquivo);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novo;
	}

	@Transactional(readOnly = true)
	public TipoArquivo alterar(TipoArquivo tipoArquivo) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(tipoArquivo);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return tipoArquivo;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TipoArquivo> buscarTiposArquivo(){
		Criteria criteria = getCriteria(TipoArquivo.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
	
	public TipoArquivo buscarPorTipoArquivo(TipoArquivoEnum tipoArquivo){
		Criteria criteria = getCriteria(TipoArquivo.class);
		criteria.add(Restrictions.eq("tipoArquivo", tipoArquivo));
		criteria.addOrder(Order.asc("id"));
		return TipoArquivo.class.cast(criteria.uniqueResult());
	}
}
