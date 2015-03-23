package br.com.ieptbto.cra.entidade;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
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
	private Date dataRecebimento;
	private Instituicao instituicaoEnvio;
	private Instituicao instituicaoDestino;
	private TipoArquivo tipoArquivo;
	private Usuario usuarioEnvio;
	private Usuario usuarioRecebe;
	private StatusArquivo statusArquivo;
	private List<Titulo> titulos;
	private List<Cabecalho> cabecalhos;
	private List<Rodape> rodapes;

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

	@Column(name = "DATA_RECEBIMENTO", columnDefinition = "timestamp without time zone NOT NULL")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	@OneToOne
	@JoinColumn(name = "INSTITUICAO_ENVIO_ID")
	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	@OneToOne
	@JoinColumn(name = "INSTITUICAO_DESTINO_ID")
	public Instituicao getInstituicaoDestino() {
		return instituicaoDestino;
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

	@OneToOne
	@JoinColumn(name = "USUARIO_RECEBE_ID")
	public Usuario getUsuarioRecebe() {
		return usuarioRecebe;
	}

	@OneToMany(mappedBy = "arquivo", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.LOCK })
	public List<Titulo> getTitulos() {
		return titulos;
	}

	@OneToMany(mappedBy = "arquivo", fetch = FetchType.LAZY)
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.LOCK })
	public List<Cabecalho> getCabecalhos() {
		return cabecalhos;
	}

	@OneToMany(mappedBy = "arquivo", fetch = FetchType.LAZY)
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.LOCK })
	public List<Rodape> getRodapes() {
		return rodapes;
	}

	public void setTitulos(List<Titulo> titulos) {
		this.titulos = titulos;
	}

	public void setCabecalhos(List<Cabecalho> cabecalhos) {
		this.cabecalhos = cabecalhos;
	}

	public void setRodapes(List<Rodape> rodapes) {
		this.rodapes = rodapes;
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

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

	public void setInstituicaoDestino(Instituicao instituicaoDestino) {
		this.instituicaoDestino = instituicaoDestino;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setUsuarioEnvio(Usuario usuarioEnvio) {
		this.usuarioEnvio = usuarioEnvio;
	}

	public void setUsuarioRecebe(Usuario usuarioRecebe) {
		this.usuarioRecebe = usuarioRecebe;
	}

	public void setStatusArquivo(StatusArquivo statusArquivo) {
		this.statusArquivo = statusArquivo;
	}

	@Override
	public int compareTo(Arquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
