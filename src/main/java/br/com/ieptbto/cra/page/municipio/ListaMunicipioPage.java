package br.com.ieptbto.cra.page.municipio;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.dataProvider.DataProvider;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaMunicipioPage extends BasePage<Municipio> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	MunicipioMediator municipioMediator;

	private Municipio municipio;

	public ListaMunicipioPage() {
		this.municipio = new Municipio();
		adicionarComponentes();
	}

	public ListaMunicipioPage(String mensagem) {
		this.municipio = new Municipio();
		success(mensagem);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		botaoNovoMunicipio();
		dataTableMunicipio();

	}

	private void botaoNovoMunicipio() {
		add(new Link<Municipio>("botaoNovo") {

			/***/
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(new IncluirMunicipioPage());
			}
		});
	}

	private void dataTableMunicipio() {
		DataProvider<Municipio> dataProvider = new DataProvider<Municipio>(municipioMediator.listarTodos());

		List<IColumn<Municipio, String>> columns = new ArrayList<>();
		columns.add(new AbstractColumn<Municipio, String>(new Model<String>("EDITAR")) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<Municipio>> cellItem, String id, IModel<Municipio> model) {
				cellItem.add(new MunicipioActionPanel(id, model));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center col-action";
			}
		});
		columns.add(new PropertyColumn<Municipio, String>(new Model<String>("MUNICÍPIO"), "nomeMunicipio"));
		columns.add(new PropertyColumn<Municipio, String>(new Model<String>("UF"), "uf") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<Municipio, String>(new Model<String>("CÓD. IBGE"), "codigoIBGE") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<Municipio, String>(new Model<String>("CEP INÍCIAL"), "faixaInicialCep") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<Municipio, String>(new Model<String>("CEP FINAL"), "faixaFinalCep") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});

		DataTable<Municipio, String> dataTable = new DefaultDataTable<>("table", columns, dataProvider, 1000);
		add(dataTable);
	}

	@Override
	protected IModel<Municipio> getModel() {
		return new CompoundPropertyModel<Municipio>(municipio);
	}

}
