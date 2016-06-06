package br.com.ieptbto.cra.page.batimento;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaDepositoPage extends BasePage<Deposito> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	BatimentoMediator batimentoMediator;

	private Deposito deposito;
	private List<Deposito> depositos;

	public ListaDepositoPage(Deposito deposito, LocalDate dataInicio, LocalDate dataFim) {
		this.deposito = deposito;
		this.depositos = batimentoMediator.consultarDepositos(deposito, dataInicio, dataFim);

		adicionarComponentes();
	}

	public ListaDepositoPage(List<Deposito> depositos) {
		this.deposito = new Deposito();
		this.depositos = depositos;

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaDepositos();
	}

	private void listaDepositos() {
		add(new ListView<Deposito>("extrato", getDepositos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Deposito> item) {
				final Deposito deposito = item.getModelObject();
				item.add(new Label("dataEntrada", DataUtil.localDateToString(deposito.getDataImportacao())));
				item.add(new Label("data", DataUtil.localDateToString(deposito.getData())));
				item.add(new Label("lancamento", deposito.getLancamento().toUpperCase()));
				item.add(new Label("numeroDocumento", deposito.getNumeroDocumento()));
				item.add(new Label("valor", "R$ " + deposito.getValorCredito()));

				if (deposito.getTipoDeposito() == null) {
					item.add(new Label("tipoDeposito", StringUtils.EMPTY));
				} else if (deposito.getTipoDeposito().equals(TipoDeposito.NAO_INFORMADO)) {
					item.add(new Label("tipoDeposito", StringUtils.EMPTY));
				} else {
					item.add(new Label("tipoDeposito", deposito.getTipoDeposito().getLabel().toUpperCase()));
				}
				item.add(new Label("situacao", deposito.getSituacaoDeposito().getLabel()));
				item.add(new Label("descricao", deposito.getDescricao()));

				Link<Deposito> editarDeposito = new Link<Deposito>("editarDeposito") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new IncluirDepositoPage(deposito, getDepositos()));
					}
				};
				item.add(editarDeposito);
			}
		});
	}

	public List<Deposito> getDepositos() {
		return depositos;
	}

	@Override
	protected IModel<Deposito> getModel() {
		return new CompoundPropertyModel<Deposito>(deposito);
	}
}
