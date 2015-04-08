package br.com.ieptbto.cra.page.instituicao;

import org.apache.log4j.Logger;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

@SuppressWarnings("serial")
public class InstituicaoForm extends BaseForm<Instituicao> {

	private static final Logger logger = Logger.getLogger(InstituicaoForm.class);
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;

	public InstituicaoForm(String id, IModel<Instituicao> model) {
		super(id, model);
	}

	public InstituicaoForm(String id, Instituicao colaboradorModel) {
		this(id, new CompoundPropertyModel<Instituicao>(colaboradorModel));
	}

	@SuppressWarnings("unused")
	@Override
	public void onSubmit() {
		Instituicao instituicao = getModelObject();
		
		try {
			if (getModelObject().getId() != 0) {
				Instituicao instituicaoSalvo = instituicaoMediator.alterar(instituicao);
				info("Dados alterados com sucesso!.");
			}else{
				if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
					Municipio municipio = municipioMediator.buscarMunicipio("Palmas");
					instituicao.setMunicipio(municipio);
					Instituicao instituicaoSalvo = instituicaoMediator.salvar(instituicao);
					info("Dados salvos com sucesso!.");
				} else {
					error("Instituição não criada, pois já existe!");
				}
			}	
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			error("Não foi possível realizar esta operação! \n Entre em contato com a CRA!");
		}
	}
}
