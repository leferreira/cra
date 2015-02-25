package br.com.ieptbto.cra.dao;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.TipoInstituicao;

@Repository
public class TipoInstituicaoDAO extends AbstractBaseDAO {

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
	
	public TipoInstituicao buscarTipoInstituicao(String tipoInstituicao){
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipoInstituicao));
		return TipoInstituicao.class.cast(criteria.uniqueResult());
	}
}
