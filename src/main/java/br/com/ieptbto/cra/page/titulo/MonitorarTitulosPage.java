package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;

@AuthorizeInstantiation(value = "USER")
public class MonitorarTitulosPage extends BasePage<Titulo>{

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private TituloMediator tituloMediator;
	private Titulo titulo;
	private Form<Titulo> form;

	public MonitorarTitulosPage() {
		this.titulo = new Titulo();
		
		form = new Form<Titulo>("form", getModel());		
		form.add(new MonitorarTitulosInputPanel("titulosInputPanel", getModel()));
		add(form);
	}

	@Override
	protected IModel<Titulo> getModel() {
		return new CompoundPropertyModel<Titulo>(titulo);
	}
}
