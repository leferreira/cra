package br.com.ieptbto.cra.page.usuario;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class UsuarioForm extends BaseForm<Usuario> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	UsuarioMediator usuarioMediator;

	public UsuarioForm(String id, IModel<Usuario> model) {
		super(id, model);

	}

	@Override
	protected void onSubmit() {
		Usuario usuario = getModelObject();

		try {
			if (getModelObject().getId() != 0) {
				if (usuario.getSenha() != null) {
					usuario.setSenha(Usuario.cryptPass(usuario.getSenha()));
				} else {
					usuario.setSenha(getModel().getObject().getSenhaAtual());
				}
				usuarioMediator.alterar(usuario);
				setResponsePage(new ListaUsuarioPage("Os dados do usuário foram salvos com sucesso!"));
			} else {
				if (usuarioMediator.isSenhasIguais(usuario)) {
					if (usuarioMediator.isLoginNaoExiste(usuario)) {
						usuarioMediator.salvar(usuario);
						setResponsePage(new ListaUsuarioPage("Os dados do usuário foram salvos com sucesso!"));
					} else
						throw new InfraException("Usuário não criado. O login já existe!");
				} else {
					throw new InfraException("As senhas não são iguais!");
				}
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
