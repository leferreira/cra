package br.com.ieptbto.cra.page.base;

import br.com.ieptbto.cra.mediator.UsuarioFiliadoMediator;
import org.apache.wicket.PageReference;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "serial", "unused" })
public class ComunicadoModal extends WebPage {

	private ModalWindow modalWindow;
	private PageReference modalWindowPage;

	@SpringBean
	UsuarioFiliadoMediator usuarioFiliadoMediator;

	public ComunicadoModal(final PageReference modalWindowPage, final ModalWindow window) {
		this.modalWindowPage = modalWindowPage;
		this.modalWindow = window;

		// carregarIFramePDF();
	}

	// private void carregarIFramePDF() {
	// File file = new File(ConfiguracaoBase.DIRETORIO_BASE + "banner.jpg");
	// IResourceStream resourceStream = new FileResourceStream(file);
	// DocumentInlineFrame iFramePDF = new DocumentInlineFrame("pdf",
	// resourceStream);
	// add(iFramePDF);
	// }
}