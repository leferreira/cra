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
	UsuarioMediator usuarioMediator;

	public UsuarioForm(String id, IModel<Usuario> model) {
		super(id, model);
	}

	public UsuarioForm(String id, Usuario colaboradorModel) {
		this(id, new CompoundPropertyModel<Usuario>(colaboradorModel));
	}

	@Override
	public void onSubmit() {
		
		Usuario usuario = getModelObject();
		if(usuarioMediator.isSenhasIguais(usuario)){
			if (getModelObject().getId() != 0) {
				Usuario usuarioSalvo = usuarioMediator.alterar(usuario);
				if (usuarioSalvo != null) {
					info("Usuário alterado com sucesso.");
				} else {
					error("Usuário não alterado");
				}
			} else {
				if (usuarioMediator.isLoginNaoExiste(usuario)) {
					if(!usuario.isStatus()){
						usuario.setStatus(true);
					}
					Usuario usuarioSalvo = usuarioMediator.salvar(usuario);
					if (usuarioSalvo != null) {
						info("Usuário criado com sucesso");
					} else {
						error("Usuário não criado");
					}
				} else {
					error("Usuário não criado. O login já existe!");
				}
			}
			usuario=null;
		}else{
			error("As senhas não são iguais!");
		}
	}
}
