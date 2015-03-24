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
 * 
 * @author Lefer
 *
 */
@Entity
@Audited
@Table(name = "TB_ARQUIVO")
@org.hibernate.annotations.Table(appliesTo = "TB_ARQUIVO")
public class Arquivo extends AbstractEntidade<Arquivo> {

	/****/
	private static final long serialVersionUID = 8563214L;
	private int id;
	private String nomeArquivo;
	private String comentario;
	private String path;
	private Date dataEnvio;
	private List<Remessa> remessas;
	private Instituicao instituicaoEnvio;
	private TipoArquivo tipoArquivo;
	private Usuario usuarioEnvio;
	private StatusArquivo statusArquivo;

	@Id
	@Column(name = "ID_ARQUIVO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_ARQUIVO", length = 13)
	public String getNomeArquivo() {
		return nomeArquivo;
	}

	@Column(name = "COMENTARIO", columnDefinition = "TEXT")
	public String getComentario() {
		return comentario;
	}

	@Column(name = "PATH", length = 255)
	public String getPath() {
		return path;
	}

	@Column(name = "DATA_ENVIO", columnDefinition = "timestamp without time zone NOT NULL")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataEnvio() {
		return dataEnvio;
	}

	@OneToMany(mappedBy = "arquivo", fetch = FetchType.LAZY)
	public List<Remessa> getRemessas() {
		return remessas;
	}

	@ManyToOne
	@JoinColumn(name = "INSTITUICAO_ENVIO_ID")
	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	@OneToOne
	@JoinColumn(name = "TIPO_ARQUIVO_ID")
	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	@OneToOne
	@JoinColumn(name = "USUARIO_ENVIO_ID")
	public Usuario getUsuarioEnvio() {
		return usuarioEnvio;
	}

	public StatusArquivo getStatusArquivo() {
		return statusArquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setUsuarioEnvio(Usuario usuarioEnvio) {
		this.usuarioEnvio = usuarioEnvio;
	}

	public void setStatusArquivo(StatusArquivo statusArquivo) {
		this.statusArquivo = statusArquivo;
	}

	public void setRemessas(List<Remessa> remessas) {
		this.remessas = remessas;
	}

	public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

	@Override
	public int compareTo(Arquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
