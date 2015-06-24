package br.com.ieptbto.cra.page.instituicao;

import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

/**
 * @author Thasso Araújo
 *
 */
public class InstituicaoForm extends BaseForm<Instituicao> {

	/***/
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(InstituicaoForm.class);
	
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;

	public InstituicaoForm(String id, IModel<Instituicao> model) {
		super(id, model);
	}

	@Override
	public void onSubmit() {
		Instituicao instituicao = getModelObject();
		
		try {
			if (getModelObject().getId() != 0) {
				Instituicao instituicaoSalvo = instituicaoMediator.alterar(instituicao);
				setResponsePage(new DetalharInstituicaoPage(instituicaoSalvo));
			}else{
				if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
					Municipio municipio = municipioMediator.buscarMunicipio("Palmas");
					instituicao.setMunicipio(municipio);
					Instituicao instituicaoSalvo = instituicaoMediator.salvar(instituicao);
					setResponsePage(new DetalharInstituicaoPage(instituicaoSalvo));
				} else {
					error("Instituição não criada, pois já existe!");
				}
			}	
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			error("Não foi possível realizar esta operação! Entre em contato com a CRA!");
		}
	}
}
