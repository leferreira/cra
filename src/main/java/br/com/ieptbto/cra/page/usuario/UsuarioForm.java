package br.com.ieptbto.cra.page.usuario;

import org.apache.log4j.Logger;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

@SuppressWarnings("serial")
public class UsuarioForm extends BaseForm<Usuario> {

	private static final Logger logger = Logger.getLogger(UsuarioForm.class);
	@SpringBean
	private UsuarioMediator usuarioMediator;

	public UsuarioForm(String id, IModel<Usuario> model) {
		super(id, model);
	}

	public UsuarioForm(String id, Usuario colaboradorModel) {
		this(id, new CompoundPropertyModel<Usuario>(colaboradorModel));
	}

	@Override
	public void onSubmit() {
		
		Usuario usuario = getModelObject();
		try {
			if (usuario.getSenha() == null || usuario.getConfirmarSenha() == null){
				error("A senha deve ser informada!");
			} else if(usuarioMediator.isSenhasIguais(usuario)){
				if (getModelObject().getId() != 0) {
					Usuario usuarioSalvo = usuarioMediator.alterar(usuario);
					setResponsePage(new DetalharUsuarioPage(usuarioSalvo));
				} else {
					if (usuarioMediator.isLoginNaoExiste(usuario)) {
						if(!usuario.isStatus())
							usuario.setStatus(true);
						Usuario usuarioSalvo = usuarioMediator.salvar(usuario);
						setResponsePage(new DetalharUsuarioPage(usuarioSalvo));
					} else 
						error("Usuário não criado. O login já existe!");
				}
			}else{
				error("As senhas não são iguais!");
			}
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
		}
		
	}
}
