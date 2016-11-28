package br.com.ieptbto.cra.page.instituicao;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

public class InstituicaoForm extends BaseForm<Instituicao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	public InstituicaoForm(String id, IModel<Instituicao> model) {
		super(id, model);
	}

	@Override
	public void onSubmit() {
		Instituicao instituicao = getModelObject();

		try {
			if (getModelObject().getId() != 0) {
				instituicao.setVersao(instituicao.getVersao() + 1);
				instituicaoMediator.alterar(instituicao);
				setResponsePage(new ListaInstituicaoPage("Os dados da instituição foram alterados com sucesso !"));
			} else {
				if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
					instituicaoMediator.salvar(instituicao);
					setResponsePage(new ListaInstituicaoPage("Os dados da instituição foram salvos com sucesso !"));
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
