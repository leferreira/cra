package br.com.ieptbto.cra.entidade;

import org.joda.time.DateTime;

/**
 * 
 * @author Lefer
 *
 */
public class Arquivo extends AbstractEntidade<Arquivo> {

	/****/
	private static final long serialVersionUID = 8563214L;
	private int id;
	private String nomeArquivo;

	private DateTime comentario;
	private String path;
	private DateTime dataEnvio;
	private String dataRecebimento;
	private Instituicao instituicaoEnvio;
	private Instituicao instituicaoDestino;
	private TipoArquivo tipoArquivo;
	private Usuario usuarioEnvio;
	private Usuario usuarioRecebe;
	private StatusArquivo statusArquivo;

	public int getId() {
		return id;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public DateTime getComentario() {
		return comentario;
	}

	public String getPath() {
		return path;
	}

	public DateTime getDataEnvio() {
		return dataEnvio;
	}

	public String getDataRecebimento() {
		return dataRecebimento;
	}

	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	public Instituicao getInstituicaoDestino() {
		return instituicaoDestino;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public Usuario getUsuarioEnvio() {
		return usuarioEnvio;
	}

	public Usuario getUsuarioRecebe() {
		return usuarioRecebe;
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

	public void setComentario(DateTime comentario) {
		this.comentario = comentario;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setDataEnvio(DateTime dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setDataRecebimento(String dataRecebimento) {
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
