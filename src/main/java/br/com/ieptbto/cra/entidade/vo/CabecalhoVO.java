package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;

/**
 * 
 * @author Lefer
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CabecalhoVO extends AbstractArquivoVO {

	@XmlAttribute(name = "t02")
	@IAtributoArquivo(ordem = 2, posicao = 2, tamanho = 3, descricao = "Identificar o código do banco/portador. Preencher com o código compensação do Banco ou número de identificação do portador.", obrigatoriedade = true)
	private String numeroCodigoPortador;

	@XmlAttribute(name = "t03")
	@IAtributoArquivo(ordem = 3, posicao = 5, tamanho = 40, descricao = "Preencher com o nome do Portador.('Razão Social')", obrigatoriedade = true)
	private String nomePortador;

	@XmlAttribute(name = "t04")
	@IAtributoArquivo(ordem = 4, posicao = 45, tamanho = 8, formato = "ddMMyyyy HH:mm:ss", descricao = "Identificar a data de envio da Remessa ao serviço de distribuição, no formato DDMMAAAA.", obrigatoriedade = true)
	private String dataMovimento;

	@XmlAttribute(name = "t05")
	@IAtributoArquivo(ordem = 5, posicao = 53, tamanho = 3, descricao = "Preencher com a sigla do remetente do arquivo. BFO - Banco,Instituição Financeira e outros.", obrigatoriedade = true)
	private String identificacaoTransacaoRemetente;

	@XmlAttribute(name = "t06")
	@IAtributoArquivo(ordem = 6, posicao = 56, tamanho = 3, descricao = "Preencher com a sigla do destinatário do arquivo. SDT - Serviços de Distribuição de Títulos.", obrigatoriedade = true)
	private String identificacaoTransacaoDestinatario;

	@XmlAttribute(name = "t07")
	@IAtributoArquivo(ordem = 7, posicao = 59, tamanho = 3, descricao = "Preencher com a sigla de identificação da transação. TPR - Remessa de títulos para protesto.", obrigatoriedade = true)
	private String identificacaoTransacaoTipo;

	@XmlAttribute(name = "t08")
	@IAtributoArquivo(ordem = 8, posicao = 62, tamanho = 6, descricao = "Controlar o sequencial de remessas, que deverá ser contínuo.", obrigatoriedade = true)
	private String numeroSequencialRemessa;

	@XmlAttribute(name = "t09")
	@IAtributoArquivo(ordem = 9, posicao = 68, tamanho = 4, descricao = "Preencher com o somatório da quantidade de registros constantes no arquivo.", obrigatoriedade = true)
	private String qtdRegistrosRemessa;

	@XmlAttribute(name = "t10")
	@IAtributoArquivo(ordem = 10, posicao = 72, tamanho = 4, descricao = "Preencher com o somatório da quantidade de registros constantes no arquivo. Devem constar o somatório de todos os registros, cujo o campo 22 for igual a 1.", obrigatoriedade = true)
	private String qtdTitulosRemessa;;

	@XmlAttribute(name = "t11")
	@IAtributoArquivo(ordem = 11, posicao = 76, tamanho = 4, descricao = "Preencher com o somatório da quantidade de títulos do tipo 'DMI', 'DRI' e 'CBI'.", obrigatoriedade = true)
	private String qtdIndicacoesRemessa;

	@XmlAttribute(name = "t12")
	@IAtributoArquivo(ordem = 12, posicao = 80, tamanho = 4, descricao = "Preencher com o somatório da quantidade dos demais títulos.", obrigatoriedade = true)
	private String qtdOriginaisRemessa;

	@XmlAttribute(name = "t13")
	@IAtributoArquivo(ordem = 13, posicao = 84, tamanho = 6, descricao = "Identificar a agência centralizadora - Uso do banco.", obrigatoriedade = true)
	private String agenciaCentralizadora;

	@XmlAttribute(name = "t14")
	@IAtributoArquivo(ordem = 14, posicao = 90, tamanho = 3, descricao = "Identificação da versão vigente do layout.", obrigatoriedade = true)
	private String versaoLayout;

	@XmlAttribute(name = "t15")
	@IAtributoArquivo(ordem = 15, posicao = 93, tamanho = 7, descricao = "Preencher com dois dígitos para o Código da Unidade da Federação e 5 para Código do Município.", obrigatoriedade = true)
	private String codigoMunicipio;

	@XmlAttribute(name = "t16")
	@IAtributoArquivo(ordem = 16, posicao = 100, tamanho = 497, descricao = "Ajustar o tamanho do registro do header com o tamanho do registro de transação. Preencher com brancos.", obrigatoriedade = true)
	private String complementoRegistro;

	@XmlAttribute(name = "t17")
	@IAtributoArquivo(ordem = 17, posicao = 597, tamanho = 4, descricao = "Constante 0001. Sempre reiniciar a contagem do lote de registros para as praças implantadas no processo de centralização.", obrigatoriedade = true, validacao = "0001")
	private String numeroSequencialRegistroArquivo;

	public String getNumeroCodigoPortador() {
		return numeroCodigoPortador;
	}

	public String getNomePortador() {
		return nomePortador;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public String getIdentificacaoTransacaoRemetente() {
		return identificacaoTransacaoRemetente;
	}

	public String getIdentificacaoTransacaoDestinatario() {
		return identificacaoTransacaoDestinatario;
	}

	public String getIdentificacaoTransacaoTipo() {
		return identificacaoTransacaoTipo;
	}

	public String getNumeroSequencialRemessa() {
		return numeroSequencialRemessa;
	}

	public String getQtdRegistrosRemessa() {
		return qtdRegistrosRemessa;
	}

	public String getQtdTitulosRemessa() {
		return qtdTitulosRemessa;
	}

	public String getQtdIndicacoesRemessa() {
		return qtdIndicacoesRemessa;
	}

	public String getQtdOriginaisRemessa() {
		return qtdOriginaisRemessa;
	}

	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	public String getVersaoLayout() {
		return versaoLayout;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	public String getNumeroSequencialRegistroArquivo() {
		return numeroSequencialRegistroArquivo;
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

	public void setIdentificacaoTransacaoRemetente(String identificacaoTransacaoRemetente) {
		this.identificacaoTransacaoRemetente = identificacaoTransacaoRemetente;
	}

	public void setIdentificacaoTransacaoDestinatario(String identificacaoTransacaoDestinatario) {
		this.identificacaoTransacaoDestinatario = identificacaoTransacaoDestinatario;
	}

	public void setIdentificacaoTransacaoTipo(String identificacaoTransacaoTipo) {
		this.identificacaoTransacaoTipo = identificacaoTransacaoTipo;
	}

	public void setNumeroSequencialRemessa(String numeroSequencialRemessa) {
		this.numeroSequencialRemessa = numeroSequencialRemessa;
	}

	public void setQtdRegistrosRemessa(String qtdRegistrosRemessa) {
		this.qtdRegistrosRemessa = qtdRegistrosRemessa;
	}

	public void setQtdTitulosRemessa(String qtdTitulosRemessa) {
		this.qtdTitulosRemessa = qtdTitulosRemessa;
	}

	public void setQtdIndicacoesRemessa(String qtdIndicacoesRemessa) {
		this.qtdIndicacoesRemessa = qtdIndicacoesRemessa;
	}

	public void setQtdOriginaisRemessa(String qtdOriginaisRemessa) {
		this.qtdOriginaisRemessa = qtdOriginaisRemessa;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public void setVersaoLayout(String versaoLayout) {
		this.versaoLayout = versaoLayout;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}

}
