package br.com.ieptbto.cra.page.tipoArquivo;

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
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaTipoArquivoPage extends BasePage<TipoArquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TipoArquivoMediator tipoArquivoMediator;

	private TipoArquivo tipoArquivo;

	public ListaTipoArquivoPage() {
		this.tipoArquivo = new TipoArquivo();
		add(carregarListaTipos());
	}

	public ListaTipoArquivoPage(String mensagem) {
		this.tipoArquivo = new TipoArquivo();
		success(mensagem);
		add(carregarListaTipos());
	}

	@Override
	protected void adicionarComponentes() {
		// TODO Auto-generated method stub

	}

	private ListView<TipoArquivo> carregarListaTipos() {
		return new ListView<TipoArquivo>("listViewTipos", tipoArquivoMediator.getTiposArquivos()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TipoArquivo> item) {
				final TipoArquivo tipoLista = item.getModelObject();
				DateTimeFormatter outputFormat = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();

				@SuppressWarnings("rawtypes")
				Link linkTipoArquivo = new Link("linkAlterar") {
					/**/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new IncluirTipoArquivoPage(tipoLista));
					}
				};
				linkTipoArquivo.add(new Label("tipoArquivo", tipoLista.getTipoArquivo().getLabel()));
				item.add(linkTipoArquivo);
				item.add(new Label("constante", tipoLista.getTipoArquivo().getConstante()));
				if (tipoLista.getHoraEnvioInicio() != null) {
					item.add(new Label("horaInicio", tipoLista.getHoraEnvioInicio().toString(outputFormat)));
				} else {
					item.add(new Label("horaInicio", StringUtils.EMPTY));
				}
				if (tipoLista.getHoraEnvioFim() != null) {
					item.add(new Label("horaFim", tipoLista.getHoraEnvioFim().toString(outputFormat)));
				} else {
					item.add(new Label("horaFim", StringUtils.EMPTY));
				}
			}
		};
	}

	@Override
	protected IModel<TipoArquivo> getModel() {
		return new CompoundPropertyModel<TipoArquivo>(tipoArquivo);
	}
}
