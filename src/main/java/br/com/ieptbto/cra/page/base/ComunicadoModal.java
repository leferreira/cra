package br.com.ieptbto.cra.page.base;

import java.io.File;

import org.apache.wicket.PageReference;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.iframe.DocumentInlineFrame;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings( {"serial","unused"} )
public class ComunicadoModal extends WebPage {
	
	private ModalWindow modalWindow;
	private PageReference modalWindowPage;
	
	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;
	
	public ComunicadoModal(final PageReference modalWindowPage, final ModalWindow window) {
        this.modalWindowPage = modalWindowPage;
        this.modalWindow = window;

        carregarIFramePDF();
    }

	private void carregarIFramePDF() {
		File file = new File(ConfiguracaoBase.DIRETORIO_BASE + "Comunicado Greve 2015 - Of.pdf");
		IResourceStream resourceStream = new FileResourceStream(file);
		DocumentInlineFrame iFramePDF = new DocumentInlineFrame("pdf", resourceStream);
		add(iFramePDF);
	}
}