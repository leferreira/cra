package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.enumeration.TipoRegistro;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_TITULO", uniqueConstraints = { @UniqueConstraint(columnNames = { "CODIGO_PORTADOR", "NOSSO_NUMERO", "NUMERO_TITULO" }) })
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO")
public class Titulo extends AbstractEntidade<Titulo> {

	/*** */
	private static final long serialVersionUID = 1L;

	private int id;
	private Remessa remessa;
	private List<Historico> historicos;

	private TipoRegistro identificacaoRegistro;
	private String codigoPortador;
	private String agenciaCodigoCedente;
	private String nomeCedenteFavorecido;
	private String nomeSacadorVendedor;
	private String documentoSacador;
	private String enderecoSacadorVendedor;
	private String cepSacadorVendedor;
	private String cidadeSacadorVendedor;
	private String ufSacadorVendedor;
	private String nossoNumero;
	private String especieTitulo;
	private String numeroTitulo;
	private LocalDate dataEmissaoTitulo; // As datas são passada assim: DDMMAAAA
	private LocalDate dataVencimentoTitulo;
	private String tipoMoeda;
	private BigDecimal valorTitulo;
	private BigDecimal saldoTitulo;
	private String pracaProtesto;
	private String tipoEndoso;
	private String informacaoSobreAceite;
	private Integer numeroControleDevedor;
	private String nomeDevedor;
	private String tipoIdentificacaoDevedor;
	private String numeroIdentificacaoDevedor;
	private String documentoDevedor;
	private String enderecoDevedor;
	private String cepDevedor;
	private String cidadeDevedor;
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
	private Integer numeroOperacaoBanco;
	private Integer numeroContratoBanco;
	private Integer numeroParcelaContrato;
	private String tipoLetraCambio;
	private String complementoCodigoIrregularidade;
	private String protestoMotivoFalencia;
	private String instrumentoProtesto;
	private BigDecimal valorDemaisDespesas;
	private String complementoRegistro;
	private String numeroSequencialArquivo;

	@Id
	@Column(name = "ID_TITULO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	@JoinColumn(name = "REMESSA_ID")
	public Remessa getRemessa() {
		return remessa;
	}

	@OneToMany(mappedBy="titulo")
	public List<Historico> getHistoricos() {
		return historicos;
	}
	
	@Column(name = "IDENTIFICACAO_REGISTRO_ID")
	public TipoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "CODIGO_PORTADOR", length = 3, nullable = false)
	public String getCodigoPortador() {
		return codigoPortador;
	}

	@Column(name = "AGENCIA_CODIGO_CEDENTE")
	public String getAgenciaCodigoCedente() {
		return agenciaCodigoCedente;
	}

	@Column(name = "NOME_CEDENTE_FAVORECIDO")
	public String getNomeCedenteFavorecido() {
		return nomeCedenteFavorecido;
	}

	@Column(name = "NOME_SACADOR_VENDEDOR")
	public String getNomeSacadorVendedor() {
		return nomeSacadorVendedor;
	}

	@Column(name = "DOCUMENTO_SACADOR")
	public String getDocumentoSacador() {
		return documentoSacador;
	}

	@Column(name = "ENDERECO_SACADOR_DEVEDOR")
	public String getEnderecoSacadorVendedor() {
		return enderecoSacadorVendedor;
	}

	@Column(name = "CEP_SACADOR_DEVEDOR")
	public String getCepSacadorVendedor() {
		return cepSacadorVendedor;
	}

	@Column(name = "CIDADE_CIDADE")
	public String getCidadeSacadorVendedor() {
		return cidadeSacadorVendedor;
	}

	@Column(name = "UF_SACADOR_VENDEDOR")
	public String getUfSacadorVendedor() {
		return ufSacadorVendedor;
	}

	@Column(name = "NOSSO_NUMERO", nullable = false)
	public String getNossoNumero() {
		return nossoNumero;
	}

	@Column(name = "ESPECIE_TITULO", length = 3)
	public String getEspecieTitulo() {
		return especieTitulo;
	}

	@Column(name = "NUMERO_TITULO", nullable = false)
	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	@Column(name = "DATA_EMISSAO")
	public LocalDate getDataEmissaoTitulo() {
		return dataEmissaoTitulo;
	}

