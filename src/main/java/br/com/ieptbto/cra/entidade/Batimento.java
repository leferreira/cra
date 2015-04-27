package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDateTime;

import br.com.ieptbto.cra.enumeration.SituacaoBatimento;

@Entity
@Audited
@Table(name = "TB_BATIMENTO")
@org.hibernate.annotations.Table(appliesTo = "TB_BATIMENTO")
public class Batimento extends AbstractEntidade<Batimento>{

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private LocalDateTime dataBatimento;
	private Remessa remessa;
	private BigDecimal valorTotal;
	private SituacaoBatimento situacaoBatimento;

	@Id
	@Column(name = "ID_BATIMENTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	
	@Column(name = "DATA_BATIMENTO")	
	public LocalDateTime getDataBatimento() {
		return dataBatimento;
	}
	
	@OneToOne
	@JoinColumn(name = "REMESSA_ID")
	public Remessa getRemessa() {
		return remessa;
	}

	@Column(name = "SITUACAO_BATIMENTO")	
	@Enumerated(EnumType.STRING)
	public SituacaoBatimento getSituacaoBatimento() {
		return situacaoBatimento;
	}
	
	@Column(name="VALOR_TOTAL")
	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDataBatimento(LocalDateTime dataBatimento) {
		this.dataBatimento = dataBatimento;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setSituacaoBatimento(SituacaoBatimento situacaoBatimento) {
		this.situacaoBatimento = situacaoBatimento;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Override
	public int compareTo(Batimento entidade) {
		CompareToBuilder compare = new CompareToBuilder();
		compare.append(entidade.getId(), this.getId());
		compare.append(entidade.getDataBatimento(), this.getDataBatimento());
		compare.append(entidade.getRemessa(), this.getRemessa());
		return compare.toComparison();
	}

}