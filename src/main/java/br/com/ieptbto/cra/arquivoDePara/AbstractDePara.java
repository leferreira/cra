package br.com.ieptbto.cra.arquivoDePara;

import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
public abstract class AbstractDePara {

	protected PadraoArquivoDePara padrao;

	public PadraoArquivoDePara getPadrao() {
		return this.padrao;
	}

	public abstract List<?> processar(FileUpload file);
}
