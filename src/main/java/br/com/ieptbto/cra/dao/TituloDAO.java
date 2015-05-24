package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.relatorio.SinteticoJRDataSource;

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

	public List<TituloRemessa> buscarTitulosPorRemessa(Remessa remessa, Instituicao instituicaoCorrente) {
		Criteria criteria = getCriteria(TituloRemessa.class);
		
		if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)){
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
	
	private Disjunction filtrarTitulosPorInstituicao(Instituicao instituicaoCorrente){
		Disjunction disj = Restrictions.disjunction();
		
		if (!instituicaoCorrente.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			disj.add(Restrictions.eq("remessa.instituicaoOrigem", instituicaoCorrente)).add(Restrictions.eq("remessa.instituicaoDestino", instituicaoCorrente));
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

	
	
	public List<SinteticoJRDataSource> relatorioDeRemessaSintetico(Instituicao bancoPortador,LocalDate dataInicio, LocalDate dataFim) {
		Query query = createSQLQuery("select m.nome_municipio,"
				+ " count(titulo.id_titulo) AS total_titulos,"
				+ " sum (titulo.valor_titulo) AS valor_titulo,"
				+ " sum (titulo.valor_saldo_titulo) AS valor_saldo"
				+ " from tb_remessa AS r"
				+ " inner join tb_titulo AS titulo ON titulo.remessa_id=r.id_remessa"
				+ " inner join tb_arquivo AS a on r.arquivo_id=a.id_arquivo"
				+ " inner join tb_tipo_arquivo AS t on a.tipo_arquivo_id=t.id_tipo_arquivo"
				+ " inner join tb_instituicao AS i on r.instituicao_origem_id=i.id_instituicao"
				+ " INNER JOIN tb_municipio AS m ON i.municipio_id = m.id_municipio"
				+ "	WHERE r.instituicao_origem_id = " + bancoPortador.getId() 
				+ " GROUP BY m.nome_municipio");
		List<SinteticoJRDataSource> lista = new ArrayList<>(); 
		Iterator iterator= query.list().iterator();
		    while(iterator.hasNext()){
		    	SinteticoJRDataSource bean = new SinteticoJRDataSource();
		        Object[] posicao = (Object[]) iterator.next();
		        bean.setNomeMunicipio((String)posicao[0]);
		        bean.setTotalTitulos(BigInteger.class.cast(posicao[1]));
		        bean.setValorTitulo(BigDecimal.class.cast(posicao[2]));
		        bean.setValorSaldo(BigDecimal.class.cast(posicao[3]));
		        lista.add(bean);
		    }
		return lista;
	}
	
	public List<SinteticoJRDataSource> relatorioDeConfirmacaoSintetico(Instituicao bancoPortador,LocalDate dataInicio, LocalDate dataFim) {
		Query query = createSQLQuery("select m.nome_municipio,"
				+ " Count (titulo.id_titulo) AS total_titulos,"
				+ " sum (CASE WHEN conf.numero_protocolo_cartorio<>'' THEN 1 ELSE 0 END) AS apontados,"
				+ " sum (CASE WHEN conf.tipo_ocorrencia='5' THEN 1 ELSE 0 END) AS devolvidos,"
				+ " sum (titulo.valor_saldo_titulo) AS valor_saldo,"
				+ " sum (titulo.valor_titulo) AS valor_titulo"
				+ " from tb_remessa AS r"
				+ " INNER join tb_confirmacao AS conf ON conf.remessa_id=r.id_remessa"
				+ " inner join tb_titulo AS titulo ON conf.titulo_id=id_titulo"
				+ " inner join tb_arquivo AS a on r.arquivo_id=a.id_arquivo"
				+ " inner join tb_tipo_arquivo AS t on a.tipo_arquivo_id=t.id_tipo_arquivo"
				+ " inner join tb_instituicao AS i on r.instituicao_origem_id=i.id_instituicao"
				+ " INNER JOIN tb_municipio AS m ON i.municipio_id = m.id_municipio"
				+ " WHERE r.instituicao_destino_id = " + bancoPortador.getId()
				+ " GROUP BY m.nome_municipio");
		List<SinteticoJRDataSource> lista = new ArrayList<>(); 
		Iterator iterator= query.list().iterator();
		    while(iterator.hasNext()){
		    	SinteticoJRDataSource bean = new SinteticoJRDataSource();
		        Object[] posicao = (Object[]) iterator.next();
		        bean.setNomeMunicipio((String)posicao[0]);
		        bean.setTotalTitulos(BigInteger.class.cast(posicao[1]));
		        bean.setApontados(BigInteger.class.cast(posicao[2]));
		        bean.setIrregulares(BigInteger.class.cast(posicao[3]));//devolvidos
		        bean.setValorSaldo(BigDecimal.class.cast(posicao[4]));
		        bean.setValorTitulo(BigDecimal.class.cast(posicao[5]));
		        lista.add(bean);
		    }
		return lista;
	}
	
	public List<SinteticoJRDataSource> relatorioDeRetornoSintetico(Instituicao bancoPortador,LocalDate dataInicio, LocalDate dataFim) {
		Query query = createSQLQuery("select m.nome_municipio AS nome_municipio,"
				+ " Count (titulo.id_titulo) AS total_titulos,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='5' THEN 1 ELSE 0 END) AS irregulares,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='4' THEN 1 ELSE 0 END) AS sustados,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='3' THEN 1 ELSE 0 END) AS retirados,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='2' THEN 1 ELSE 0 END) AS protestados,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='1' THEN 1 ELSE 0 END) AS pagos,"
				+ " sum (titulo.valor_saldo_titulo) AS valor_saldo,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='1' THEN titulo.valor_saldo_titulo ELSE 0 END) AS valor_repasse,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='2' THEN titulo.valor_custa_cartorio ELSE 0 END) AS valor_custa,"
				+ " sum (titulo.valor_demais_despesas) AS valor_demais_despesas"
				+ " from tb_remessa AS r"
				+ " INNER JOIN tb_retorno AS ret ON ret.remessa_id=r.id_remessa"
				+ " INNER JOIN tb_titulo AS titulo ON ret.titulo_id=id_titulo"
				+ " INNER JOIN tb_arquivo AS a ON r.arquivo_id=a.id_arquivo"
				+ " INNER JOIN tb_tipo_arquivo AS t ON a.tipo_arquivo_id=t.id_tipo_arquivo"
				+ " INNER JOIN tb_instituicao AS i ON r.instituicao_origem_id=i.id_instituicao"
				+ " INNER JOIN tb_municipio AS m ON i.municipio_id=m.id_municipio"
				+ " WHERE r.instituicao_destino_id=" + bancoPortador.getId()
				+ " GROUP BY m.nome_municipio");
		List<SinteticoJRDataSource> lista = new ArrayList<>(); 
		Iterator iterator= query.list().iterator();
		    while(iterator.hasNext()){
		    	SinteticoJRDataSource bean = new SinteticoJRDataSource();
		        Object[] posicao = (Object[]) iterator.next();
		        bean.setNomeMunicipio((String)posicao[0]);
		        bean.setTotalTitulos(BigInteger.class.cast(posicao[1]));
		        bean.setIrregulares(BigInteger.class.cast(posicao[2]));
		        bean.setSustados(BigInteger.class.cast(posicao[3]));
		        bean.setRetirados(BigInteger.class.cast(posicao[4]));
		        bean.setProtestados(BigInteger.class.cast(posicao[5]));
		        bean.setPagos(BigInteger.class.cast(posicao[6]));
		        bean.setValorSaldo(BigDecimal.class.cast(posicao[7]));
		        bean.setValorRepasse(BigDecimal.class.cast(posicao[8]));
		        bean.setValorCusta(BigDecimal.class.cast(posicao[9]));
		        bean.setValorDemaisDespesas(BigDecimal.class.cast(posicao[10]));
		        lista.add(bean);
		    }
		return lista;
	}

	public List<SinteticoJRDataSource> relatorioDeRemessaSinteticoPorMunicipio(
			Municipio pracaProtesto, LocalDate dataInicio, LocalDate dataFim) {
		Query query = createSQLQuery("select i.nome_fantasia,"
				+ " Count (titulo.id_titulo) AS total_titulos,"
				+ " sum (CASE WHEN conf.numero_protocolo_cartorio<>'' THEN 1 ELSE 0 END) AS apontados,"
				+ " sum (CASE WHEN conf.tipo_ocorrencia='5' THEN 1 ELSE 0 END) AS devolvidos,"
				+ " sum (titulo.valor_saldo_titulo) AS valor_saldo,"
				+ " sum (titulo.valor_titulo) AS valor_titulo"
				+ " from tb_remessa AS r"
				+ " INNER join tb_confirmacao AS conf ON conf.remessa_id=r.id_remessa"
				+ " inner join tb_titulo AS titulo ON conf.titulo_id=id_titulo"
				+ " inner join tb_arquivo AS a on r.arquivo_id=a.id_arquivo"
				+ " inner join tb_tipo_arquivo AS t on a.tipo_arquivo_id=t.id_tipo_arquivo"
				+ " inner join tb_instituicao AS i on r.instituicao_origem_id=i.id_instituicao"
				+ " INNER JOIN tb_municipio AS m ON i.municipio_id = m.id_municipio"
				+ " WHERE m.id_municipio = " + pracaProtesto.getId()
				+ " GROUP BY i.nome_fantasia");
		List<SinteticoJRDataSource> lista = new ArrayList<>(); 
		Iterator iterator= query.list().iterator();
		    while(iterator.hasNext()){
		    	SinteticoJRDataSource bean = new SinteticoJRDataSource();
		        Object[] posicao = (Object[]) iterator.next();
		        bean.setNomeMunicipio((String)posicao[0]);
		        bean.setTotalTitulos(BigInteger.class.cast(posicao[1]));
		        bean.setApontados(BigInteger.class.cast(posicao[2]));
		        bean.setIrregulares(BigInteger.class.cast(posicao[3]));//devolvidos
		        bean.setValorSaldo(BigDecimal.class.cast(posicao[4]));
		        bean.setValorTitulo(BigDecimal.class.cast(posicao[5]));
		        lista.add(bean);
		    }
		return lista;
	}
}
