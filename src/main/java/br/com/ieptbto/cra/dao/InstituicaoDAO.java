package br.com.ieptbto.cra.dao;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;

@SuppressWarnings({ "unchecked" })
@Repository
public class InstituicaoDAO extends AbstractBaseDAO {

	public void inserirInstituicaoInicial(){
		Transaction transaction = getBeginTransation();
		try {
			Instituicao instituicao = new Instituicao();
			instituicao.setInstituicao("CRA");
			instituicao.setCnpj("123");
			save(instituicao);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}

	}
	
	public Instituicao buscarInstituicaoInicial(String instituicao){
		inserirInstituicaoInicial();
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("instituicao", instituicao));
		return Instituicao.class.cast(criteria.uniqueResult());
	}
}
