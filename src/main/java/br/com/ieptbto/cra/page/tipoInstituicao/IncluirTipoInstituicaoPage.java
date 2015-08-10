package br.com.ieptbto.cra.page.tipoInstituicao;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirTipoInstituicaoPage extends BasePage<TipoInstituicao> {


	private TipoInstituicao tipoInstituicao;
	private Instituicao instituicao;
	private ArrayList<TipoArquivo> tipoArquivoSelects = new ArrayList<TipoArquivo>();
	
	@SpringBean
	TipoArquivoMediator tipoArquivoMediator;
	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;

	public IncluirTipoInstituicaoPage() {
		tipoInstituicao = new TipoInstituicao();
		setInstituicao(getUser().getInstituicao());
		setForm();
	}

	public IncluirTipoInstituicaoPage(TipoInstituicao tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
		setInstituicao(getUser().getInstituicao());
		setForm();
	}

	private void setForm() {
		Form<TipoInstituicao> form = new Form<TipoInstituicao>("form", getModel()) {
			/** */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				
				try {
					TipoInstituicao tipoInstituicao = getModelObject();
				
					List<PermissaoEnvio> permissoes = new ArrayList<PermissaoEnvio>();
					for (TipoArquivo tipoArquivo : tipoArquivoSelects) {
						PermissaoEnvio permissaoEnvio = new PermissaoEnvio();
						permissaoEnvio.setTipoArquivo(tipoArquivo);
						permissaoEnvio.setTipoInstituicao(tipoInstituicao);
						
						permissoes.add(permissaoEnvio);
					}
					tipoInstituicaoMediator.alterarPermissoesTipoInstituicao(tipoInstituicao, permissoes);
					setResponsePage(new ListaTipoInstituicaoPage("As permissões foram alteradas com sucesso !"));
				} catch (Exception ex){
					System.out.println(ex.getMessage());
					error("Não foi possível alterar as permissões do tipo instituicao ! Entre em contato com a CRA !");
				}
			}
		};
		form.add(campoTipoInstituicao());
		form.add(comboTipoArquivos());
		add(form);
	}

	private TextField<String> campoTipoInstituicao() {
		TextField<String> nomeTipo = new TextField<String>("tipoInstituicao");
		nomeTipo.setEnabled(false);
		return nomeTipo;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Component comboTipoArquivos() {
		List<TipoArquivo> listaTipos = new ArrayList<TipoArquivo>();
		for (TipoArquivoEnum tipoArquivo : TipoArquivoEnum.values()){
			TipoArquivo tipo = tipoArquivoMediator.buscarTipoPorNome(tipoArquivo);
			listaTipos.add(tipo);
		}
		return new CheckBoxMultipleChoice<TipoArquivo>("arquivosEnvioPermitido", new Model(tipoArquivoSelects) , listaTipos);
	}

	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}
}
