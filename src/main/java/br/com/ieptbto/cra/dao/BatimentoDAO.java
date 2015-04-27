package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
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
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.RETORNO));
		criteria.add(Restrictions.eq("situacaoBatimento", false));
		criteria.addOrder(Order.asc("instituicaoDestino"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Remessa> buscarBatimentosConfirmados(){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.RETORNO));
		criteria.add(Restrictions.eq("situacaoBatimento", true));
		criteria.addOrder(Order.asc("instituicaoDestino"));
		return criteria.list();
	}
	
	public void removerConfirmado(Remessa retorno){
		Session session = getSession();
		Transaction transaction = session.beginTransaction();

		try {
			retorno.setSituacaoBatimento(false);
			update(retorno);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível confirmar estas remessas.");
		}
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}
	
	public void confirmarBatimento(List<Remessa> retornosConfirmados){
		Session session = getSession();
		Transaction transaction = session.beginTransaction();

		try {
			for (Remessa retorno : retornosConfirmados) {
				Batimento batimento = new Batimento();
				retorno.setSituacaoBatimento(true);
				batimento.setRemessa(update(retorno));
				batimento.setSituacaoBatimento(SituacaoBatimento.CONFIRMADO);
				batimento.setDataBatimento(new LocalDateTime());
				save(batimento);
			}
			transaction.commit();
			logger.info("A confirmação do batimento foi realizado com sucesso!");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível confirmar estas remessas.");
		}
	}
	
	/**
	 * Método que irá chamar a Fábrica de Arquivo de Retorno
	 * */
	public void realizarBatimento(List<Remessa> retornos){
		Session session = getSession();
		Transaction transaction = session.beginTransaction();

		/***
		 * Gerar arquivo de retorno 
		 * */
		try {
			for (Remessa retorno : retornos) {
				Batimento batimento = new Batimento();
				batimento.setRemessa(retorno);
				batimento.setSituacaoBatimento(SituacaoBatimento.GERADO);
				batimento.setDataBatimento(new LocalDateTime());
				save(batimento);
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
