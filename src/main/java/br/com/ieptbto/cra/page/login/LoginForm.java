package br.com.ieptbto.cra.page.login;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;
import br.com.ieptbto.cra.page.base.HomePage;

@SuppressWarnings("serial")
public class LoginForm extends BaseForm<Usuario> {

    @SpringBean
    private UsuarioMediator usuarioMediator;

    public LoginForm(String id, IModel<Usuario> model) {
	super(id, model);
    }

    public LoginForm(String id, Usuario userModel) {
	this(id, new CompoundPropertyModel<Usuario>(userModel));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onSubmit() {
	try {
	    Usuario usuario = usuarioMediator.autenticar(getModelObject().getLogin(), getModelObject().getSenha());

	    if (usuario != null) {
		getSession().setUser(usuario);
		setResponsePage(new HomePage());
	    }
	} catch (Exception ex) {
	    transError(ex.getMessage());
	}
    }
}
