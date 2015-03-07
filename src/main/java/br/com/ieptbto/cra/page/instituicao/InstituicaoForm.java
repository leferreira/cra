package br.com.ieptbto.cra.page.instituicao;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

@SuppressWarnings("serial")
public class InstituicaoForm extends BaseForm<Instituicao> {

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	public InstituicaoForm(String id, IModel<Instituicao> model) {
		super(id, model);
	}

	public InstituicaoForm(String id, Instituicao colaboradorModel) {
		this(id, new CompoundPropertyModel<Instituicao>(colaboradorModel));
	}

	@Override
	public void onSubmit() {
		Instituicao instituicao = getModelObject();
		if (getModelObject().getId() != 0) {
			Instituicao instituicaoSalvo = instituicaoMediator
					.alterar(instituicao);
			if (instituicaoSalvo != null) {
				info("Instituição alterada com sucesso!");
			} else {
				error("Instituição não alterada!");
			}
		} else {
			if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
				if(!instituicao.isSituacao()){
					instituicao.setSituacao(true);
				}
				Instituicao instituicaoSalvo = instituicaoMediator
						.salvar(instituicao);
				if (instituicaoSalvo != null) {
					info("Instituição criada com sucesso!");
				} else {
					error("Instituição não criada!");
				}
			} else {
				error("Instituição não criada, pois já existe!");
			}
		}
	}
}
