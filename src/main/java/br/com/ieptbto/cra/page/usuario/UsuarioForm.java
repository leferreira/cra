package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

@SuppressWarnings("serial")
public class UsuarioForm extends BaseForm<Usuario> {
	
	@SpringBean
	private UsuarioMediator usuarioMediator;

	public UsuarioForm(String id, IModel<Usuario> model) {
		super(id, model);
	}
	
	public UsuarioForm(String id, Usuario colaboradorModel){
		this(id, new CompoundPropertyModel<Usuario>(colaboradorModel));
	}
	
	@Override
	public void onSubmit() {
		Usuario usuario = getModelObject();
		if (getModelObject().getId() != 0) {
			Usuario usuarioSalvo = usuarioMediator.alterar(usuario);
			if (usuarioSalvo != null) {
				info("Usuário alterado com sucesso.");
				//setResponsePage(new DetalharUsuarioPage(usuarioSalvo));
			} else {
				error("Usuario não alterado");
			}
		} else {

			Usuario usuarioSalvo = usuarioMediator.salvar(usuario);
			if (usuarioSalvo != null) {
				info("Usuário criado com sucesso");
				//setResponsePage(new DetalharUsuarioPage(usuarioSalvo));
			} else {
				error("Usuario não criado");
			}
		}
	}
}
