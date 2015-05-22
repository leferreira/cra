package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_CABECALHO")
@org.hibernate.annotations.Table(appliesTo = "TB_CABECALHO")
public class CabecalhoRemessa extends Cabecalho<CabecalhoRemessa> {

	/*** */
	private static final long serialVersionUID = 1L;
	private int id;
	private Remessa remessa;

	private TipoRegistro identificacaoRegistro;
	private String numeroCodigoPortador;
	private String nomePortador;
	private LocalDate dataMovimento;
	private Integer numeroSequencialRemessa;
	private Integer qtdRegistrosRemessa;
	private Integer qtdTitulosRemessa;;
	private Integer qtdIndicacoesRemessa;
	private Integer qtdOriginaisRemessa;
	private String agenciaCentralizadora;
	private String versaoLayout;
	private Integer codigoMunicipio;
	private String complementoRegistro;
	private String numeroSequencialRegistroArquivo;

	@Override
	@Id
	@Column(name = "ID_CABECALHO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToOne(mappedBy = "cabecalho")
	public Remessa getRemessa() {
		return remessa;
	}

	@Column(name = "TIPO_REGISTRO_ID")
	public TipoRegistro getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	@Column(name = "NUMERO_CODIGO_PORTADOR", length = 3)
	public String getNumeroCodigoPortador() {
		return numeroCodigoPortador;
	}

	@Column(name = "NOME_PORTADOR")
	public String getNomePortador() {
		return nomePortador;
	}

	@Column(name = "DATA_MOVIMENTO")
	public LocalDate getDataMovimento() {
		return dataMovimento;
	}

	@Column(name = "NUMERO_SEQUENCIAL_REMESSA")
	public Integer getNumeroSequencialRemessa() {
		return numeroSequencialRemessa;
	}

	@Column(name = "QUANTIDADE_REGISTROS_REMESSA")
	public Integer getQtdRegistrosRemessa() {
		return qtdRegistrosRemessa;
	}

	@Column(name = "QUANTIDADE_TITULOS_REMESSA")
	public Integer getQtdTitulosRemessa() {
		return qtdTitulosRemessa;
	}

	@Column(name = "QUANTIDADE_INDICACOES_REMESSA")
	public Integer getQtdIndicacoesRemessa() {
		return qtdIndicacoesRemessa;
	}

	@Column(name = "QUANTIDADE_ORIGINAL_REMESSA")
	public Integer getQtdOriginaisRemessa() {
		return qtdOriginaisRemessa;
	}

	@Column(name = "AGENCIA_CENTRALIZADORA")
	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	@Column(name = "VERSAO_LAYOUT", length = 3)
	public String getVersaoLayout() {
		return versaoLayout;
	}

	@Column(name = "CODIGO_MUNICIPIO")
	public Integer getCodigoMunicipio() {
		return codigoMunicipio;
	}

	@Column(name = "COMPLEMENTO_REGISTRO")
	public String getComplementoRegistro() {
		return complementoRegistro;
	}

	@Column(name = "NUMERO_SEQUENCIAL_ARQUIVO")
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

	public void setDataMovimento(LocalDate dataMovimento) {
		this.dataMovimento = dataMovimento;
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

	public void setCodigoMunicipio(Integer codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setComplementoRegistro(String complementoRegistro) {
		this.complementoRegistro = complementoRegistro;
	}

	public void setNumeroSequencialRegistroArquivo(String numeroSequencialRegistroArquivo) {
		this.numeroSequencialRegistroArquivo = numeroSequencialRegistroArquivo;
	}

	@Override
	public int compareTo(CabecalhoRemessa entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		return compareToBuilder.toComparison();
	}

	public static CabecalhoRemessa parseCabecalhoVO(CabecalhoVO cabecalhoVO) {
		CabecalhoRemessa cabecalho = new CabecalhoRemessa();
		cabecalho.setAgenciaCentralizadora(cabecalhoVO.getAgenciaCentralizadora());
		cabecalho.setCodigoMunicipio(Integer.parseInt(cabecalhoVO.getCodigoMunicipio()));
		cabecalho.setComplementoRegistro(cabecalhoVO.getComplementoRegistro());
		cabecalho.setDataMovimento(DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, cabecalhoVO.getDataMovimento()));
		cabecalho.setIdentificacaoRegistro(TipoRegistro.get(cabecalhoVO.getIdentificacaoRegistro()));
		cabecalho.setIdentificacaoTransacaoDestinatario(cabecalhoVO.getIdentificacaoTransacaoDestinatario());
		cabecalho.setIdentificacaoTransacaoRemetente(cabecalhoVO.getIdentificacaoTransacaoRemetente());
		cabecalho.setIdentificacaoTransacaoTipo(cabecalhoVO.getIdentificacaoTransacaoTipo());
		cabecalho.setNomePortador(cabecalhoVO.getNomePortador());
		cabecalho.setNumeroCodigoPortador(cabecalhoVO.getNumeroCodigoPortador());
		cabecalho.setNumeroSequencialRegistroArquivo(cabecalhoVO.getNumeroSequencialRegistroArquivo());
		cabecalho.setNumeroSequencialRemessa(Integer.parseInt(cabecalhoVO.getNumeroSequencialRemessa()));
		cabecalho.setQtdIndicacoesRemessa(Integer.parseInt(cabecalhoVO.getQtdIndicacoesRemessa()));
		cabecalho.setQtdOriginaisRemessa(Integer.parseInt(cabecalhoVO.getQtdOriginaisRemessa()));
		cabecalho.setQtdRegistrosRemessa(Integer.parseInt(cabecalhoVO.getQtdRegistrosRemessa()));
		cabecalho.setQtdTitulosRemessa(Integer.parseInt(cabecalhoVO.getQtdTitulosRemessa()));
		cabecalho.setVersaoLayout(cabecalhoVO.getVersaoLayout());

		return cabecalho;
	}
}
