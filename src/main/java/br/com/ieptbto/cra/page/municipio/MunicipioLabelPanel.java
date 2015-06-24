package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Municipio;

/**
 * @author Thasso Araújo
 *
 */
public class MunicipioLabelPanel extends Panel {
	
	/***/
	private static final long serialVersionUID = 1L;
	Municipio municipio;
	
	public MunicipioLabelPanel(String id, IModel<?> model, Municipio m) {
		super(id, model);
		this.municipio = m;
		add(nomeMunicipio());
		add(uf());
		add(codigoIBGE());
	}
	
	public Component nomeMunicipio(){
		return new Label("nomeMunicipio", new Model<String>(municipio.getNomeMunicipio()));
	}
	
	public Component uf(){
		return new Label("uf", new Model<String>(municipio.getUf()));
	}
	
	public Component codigoIBGE(){
		return new Label("codigoIBGE", new Model<String>(municipio.getCodigoIBGE().toString()));
	}
}
