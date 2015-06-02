package br.com.ieptbto.cra.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.relatorio.SinteticoJRDataSource;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Repository
public class RelatorioDAO extends AbstractBaseDAO{
	
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
				+ " sum (CASE WHEN ret.tipo_ocorrencia='2' OR ret.tipo_ocorrencia='5' THEN ret.valor_custa_cartorio ELSE 0 END) AS valor_custa,"
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
				+ " inner join tb_instituicao AS i on r.instituicao_destino_id=i.id_instituicao"
				+ " INNER JOIN tb_municipio AS m ON i.municipio_id = m.id_municipio"
				+ " WHERE m.id_municipio = " + pracaProtesto.getId()
				+ " GROUP BY i.nome_fantasia");
		List<SinteticoJRDataSource> lista = new ArrayList<>(); 
		Iterator iterator= query.list().iterator();
		    while(iterator.hasNext()){
		    	SinteticoJRDataSource bean = new SinteticoJRDataSource();
		        Object[] posicao = (Object[]) iterator.next();
		        bean.setInstituicao((String)posicao[0]);
		        bean.setTotalTitulos(BigInteger.class.cast(posicao[1]));
		        bean.setApontados(BigInteger.class.cast(posicao[2]));
		        bean.setIrregulares(BigInteger.class.cast(posicao[3]));//devolvidos
		        bean.setValorSaldo(BigDecimal.class.cast(posicao[4]));
		        bean.setValorTitulo(BigDecimal.class.cast(posicao[5]));
		        lista.add(bean);
		    }
		return lista;
	}
	
	public List<SinteticoJRDataSource> relatorioDeConfirmacaoSinteticoPorMunicipio(Municipio pracaProtesto,LocalDate dataInicio, LocalDate dataFim) {
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
				+ " inner join tb_instituicao AS i on r.instituicao_destino_id=i.id_instituicao"
				+ " INNER JOIN tb_municipio AS m ON i.municipio_id = m.id_municipio"
				+ " WHERE m.id_municipio = " + pracaProtesto.getId()
				+ " GROUP BY i.nome_fantasia");
		List<SinteticoJRDataSource> lista = new ArrayList<>(); 
		Iterator iterator= query.list().iterator();
		    while(iterator.hasNext()){
		    	SinteticoJRDataSource bean = new SinteticoJRDataSource();
		        Object[] posicao = (Object[]) iterator.next();
		        bean.setInstituicao((String)posicao[0]);
		        bean.setTotalTitulos(BigInteger.class.cast(posicao[1]));
		        bean.setApontados(BigInteger.class.cast(posicao[2]));
		        bean.setIrregulares(BigInteger.class.cast(posicao[3]));//devolvidos
		        bean.setValorSaldo(BigDecimal.class.cast(posicao[4]));
		        bean.setValorTitulo(BigDecimal.class.cast(posicao[5]));
		        lista.add(bean);
		    }
		return lista;
	}
	
	public List<SinteticoJRDataSource> relatorioDeRetornoSinteticoPorMunicipio(Municipio pracaProtesto,LocalDate dataInicio, LocalDate dataFim) {
		Query query = createSQLQuery("select i.nome_fantasia,"
				+ " Count (titulo.id_titulo) AS total_titulos,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='5' THEN 1 ELSE 0 END) AS irregulares,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='4' THEN 1 ELSE 0 END) AS sustados,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='3' THEN 1 ELSE 0 END) AS retirados,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='2' THEN 1 ELSE 0 END) AS protestados,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='1' THEN 1 ELSE 0 END) AS pagos,"
				+ " sum (titulo.valor_saldo_titulo) AS valor_saldo,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='1' THEN titulo.valor_saldo_titulo ELSE 0 END) AS valor_repasse,"
				+ " sum (CASE WHEN ret.tipo_ocorrencia='2' OR ret.tipo_ocorrencia='5' THEN ret.valor_custa_cartorio ELSE 0 END) AS valor_custa,"
				+ " sum (titulo.valor_demais_despesas) AS valor_demais_despesas"
				+ " from tb_remessa AS r"
				+ " INNER JOIN tb_retorno AS ret ON ret.remessa_id=r.id_remessa"
				+ " INNER JOIN tb_titulo AS titulo ON ret.titulo_id=id_titulo"
				+ " INNER JOIN tb_arquivo AS a ON r.arquivo_id=a.id_arquivo"
				+ " INNER JOIN tb_tipo_arquivo AS t ON a.tipo_arquivo_id=t.id_tipo_arquivo"
				+ " INNER JOIN tb_instituicao AS i ON r.instituicao_destino_id=i.id_instituicao"
				+ " INNER JOIN tb_municipio AS m ON i.municipio_id=m.id_municipio"
				+ " WHERE m.id_municipio = " + pracaProtesto.getId()
				+ " GROUP BY i.nome_fantasia");
		List<SinteticoJRDataSource> lista = new ArrayList<>(); 
		Iterator iterator= query.list().iterator();
		    while(iterator.hasNext()){
		    	SinteticoJRDataSource bean = new SinteticoJRDataSource();
		        Object[] posicao = (Object[]) iterator.next();
		        bean.setInstituicao((String)posicao[0]);
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
}
