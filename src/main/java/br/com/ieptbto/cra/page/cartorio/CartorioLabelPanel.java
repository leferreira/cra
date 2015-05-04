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
		add(endereco());
	}

	private Component nomeFantasia(){
		return new Label("nomeFantasia");
	}
	
	private Component razaoSocial(){
		return new Label("razaoSocial");
	}
	
	private Component cnpj(){
		return new Label("cnpj");
	}
	
	private Component email(){
		return new Label("email");
	}
	
	private Component contato(){
		return new Label("contato");
	}
	
	private Component responsavel(){
		return new Label("responsavel");
	}
	
	private Component endereco(){
		return new Label("endereco");
	}
	
	private Component comarca(){
		return new Label("municipio.nomeMunicipio");
	}
}
