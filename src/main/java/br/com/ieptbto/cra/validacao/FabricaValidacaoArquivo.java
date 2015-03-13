package br.com.ieptbto.cra.validacao;

import java.io.File;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.validacao.regra.FabricaRegrasDeEntrada;

/**
 * 
 * @author Lefer
 * 
 *         Fábria de validações do arquivo
 *
 */
public class FabricaValidacaoArquivo {

	private File arquivo;
	private Usuario usuario;

	public FabricaValidacaoArquivo(File arquivo, Usuario usuario) {
		this.arquivo = arquivo;
		this.usuario = usuario;
	}

	public void validar() {
		validarEntradaDoArquivo();

	}

	private void validarEntradaDoArquivo() {
		FabricaRegrasDeEntrada.validar(getArquivo(), getUsuario());
	}

	public File getArquivo() {
		return arquivo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
