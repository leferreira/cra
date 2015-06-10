package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class TituloDAO extends AbstractBaseDAO {

	public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Usuario user) {
		Instituicao instituicaoUsuario = user.getInstituicao();

		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.add(filtrarTitulosPorInstituicao(instituicaoUsuario));

		if (titulo.getCodigoPortador() != null)
			criteria.add(Restrictions.ilike("codigoPortador", titulo.getCodigoPortador(), MatchMode.ANYWHERE));

		if (titulo.getNumeroProtocoloCartorio() != null) {
			criteria.createAlias("confirmacao", "confirmacao");
			criteria.add(Restrictions.ilike("confirmacao.numeroProtocoloCartorio", titulo.getNumeroProtocoloCartorio(), MatchMode.ANYWHERE));
		}

		if (titulo.getNumeroTitulo() != null)
			criteria.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.EXACT));

		if (titulo.getNomeDevedor() != null)
			criteria.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE));

		if (titulo.getDocumentoDevedor() != null)
			criteria.add(Restrictions.ilike("documentoDevedor", titulo.getDocumentoDevedor(), MatchMode.ANYWHERE));

		if (titulo.getDataEmissaoTitulo() != null)
			criteria.add(Restrictions.between("dataEmissaoTitulo", titulo.getDataEmissaoTitulo(), titulo.getDataEmissaoTitulo()));

		if (titulo.getDataOcorrencia() != null)
			criteria.add(Restrictions.between("dataOcorrencia", titulo.getDataOcorrencia(), titulo.getDataEmissaoTitulo()));

		if (titulo.getNossoNumero() != null)
			criteria.add(Restrictions.ilike("nossoNumero", titulo.getNossoNumero(), MatchMode.ANYWHERE));

		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}

	public List<TituloRemessa> buscarTitulosPorRemessa(Remessa remessa, Instituicao instituicaoCorrente) {
		Criteria criteria = getCriteria(TituloRemessa.class);

		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			criteria.createAlias("remessa", "remessa");
			criteria.add(Restrictions.eq("remessa", remessa));
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			criteria.createAlias("confirmacao", "confirmacao");
			criteria.add(Restrictions.eq("confirmacao.remessa", remessa));
		} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			criteria.createAlias("retorno", "retorno");
			criteria.add(Restrictions.eq("retorno.remessa", remessa));
		}

		filtrarTitulosPorInstituicao(instituicaoCorrente);
		return criteria.list();
	}

	private Disjunction filtrarTitulosPorInstituicao(Instituicao instituicaoCorrente) {
		Disjunction disj = Restrictions.disjunction();

		if (!instituicaoCorrente.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			disj.add(Restrictions.eq("remessa.instituicaoOrigem", instituicaoCorrente)).add(
			        Restrictions.eq("remessa.instituicaoDestino", instituicaoCorrente));
		}
		return disj;
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
		criteria.add(Restrictions.eq("codigoPortador", tituloRetorno.getCodigoPortador().trim()));
		criteria.add(Restrictions.eq("nossoNumero", tituloRetorno.getNossoNumero().trim()));
		criteria.add(Restrictions.eq("agenciaCodigoCedente", tituloRetorno.getAgenciaCodigoCedente().trim()));

		TituloRemessa titulo = TituloRemessa.class.cast(criteria.uniqueResult());

		if (titulo == null) {
			new InfraException("O título [Nosso número =" + tituloRetorno.getNossoNumero() + "] não existe em nossa base de dados.");
			return null;
		}
		try {
			tituloRetorno.setTitulo(titulo);
			titulo.setRetorno(save(tituloRetorno));
			save(titulo);
			logger.info("Retorno salvo com sucesso");
			return titulo;

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			new InfraException("O título [Nosso número =" + tituloRetorno.getNossoNumero() + "] não existe em nossa base de dados.");
		}
		return null;
	}

	private TituloRemessa salvarTituloConfirmacao(Confirmacao tituloConfirmacao, Transaction transaction) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("codigoPortador", tituloConfirmacao.getCodigoPortador().trim()));
		criteria.add(Restrictions.eq("nossoNumero", tituloConfirmacao.getNossoNumero().trim()));
		criteria.add(Restrictions.eq("numeroTitulo", tituloConfirmacao.getNumeroTitulo().trim()));

		TituloRemessa titulo = TituloRemessa.class.cast(criteria.uniqueResult());

		if (titulo == null) {
			new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero() + "] não existe em nossa base de dados.");
			return null;
		}
		try {
			tituloConfirmacao.setTitulo(titulo);
			titulo.setConfirmacao(save(tituloConfirmacao));
			save(titulo);
			logger.info("Confirmação salva com sucesso");

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			new InfraException("O título [Nosso número =" + tituloConfirmacao.getNossoNumero() + "] não existe em nossa base de dados.");
		}
		return titulo;
	}

	private TituloRemessa salvarTituloRemessa(TituloRemessa tituloRemessa, Transaction transaction) {
		try {
			return save(tituloRemessa);
		} catch (Exception ex) {
			if (PSQLException.class.isInstance(ex)) {
				logger.error(ex.getMessage(), ex.getCause());
				new InfraException("O Título número: " + tituloRemessa.getNumeroTitulo() + " já existe na base de dados.");
				return null;
			} else {
				transaction.rollback();
				logger.error(ex.getMessage(), ex.getCause());
				throw new InfraException("O Título número: " + tituloRemessa.getNumeroTitulo() + " não pode ser inserido.");
			}
		}
	}

	@Transactional(readOnly = true)
	public List<TituloRemessa> buscarTitulosPorArquivo(Arquivo arquivo, Instituicao instituicao) {
		List<TituloRemessa> listaTitulos = new ArrayList<TituloRemessa>();

		Criteria criteria = getCriteria(Remessa.class);
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoOrigem", instituicao))
			        .add(Restrictions.eq("instituicaoDestino", instituicao)));
		}
		criteria.add(Restrictions.eq("arquivo", arquivo));
		List<Remessa> remessas = criteria.list();

		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
			List<TituloRemessa> titulos = new ArrayList<TituloRemessa>();

			if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
				criteriaTitulo.createAlias("remessa", "remessa");
				criteriaTitulo.add(Restrictions.eq("remessa", remessa));
			} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				criteriaTitulo.createAlias("confirmacao", "confirmacao");
				criteriaTitulo.add(Restrictions.eq("confirmacao.remessa", remessa));
			} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				criteriaTitulo.createAlias("retorno", "retorno");
				criteriaTitulo.add(Restrictions.eq("retorno.remessa", remessa));
			}
			criteriaTitulo.addOrder(Order.asc("pracaProtesto"));
			titulos = criteriaTitulo.list();

			listaTitulos.addAll(titulos);
		}
		return listaTitulos;
	}

	@Transactional(readOnly = true)
	public List<TituloRemessa> buscarTitulosConfirmacaoRetorno(Arquivo arquivo, Instituicao instituicao) {
		List<TituloRemessa> listaTitulos = new ArrayList<TituloRemessa>();

		Criteria criteria = getCriteria(Remessa.class);
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoOrigem", instituicao))
			        .add(Restrictions.eq("instituicaoDestino", instituicao)));
		}
		criteria.add(Restrictions.eq("arquivoGeradoProBanco", arquivo));
		List<Remessa> remessas = criteria.list();

		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
			List<TituloRemessa> titulos = new ArrayList<TituloRemessa>();

			if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
				criteriaTitulo.createAlias("remessa", "remessa");
				criteriaTitulo.add(Restrictions.eq("remessa", remessa));
			} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
				criteriaTitulo.createAlias("confirmacao", "confirmacao");
				criteriaTitulo.add(Restrictions.eq("confirmacao.remessa", remessa));
			} else if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				criteriaTitulo.createAlias("retorno", "retorno");
				criteriaTitulo.add(Restrictions.eq("retorno.remessa", remessa));
			}
			criteriaTitulo.addOrder(Order.asc("pracaProtesto"));
			titulos = criteriaTitulo.list();

			listaTitulos.addAll(titulos);
		}
		return listaTitulos;
	}

	public List<TituloRemessa> buscarTitulosParaRelatorio(Instituicao instituicao, Instituicao cartorioProtesto, LocalDate dataInicio,
	        LocalDate dataFim, Usuario usuarioCorrente) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");

		if (instituicao != null) {
			criteria.add(filtrarTitulosPorInstituicao(instituicao));
		} else if (cartorioProtesto != null) {
			criteria.add(filtrarTitulosPorInstituicao(cartorioProtesto));
		}

		if (!usuarioCorrente.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("remessa.instituicaoOrigem", usuarioCorrente.getInstituicao()))
			        .add(Restrictions.eq("remessa.instituicaoDestino", usuarioCorrente.getInstituicao())));
		}
		criteria.add(Restrictions.between("remessa.dataRecebimento", dataInicio, dataFim));
		return criteria.list();
	}

	public Retorno buscarTituloProtestado(String numeroProtocolo, Integer codigoIBGE) {
		Criteria criteria = getCriteria(Retorno.class);
		criteria.createAlias("titulo", "titulo");
		criteria.createAlias("cabecalho", "cabecalho");
		criteria.add(Restrictions.eq("cabecalho.codigoMunicipio", codigoIBGE));
		criteria.add(Restrictions.eq("numeroProtocoloCartorio", numeroProtocolo));
		criteria.setMaxResults(1);
		return Retorno.class.cast(criteria.uniqueResult());
	}

	public TituloRemessa carregarTitulo(TituloRemessa titulo) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.add(Restrictions.eq("codigoPortador", titulo.getCodigoPortador()));
		criteria.add(Restrictions.eq("nossoNumero", titulo.getNossoNumero()));

		criteria.setMaxResults(1);
		return TituloRemessa.class.cast(criteria.uniqueResult());
	}
}
