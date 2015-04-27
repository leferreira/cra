package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class TituloDAO extends AbstractBaseDAO {

	private Instituicao instituicaoUsuario;

	public List<TituloRemessa> buscarTitulosRemessa(Arquivo arquivo){
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", arquivo));
		return criteria.list();
	}
	
	public List<Confirmacao> buscarTitulosConfirmacao(Arquivo arquivo) {
		Criteria criteria = getCriteria(Confirmacao.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", arquivo));
		return criteria.list();
	}
	
	public List<Retorno> buscarTitulosRetorno(Arquivo arquivo) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(Restrictions.eq("remessa.arquivo", arquivo));
		return criteria.list();
	}
	
	public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Usuario user) {
		this.instituicaoUsuario = user.getInstituicao();

		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");
		if (!instituicaoUsuario.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("remessa.instituicaoDestino", instituicaoUsuario))
			        .add(Restrictions.eq("arquivo.instituicaoEnvio", instituicaoUsuario)));
		}
		if (titulo.getCodigoPortador() != null)
			criteria.add(Restrictions.ilike("codigoPortador", titulo.getCodigoPortador(), MatchMode.ANYWHERE));
		
		if (titulo.getNumeroProtocoloCartorio() != null)
		    criteria.add(Restrictions.ilike("numeroProtocoloCartorio", titulo.getNumeroProtocoloCartorio(), MatchMode.ANYWHERE));
		
		if (titulo.getNumeroTitulo() != null)
	        criteria.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.ANYWHERE));
		
		if (titulo.getNomeDevedor() != null)
		    criteria.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));
		
		if (titulo.getDocumentoDevedor() != null)
		    criteria.add(Restrictions.ilike("documentoDevedor", titulo.getDocumentoDevedor(), MatchMode.ANYWHERE));
		
		if (titulo.getDataEmissaoTitulo() != null)
		    criteria.add(Restrictions.eq("dataEmissaoTitulo", titulo.getDataEmissaoTitulo()));
		
		if (titulo.getDataOcorrencia() != null)
		    criteria.add(Restrictions.eq("dataOcorrencia", titulo.getDataOcorrencia()));
		
		if (titulo.getNossoNumero() != null)
		    criteria.add(Restrictions.ilike("nossoNumero", titulo.getNossoNumero(), MatchMode.ANYWHERE));

		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}

	public TituloRemessa buscarTituloPorChave(TituloRemessa titulo) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("codigoPortador", titulo.getCodigoPortador()));
		criteria.add(Restrictions.eq("nossoNumero", titulo.getNossoNumero()));
		if (titulo.getNumeroTitulo() != null)
			criteria.add(Restrictions.eq("numeroTitulo", titulo.getNumeroTitulo()));

		return TituloRemessa.class.cast(criteria.uniqueResult());
	}
	
	public List<Historico> buscarHistoricoDoTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(Historico.class);
		Hibernate.initialize(titulo);
		criteria.createAlias("titulo", "titulo");
		criteria.add(Restrictions.eq("titulo.codigoPortador", titulo.getCodigoPortador()));
		criteria.add(Restrictions.eq("titulo.nossoNumero", titulo.getNossoNumero()));
		
		return criteria.list();
	}

	public TituloRemessa salvar(Titulo titulo, Transaction transaction) {
		if (TituloRemessa.class.isInstance(titulo)) {
			TituloRemessa tituloRemessa = TituloRemessa.class.cast(titulo);
			return salvarTituloRemessa(tituloRemessa, transaction);
		}
		if (Confirmacao.class.isInstance(titulo)) {
			Confirmacao tituloConfirmacao = Confirmacao.class.cast(titulo);
			return salvarTituloConfirmacao(tituloConfirmacao, transaction);
		}
		if (Retorno.class.isInstance(titulo)) {
			Retorno tituloConfirmacao = Retorno.class.cast(titulo);
			return salvarTituloRetorno(tituloConfirmacao, transaction);
		}
		return null;
	}

	private TituloRemessa salvarTituloRetorno(Retorno tituloRetorno, Transaction transaction) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("codigoPortador", tituloRetorno.getCodigoPortador()));
		criteria.add(Restrictions.eq("nossoNumero", tituloRetorno.getNossoNumero()));
//		criteria.add(Restrictions.eq("numeroTitulo", tituloRetorno.getNumeroTitulo()));

		TituloRemessa titulo = TituloRemessa.class.cast(criteria.uniqueResult());

		if (titulo == null) {
			throw new InfraException("Não existe um título para esse retorno");
		}
		try {
			tituloRetorno.setTitulo(titulo);
			titulo.setRetorno(save(tituloRetorno));
			save(titulo);
			logger.info("Retorno salvo com sucesso");

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			throw new InfraException("Não existe um título para esse retorno");
		}
		return titulo;
	}

	private TituloRemessa salvarTituloConfirmacao(Confirmacao tituloConfirmacao, Transaction transaction) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("codigoPortador", tituloConfirmacao.getCodigoPortador()));
		criteria.add(Restrictions.eq("nossoNumero", tituloConfirmacao.getNossoNumero()));
		criteria.add(Restrictions.eq("numeroTitulo", tituloConfirmacao.getNumeroTitulo()));

		TituloRemessa titulo = TituloRemessa.class.cast(criteria.uniqueResult());

		if (titulo == null) {
			throw new InfraException("Não existe um título para essa confirmação");
		}
		try {
			tituloConfirmacao.setTitulo(titulo);
			titulo.setConfirmacao(save(tituloConfirmacao));
			save(titulo);
			logger.info("Confirmação salva com sucesso");

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			throw new InfraException("Não existe um título para essa confirmação");
		}
		return titulo;
	}

	private TituloRemessa salvarTituloRemessa(TituloRemessa tituloRemessa, Transaction transaction) {
		try {
			return save(tituloRemessa);
		} catch (Exception ex) {
			if (PSQLException.class.isInstance(ex)) {
				logger.error(ex.getMessage(), ex.getCause());
				new InfraException("O Título número: " + tituloRemessa.getNumeroTitulo() + " já existe já foi inserido.");
				return null;
			} else {
				transaction.rollback();
				logger.error(ex.getMessage(), ex.getCause());
				throw new InfraException("O Título número: " + tituloRemessa.getNumeroTitulo() + " não pode ser inserido.");
			}
		}
	}
}