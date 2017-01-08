package br.com.ieptbto.cra.page.titulo.historico;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloRemessa tituloRemessa;

	public HistoricoPage(TituloRemessa titulo) {
		this.tituloRemessa = titulo;

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(arquivosOcorrenciasPanel());
		add(informacoesTituloPanel());
		add(acoesTituloMenu());
	}

	private Panel informacoesTituloPanel() {
		return new InformacoesTituloPanel("informacoesTituloPanel", getModel(), getUser());
	}

	private Panel arquivosOcorrenciasPanel() {
		return new OcorrenciasTituloPanel("ocorrenciasTituloPanel", getModel());
	}

	private WebMarkupContainer acoesTituloMenu() {
		return new AcoesTituloMenu("acoesTituloMenu", getModel(), getUser());
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}