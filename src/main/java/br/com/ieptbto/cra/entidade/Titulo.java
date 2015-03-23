package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private String dataEmissaoTitulo;
	private String dataVencimentoTitulo;
	private String tipoMoeda;
	private String valorTitulo;
	private String saldoTitulo;
	private String pracaProtesto;
	private String tipoEndoso;
	private String informacaoSobreAceite;
	private String numeroControleDevedor;
	private String nomeDevedor;
	private String tipoIdentificacaoDevedor;
	private String numeroIdentificacaoDevedor;
	private String documentoDevedor;
	private String enderecoDevedor;
	private String cepDevedor;
	private String cidadeDevedor;
	private String ufDevedor;
	private String codigoCartorio;
	private String numeroProtocoloCartorio;
	private String tipoOcorrencia;
	private String dataProtocolo;
	private String valorCustaCartorio;
	private String declaracaoPortador;
	private String dataOcorrencia;
	private String codigoIrregularidade;
	private String bairroDevedor;
	private String valorCustasCartorioDistribuidor;
	private String registroDistribuicao;
	private String valorGravacaoEletronica;
	private String numeroOperacaoBanco;
	private String numeroContratoBanco;
	private String numeroParcelaContrato;
	private String tipoLetraCambio;
	private String complementoCodigoIrregularidade;
	private String protestoMotivoFalencia;
	private String instrumentoProtesto;
	private String valorDemaisDespesas;
	private String complementoRegistro;
	private String numeroSequencialArquivo;
	private Cabecalho cabecalho;
	private Rodape rodape;
	private Arquivo arquivo;

	@Id
	@Column(name = "ID_TITULO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_ID")
	public Arquivo getArquivo() {
		return arquivo;
	}

	@ManyToOne
	@JoinColumn(name = "CABECALHO_ID")
	public Cabecalho getCabecalho() {
		return cabecalho;
	}

	@ManyToOne
	@JoinColumn(name = "RODAPE_ID")
	public Rodape getRodape() {
		return rodape;
	}

	@Column(name = "IDENTIFICACAO_REGISTRO")
	@Enumerated(EnumType.STRING)
	public TipoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "CODIGO_PORTADOR", length = 3)
	public String getCodigoPortador() {
		return codigoPortador;
	}

	@Column(name = "AGENCIA_CODIGO_CEDENTE", length = 15)
	public String getAgenciaCodigoCedente() {
		return agenciaCodigoCedente;
	}

	@Column(name = "NOME_CEDENTE_FAVORECIDO", length = 50)
	public String getNomeCedenteFavorecido() {
		return nomeCedenteFavorecido;
	}

	@Column(name = "NOME_SACADOR_VENDEDOR", length = 50)
	public String getNomeSacadorVendedor() {
		return nomeSacadorVendedor;
	}

	public String getDocumentoSacador() {
		return documentoSacador;
	}

	public String getEnderecoSacadorVendedor() {
		return enderecoSacadorVendedor;
	}

	public String getCepSacadorVendedor() {
		return cepSacadorVendedor;
	}

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

	public String getDataEmissaoTitulo() {
		return dataEmissaoTitulo;
	}

	public String getDataVencimentoTitulo() {
		return dataVencimentoTitulo;
	}

	public String getTipoMoeda() {
		return tipoMoeda;
	}

	public String getValorTitulo() {
		return valorTitulo;
	}

	public String getSaldoTitulo() {
		return saldoTitulo;
	}

	public String getPracaProtesto() {
		return pracaProtesto;
	}

	public String getTipoEndoso() {
		return tipoEndoso;
	}

	public String getInformacaoSobreAceite() {
		return informacaoSobreAceite;
	}

	public String getNumeroControleDevedor() {
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

	public String getCodigoCartorio() {
		return codigoCartorio;
	}

	public String getNumeroProtocoloCartorio() {
		return numeroProtocoloCartorio;
	}

	public String getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public String getDataProtocolo() {
		return dataProtocolo;
	}

	public String getValorCustaCartorio() {
		return valorCustaCartorio;
	}

	public String getDeclaracaoPortador() {
		return declaracaoPortador;
	}

	public String getDataOcorrencia() {
		return dataOcorrencia;
	}

	public String getCodigoIrregularidade() {
		return codigoIrregularidade;
	}

	public String getBairroDevedor() {
		return bairroDevedor;
	}

	public String getValorCustasCartorioDistribuidor() {
		return valorCustasCartorioDistribuidor;
	}

	public String getRegistroDistribuicao() {
		return registroDistribuicao;
	}

	public String getValorGravacaoEletronica() {
		return valorGravacaoEletronica;
	}

	public String getNumeroOperacaoBanco() {
		return numeroOperacaoBanco;
	}

	public String getNumeroContratoBanco() {
		return numeroContratoBanco;
	}

	public String getNumeroParcelaContrato() {
		return numeroParcelaContrato;
	}

	public String getTipoLetraCambio() {
		return tipoLetraCambio;
	}

	public String getComplementoCodigoIrregularidade() {
		return complementoCodigoIrregularidade;
	}

	public String getProtestoMotivoFalencia() {
		return protestoMotivoFalencia;
	}

	public String getInstrumentoProtesto() {
		return instrumentoProtesto;
	}

	public String getValorDemaisDespesas() {
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

	public void setDataEmissaoTitulo(String dataEmissaoTitulo) {
		this.dataEmissaoTitulo = dataEmissaoTitulo;
	}

	public void setDataVencimentoTitulo(String dataVencimentoTitulo) {
		this.dataVencimentoTitulo = dataVencimentoTitulo;
	}

	public void setTipoMoeda(String tipoMoeda) {
		this.tipoMoeda = tipoMoeda;
	}

	public void setValorTitulo(String valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public void setSaldoTitulo(String saldoTitulo) {
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

	public void setNumeroControleDevedor(String numeroControleDevedor) {
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

	public void setCodigoCartorio(String codigoCartorio) {
		this.codigoCartorio = codigoCartorio;
	}

	public void setNumeroProtocoloCartorio(String numeroProtocoloCartorio) {
		this.numeroProtocoloCartorio = numeroProtocoloCartorio;
	}

	public void setTipoOcorrencia(String tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public void setDataProtocolo(String dataProtocolo) {
		this.dataProtocolo = dataProtocolo;
	}

	public void setValorCustaCartorio(String valorCustaCartorio) {
		this.valorCustaCartorio = valorCustaCartorio;
	}

	public void setDeclaracaoPortador(String declaracaoPortador) {
		this.declaracaoPortador = declaracaoPortador;
	}

	public void setDataOcorrencia(String dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public void setCodigoIrregularidade(String codigoIrregularidade) {
		this.codigoIrregularidade = codigoIrregularidade;
	}

	public void setBairroDevedor(String bairroDevedor) {
		this.bairroDevedor = bairroDevedor;
	}

	public void setValorCustasCartorioDistribuidor(String valorCustasCartorioDistribuidor) {
		this.valorCustasCartorioDistribuidor = valorCustasCartorioDistribuidor;
	}

	public void setRegistroDistribuicao(String registroDistribuicao) {
		this.registroDistribuicao = registroDistribuicao;
	}

	public void setValorGravacaoEletronica(String valorGravacaoEletronica) {
		this.valorGravacaoEletronica = valorGravacaoEletronica;
	}

	public void setNumeroOperacaoBanco(String numeroOperacaoBanco) {
		this.numeroOperacaoBanco = numeroOperacaoBanco;
	}

	public void setNumeroContratoBanco(String numeroContratoBanco) {
		this.numeroContratoBanco = numeroContratoBanco;
	}

	public void setNumeroParcelaContrato(String numeroParcelaContrato) {
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

	public void setValorDemaisDespesas(String valorDemaisDespesas) {
		this.valorDemaisDespesas = valorDemaisDespesas;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialArquivo(String numeroSequencialArquivo) {
		this.numeroSequencialArquivo = numeroSequencialArquivo;
	}

	public void setCabecalho(Cabecalho cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setRodape(Rodape rodape) {
		this.rodape = rodape;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	public int compareTo(Titulo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
