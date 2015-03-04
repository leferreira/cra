package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Instituicao;

@Repository
public class InstituicaoDAO extends AbstractBaseDAO {

	@Autowired
	TipoInstituicaoDAO tipoInstituicaoDAO;
	
	@Transactional(readOnly = true)
	public Instituicao salvar(Instituicao instituicao) {
		Instituicao nova = new Instituicao();
		Transaction transaction = getBeginTransation();
		try {
			nova = save(instituicao);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return nova;
	}

	@Transactional(readOnly = true)
	public Instituicao alterar(Instituicao instituicao) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(instituicao);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return instituicao;
	}
	
	public void inserirInstituicaoInicial() {
		Transaction transaction = getBeginTransation();
		try {
			Instituicao instituicao = new Instituicao();
			instituicao.setInstituicao("CRA");
			instituicao.setCnpj("123");
			instituicao.setTipoInstituicao(tipoInstituicaoDAO.buscarTipoInstituicao("CRA"));
			save(instituicao);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}

	}

	public Instituicao buscarInstituicao(String instituicao) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("instituicao", instituicao));
		return Instituicao.class.cast(criteria.uniqueResult());
	}
	
	@SuppressWarnings("unchecked")
	public List<Instituicao> buscarListaInstituicao(){
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
	
	public Instituicao buscarInstituicaoInicial(String instituicao) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("instituicao", instituicao));
		return Instituicao.class.cast(criteria.uniqueResult());
	}
}
