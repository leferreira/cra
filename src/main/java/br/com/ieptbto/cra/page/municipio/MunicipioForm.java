package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

/**
 * @author Thasso Araújo
 *
 */
public class MunicipioForm extends BaseForm<Municipio> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	MunicipioMediator municipioMediator;

	public MunicipioForm(String id, IModel<Municipio> model) {
		super(id, model);
	}

	@Override
	public void onSubmit() {
		Municipio municipio = getModelObject();
		if (getModelObject().getId() != 0) {
			Municipio municipioSalvo = municipioMediator.alterarMunicipio(municipio);
			if (municipioSalvo != null) {
				setResponsePage(new ListaMunicipioPage("Os dados do município foram salvos com sucesso!"));
			} else {
				error("Município não alterado");
			}
		} else {
			if (municipioMediator.isMunicipioNaoExiste(municipio)) {
				if (!municipio.isSituacao()) {
					municipio.setSituacao(true);
				}
				Municipio municipioSalvo = municipioMediator.adicionarMunicipio(municipio);
				if (municipioSalvo != null) {
					setResponsePage(new ListaMunicipioPage("Os dados do município foram salvos com sucesso!"));
				} else {
					error("Município não criado");
				}
			} else {
				error("Município não criado, pois já existe!");
			}
		}
		municipio = null;
	}
}