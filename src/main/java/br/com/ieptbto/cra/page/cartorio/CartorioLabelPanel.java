package br.com.ieptbto.cra.page.cartorio;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class CartorioLabelPanel<Instituicao> extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	Instituicao cartorio;

	public CartorioLabelPanel(String id, IModel<?> model, Instituicao c) {
		super(id, model);
		this.cartorio = c;
		addLabels();
	}

	public void addLabels(){
		add(nomeFantasia());
		add(razaoSocial());
		add(cnpj());
		add(email());
		add(contato());
		add(responsavel());
		add(comarca());
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
	
	public Component comarca(){
		return new Label("municipio.nomeMunicipio");
	}
}