	@Column(name = "DATA_VENCIMENTO")
	public LocalDate getDataVencimentoTitulo() {
		return dataVencimentoTitulo;
	}

	@Column(name = "TIPO_MOEDA")
	public String getTipoMoeda() {
		return tipoMoeda;
	}

	@Column(name = "VALOR_TITULO")
	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	@Column(name = "VALOR_SALDO_TITULO")
	public BigDecimal getSaldoTitulo() {
		return saldoTitulo;
	}

	@Column(name = "PRACA_PROTESTO")
	public String getPracaProtesto() {
		return pracaProtesto;
	}

	@Column(name = "TIPO_ENDOSO")
	public String getTipoEndoso() {
		return tipoEndoso;
	}

	@Column(name = "INFORMACAO_SOBRE_ACEITE")
	public String getInformacaoSobreAceite() {
		return informacaoSobreAceite;
	}

	@Column(name = "NUMERO_CONTROLE_DEVEDOR")
	public Integer getNumeroControleDevedor() {
		return numeroControleDevedor;
	}

	@Column(name = "NOME_DEVEDOR")
	public String getNomeDevedor() {
		return nomeDevedor;
	}

	@Column(name = "TIPO_IDENTIFICACAO_DEVEDOR")
	public String getTipoIdentificacaoDevedor() {
		return tipoIdentificacaoDevedor;
	}

	@Column(name = "NUMERO_IDENTIFICACAO_DEVEDOR")
	public String getNumeroIdentificacaoDevedor() {
		return numeroIdentificacaoDevedor;
	}

	@Column(name = "DOCUMENTO_DEVEDOR")
	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	@Column(name = "ENDERECO_DEVEDOR")
	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	@Column(name = "CEP_DEVEDOR")
	public String getCepDevedor() {
		return cepDevedor;
	}

