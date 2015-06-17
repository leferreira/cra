package br.com.ieptbto.cra.page.cartorio;

import org.apache.log4j.Logger;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
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

	@Override
	public void onSubmit() {

		Instituicao instituicao = getModelObject();
		TipoInstituicao tipo = tipoMediator.buscarTipoInstituicao(TipoInstituicaoCRA.CARTORIO.toString());
		instituicao.setTipoInstituicao(tipo);
		try{
			Instituicao cartorio = municipioMediator.isMunicipioTemCartorio(instituicao.getMunicipio());
			if (instituicao.getId() != 0) {
				if (cartorio.getId() == instituicao.getId()) {
					System.out.println(cartorio.getId());
					instituicaoMediator.alterar(instituicao);
					setResponsePage(new DetalharCartorioPage(instituicao));
				} else
					throw new InfraException("Já existe um cartório cadastrado nesta cidade!");
			}else{
				if (instituicaoMediator.isInstituicaoNaoExiste(instituicao)) {
					if (cartorio == null) {
						instituicaoMediator.salvar(instituicao);
						setResponsePage(new DetalharCartorioPage(instituicao));
					} else
						throw new InfraException("Já existe um cartório cadastrado nesta cidade!");
				} else 
					error("Cartório não criado, pois já existe!");
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