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
@Table(name = "TB_RODAPE")
@org.hibernate.annotations.Table(appliesTo = "TB_RODAPE")
public class Rodape extends AbstractEntidade<Rodape> {

	/** **/
	private static final long serialVersionUID = 1L;
	private int id;
	private Remessa remessa;
	/**
	 * Registros do rodape
	 * */
	private TipoRegistro identificacaoRegistro;
	private String numeroCodigoPortador;
	private String nomePortador;
	private String dataMovimento;
	private BigDecimal somatorioQtdRemessa;
	private BigDecimal somatorioValorRemessa;
	private String complementoRegistro;
	private String numeroSequencialRegistroArquivo;

	@Override
	public int compareTo(Rodape entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Id
	@Column(name = "ID_RODAPE", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(mappedBy="rodape")
	public Remessa getRemessa() {
		return remessa;
	}

	@Column(name = "IDENTIFICACAO_REGISTRO")
	public TipoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "CODIGO_PORTADOR", columnDefinition = "serial")
	public String getNumeroCodigoPortador() {
		return numeroCodigoPortador;
	}

	@Column(name = "NOME_PORTADOR", columnDefinition = "serial")
	public String getNomePortador() {
		return nomePortador;
	}

	@Column(name = "DATA_MOVIMENTO", columnDefinition = "serial")
	public String getDataMovimento() {
		return dataMovimento;
	}

	@Column(name = "QTD_REMESSA", columnDefinition = "serial")
	public BigDecimal getSomatorioQtdRemessa() {
		return somatorioQtdRemessa;
	}

	@Column(name = "SOMATORIO_VLR_REMESSA", columnDefinition = "serial")
	public BigDecimal getSomatorioValorRemessa() {
		return somatorioValorRemessa;
	}

	@Column(name = "COMPLEMENTO_REGISTRO", columnDefinition = "serial")
	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	@Column(name = "NUMERO_SEQUENCIAL", columnDefinition = "serial")
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

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setSomatorioQtdRemessa(BigDecimal somatorioQtdRemessa) {
		this.somatorioQtdRemessa = somatorioQtdRemessa;
	}

	public void setSomatorioValorRemessa(BigDecimal somatorioValorRemessa) {
		this.somatorioValorRemessa = somatorioValorRemessa;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(
			String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}
}
