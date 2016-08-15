package br.com.ieptbto.cra.page.filiado;

import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
public class SetorFiliadoInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	private SetorFiliado setorFiliado;
	private Filiado filiado;
	private List<SetorFiliado> setores;

	public SetorFiliadoInputPanel(String id, IModel<Filiado> model, List<SetorFiliado> setores) {
		super(id, model);
		this.setores = setores;
		this.filiado = model.getObject();
		this.setorFiliado = new SetorFiliado();
		this.setorFiliado.setSituacaoAtivo(true);
		
		carregarComponentes();
	}

	private void carregarComponentes() {
		Form<SetorFiliado> formSetores = new Form<SetorFiliado>("formSetorFiliado",  new CompoundPropertyModel<SetorFiliado>(setorFiliado)){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				SetorFiliado setor = getModelObject();
				
				try {
					getSetores().add(setor);		
					
					SetorFiliado novoSetor = new SetorFiliado();
					setSetorFiliado(novoSetor);
					getModel().setObject(novoSetor);
					
				} catch (InfraException ex) {
					error(ex.getMessage());
				}
			}
		};
		formSetores.add(campoDescricao());
		formSetores.add(new Button("adicionarSetor"));
		add(formSetores);
	}
	
	private TextField<String> campoDescricao() {
		TextField<String> fieldNome = new TextField<String>("descricao");
		fieldNome.setLabel(new Model<String>("Descrição"));
		return fieldNome;
	}
	
	public Filiado getFiliado() {
		return filiado;
	}
	
	public List<SetorFiliado> getSetores() {
		return setores;
	}

	public void setSetorFiliado(SetorFiliado setorFiliado) {
		this.setorFiliado = setorFiliado;
	}
}
