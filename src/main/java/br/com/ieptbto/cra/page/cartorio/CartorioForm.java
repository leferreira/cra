package br.com.ieptbto.cra.page.cartorio;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.InstituicaoMunicipio;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMunicipioMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

@SuppressWarnings("serial")
public class CartorioForm extends BaseForm<Instituicao> {

	@SpringBean
	TipoInstituicaoMediator tipoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	InstituicaoMunicipioMediator imMediator;
 
	public CartorioForm(String id, IModel<Instituicao> model) {
		super(id, model);
	}

	public CartorioForm(String id, Instituicao colaboradorModel) {
		this(id, new CompoundPropertyModel<Instituicao>(colaboradorModel));
	}

	@Override
	public void onSubmit() {

		Instituicao instituicao = getModelObject();
		TipoInstituicao tipo = tipoMediator.buscarTipoInstituicao("Cartório");
		instituicao.setTipoInstituicao(tipo);

		if (getModelObject().getId() != 0) {
			Instituicao instituicaoSalvo = instituicaoMediator
					.alterar(instituicao);
			if (instituicaoSalvo != null) {
				info("Cartório alterado com sucesso!");
			} else {
				error("Cartório não alterado!");
			}
		} else {
			Municipio m = instituicao.getComarcaCartorio();
			InstituicaoMunicipio im = new InstituicaoMunicipio();
			im.setInstituicao(instituicao);
			im.setMunicipio(m);
			
			if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
				if (!instituicao.isSituacao()) {
					instituicao.setSituacao(true);
				}
				Instituicao instituicaoSalvo = instituicaoMediator
						.salvar(instituicao);
				imMediator.salvar(im);
				if (instituicaoSalvo != null) {
					info("Cartório cadastrado com sucesso!");
				} else {
					error("Cartório não criado!");
				}
			} else {
				error("Cartório não criado, pois já existe!");
			}
		}
	}
}