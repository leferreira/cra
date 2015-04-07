package br.com.ieptbto.cra.conversor.arquivo;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.exception.ArquivoException;

/**
 * 
 * @author Lefer
 *
 */
public abstract class AbstractFabricaDeArquivo {
	protected static final Logger logger = Logger.getLogger(AbstractFabricaDeArquivo.class);
	protected File arquivoFisico;
	protected Arquivo arquivo;
	protected List<ArquivoException> erros;

	public abstract Arquivo converter();

	public abstract void validar();

	public File getArquivoFisico() {
		return arquivoFisico;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivoFisico(File arquivoFisico) {
		this.arquivoFisico = arquivoFisico;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public List<ArquivoException> getErros() {
		return erros;
	}

	public void setErros(List<ArquivoException> erros) {
		this.erros = erros;
	}
}
