package br.com.ieptbto.cra.page.filiado;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.enumeration.BooleanSimNao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirFiliadoPage extends BasePage<Filiado> {

	/**/
	private static final long serialVersionUID = 1L;

	@SpringBean
	FiliadoMediator filiadoMediator;

	private Filiado filiado;
	private List<SetorFiliado> setoresFiliado;

	public IncluirFiliadoPage() {
		this.filiado = new Filiado();
		this.filiado.setSetoresFiliado(getSetoresFiliado());
		getSetoresFiliado().add(getSetorPadraoCra());

		adicionarComponentes();
	}

	public IncluirFiliadoPage(Filiado filiado) {
		this.filiado = filiado;
		this.setoresFiliado = filiadoMediator.buscarSetoresFiliado(filiado);
		this.filiado.setSetoresFiliado(setoresFiliado);

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioFiliado();
		divSetores();
	}

	private void formularioFiliado() {
		FiliadoForm form = new FiliadoForm("form", getModel(), setoresFiliado);
		form.add(new FiliadoInputPanel("filiadoInputPanel", getModel()));
		add(form);
	}

	private void divSetores() {
		WebMarkupContainer divSetoresFiliados = new WebMarkupContainer("divSetoresFiliados");
		divSetoresFiliados.add(new SetorFiliadoInputPanel("setorPanel", getModel(), getSetoresFiliado()));
		divSetoresFiliados.add(listaSetoresFiliado());
		if (getModel().getObject().getInstituicaoConvenio().getPermitidoSetoresConvenio().equals(BooleanSimNao.NAO)) {
			divSetoresFiliados.setVisible(false);
		}
		add(divSetoresFiliados);
	}

	private ListView<SetorFiliado> listaSetoresFiliado() {
		return new ListView<SetorFiliado>("listaSetor", getSetoresFiliado()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<SetorFiliado> item) {
				SetorFiliado setor = item.getModelObject();
				item.add(new Label("contador", item.getIndex() + 1));
				item.add(new Label("descricao", setor.getDescricao()));
				item.add(new Label("ativo", verificarSituacao(setor.isSituacaoAtivo())));
				item.add(alterarSituacao(setor));
			}

			private String verificarSituacao(Boolean ativo) {
				if (ativo.equals(true))
					return "Sim";
				return "Não";
			}

			private Link<SetorFiliado> alterarSituacao(final SetorFiliado setor) {
				return new Link<SetorFiliado>("alterar") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							if (setor.isSituacaoAtivo() == true) {
								setor.setSituacaoAtivo(false);
							} else {
								setor.setSituacaoAtivo(true);
							}

						} catch (InfraException e) {
							System.out.println(e.getMessage());
							getFeedbackPanel().error(e.getMessage());
						} catch (Exception e) {
							System.out.println(e.getMessage());
							getFeedbackPanel().error("Não foi possível remover o setor da empresa filiada! Entre em contato com o CRA !");
						}
					}
				};
			}
		};
	}

	public SetorFiliado getSetorPadraoCra() {
		SetorFiliado setorPadraoCra = new SetorFiliado();
		setorPadraoCra.setDescricao("GERAL");
		setorPadraoCra.setSetorPadraoFiliado(true);
		setorPadraoCra.setSituacaoAtivo(true);
		return setorPadraoCra;
	}

	public List<SetorFiliado> getSetoresFiliado() {
		if (setoresFiliado == null) {
			setoresFiliado = new ArrayList<SetorFiliado>();
		}
		return setoresFiliado;
	}

	@Override
	protected IModel<Filiado> getModel() {
		return new CompoundPropertyModel<Filiado>(filiado);
	}
}