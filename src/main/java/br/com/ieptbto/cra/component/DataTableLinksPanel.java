package br.com.ieptbto.cra.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.titulo.historico.HistoricoPage;

/**
 * @author Thasso Araújo
 *
 */
public class DataTableLinksPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	public DataTableLinksPanel(String id, final Integer idTituloRemessa, final String nomeDevedor) {
		super(id);

		Link<Remessa> linkGenerate = new Link<Remessa>("link") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new HistoricoPage(idTituloRemessa));
			}
		};
		linkGenerate.add(new Label("nomeLink", nomeDevedor));
		this.add(linkGenerate);
	}

	public DataTableLinksPanel(String id, final String nomeArquivoRemessa, final Integer idRemessa) {
		super(id);

		Link<Remessa> linkGenerate = new Link<Remessa>("link") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new TitulosArquivoPage(idRemessa));
			}
		};
		linkGenerate.add(new Label("nomeLink", nomeArquivoRemessa));
		this.add(linkGenerate);
	}
}
