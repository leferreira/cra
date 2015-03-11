package br.com.ieptbto.cra.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class TipoArquivoDAO extends AbstractBaseDAO {

	public TipoArquivo buscarTipoArquivo(Arquivo arquivo) {
		Criteria criteria = getCriteria(TipoArquivo.class);
		criteria.add(Restrictions.eq("constante", arquivo.getNomeArquivo().substring(0, 1).toUpperCase()));
		return TipoArquivo.class.cast(criteria.uniqueResult());
	}

}
