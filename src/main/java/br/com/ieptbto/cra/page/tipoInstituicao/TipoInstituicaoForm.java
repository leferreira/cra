package br.com.ieptbto.cra.page.tipoInstituicao;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

@SuppressWarnings("serial")
public class TipoInstituicaoForm extends BaseForm<TipoInstituicao> {

	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;

	public TipoInstituicaoForm(String id, IModel<TipoInstituicao> model) {
		super(id, model);
	}

	public TipoInstituicaoForm(String id, TipoInstituicao colaboradorModel) {
		this(id, new CompoundPropertyModel<TipoInstituicao>(colaboradorModel));
	}

	@Override
	public void onSubmit() {
		
	}
}
