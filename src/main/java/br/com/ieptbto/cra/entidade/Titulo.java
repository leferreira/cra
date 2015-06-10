package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.TipoRegistro;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Titulo<T> extends AbstractEntidade<T> {

	private TipoRegistro identificacaoRegistro;
	private String codigoPortador;
	private String numeroTitulo;
	private String nossoNumero;
	private String agenciaCodigoCedente;
	private BigDecimal saldoTitulo;

	private String ufDevedor;
	private Integer codigoCartorio;
	private String numeroProtocoloCartorio;
	private String tipoOcorrencia;
	private LocalDate dataProtocolo;
	private BigDecimal valorCustaCartorio;
	private String declaracaoPortador;
	private LocalDate dataOcorrencia;
	private String codigoIrregularidade;
	private String bairroDevedor;
	private BigDecimal valorCustasCartorioDistribuidor;
	private Integer registroDistribuicao;
	private BigDecimal valorGravacaoEletronica;
	private String complementoCodigoIrregularidade;
	private BigDecimal valorDemaisDespesas;
	private String numeroSequencialArquivo;
	private Remessa remessa;

	@Override
	public abstract int getId();

	@ManyToOne
	@JoinColumn(name = "REMESSA_ID")
	public Remessa getRemessa() {
		return remessa;
	}

	@Column(name = "IDENTIFICACAO_REGISTRO_ID")
	public TipoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "CODIGO_PORTADOR", length = 3, nullable = false)
	public String getCodigoPortador() {
		return codigoPortador;
	}

	@Column(name = "NOSSO_NUMERO", nullable = false)
	public String getNossoNumero() {
		if (nossoNumero != null) {
			return nossoNumero.trim();
		}
		return nossoNumero;
	}

	@Column(name = "NUMERO_TITULO", nullable = false)
	public String getNumeroTitulo() {
		if (numeroTitulo != null) {
			return numeroTitulo.trim();
		}
		return numeroTitulo;
	}

	@Column(name = "AGENCIA_CODIGO_CEDENTE")
	public String getAgenciaCodigoCedente() {
		return agenciaCodigoCedente;
	}

	@Column(name = "VALOR_SALDO_TITULO")
	public BigDecimal getSaldoTitulo() {
		return saldoTitulo;
	}

	@Column(name = "UF_DEVEDOR")
	public String getUfDevedor() {
		return ufDevedor;
	}

	@Column(name = "CODIGO_CARTORIO")
	public Integer getCodigoCartorio() {
		return codigoCartorio;
	}

	@Column(name = "NUMERO_PROTOCOLO_CARTORIO")
	public String getNumeroProtocoloCartorio() {
		if (numeroProtocoloCartorio != null) {
			return numeroProtocoloCartorio.trim();
		}
		return numeroProtocoloCartorio;
	}

	@Column(name = "TIPO_OCORRENCIA")
	public String getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	@Column(name = "DATA_PROTOCOLO")
	public LocalDate getDataProtocolo() {
		return dataProtocolo;
	}

	@Column(name = "VALOR_CUSTA_CARTORIO")
	public BigDecimal getValorCustaCartorio() {
		return valorCustaCartorio;
	}

	@Column(name = "DECLARACAO_PORTADOR")
	public String getDeclaracaoPortador() {
		return declaracaoPortador;
	}

	@Column(name = "DATA_OCORRENCIA")
	public LocalDate getDataOcorrencia() {
		return dataOcorrencia;
	}

	@Column(name = "CODIGO_IRREGULARIDADE")
	public String getCodigoIrregularidade() {
		return codigoIrregularidade;
	}

	@Column(name = "BAIRRO_DEVEDOR")
	public String getBairroDevedor() {
		return bairroDevedor;
	}

	@Column(name = "VALOR_CUSTA_CARTORIO_DISTRIBUIDOR")
	public BigDecimal getValorCustasCartorioDistribuidor() {
		return valorCustasCartorioDistribuidor;
	}

	@Column(name = "REGISTRO_DISTRIBUICAO")
	public Integer getRegistroDistribuicao() {
		return registroDistribuicao;
	}

	@Column(name = "VALOR_GRAVACAO_ELETRONICA")
	public BigDecimal getValorGravacaoEletronica() {
		return valorGravacaoEletronica;
	}

	@Column(name = "VALOR_DEMAIS_DESPESAS")
	public BigDecimal getValorDemaisDespesas() {
		return valorDemaisDespesas;
	}

	@Column(name = "COMPLEMENTO_CODIGO_IRREGULARIDADE")
	public String getComplementoCodigoIrregularidade() {
		return complementoCodigoIrregularidade;
	}

	@Column(name = "NUMERO_SEQUENCIAL_ARQUIVO")
	public String getNumeroSequencialArquivo() {
		return numeroSequencialArquivo;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setValorDemaisDespesas(BigDecimal valorDemaisDespesas) {
		this.valorDemaisDespesas = valorDemaisDespesas;
	}

	public void setUfDevedor(String ufDevedor) {
		this.ufDevedor = ufDevedor;
	}

	public void setCodigoCartorio(Integer codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setTipoOcorrencia(String tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public void setDataProtocolo(LocalDate dataProtocolo) {
		this.dataProtocolo = dataProtocolo;
	}

	public void setValorCustaCartorio(BigDecimal valorCustaCartorio) {
		this.valorCustaCartorio = valorCustaCartorio;
	}

	public void setDeclaracaoPortador(String declaracaoPortador) {
		this.declaracaoPortador = declaracaoPortador;
	}

	public void setDataOcorrencia(LocalDate dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public void setCodigoIrregularidade(String codigoIrregularidade) {
		this.codigoIrregularidade = codigoIrregularidade;
	}

	public void setBairroDevedor(String bairroDevedor) {
		this.bairroDevedor = bairroDevedor;
	}

	public void setValorCustasCartorioDistribuidor(BigDecimal valorCustasCartorioDistribuidor) {
		this.valorCustasCartorioDistribuidor = valorCustasCartorioDistribuidor;
	}

	public void setRegistroDistribuicao(Integer registroDistribuicao) {
		this.registroDistribuicao = registroDistribuicao;
	}

	public void setValorGravacaoEletronica(BigDecimal valorGravacaoEletronica) {
		this.valorGravacaoEletronica = valorGravacaoEletronica;
	}

	public void setIdentificacaoRegistro(TipoRegistro identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setCodigoPortador(String codigoPortador) {
		this.codigoPortador = codigoPortador;
	}

	public void setAgenciaCodigoCedente(String agenciaCodigoCedente) {
		this.agenciaCodigoCedente = agenciaCodigoCedente;
	}

	public void setSaldoTitulo(BigDecimal saldoTitulo) {
		this.saldoTitulo = saldoTitulo;
	}

	public void setComplementoCodigoIrregularidade(String complementoCodigoIrregularidade) {
		this.complementoCodigoIrregularidade = complementoCodigoIrregularidade;
	}

	public void setNumeroSequencialArquivo(String numeroSequencialArquivo) {
		this.numeroSequencialArquivo = numeroSequencialArquivo;
	}

	@Override
	public int compareTo(T entidade) {
		return 0;
	};

}
