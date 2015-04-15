package br.com.ieptbto.cra.page.instituicao;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class InstituicaoLabelPanel<Instituicao> extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	Instituicao instituicao;

	public InstituicaoLabelPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		addLabels();
	}

	public void addLabels(){
		add(nomeFantasia());
		add(razaoSocial());
		add(cnpj());
		add(email());
		add(contato());
		add(responsavel());
	}

	public Component nomeFantasia(){
		return new Label("nomeFantasia");
	}
	
	public Component razaoSocial(){
		return new Label("razaoSocial");
	}
	
	public Component cnpj(){
		return new Label("cnpj");
	}
	
	public Component email(){
		return new Label("email");
	}
	
	public Component contato(){
		return new Label("contato");
	}
	
	public Component responsavel(){
		return new Label("responsavel");
	}
}
