package br.com.ieptbto.cra.page.convenio;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConvenioMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER, CraRoles.ADMIN })
public class GerarRemessaConvenioPage extends BasePage<TituloFiliado> {

	@SpringBean
	private ConvenioMediator convenioMediator;
	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	
	private static final long serialVersionUID = 1L;
	private TituloFiliado titulo;
	private ListView<TituloFiliado> titulos;

	public GerarRemessaConvenioPage() {
		this.titulo = new TituloFiliado();
		adicionarComponentes();
	}

	public GerarRemessaConvenioPage(String message) {
		this.titulo = new TituloFiliado();
		success(message);
		adicionarComponentes();
	}
	
	@Override
	protected void adicionarComponentes() {
		add(formGerarRemessaConvenio());
		add(listaTitulosConvenio());
	}

	private Form<TituloFiliado> formGerarRemessaConvenio() {
		return new Form<TituloFiliado>("form", getModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				List<TituloFiliado> titulosFiliados = titulos.getModelObject();
				
				try {
					convenioMediator.gerarRemessas(getUser(), titulosFiliados);
					setResponsePage(new GerarRemessaConvenioPage("Os arquivos de remessa dos convênios foram gerados com sucesso!"));
				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error("Não foi gerar os arquivos de remessa dos convênios ! \n Entre em contato com a CRA ");
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
	}

	private ListView<TituloFiliado> listaTitulosConvenio() {
		return titulos = new ListView<TituloFiliado>("listViewTitulos", buscarTitulosFiliados()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new Label("convenio", tituloLista.getFiliado().getInstituicaoConvenio().getNomeFantasia()));
				item.add(new Label("devedor", tituloLista.getNomeDevedor()));
				item.add(new Label("dataEnvioCRA", DataUtil.localDateToString(new LocalDate(tituloLista.getDataEnvioCRA()))));
				item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
			}
		};
	}
	
	public IModel<List<TituloFiliado>> buscarTitulosFiliados() {
		return new LoadableDetachableModel<List<TituloFiliado>>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloFiliado> load() {
				return convenioMediator.buscarTitulosConveniosGerarRemessa();
			}
		};
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
