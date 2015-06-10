package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class ArquivoDAO extends AbstractBaseDAO {

	@Autowired
	InstituicaoMediator InstituicaoMediator;
	@Autowired
	TituloDAO tituloDAO;

	public List<Arquivo> buscarTodosArquivos() {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public Arquivo salvar(Arquivo arquivo, Usuario usuarioAcao) {
		Arquivo arquivoSalvo = new Arquivo();
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		BigDecimal valorTotalSaldo = BigDecimal.ZERO;
		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivoSalvo = save(arquivo);

			for (Remessa remessa : arquivo.getRemessas()) {
				remessa.setArquivo(arquivoSalvo);
				remessa.setCabecalho(save(remessa.getCabecalho()));
				remessa.setRodape(save(remessa.getRodape()));
				/**
				 * @TODO gambiarra gigante pra funcionar gerar confirmacao e
				 *       retorno [feito pelo Thasso] - corrigir o quanto antes.
				 */
				remessa.setArquivoGeradoProBanco(arquivoSalvo);

				remessa.setDataRecebimento(new LocalDate());
				setSituacaoRemessa(arquivo, remessa);
				save(remessa);
				for (Titulo titulo : remessa.getTitulos()) {
					titulo.setRemessa(remessa);
					if (Retorno.class.isInstance(titulo)) {
						Retorno.class.cast(titulo).setCabecalho(remessa.getCabecalho());
					}
					TituloRemessa tituloSalvo = tituloDAO.salvar(titulo, transaction);

					Historico historico = new Historico();
					if (tituloSalvo != null) {
						historico.setDataOcorrencia(new LocalDateTime());
						historico.setRemessa(remessa);
						historico.setTitulo(tituloSalvo);
						historico.setUsuarioAcao(usuarioAcao);
						save(historico);
					} else {
						titulo.setSaldoTitulo(BigDecimal.ZERO);
						remessa.getTitulos().remove(titulo);
					}

					valorTotalSaldo = valorTotalSaldo.add(titulo.getSaldoTitulo());
					remessa.getCabecalho().setQtdTitulosRemessa(remessa.getTitulos().size());
					remessa.getRodape().setSomatorioValorRemessa(valorTotalSaldo);

					remessa.setCabecalho(save(remessa.getCabecalho()));
					remessa.setRodape(save(remessa.getRodape()));

				}
			}
			transaction.commit();
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
			        + " foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo na base de dados.");
		}
		return arquivoSalvo;

	}

	private void setSituacaoRemessa(Arquivo arquivo, Remessa remessa) {
		if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)
		        || arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			remessa.setSituacao(false);
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				remessa.setSituacaoBatimento(false);
			}
		}
	}

	public StatusArquivo buscarStatusArquivo(SituacaoArquivo situacao) {
		Criteria criteria = getCriteria(StatusArquivo.class);
		criteria.add(Restrictions.eq("situacaoArquivo", situacao));
		criteria.addOrder(Order.asc("id"));
		return StatusArquivo.class.cast(criteria.uniqueResult());
	}

	public Arquivo buscarArquivosPorNome(Instituicao instituicao, String nomeArquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.createAlias("remessas", "remessas");
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("remessas.instituicaoOrigem", instituicao))
			        .add(Restrictions.eq("remessas.instituicaoDestino", instituicao)));
		}
		criteria.add(Restrictions.eq("nomeArquivo", nomeArquivo));
		criteria.setMaxResults(1);
		return Arquivo.class.cast(criteria.uniqueResult());
	}

	public List<Arquivo> buscarArquivosPorInstituicao(Instituicao instituicao, ArrayList<String> tipos, ArrayList<String> situacoes,
	        LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(Arquivo.class);

		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoEnvio", instituicao))
			        .add(Restrictions.eq("instituicaoRecebe", instituicao)));
		}

		if (!tipos.isEmpty()) {
			criteria.createAlias("tipoArquivo", "tipoArquivo");
			criteria.add(filtrarArquivoPorTipoArquivo(tipos));
		}

		if (!situacoes.isEmpty()) {
			if (situacoes.contains(SituacaoArquivo.AGUARDANDO.getLabel()))
				criteria.createAlias("statusArquivo", "statusArquivo");

			criteria.add(filtrarArquivoPorSituacao(situacoes, instituicao));
		}

		if (dataInicio == null) {
			criteria.createAlias("statusArquivo", "statusArquivo");
			criteria.add(Restrictions.eq("statusArquivo.status", SituacaoArquivo.AGUARDANDO.getLabel()));
		}

		if (situacoes.isEmpty() && dataInicio != null)
			criteria.add(Restrictions.between("dataEnvio", dataInicio, dataFim));

		criteria.addOrder(Order.desc("dataEnvio"));
		return criteria.list();
	}

	private Disjunction filtrarArquivoPorTipoArquivo(ArrayList<String> tipos) {
		Disjunction disjunction = Restrictions.disjunction();
		for (String tipo : tipos) {
			disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.getTipoArquivoEnum(tipo)));
		}
		return disjunction;
	}

	private Disjunction filtrarArquivoPorSituacao(ArrayList<String> situacao, Instituicao instituicao) {
		Disjunction disjunction = Restrictions.disjunction();
		for (String s : situacao) {
			if (s.equals("Enviados"))
				disjunction.add(Restrictions.eq("remessas.instituicaoOrigem", instituicao));
			else
				disjunction.add(Restrictions.eq("remessas.instituicaoDestino", instituicao));
		}
		return disjunction;
	}
}
