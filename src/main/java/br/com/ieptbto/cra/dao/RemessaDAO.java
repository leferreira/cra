package br.com.ieptbto.cra.dao;

import java.util.List;

import org.apache.log4j.Logger;
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
@SuppressWarnings({ "unchecked", "unused" })
@Repository
public class RemessaDAO extends AbstractBaseDAO {

	private static final Logger logger = Logger.getLogger(RemessaDAO.class);

	public List<Remessa> listarRemessasPorInstituicao(Instituicao instituicao) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");
//		if (!instituicao.getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("instituicaoDestino", instituicao))
					.add(Restrictions.eq("a.instituicaoEnvio", instituicao)));
//		}
		criteria.addOrder(Order.desc("a.dataEnvio"));

		List<Remessa> listaRemessas = criteria.list();
		return listaRemessas;
	}
}