	@Column(name = "CIDADE_DEVEDOR")
	public String getCidadeDevedor() {
		return cidadeDevedor;
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

	@Column(name = "NUMERO_OPERACAO_BANCO")
	public Integer getNumeroOperacaoBanco() {
		return numeroOperacaoBanco;
	}

	@Column(name = "NUMERO_CONTRATO_BANCO")
	public Integer getNumeroContratoBanco() {
		return numeroContratoBanco;
	}

	@Column(name = "NUMERO_PARCELA_CONTRATO")
	public Integer getNumeroParcelaContrato() {
		return numeroParcelaContrato;
	}

	@Column(name = "TIPO_LETRA_CAMBIO")
	public String getTipoLetraCambio() {
		return tipoLetraCambio;
	}

	@Column(name = "COMPLEMENTO_CODIGO_IRREGULARIDADE")
	public String getComplementoCodigoIrregularidade() {
		return complementoCodigoIrregularidade;
	}

	@Column(name = "PROTESTO_MOTIVO_FALENCIA")
	public String getProtestoMotivoFalencia() {
		return protestoMotivoFalencia;
	}

	@Column(name = "INSTRUMENTO_PROTESTO")
	public String getInstrumentoProtesto() {
		return instrumentoProtesto;
	}

	@Column(name = "VALOR_DEMAIS_DESPESAS")
	public BigDecimal getValorDemaisDespesas() {
		return valorDemaisDespesas;
	}

	@Column(name = "COMPLEMENTO_REGISTRO")
	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	@Column(name = "NUMERO_SEQUENCIAL_ARQUIVO")
	public String getNumeroSequencialArquivo() {
		return numeroSequencialArquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setHistoricos(List<Historico> historicos) {
		this.historicos = historicos;
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

	public void setNomeCedenteFavorecido(String nomeCedenteFavorecido) {
		this.nomeCedenteFavorecido = nomeCedenteFavorecido;
	}

	public void setNomeSacadorVendedor(String nomeSacadorVendedor) {
		this.nomeSacadorVendedor = nomeSacadorVendedor;
	}

	public void setDocumentoSacador(String documentoSacador) {
		this.documentoSacador = documentoSacador;
	}

	public void setEnderecoSacadorVendedor(String enderecoSacadorVendedor) {
		this.enderecoSacadorVendedor = enderecoSacadorVendedor;
	}

	public void setCepSacadorVendedor(String cepSacadorVendedor) {
		this.cepSacadorVendedor = cepSacadorVendedor;
	}

	public void setCidadeSacadorVendedor(String cidadeSacadorVendedor) {
		this.cidadeSacadorVendedor = cidadeSacadorVendedor;
	}

	public void setUfSacadorVendedor(String ufSacadorVendedor) {
		this.ufSacadorVendedor = ufSacadorVendedor;
	}

	public void setNossoNumero(String nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public void setEspecieTitulo(String especieTitulo) {
		this.especieTitulo = especieTitulo;
	}

	public void setNumeroTitulo(String numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public void setDataEmissaoTitulo(LocalDate dataEmissaoTitulo) {
		this.dataEmissaoTitulo = dataEmissaoTitulo;
	}

	public void setDataVencimentoTitulo(LocalDate dataVencimentoTitulo) {
		this.dataVencimentoTitulo = dataVencimentoTitulo;
	}

	public void setTipoMoeda(String tipoMoeda) {
		this.tipoMoeda = tipoMoeda;
	}

	public void setValorTitulo(BigDecimal valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setSaldoTitulo(BigDecimal saldoTitulo) {
		this.saldoTitulo = saldoTitulo;
	}

	public void setPracaProtesto(String pracaProtesto) {
		this.pracaProtesto = pracaProtesto;
	}

	public void setTipoEndoso(String tipoEndoso) {
		this.tipoEndoso = tipoEndoso;
	}

	public void setInformacaoSobreAceite(String informacaoSobreAceite) {
		this.informacaoSobreAceite = informacaoSobreAceite;
	}

	public void setNumeroControleDevedor(Integer numeroControleDevedor) {
		this.numeroControleDevedor = numeroControleDevedor;
	}

	public void setNomeDevedor(String nomeDevedor) {
		this.nomeDevedor = nomeDevedor;
	}

	public void setTipoIdentificacaoDevedor(String tipoIdentificacaoDevedor) {
		this.tipoIdentificacaoDevedor = tipoIdentificacaoDevedor;
	}

	public void setNumeroIdentificacaoDevedor(String numeroIdentificacaoDevedor) {
		this.numeroIdentificacaoDevedor = numeroIdentificacaoDevedor;
	}

	public void setDocumentoDevedor(String documentoDevedor) {
		this.documentoDevedor = documentoDevedor;
	}

	public void setEnderecoDevedor(String enderecoDevedor) {
		this.enderecoDevedor = enderecoDevedor;
	}

	public void setCepDevedor(String cepDevedor) {
		this.cepDevedor = cepDevedor;
	}

	public void setCidadeDevedor(String cidadeDevedor) {
		this.cidadeDevedor = cidadeDevedor;
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

	public void setNumeroOperacaoBanco(Integer numeroOperacaoBanco) {
		this.numeroOperacaoBanco = numeroOperacaoBanco;
	}

	public void setNumeroContratoBanco(Integer numeroContratoBanco) {
		this.numeroContratoBanco = numeroContratoBanco;
	}

	public void setNumeroParcelaContrato(Integer numeroParcelaContrato) {
		this.numeroParcelaContrato = numeroParcelaContrato;
	}

	public void setTipoLetraCambio(String tipoLetraCambio) {
		this.tipoLetraCambio = tipoLetraCambio;
	}

	public void setComplementoCodigoIrregularidade(String complementoCodigoIrregularidade) {
		this.complementoCodigoIrregularidade = complementoCodigoIrregularidade;
	}

	public void setProtestoMotivoFalencia(String protestoMotivoFalencia) {
		this.protestoMotivoFalencia = protestoMotivoFalencia;
	}

	public void setInstrumentoProtesto(String instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
	}

	public void setValorDemaisDespesas(BigDecimal valorDemaisDespesas) {
		this.valorDemaisDespesas = valorDemaisDespesas;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialArquivo(String numeroSequencialArquivo) {
		this.numeroSequencialArquivo = numeroSequencialArquivo;
	}

	@Transient
	public String chaveTitulo() {
		return this.codigoPortador + this.nossoNumero + this.numeroTitulo;
	}

	@Override
	public int compareTo(Titulo entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getCodigoPortador(), entidade.getCodigoPortador());
		compareToBuilder.append(this.getNossoNumero(), entidade.getNossoNumero());
		compareToBuilder.append(this.getNumeroTitulo(), entidade.getNumeroTitulo());

		return compareToBuilder.toComparison();
	}

}
