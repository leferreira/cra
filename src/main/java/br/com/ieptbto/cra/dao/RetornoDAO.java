package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.SituacaoBatimento;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class RetornoDAO extends AbstractBaseDAO {
	
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	
	public List<Remessa> buscarRetornosParaBatimento(){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.RETORNO));
		criteria.add(Restrictions.eq("situacaoBatimento", false));
		criteria.addOrder(Order.asc("instituicaoDestino"));
		return criteria.list();
	}

	public List<Remessa> buscarRetornosConfirmados(){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.RETORNO));
		criteria.add(Restrictions.eq("situacaoBatimento", true));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.addOrder(Order.asc("instituicaoDestino"));
		return criteria.list();
	}
	
	public List<Remessa> buscarRetornosConfirmadosPorInstituicao(Instituicao instituicaoDestino){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.RETORNO));
		criteria.add(Restrictions.eq("situacaoBatimento", true));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicaoDestino));
		return criteria.list();
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		Criteria criteria = getCriteria(Retorno.class);
		criteria.add(Restrictions.eq("tipoOcorrencia", TipoOcorrencia.PAGO.getConstante()));
		criteria.add(Restrictions.eq("remessa", retorno));
		criteria.setProjection(Projections.sum("saldoTitulo"));
		return BigDecimal.class.cast(criteria.uniqueResult());
	}
	

	public void confirmarBatimento(List<Remessa> retornosConfirmados){
		Transaction transaction = getBeginTransation();
		try {
			for (Remessa retorno : retornosConfirmados) {
				retorno.setSituacaoBatimento(true);
				update(retorno);
			}
			transaction.commit();
			logger.info("A confirmação do batimento foi realizado com sucesso!");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível confirmar estas remessas.");
		}
	}
	
	public void removerConfirmado(Remessa retorno){
		
		Transaction transaction = getBeginTransation();
		try {
			retorno.setSituacaoBatimento(false);
			update(retorno);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void gerarRetornos(Usuario usuarioAcao, List<Arquivo> arquivosDeRetorno){
		Transaction transaction = getBeginTransation();
		
		try {
			for (Arquivo retorno : arquivosDeRetorno){
				StatusArquivo status = new StatusArquivo();
				status.setData(new Date());
				status.setStatus(SituacaoArquivo.AGUARDANDO.getLabel());
				
				retorno.setUsuarioEnvio(usuarioAcao);
				retorno.setStatusArquivo(save(status));
				save(retorno);
				
				for (Remessa r : retorno.getRemessas()) {
					r.setSituacao(true);
					r.setArquivoGeradoProBanco(retorno);
					update(r);
					
					Batimento batimento = new Batimento();
					batimento.setRemessa(r);
					batimento.setSituacaoBatimento(SituacaoBatimento.GERADO);
					batimento.setDataBatimento(new LocalDateTime());
					
					save(batimento);
				}
				logger.info("O arquivo " + retorno.getNomeArquivo() + " foi inserido na base com sucesso!");
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
