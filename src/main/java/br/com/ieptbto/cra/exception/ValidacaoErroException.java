package br.com.ieptbto.cra.exception;

import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRegistro;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class ValidacaoErroException extends RuntimeException {

	private String arquivo;
	private TipoArquivoEnum tipoArquivo;
	private TipoRegistro tipoRegistro;
	private Titulo titulo;
	private int linha;
	private Erro erro;
	private String mensagem;

	public ValidacaoErroException(String arquivo, TipoArquivoEnum tipoArquivo, TipoRegistro tipoRegistro, Titulo titulo, int linha,
	        Erro erro, String mensagem, Throwable cause) {
		super(erro.getMensagemErro(), cause);
		this.arquivo = arquivo;
		this.tipoArquivo = tipoArquivo;
		this.tipoRegistro = tipoRegistro;
		this.titulo = titulo;
		this.linha = linha;
		this.erro = erro;
		this.mensagem = mensagem;
	}

	public ValidacaoErroException(Erro erro, Throwable cause) {
		super(erro.getMensagemErro(), cause);
		this.erro = erro;
	}

	public ValidacaoErroException(String mensagem, Throwable cause) {
		super(mensagem, cause);
		this.mensagem = mensagem;
	}

	public ValidacaoErroException(String arquivo, Erro erro, String complementoMsg) {
		super(erro.getMensagemErro() + " " + complementoMsg);
		this.mensagem = erro.getMensagemErro() + " " + complementoMsg;
		this.arquivo = arquivo;
	}

	public ValidacaoErroException(String arquivo, Erro erro, int linha) {
		super(erro.getMensagemErro());
		this.mensagem = erro.getMensagemErro();
		this.arquivo = arquivo;
		this.linha = linha;

	}

	public ValidacaoErroException(String arquivo, String mensagem, Throwable cause) {
		super(mensagem, cause);
		this.arquivo = arquivo;
		this.mensagem = mensagem;
	}

	public String getArquivo() {
		return arquivo;
	}

	public TipoArquivoEnum getTipoArquivo() {
		return tipoArquivo;
	}

	public TipoRegistro getTipoRegistro() {
		return tipoRegistro;
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public int getLinha() {
		return linha;
	}

	public Erro getErro() {
		return erro;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public void setTipoArquivo(TipoArquivoEnum tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setTipoRegistro(TipoRegistro tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

	public void setErro(Erro erro) {
		this.erro = erro;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
