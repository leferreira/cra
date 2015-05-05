package br.com.ieptbto.cra.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.CabecalhoRemessa;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class CabecalhoDAO extends AbstractBaseDAO {

	/**
	 * Verifica se o número seguencial da remessa é único para aquele banco e
	 * cidade.
	 * 
	 * @param cabecalhoRemessa
	 * @return
	 */
	public boolean isSequencialUnicoPorBanco(CabecalhoRemessa cabecalhoRemessa) {
		Criteria criteria = getCriteria(CabecalhoRemessa.class);

		criteria.add(Restrictions.eq("codigoMunicipio", cabecalhoRemessa.getCodigoMunicipio()));
		criteria.add(Restrictions.eq("numeroCodigoPortador", cabecalhoRemessa.getNumeroCodigoPortador()));
		criteria.add(Restrictions.eq("numeroSequencialRemessa", cabecalhoRemessa.getNumeroSequencialRemessa()));

		if (criteria.uniqueResult() == null || criteria.list().isEmpty()) {
			return true;
		}
		return false;
	}

}
