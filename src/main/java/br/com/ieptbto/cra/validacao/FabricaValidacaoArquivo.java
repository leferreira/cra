package br.com.ieptbto.cra.validacao;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.validacao.regra.FabricaRegrasDeEntrada;

/**
 * 
 * @author Lefer
 * 
 *         Fábria de validações do arquivo
 *
 */
@Service
public class FabricaValidacaoArquivo {

	@Autowired
	private FabricaRegrasDeEntrada fabricaRegrasDeEntrada;
	private File arquivo;
	private Usuario usuario;
	private List<Exception> erros;

	public void validar(File arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		this.erros = erros;
		validarEntradaDoArquivo();

	}

	private void validarEntradaDoArquivo() {
		fabricaRegrasDeEntrada.validar(getArquivo(), getUsuario(), getErros());
	}

	public File getArquivo() {
		return arquivo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public List<Exception> getErros() {
		return erros;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

}
