package br.com.ieptbto.cra.page.convenio.layout;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
public class IncluirLayoutEmpresaPage extends BasePage<LayoutFiliado> {

	private LayoutFiliado layoutEmpresa;

	public IncluirLayoutEmpresaPage() {
		layoutEmpresa = new LayoutFiliado();

		setComponent();
	}

	private void setComponent() {
		LayoutEmpresaForm form = new LayoutEmpresaForm("form", getModel());

		LayoutEmpresaInputPanel inputPanel = new LayoutEmpresaInputPanel("layoutEmpresaInputPanel", getModel());
		form.add(inputPanel);

		add(form);

	}

	@Override
	protected IModel<LayoutFiliado> getModel() {
		return new CompoundPropertyModel<LayoutFiliado>(layoutEmpresa);
	}

}
