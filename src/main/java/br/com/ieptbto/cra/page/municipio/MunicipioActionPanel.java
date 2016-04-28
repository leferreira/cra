package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Municipio;

/**
 * @author Thasso Araújo
 *
 */
public class MunicipioActionPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	public MunicipioActionPanel(String id, final IModel<Municipio> model) {
		super(id, model);
		add(new Link<Municipio>("edit") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new IncluirMunicipioPage(model.getObject()));
			}
		});
	}
}
