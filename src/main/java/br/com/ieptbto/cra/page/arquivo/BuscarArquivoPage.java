package br.com.ieptbto.cra.page.arquivo;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
public class BuscarArquivoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private Arquivo arquivo;
	private Form<Arquivo> form;

	public BuscarArquivoPage() {
		this.arquivo = new Arquivo();
		
		form = new Form<Arquivo>("form", getModel());		
		form.add(new BuscarArquivoInputPanel("buscarArquivoInputPanel", getModel()));
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}