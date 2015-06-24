package br.com.ieptbto.cra.page.usuario;

import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

/**
 * @author Thasso Araújo
 *
 */
public class UsuarioForm extends BaseForm<Usuario> {

	/***/
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(UsuarioForm.class);
	
	@SpringBean
	UsuarioMediator usuarioMediator;

	public UsuarioForm(String id, IModel<Usuario> model) {
		super(id, model);
	}

	@Override
	public void onSubmit() {
		
		Usuario usuario = getModelObject();
		
		try {
				if (getModelObject().getId() != 0) {
					Usuario usuarioSalvo = usuarioMediator.alterar(usuario);
					setResponsePage(new DetalharUsuarioPage(usuarioSalvo));
				} else {
					if(usuarioMediator.isSenhasIguais(usuario)){
						if (usuarioMediator.isLoginNaoExiste(usuario)) {
							Usuario usuarioSalvo = usuarioMediator.salvar(usuario);
							setResponsePage(new DetalharUsuarioPage(usuarioSalvo));
						} else 
							error("Usuário não criado. O login já existe!");
					}else{
						error("As senhas não são iguais!");
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
