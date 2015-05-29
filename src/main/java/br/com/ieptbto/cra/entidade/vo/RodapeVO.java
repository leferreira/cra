package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "identificacaoRegistro", "numeroCodigoPortador", "nomePortador", "dataMovimento", "somatorioQtdRemessa",
        "somatorioValorRemessa", "complementoRegistro", "numeroSequencialRegistroArquivo" })
public class RodapeVO extends AbstractArquivoVO {

	@XmlAttribute(name = "t01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar o registro trailler no arquivo. Constante 9.", obrigatoriedade = true, validacao = "9", tipo = Integer.class)
	private String identificacaoRegistro;

	@XmlAttribute(name = "t02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 3, descricao = "Identificar o código do banco/portador. Preencher com o código compensação do Banco ou número de identificação do portador.", obrigatoriedade = true)
	private String numeroCodigoPortador;

	@XmlAttribute(name = "t03")
	@IAtributoArquivo(ordem = 3, posicao = 5, tamanho = 40, descricao = "Preencher com o nome do Portador.('Razão Social')", obrigatoriedade = true)
	private String nomePortador;

	@XmlAttribute(name = "t04")
	@IAtributoArquivo(ordem = 4, posicao = 45, tamanho = 8, descricao = "Informar a data do envio da remessa ao Serviço de Distribuição de Títulos, no formato DDMMAAAA.", obrigatoriedade = true)
	private String dataMovimento;

	@XmlAttribute(name = "t05")
	@IAtributoArquivo(ordem = 5, posicao = 53, tamanho = 5, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Informar o somatório dos registros. Conforme a regra estabelecida para os campos do registro header. Campos 09,10,11 e 12.", obrigatoriedade = true)
	private String somatorioQtdRemessa;

	@XmlAttribute(name = "t06")
	@IAtributoArquivo(ordem = 6, posicao = 58, tamanho = 18, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Informar o somatório do Saldos dos Títulos.", obrigatoriedade = true)
	private String somatorioValorRemessa;

	@XmlAttribute(name = "t07")
	@IAtributoArquivo(ordem = 7, posicao = 76, tamanho = 521, descricao = "Ajusatar o tamanho do registro do trailler com o tamanho do registro de transação. Preencher com brancos.", obrigatoriedade = true)
	private String complementoRegistro;

	@XmlAttribute(name = "t08")
	@IAtributoArquivo(ordem = 8, posicao = 597, tamanho = 04, formato = "0", posicaoCampoVazio = PosicaoCampoVazio.ESQUERDO, descricao = "Informar o número sequêncial do registro.", obrigatoriedade = true)
	private String numeroSequencialRegistroArquivo;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public String getNumeroCodigoPortador() {
		return numeroCodigoPortador;
	}

	public String getNomePortador() {
		return nomePortador;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public String getSomatorioQtdRemessa() {
		return somatorioQtdRemessa;
	}

	public String getSomatorioValorRemessa() {
		return somatorioValorRemessa;
	}

	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	public String getNumeroSequencialRegistroArquivo() {
		return numeroSequencialRegistroArquivo;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setNumeroCodigoPortador(String numeroCodigoPortador) {
		this.numeroCodigoPortador = numeroCodigoPortador;
	}

	public void setNomePortador(String nomePortador) {
		this.nomePortador = nomePortador;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setSomatorioQtdRemessa(String somatorioQtdRemessa) {
		this.somatorioQtdRemessa = somatorioQtdRemessa;
	}

	public void setSomatorioValorRemessa(String somatorioValorRemessa) {
		this.somatorioValorRemessa = somatorioValorRemessa;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}

	public static RodapeVO parseRodape(Rodape rodape) {
		RodapeVO rodapeVO = new RodapeVO();
		rodapeVO.setComplementoRegistro(rodape.getComplementoRegistro().trim());
		rodapeVO.setDataMovimento(rodape.getDataMovimento().toString(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY));
		rodapeVO.setIdentificacaoRegistro(rodape.getIdentificacaoRegistro().getConstante().trim());
		rodapeVO.setNomePortador(rodape.getNomePortador().trim());
		rodapeVO.setNumeroCodigoPortador(rodape.getNumeroCodigoPortador().trim());
		rodapeVO.setNumeroSequencialRegistroArquivo(rodape.getNumeroSequencialRegistroArquivo().trim());
		rodapeVO.setSomatorioQtdRemessa(StringUtils.leftPad(rodape.getSomatorioQtdRemessa().toString().replaceAll("\\D", ""), 5, "0"));
		rodapeVO.setSomatorioValorRemessa(StringUtils.leftPad(rodape.getSomatorioValorRemessa().toString().replaceAll("\\D", ""), 5, "0"));

		return rodapeVO;
	}
}
