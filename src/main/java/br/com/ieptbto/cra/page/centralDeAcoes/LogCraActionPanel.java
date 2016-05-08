package br.com.ieptbto.cra.page.centralDeAcoes;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.LogCra;

/**
 * @author Thasso Araújo
 *
 */
public class LogCraActionPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	public LogCraActionPanel(String id, final IModel<LogCra> model) {
		super(id, model);
		add(new Link<LogCra>("descricaoGeral") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
			}
		});
	}
}
