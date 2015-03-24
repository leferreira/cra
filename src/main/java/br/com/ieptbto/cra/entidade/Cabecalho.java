package br.com.ieptbto.cra.entidade;

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
@Table(name = "TB_CABECALHO")
@org.hibernate.annotations.Table(appliesTo = "TB_CABECALHO")
public class Cabecalho extends AbstractEntidade<Cabecalho> {

	/*** */
	private static final long serialVersionUID = 1L;
	private int id;
	private Remessa remessa;
	
	private TipoRegistro identificacaoRegistro;
	private String numeroCodigoPortador;
	private String nomePortador;
	private Integer dataMovimento;
	private String identificacaoTransacaoRemetente;
	private String identificacaoTransacaoDestinatario;
	private String identificacaoTransacaoTipo;
	private Integer numeroSequencialRemessa;
	private Integer qtdRegistrosRemessa;
	private Integer qtdTitulosRemessa;;
	private Integer qtdIndicacoesRemessa;
	private Integer qtdOriginaisRemessa;
	private String agenciaCentralizadora;
	private String versaoLayout;
	private Integer codigoMunicipioPraçaPagamento;
	private String complementoRegistro;
	private String numeroSequencialRegistroArquivo;

	@Override
	@Id
	@Column(name = "ID_CABECALHO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Override
	public int compareTo(Cabecalho entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@OneToOne(mappedBy="cabecalho")
	public Remessa getRemessa() {
		return remessa;
	}

	@Column(name= "IDENTIFICACAO_REGISTRO")
	public TipoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name= "NUMERO_CODIGO_PORTADOR")
	public String getNumeroCodigoPortador() {
		return numeroCodigoPortador;
	}

	@Column(name= "NOME_PORTADOR")
	public String getNomePortador() {
		return nomePortador;
	}

	@Column(name= "DATA_MOVIMENTO")
	public Integer getDataMovimento() {
		return dataMovimento;
	}

	@Column(name= "IDENTIFICAO_TRANSACAO_REMENTE")
	public String getIdentificacaoTransacaoRemetente() {
		return identificacaoTransacaoRemetente;
	}

	@Column(name= "IDENTIFICAO_TRANSACAO_DESTINATARIO")
	public String getIdentificacaoTransacaoDestinatario() {
		return identificacaoTransacaoDestinatario;
	}

	@Column(name= "IDENTIFICAO_TRANSACAO_TIPO")
	public String getIdentificacaoTransacaoTipo() {
		return identificacaoTransacaoTipo;
	}

	@Column(name= "NUMERO_SEQUENCIAL_REMESSA")
	public Integer getNumeroSequencialRemessa() {
		return numeroSequencialRemessa;
	}

	@Column(name= "QUANTIDADE_REGISTROS_REMESSA")
	public Integer getQtdRegistrosRemessa() {
		return qtdRegistrosRemessa;
	}

	@Column(name= "QUANTIDADE_TITULOS_REMESSA")
	public Integer getQtdTitulosRemessa() {
		return qtdTitulosRemessa;
	}

	@Column(name= "QUANTIDADE_INDICACOES_REMESSA")
	public Integer getQtdIndicacoesRemessa() {
		return qtdIndicacoesRemessa;
	}

	@Column(name= "QUANTIDADE_ORIGINAL_REMESSA")
	public Integer getQtdOriginaisRemessa() {
		return qtdOriginaisRemessa;
	}

	@Column(name= "AGENCIA_CENTRALIZADORA")
	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	@Column(name= "VERSAO_LAYOUT")
	public String getVersaoLayout() {
		return versaoLayout;
	}

	@Column(name= "CODIGO_PRACA_PAGAMENTO")
	public Integer getCodigoMunicipioPraçaPagamento() {
		return codigoMunicipioPraçaPagamento;
	}

	@Column(name= "COMPLEMENTO_REGISTRO")
	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	@Column(name= "NUMERO_SEQUENCIAL_ARQUIVO")
	public String getNumeroSequencialRegistroArquivo() {
		return numeroSequencialRegistroArquivo;
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

	public void setNumeroCodigoPortador(String numeroCodigoPortador) {
		this.numeroCodigoPortador = numeroCodigoPortador;
	}

	public void setNomePortador(String nomePortador) {
		this.nomePortador = nomePortador;
	}

	public void setDataMovimento(Integer dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setIdentificacaoTransacaoRemetente(
			String identificacaoTransacaoRemetente) {
		this.identificacaoTransacaoRemetente = identificacaoTransacaoRemetente;
	}

	public void setIdentificacaoTransacaoDestinatario(
			String identificacaoTransacaoDestinatario) {
		this.identificacaoTransacaoDestinatario = identificacaoTransacaoDestinatario;
	}

	public void setIdentificacaoTransacaoTipo(String identificacaoTransacaoTipo) {
		this.identificacaoTransacaoTipo = identificacaoTransacaoTipo;
	}

	public void setNumeroSequencialRemessa(Integer numeroSequencialRemessa) {
		this.numeroSequencialRemessa = numeroSequencialRemessa;
	}

	public void setQtdRegistrosRemessa(Integer qtdRegistrosRemessa) {
		this.qtdRegistrosRemessa = qtdRegistrosRemessa;
	}

	public void setQtdTitulosRemessa(Integer qtdTitulosRemessa) {
		this.qtdTitulosRemessa = qtdTitulosRemessa;
	}

	public void setQtdIndicacoesRemessa(Integer qtdIndicacoesRemessa) {
		this.qtdIndicacoesRemessa = qtdIndicacoesRemessa;
	}

	public void setQtdOriginaisRemessa(Integer qtdOriginaisRemessa) {
		this.qtdOriginaisRemessa = qtdOriginaisRemessa;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public void setVersaoLayout(String versaoLayout) {
		this.versaoLayout = versaoLayout;
	}

	public void setCodigoMunicipioPraçaPagamento(
			Integer codigoMunicipioPraçaPagamento) {
		this.codigoMunicipioPraçaPagamento = codigoMunicipioPraçaPagamento;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(
			String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}
}