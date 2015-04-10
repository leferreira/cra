package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;

@AuthorizeInstantiation(value = "USER")
public class MonitorarTitulosPage extends BasePage<TituloRemessa>{

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private TituloMediator tituloMediator;
	private TituloRemessa titulo;
	private Form<TituloRemessa> form;

	public MonitorarTitulosPage() {
		this.titulo = new TituloRemessa();
		
		form = new Form<TituloRemessa>("form", getModel());		
		form.add(new MonitorarTitulosInputPanel("titulosInputPanel", getModel()));
		add(form);
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}
}
