package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
public class ListaTitulosPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private final TituloRemessa titulo;
	private Instituicao portador;
	
	public ListaTitulosPage(TituloRemessa titulo) {
		super();
		this.titulo=titulo;
		add(carregarListaTitulos());
	}

	@SuppressWarnings("rawtypes")
	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTitulos", buscarTitulos()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				portador = instituicaoMediator.getInstituicaoPorCodigoPortador(tituloLista.getCodigoPortador());
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else { 
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new Label("portador", portador.getNomeFantasia()));
				
				Link linkHistorico = new Link("linkHistorico") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto()));
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}
	
	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return tituloMediator.buscarListaTitulos(titulo, getUser());
			}
		};
	}

    
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}

}
