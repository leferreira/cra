package br.com.ieptbto.cra.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author thasso
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class ConfirmacaoDAO extends AbstractBaseDAO {
	
	public void salvarArquivosDeConfirmacaoGerados(Usuario usuarioAcao, List<Arquivo> arquivosDeConfirmacao) {
		Transaction transaction = getBeginTransation();
		
		try {
			for (Arquivo confirmacao : arquivosDeConfirmacao){
				StatusArquivo status = new StatusArquivo();
				status.setData(new Date());
				status.setStatus(SituacaoArquivo.AGUARDANDO.getLabel());
				
				confirmacao.setUsuarioEnvio(usuarioAcao);
				confirmacao.setStatusArquivo(save(status));
				save(confirmacao);
				
				for (Remessa remessa: confirmacao.getRemessas()){
					remessa.setSituacao(true);
					remessa.setArquivoGeradoProBanco(confirmacao);
					update(remessa);
				}

				logger.info("O arquivo " + confirmacao.getNomeArquivo() + " foi inserido na base com sucesso!");
			}
			transaction.commit();
			logger.info("As confirmações foram geradas pelo usuário  " + usuarioAcao.getLogin()
			        + "e foram inseridas na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível gerar os arquivos de confirmação.");
		}
	}

	public List<Remessa> buscarConfirmacoesPendentesDeEnvio() {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.CONFIRMACAO));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.addOrder(Order.asc("instituicaoDestino"));
		return criteria.list();
	}

	public List<Remessa> buscarConfirmacoesPendentesPorInstituicao(Instituicao instituicaoDestino) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.createAlias("arquivo.tipoArquivo", "tipoArquivo");
		criteria.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.CONFIRMACAO));
		criteria.add(Restrictions.eq("situacao", false));
		criteria.add(Restrictions.eq("instituicaoDestino", instituicaoDestino));
		return criteria.list();
	}
}
