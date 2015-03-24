package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.enumeration.TipoRegistro;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_TITULO")
@org.hibernate.annotations.Table(appliesTo = "TB_TITULO")
public class Titulo extends AbstractEntidade<Titulo> {

	/*** */
	private static final long serialVersionUID = 1L;

	private int id;
	private Remessa remessa;

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
	private Integer dataEmissaoTitulo; // As datas são passada assim: DDMMAAAA
	private Integer dataVencimentoTitulo;
	private String tipoMoeda;
	private BigDecimal valorTitulo;
	private BigDecimal saldoTitulo;
	private String pracaProtesto;
	private char tipoEndoso;
	private char informacaoSobreAceite;
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
	private Integer dataProtocolo;
	private BigDecimal valorCustaCartorio;
	private String declaracaoPortador;
	private Integer dataOcorrencia;
	private String codigoIrregularidade;
	private String bairroDevedor;
	private BigDecimal valorCustasCartorioDistribuidor;
	private Integer registroDistribuicao;
	private BigDecimal valorGravacaoEletronica;
	private Integer numeroOperacaoBanco;
	private Integer numeroContratoBanco;
	private Integer numeroParcelaContrato;
	private char tipoLetraCambio;
	private String complementoCodigoIrregularidade;
	private char protestoMotivoFalencia;
	private char instrumentoProtesto;
	private BigDecimal valorDemaisDespesas;
	private String complementoRegistro;
	private String numeroSequencialArquivo;

	@Override
	public int compareTo(Titulo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Id
	@Column(name = "ID_TITULO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne
	public Remessa getRemessa() {
		return remessa;
	}

	@Column(name = "IDENTIFICACAO_REGISTRO")
	public TipoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "CODIGO_PORTADOR")
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

	public String getUfSacadorVendedor() {
		return ufSacadorVendedor;
	}

	public String getNossoNumero() {
		return nossoNumero;
	}

	public String getEspecieTitulo() {
		return especieTitulo;
	}

	public String getNumeroTitulo() {
		return numeroTitulo;
	}

	public Integer getDataEmissaoTitulo() {
		return dataEmissaoTitulo;
	}

	public Integer getDataVencimentoTitulo() {
		return dataVencimentoTitulo;
	}

	public String getTipoMoeda() {
		return tipoMoeda;
	}

	public BigDecimal getValorTitulo() {
		return valorTitulo;
	}

	public BigDecimal getSaldoTitulo() {
		return saldoTitulo;
	}

	public String getPracaProtesto() {
		return pracaProtesto;
	}

	public char getTipoEndoso() {
		return tipoEndoso;
	}

	public char getInformacaoSobreAceite() {
		return informacaoSobreAceite;
	}

	public Integer getNumeroControleDevedor() {
		return numeroControleDevedor;
	}

	public String getNomeDevedor() {
		return nomeDevedor;
	}

	public String getTipoIdentificacaoDevedor() {
		return tipoIdentificacaoDevedor;
	}

	public String getNumeroIdentificacaoDevedor() {
		return numeroIdentificacaoDevedor;
	}

	public String getDocumentoDevedor() {
		return documentoDevedor;
	}

	public String getEnderecoDevedor() {
		return enderecoDevedor;
	}

	public String getCepDevedor() {
		return cepDevedor;
	}

	public String getCidadeDevedor() {
		return cidadeDevedor;
	}

	public String getUfDevedor() {
		return ufDevedor;
	}

	public Integer getCodigoCartorio() {
		return codigoCartorio;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public String getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public Integer getDataProtocolo() {
		return dataProtocolo;
	}

	public BigDecimal getValorCustaCartorio() {
		return valorCustaCartorio;
	}

	public String getDeclaracaoPortador() {
		return declaracaoPortador;
	}

	public Integer getDataOcorrencia() {
		return dataOcorrencia;
	}

	public String getCodigoIrregularidade() {
		return codigoIrregularidade;
	}

	public String getBairroDevedor() {
		return bairroDevedor;
	}

	public BigDecimal getValorCustasCartorioDistribuidor() {
		return valorCustasCartorioDistribuidor;
	}

	public Integer getRegistroDistribuicao() {
		return registroDistribuicao;
	}

	public BigDecimal getValorGravacaoEletronica() {
		return valorGravacaoEletronica;
	}

	public Integer getNumeroOperacaoBanco() {
		return numeroOperacaoBanco;
	}

	public Integer getNumeroContratoBanco() {
		return numeroContratoBanco;
	}

	public Integer getNumeroParcelaContrato() {
		return numeroParcelaContrato;
	}

	public char getTipoLetraCambio() {
		return tipoLetraCambio;
	}

	public String getComplementoCodigoIrregularidade() {
		return complementoCodigoIrregularidade;
	}

	public char getProtestoMotivoFalencia() {
		return protestoMotivoFalencia;
	}

	public char getInstrumentoProtesto() {
		return instrumentoProtesto;
	}

	public BigDecimal getValorDemaisDespesas() {
		return valorDemaisDespesas;
	}

	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	public String getNumeroSequencialArquivo() {
		return numeroSequencialArquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
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

	public void setDataEmissaoTitulo(Integer dataEmissaoTitulo) {
		this.dataEmissaoTitulo = dataEmissaoTitulo;
	}

	public void setDataVencimentoTitulo(Integer dataVencimentoTitulo) {
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

	public void setTipoEndoso(char tipoEndoso) {
		this.tipoEndoso = tipoEndoso;
	}

	public void setInformacaoSobreAceite(char informacaoSobreAceite) {
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

	public void setDataProtocolo(Integer dataProtocolo) {
		this.dataProtocolo = dataProtocolo;
	}

	public void setValorCustaCartorio(BigDecimal valorCustaCartorio) {
		this.valorCustaCartorio = valorCustaCartorio;
	}

	public void setDeclaracaoPortador(String declaracaoPortador) {
		this.declaracaoPortador = declaracaoPortador;
	}

	public void setDataOcorrencia(Integer dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public void setCodigoIrregularidade(String codigoIrregularidade) {
		this.codigoIrregularidade = codigoIrregularidade;
	}

	public void setBairroDevedor(String bairroDevedor) {
		this.bairroDevedor = bairroDevedor;
	}

	public void setValorCustasCartorioDistribuidor(
			BigDecimal valorCustasCartorioDistribuidor) {
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

	public void setTipoLetraCambio(char tipoLetraCambio) {
		this.tipoLetraCambio = tipoLetraCambio;
	}

	public void setComplementoCodigoIrregularidade(
			String complementoCodigoIrregularidade) {
		this.complementoCodigoIrregularidade = complementoCodigoIrregularidade;
	}

	public void setProtestoMotivoFalencia(char protestoMotivoFalencia) {
		this.protestoMotivoFalencia = protestoMotivoFalencia;
	}

	public void setInstrumentoProtesto(char instrumentoProtesto) {
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

}
