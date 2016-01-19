package br.com.ieptbto.cra.page.layoutPersonalizado;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.LayoutFiliado;

@SuppressWarnings("serial")
public class LayoutEmpresaForm extends Form<LayoutFiliado> {

	LayoutEmpresaInputPanel input;

	public LayoutEmpresaForm(String id, IModel<LayoutFiliado> model, LayoutEmpresaInputPanel inputPanel) {
		super(id, model);
		this.input = inputPanel;
	}

	@Override
	protected void onSubmit() {
		getInput();

	}

	public LayoutEmpresaInputPanel getInput() {
		return input;
	}

	public void setInput(LayoutEmpresaInputPanel input) {
		this.input = input;
	}

}
