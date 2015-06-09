package br.com.ieptbto.cra.arquivoDePara;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import br.com.ieptbto.cra.entidade.ArquivoDePara;
import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractDePara<T extends AbstractDePara> {

	protected PadraoArquivoDePara padrao;

	public PadraoArquivoDePara getPadrao() {
		return this.padrao;
	}

	public abstract ArquivoDePara processar(FileUpload file);
}
