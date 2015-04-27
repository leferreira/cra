package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.enumeration.SituacaoBatimento;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class BatimentoDAO extends AbstractBaseDAO {
	
	@SuppressWarnings("unchecked")
	public List<Remessa> buscarRetornosParaBatimento(){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("batimento", "batimento");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.RETORNO));
		criteria.add(Restrictions.eq("batimento.situacaoBatimento", SituacaoBatimento.NAO_CONFIRMADO));
		return criteria.list();
	}

	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}
	
	public void realizarBatimento(List<Remessa> retornos){
		Session session = getSession();
		Transaction transaction = session.beginTransaction();

		try {
			for (Remessa retorno : retornos) {
				Batimento batimento = buscarPorPK(retorno.getBatimento(), Batimento.class);
				batimento.setSituacaoBatimento(SituacaoBatimento.RETORNO_GERADO);
				batimento.setDataBatimento(new LocalDateTime());
				update(batimento);
			}
			transaction.commit();
			logger.info("O batimento foi realizado com sucesso!");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível realizar esse batimento.");
		}
	}
}
