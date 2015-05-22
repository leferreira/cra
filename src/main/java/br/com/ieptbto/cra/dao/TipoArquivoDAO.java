package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class TipoArquivoDAO extends AbstractBaseDAO {

	public TipoArquivo salvar(TipoArquivo tipoArquivo) {
		TipoArquivo novo = new TipoArquivo();
		Transaction transaction = getBeginTransation();
		try {
			novo = save(tipoArquivo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novo;
	}

	public TipoArquivo alterar(TipoArquivo tipoArquivo) {
		Transaction transaction = getBeginTransation();
		try {
			update(tipoArquivo);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return tipoArquivo;
	}

	@SuppressWarnings("unchecked")
	public List<TipoArquivo> buscarTiposArquivo() {
		Criteria criteria = getCriteria(TipoArquivo.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	@Transactional(readOnly = true)
	public TipoArquivo buscarPorTipoArquivo(TipoArquivoEnum tipoArquivo) {
		Criteria criteria = getCriteria(TipoArquivo.class);
		criteria.add(Restrictions.eq("tipoArquivo", tipoArquivo));
		criteria.addOrder(Order.asc("id"));
		return TipoArquivo.class.cast(criteria.uniqueResult());
	}

	public TipoArquivo buscarTipoArquivo(Arquivo arquivo) {
		Criteria criteria = getCriteria(TipoArquivo.class);
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(arquivo.getNomeArquivo().toUpperCase());
		criteria.add(Restrictions.eq("tipoArquivo", tipoArquivo));
		return TipoArquivo.class.cast(criteria.uniqueResult());
	}

	public void inserirTipoArquivo(String tipo) {
		TipoArquivo tipoArquivo = new TipoArquivo();
		tipoArquivo.setTipoArquivo(TipoArquivoEnum.getTipoArquivoEnum(tipo));

		salvar(tipoArquivo);
	}
}
