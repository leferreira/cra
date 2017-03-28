package br.com.ieptbto.cra.page.layoutPersonalizado;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.mediator.LayoutFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class IncluirLayoutEmpresaPage extends BasePage<LayoutFiliado> {

	@SpringBean
	LayoutFiliadoMediator layoutFiliadoMediator;
	
	private LayoutFiliado layoutEmpresa;
	private LayoutEmpresaInputPanel inputPanel;

	public IncluirLayoutEmpresaPage() {
		adicionarComponentes();
	}

	public IncluirLayoutEmpresaPage(String string) {
		info(string);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		layoutEmpresa = new LayoutFiliado();
		inputPanel = new LayoutEmpresaInputPanel("layoutEmpresaInputPanel", getModel());
		LayoutEmpresaForm form = new LayoutEmpresaForm("form", getModel(), inputPanel);
		form.add(inputPanel);
		form.add(new Button("submit") {

			@Override
			public void onSubmit() {
				getModel();
				List<LayoutFiliado> listaCampos = inputPanel.getListaLayoutFiliado();

				try {
					layoutFiliadoMediator.salvar(listaCampos);
					info("Layout inserido com sucesso!.");
					setResponsePage(new IncluirLayoutEmpresaPage("Layout inserido com sucesso!."));

				} catch (Exception ex) {
					error("Não foi possível Inserir o layout.");
				}
			}
		});

		add(form);

	}

	@Override
	protected IModel<LayoutFiliado> getModel() {
		return new CompoundPropertyModel<LayoutFiliado>(layoutEmpresa);
	}

}
