package br.com.ieptbto.cra.page.relatorio.taxaCra;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER, CraRoles.ADMIN })
public class RelatorioTaxaCraPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloRemessa titulo;

	public RelatorioTaxaCraPage() {
		this.titulo = new TituloRemessa();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormulario();
	}

	private void carregarFormulario() {
		RelatorioTaxaCraFormBean relatorioTitulosBean = new RelatorioTaxaCraFormBean();
		RelatorioTaxaCraForm form = new RelatorioTaxaCraForm("form", new CompoundPropertyModel<RelatorioTaxaCraFormBean>(relatorioTitulosBean));
		form.add(new RelatorioTaxaCraInputPanel("relatorioTaxaCraInputPanel",
				new CompoundPropertyModel<RelatorioTaxaCraFormBean>(relatorioTitulosBean)));
		add(form);
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}
}