package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "unchecked" })
@Repository
public class RemessaDAO extends AbstractBaseDAO {

	@Autowired
	TituloDAO tituloDAO;
	@Autowired
	InstituicaoDAO instituicaoDAO;

	public List<Remessa> buscarRemessaAvancado(Arquivo arquivo, Municipio pracaProtesto, Instituicao portador, LocalDate dataInicio,
	        LocalDate dataFim, Usuario usuarioCorrente, ArrayList<String> tipos) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");

		if (!usuarioCorrente.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("CRA"))
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoDestino", usuarioCorrente.getInstituicao()))
			        .add(Restrictions.eq("instituicaoOrigem", usuarioCorrente.getInstituicao())));

		if (StringUtils.isNotBlank(arquivo.getNomeArquivo()))
			criteria.add(Restrictions.ilike("a.nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));

		if (!tipos.isEmpty()) {
			criteria.createAlias("a.tipoArquivo", "tipoArquivo");
			criteria.add(filtrarRemessaPorTipoArquivo(tipos));
		}
		criteria.add(filtraRemessaPorInsituicaoOuPraca(portador, pracaProtesto));

		criteria.add(Restrictions.between("dataRecebimento", dataInicio, dataFim));
		criteria.addOrder(Order.desc("dataRecebimento"));
		return criteria.list();
	}

	public List<Remessa> buscarRemessaSimples(Instituicao instituicao, ArrayList<String> tipos, ArrayList<String> situacoes,
	        LocalDate dataInicio, LocalDate dataFim) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");

		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA"))
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoOrigem", instituicao))
			        .add(Restrictions.eq("instituicaoDestino", instituicao)));

		if (!tipos.isEmpty()) {
			criteria.createAlias("a.tipoArquivo", "tipoArquivo");
			criteria.add(filtrarRemessaPorTipoArquivo(tipos));
		}

		if (!situacoes.isEmpty()) {
			if (situacoes.contains(SituacaoArquivo.AGUARDANDO.getLabel()))
				criteria.createAlias("a.statusArquivo", "statusArquivo");

			criteria.add(filtrarRemessaPorSituacao(situacoes, instituicao));
		}

		if (dataInicio == null) {
			criteria.createAlias("a.statusArquivo", "statusArquivo");
			criteria.add(Restrictions.eq("statusArquivo.status", SituacaoArquivo.AGUARDANDO.getLabel()));
		}

		if (situacoes.isEmpty() && dataInicio != null)
			criteria.add(Restrictions.between("dataRecebimento", dataInicio, dataFim));

		criteria.addOrder(Order.desc("dataRecebimento"));
		return criteria.list();
	}

	public List<Remessa> listarRemessasPorInstituicao(Instituicao instituicao) {
		Criteria criteria = getCriteria(Remessa.class);
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoDestino", instituicao))
			        .add(Restrictions.eq("instituicaoOrigem", instituicao)));
		}
		criteria.addOrder(Order.desc("dataRecebimento"));
		return criteria.list();
	}

	private Disjunction filtraRemessaPorInsituicaoOuPraca(Instituicao portador, Municipio pracaProtesto) {
		Disjunction disjunction = Restrictions.disjunction();

		if (portador != null)
			disjunction.add(Restrictions.eq("instituicaoOrigem", portador)).add(Restrictions.eq("instituicaoDestino", portador));

		if (pracaProtesto != null) {
			Instituicao cartorioProtesto = instituicaoDAO.buscarCartorioPorMunicipio(pracaProtesto.getNomeMunicipio());
			disjunction.add(Restrictions.eq("instituicaoOrigem", cartorioProtesto)).add(
			        Restrictions.eq("instituicaoDestino", cartorioProtesto));
		}
		return disjunction;
	}

	private Disjunction filtrarRemessaPorTipoArquivo(ArrayList<String> tipos) {
		Disjunction disjunction = Restrictions.disjunction();
		for (String tipo : tipos) {
			disjunction.add(Restrictions.eq("tipoArquivo.tipoArquivo", TipoArquivoEnum.getTipoArquivoEnum(tipo)));
		}
		return disjunction;
	}

	private Disjunction filtrarRemessaPorSituacao(ArrayList<String> situacao, Instituicao instituicao) {
		Disjunction disjunction = Restrictions.disjunction();
		for (String s : situacao) {
			if (s.equals(SituacaoArquivo.ENVIADO.getLabel())) {
				disjunction.add(Restrictions.eq("instituicaoOrigem", instituicao));
			} else if (s.equals(SituacaoArquivo.RECEBIDO.getLabel())) {
				disjunction.add(Restrictions.eq("instituicaoDestino", instituicao));
			} else {
				disjunction.add(Restrictions.eq("statusArquivo.status", SituacaoArquivo.AGUARDANDO.getLabel()));
			}
		}
		return disjunction;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Remessa buscarArquivosPorNome(String nome) {
		List<Titulo> titulos = new ArrayList<Titulo>();
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");
		criteria.add(Restrictions.eq("arquivo.nomeArquivo", nome));
		criteria.setMaxResults(1);
		Remessa remessa = Remessa.class.cast(criteria.uniqueResult());

		if (remessa == null) {
			return null;
		}

		Criteria criteriaTitulo = getCriteria(Titulo.class);
		criteriaTitulo.createAlias("remessa", "remessa");
		criteriaTitulo.add(Restrictions.eq("remessa", remessa));
		titulos = criteriaTitulo.list();

		remessa.setTitulos(titulos);

		return remessa;
	}

	public Remessa buscarPorPK(Remessa entidade) {
		Remessa remessa = super.buscarPorPK(entidade);
		Criteria criteriaTitulo = getCriteria(Titulo.class);
		criteriaTitulo.createAlias("remessa", "remessa");
		criteriaTitulo.add(Restrictions.eq("remessa", remessa));
		remessa.setTitulos(criteriaTitulo.list());

		return remessa;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List<Remessa> buscarRemessasDoArquivo(Instituicao instituicao, Arquivo arquivo) {
		List<Titulo> titulos = new ArrayList<Titulo>();

		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "arquivo");

		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoOrigem", instituicao))
			        .add(Restrictions.eq("instituicaoDestino", instituicao)));
		}
		
		TipoArquivoEnum tipoArquivo = arquivo.getTipoArquivo().getTipoArquivo();
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA))
			criteria.add(Restrictions.eq("arquivo", arquivo));
		else 
			criteria.add(Restrictions.eq("arquivoGeradoProBanco", arquivo));
		List<Remessa> remessas = criteria.list();

		for (Remessa remessa : remessas) {
			Criteria criteriaTitulo = getCriteria(TituloRemessa.class);
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

			remessa.setTitulos(titulos);
		}
		return remessas;
	}
}
