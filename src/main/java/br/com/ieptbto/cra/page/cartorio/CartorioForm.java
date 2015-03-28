package br.com.ieptbto.cra.page.cartorio;

import org.apache.log4j.Logger;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;

@SuppressWarnings("serial")
public class CartorioForm extends BaseForm<Instituicao> {

	private static final Logger logger = Logger.getLogger(CartorioForm.class);
	@SpringBean
	private TipoInstituicaoMediator tipoMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;

	public CartorioForm(String id, IModel<Instituicao> model) {
		super(id, model);
	}

	public CartorioForm(String id, Instituicao colaboradorModel) {
		this(id, new CompoundPropertyModel<Instituicao>(colaboradorModel));
	}

	@SuppressWarnings("unused")
	@Override
	public void onSubmit() {

		Instituicao instituicao = getModelObject();
		TipoInstituicao tipo = tipoMediator.buscarTipoInstituicao("Cartório");
		instituicao.setTipoInstituicao(tipo);
		try{
			if (!municipioMediator.isMunicipioTemCartorio(instituicao.getMunicipio())) {
				if (getModelObject().getId() != 0) {
					Instituicao instituicaoSalvo = instituicaoMediator.alterar(instituicao);
					info("Dados alterados com sucesso!.");
				} else {
					if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
						if (!instituicao.isSituacao())
							instituicao.setSituacao(true);
						Instituicao instituicaoSalvo = instituicaoMediator.salvar(instituicao);
						info("Dados salvos com sucesso!.");
					} else 
						error("Cartório não criado, pois já existe!");
				}
			} else {
				error("Já existe um cartório cadastrado nesta cidade!");
			}
						
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
		}
	}
}