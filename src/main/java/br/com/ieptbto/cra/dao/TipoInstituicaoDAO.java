package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.TipoInstituicao;

@Repository
public class TipoInstituicaoDAO extends AbstractBaseDAO {

	@Transactional(readOnly = true)
	public TipoInstituicao salvar(TipoInstituicao tipoInstituicao) {
		TipoInstituicao nova = new TipoInstituicao();
		Transaction transaction = getBeginTransation();
		try {
			nova = save(tipoInstituicao);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return nova;
	}

	@Transactional(readOnly = true)
	public TipoInstituicao alterar(TipoInstituicao tipo) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(tipo);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return tipo;
	}
	
	public void inserirTipoInstituicaoInicial(){
		Transaction transaction = getBeginTransation();
		try {
			TipoInstituicao tipoInstituicao = new TipoInstituicao();
			tipoInstituicao.setTipoInstituicao("CRA");
			save(tipoInstituicao);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> buscarListaTipoInstituicao(){
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}
	
	public TipoInstituicao buscarTipoInstituicao(String tipoInstituicao){
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipoInstituicao));
		return TipoInstituicao.class.cast(criteria.uniqueResult());
	}
}
