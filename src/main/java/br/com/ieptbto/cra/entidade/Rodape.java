package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

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
	private Arquivo arquivo;
	private BigDecimal valorTotalTitulos;
	private List<Titulo> titulos;

	@Override
	@Id
	@Column(name = "ID_RODAPE", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_ID")
	public Arquivo getArquivo() {
		return arquivo;
	}

	@OneToMany(mappedBy = "rodape")
	public List<Titulo> getTitulos() {
		return titulos;
	}

	@Column(name = "VALOR_REMESSA", precision = 8, scale = 2)
	public BigDecimal getValorTotalTitulos() {
		return valorTotalTitulos;
	}

	public void setValorTotalTitulos(BigDecimal valorTotalTitulos) {
		this.valorTotalTitulos = valorTotalTitulos;
	}

	public void setTitulos(List<Titulo> titulos) {
		this.titulos = titulos;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(Rodape entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
