package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class UsuarioLabelPanel<Usuario> extends Panel {
	
	Usuario usuario;
	
	public UsuarioLabelPanel(String id, IModel<?> model, Usuario u) {
		super(id, model);
		this.usuario = u;
		addLabels();
	}
	
	public void addLabels(){
		add(campoNome());
		add(campoLogin());
		add(campoEmail());
		add(campoInstituicao());
		add(campoContato());
	}
	
	public Component campoNome(){
		return new Label("nome");
	}
	
	public Component campoLogin(){
		return new Label("login");
	}
	
	public Component campoEmail(){
		return new Label("email");
	}
	
	public Component campoInstituicao(){
		return new Label("instituicao.instituicao");
	}
	
	public Component campoContato(){
		return new Label("contato");
	}
}
