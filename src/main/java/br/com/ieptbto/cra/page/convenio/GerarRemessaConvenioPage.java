package br.com.ieptbto.cra.page.convenio;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.mediator.ConvenioMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.cra.MensagemPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class GerarRemessaConvenioPage extends BasePage<TituloFiliado> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GerarRemessaConvenioPage.class);

	@SpringBean
	private ConvenioMediator convenioMediator;
	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	private TituloFiliado titulo;
	private List<TituloFiliado> listaTitulosConvenios;

	public GerarRemessaConvenioPage() {
		this.titulo = new TituloFiliado();
		this.listaTitulosConvenios = convenioMediator.buscarTitulosConvenios();
		carrrgarPageRemessaConvenio();
	}
	
	private void carrrgarPageRemessaConvenio() {
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()) {
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				
				try {
					convenioMediator.gerarRemessas(getUser(), getListaTitulosConvenios());
					setResponsePage(new MensagemPage<TituloFiliado>(GerarRemessaConvenioPage.class, "REMESSAS CONVÊNIOS", "Os arquivos de remessa dos convênios foram gerados com sucesso!"));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
		add(form);
		add(carregarListaTitulos());
	}

	private ListView<TituloFiliado> carregarListaTitulos() {
		return new ListView<TituloFiliado>("listViewTitulos", getListaTitulosConvenios()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();

				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new Label("convenio", tituloLista.getFiliado().getInstituicaoConvenio().getNomeFantasia()));
				item.add(new Label("devedor", tituloLista.getNomeDevedor()));
				item.add(new Label("dataEnvioCRA", DataUtil.localDateToString(tituloLista.getDataEnvioCRA())));
				item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
			}
		};
	}

	public List<TituloFiliado> getListaTitulosConvenios() {
		return listaTitulosConvenios;
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}
}
