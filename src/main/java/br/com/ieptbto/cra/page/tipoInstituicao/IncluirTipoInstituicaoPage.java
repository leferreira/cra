package br.com.ieptbto.cra.page.tipoInstituicao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class IncluirTipoInstituicaoPage extends BasePage<TipoInstituicao> {

	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;
	@SpringBean
	TipoArquivoMediator tipoArquivoMediator;
	
	private TipoInstituicao tipoInstituicao;
	private CheckBoxMultipleChoice<String> tipoPermitidos;
	
	public IncluirTipoInstituicaoPage(){
		tipoInstituicao = new TipoInstituicao();
		setForm();
	}
	
	public IncluirTipoInstituicaoPage(TipoInstituicao tipoInstituicao){
		this.tipoInstituicao = tipoInstituicao;
		setForm();
	}
	
	private void setForm(){
		Form<TipoInstituicao> form = new Form<TipoInstituicao>("form", getModel()){
			/** */
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				
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

	private Component comboTipoArquivos() {
		List<String> choices = new ArrayList<String>();
		List<TipoArquivoEnum> enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.constante);
		}
		return tipoPermitidos = new CheckBoxMultipleChoice<String>("arquivosEnvioPermitido", choices);
	}
	
	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}
}
