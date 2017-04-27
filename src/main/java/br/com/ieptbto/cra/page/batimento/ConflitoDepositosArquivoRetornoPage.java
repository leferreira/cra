package br.com.ieptbto.cra.page.batimento;

import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DepositoMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ConflitoDepositosArquivoRetornoPage extends BasePage<Remessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	DepositoMediator depositoMediator;
	@SpringBean
	RetornoMediator retornoMediator;
	private Remessa remessa;
	private List<Deposito> depositos;
	private List<Remessa> retornos;

	public ConflitoDepositosArquivoRetornoPage(List<Deposito> depositos, List<Remessa> retornos) {
		this.remessa = new Remessa();
		this.depositos = depositos;
		this.retornos = retornos;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formularioBatimento());
	}

	private Form<Deposito> formularioBatimento() {
		Form<Deposito> form = new Form<Deposito>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				
				try {
					depositoMediator.processarDepositosConflito(depositos);
					setResponsePage(new ImportarExtratoPage("O arquivo de extrato processado foi importado com sucesso!"));
					
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento! Entre em contato com a CRA.");
				}
			}
		};
		form.add(carregarExtrato());
		return form;
	}

	private ListView<Deposito> carregarExtrato() {
		return new ListView<Deposito>("extrato", depositos) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Deposito> item) {
				final Deposito deposito = item.getModelObject();
				item.add(new Label("data", DataUtil.localDateToString(deposito.getData())));
				item.add(new Label("lancamento", deposito.getLancamento().toUpperCase()));
				item.add(new Label("numeroDocumento", deposito.getNumeroDocumento()));
				item.add(new Label("valor", "R$ " + deposito.getValorCredito()));
				if (StringUtils.isBlank(deposito.getDescricao())) {
					item.add(new Label("tooltip", "").setVisible(false));
				} else {
					item.add(new Label("tooltip", deposito.getDescricao()));
				}
				ArrayList<Remessa> remessasDeposito = new ArrayList<Remessa>();
				deposito.setRemessas(remessasDeposito);
				IChoiceRenderer<Remessa> renderer = new ChoiceRenderer<Remessa>("renderer"){
					
					/***/
					private static final long serialVersionUID = 1L;
					
					@Override
					public Object getDisplayValue(Remessa remessa) {
						return remessa.getArquivo().getNomeArquivo() + " - " + remessa.getInstituicaoOrigem().getMunicipio().getNomeMunicipio()
								+ " - R$ " + retornoMediator.buscarValorDeTitulosPagos(remessa);
					}
				};
				item.add(new ListMultipleChoice<Remessa>("remessas", new Model<ArrayList<Remessa>>(remessasDeposito), retornos, renderer));
			}
		};
	}

	@Override 
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}