package br.com.ieptbto.cra.page.convenio.layout;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.LayoutFiliado;

@SuppressWarnings("serial")
public class LayoutEmpresaForm extends Form<LayoutFiliado> {

	public LayoutEmpresaForm(String id, IModel<LayoutFiliado> model) {
		super(id, model);
	}

}
