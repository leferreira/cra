package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "unchecked" })
@Repository
public class RemessaDAO extends AbstractBaseDAO {

	public List<Remessa> listarRemessasPorInstituicao(Instituicao instituicao) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");
		System.out.println(instituicao.getTipoInstituicao().getTipoInstituicao());
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("instituicaoDestino", instituicao))
					.add(Restrictions.eq("a.instituicaoEnvio", instituicao)));
		}
		criteria.addOrder(Order.desc("a.dataEnvio"));

		return criteria.list();
	}
}
