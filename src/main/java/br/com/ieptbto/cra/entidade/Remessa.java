package br.com.ieptbto.cra.entidade;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_REMESSA")
@org.hibernate.annotations.Table(appliesTo = "TB_REMESSA")
public class Remessa extends AbstractEntidade<Remessa>{

	/***/
	private static final long serialVersionUID = 1L;

	private int id;
	private Arquivo arquivo;
	private Date dataRecebimento;
	private Usuario usuarioRecebe;
	private Instituicao instituicaoDestino;
	private List<Titulo> titulos;

	@Id
	@Column(name = "ID_REMESSA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "ARQUIVO_ID")
	public Arquivo getArquivo() {
		return arquivo;
	}
	
	@Column(name = "DATA_ENVIO", columnDefinition = "timestamp without time zone NOT NULL")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	@OneToOne
	public Usuario getUsuarioRecebe() {
		return usuarioRecebe;
	}

	@ManyToOne
	@JoinColumn(name = "INSTITUICAO_DESTINO_ID")
	public Instituicao getInstituicaoDestino() {
		return instituicaoDestino;
	}

	@OneToMany(mappedBy = "remessa", fetch = FetchType.LAZY)
	public List<Titulo> getTitulos() {
		return titulos;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public void setTitulos(List<Titulo> titulos) {
		this.titulos = titulos;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public void setUsuarioRecebe(Usuario usuarioRecebe) {
		this.usuarioRecebe = usuarioRecebe;
	}

	public void setInstituicaoDestino(Instituicao instituicaoDestino) {
		this.instituicaoDestino = instituicaoDestino;
	}
	
	@Override
	public int compareTo(Remessa entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
