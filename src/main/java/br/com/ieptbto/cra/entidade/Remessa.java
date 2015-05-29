package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@Entity
@Audited
@Table(name = "TB_REMESSA")
@org.hibernate.annotations.Table(appliesTo = "TB_REMESSA")
public class Remessa extends AbstractEntidade<Remessa> {

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private Arquivo arquivo;
	private Arquivo arquivoGeradoProBanco;
	private Batimento batimento;
	private LocalDate dataRecebimento;
	private Instituicao instituicaoOrigem;
	private Instituicao instituicaoDestino;
	private CabecalhoRemessa cabecalho;
	private Rodape rodape;
	private List<Historico> historicos;
	private List<Titulo> titulos;
	private Boolean situacaoBatimento;
	private Boolean situacao; // confirmacoes e retornos

	@Id
	@Column(name = "ID_REMESSA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToMany(mappedBy = "remessa")
	public List<Historico> getHistoricos() {
		return historicos;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_ID")
	public Arquivo getArquivo() {
		return arquivo;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_GERADO_BANCO_ID")
	public Arquivo getArquivoGeradoProBanco() {
		return arquivoGeradoProBanco;
	}
	
	@Column(name = "DATA_RECEBIMENTO")
	public LocalDate getDataRecebimento() {
		return dataRecebimento;
	}

	@ManyToOne
	@JoinColumn(name = "INSTITUICAO_DESTINO_ID")
	public Instituicao getInstituicaoDestino() {
		return instituicaoDestino;
	}

	@OneToOne
	@JoinColumn(name = "CABECALHO_ID")
	public CabecalhoRemessa getCabecalho() {
		return cabecalho;
	}

	@javax.persistence.Transient
	public List<Titulo> getTitulos() {
		return titulos;
	}

	@OneToOne
	@JoinColumn(name = "RODAPE_ID")
	public Rodape getRodape() {
		return rodape;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INSTITUICAO_ORIGEM_ID")
	public Instituicao getInstituicaoOrigem() {
		return instituicaoOrigem;
	}
	
	@OneToOne(optional=true ,mappedBy="remessa", fetch=FetchType.LAZY)
	public Batimento getBatimento(){
		return batimento;
	}

	@Column(name="SITUACAO", nullable=true)
	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public void setBatimento(Batimento batimento) {
		this.batimento = batimento;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public void setHistoricos(List<Historico> historicos) {
		this.historicos = historicos;
	}

	public void setTitulos(List<Titulo> titulos) {
		this.titulos = titulos;
	}

	public void setDataRecebimento(LocalDate dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public void setArquivoGeradoProBanco(Arquivo arquivoGeradoProBanco) {
		this.arquivoGeradoProBanco = arquivoGeradoProBanco;
	}
	
	public void setInstituicaoDestino(Instituicao instituicaoDestino) {
		this.instituicaoDestino = instituicaoDestino;
	}

	public void setInstituicaoOrigem(Instituicao instituicaoOrigem) {
		this.instituicaoOrigem = instituicaoOrigem;
	}

	@Override
	public int compareTo(Remessa entidade) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getId(), entidade.getId());
		return compareToBuilder.toComparison();
	}

	public void setCabecalho(CabecalhoRemessa cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setRodape(Rodape rodape) {
		this.rodape = rodape;
	}

	@Column(name="SITUACAO_BATIMENTO")
	public Boolean getSituacaoBatimento() {
		return situacaoBatimento;
	}

	public void setSituacaoBatimento(Boolean situacaoBatimento) {
		this.situacaoBatimento = situacaoBatimento;
	}
}