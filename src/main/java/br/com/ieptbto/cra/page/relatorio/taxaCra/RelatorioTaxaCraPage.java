package br.com.ieptbto.cra.page.relatorio.taxaCra;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioTaxaCraPage extends BasePage<Arquivo> {
	
	/***/
	private static final long serialVersionUID = 1L;
	private Arquivo arquivo;

	public RelatorioTaxaCraPage() {
		this.arquivo = new Arquivo();
		
		carregarFormulario();
	}

	private void carregarFormulario() {
		Form<Arquivo> form = new Form<Arquivo>("form"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				// TODO Auto-generated method stub
				super.onSubmit();
			}
		};
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}