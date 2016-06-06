package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaTitulosPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TituloMediator tituloMediator;

	private TituloRemessa titulo;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Municipio municipio;

	public ListaTitulosPage(LocalDate dataInicio, LocalDate dataFim, TituloRemessa titulo, Municipio municipio) {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.titulo = titulo;
		this.municipio = municipio;
		adicionarComponentes();

	}

	@Override
	protected void adicionarComponentes() {
		add(listaTitulos());

	}

	private ListView<TituloRemessa> listaTitulos() {
		return new ListView<TituloRemessa>("listViewTitulos", buscarTitulos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();

				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(tituloLista.getRemessa()));
					}
				};
				linkArquivo.add(new Label("nomeRemessa", tituloLista.getRemessa().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);

				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));

				String municipio = tituloLista.getRemessa().getInstituicaoDestino().getMunicipio().getNomeMunicipio();
				if (municipio.length() > 20) {
					municipio = municipio.substring(0, 19);
				}
				item.add(new Label("municipio", municipio.toUpperCase()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloLista.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else {
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloLista.getSaldoTitulo()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
					}
				};
				if (tituloLista.getNomeDevedor().length() > 25) {
					linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor().substring(0, 24).toUpperCase()));
				} else {
					linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor().toUpperCase()));
				}
				item.add(linkHistorico);
				Link<Retorno> linkRetorno = new Link<Retorno>("linkRetorno") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(tituloLista.getRetorno().getRemessa()));
					}
				};
				if (tituloLista.getRetorno() != null) {
					linkRetorno.add(new Label("retorno", tituloLista.getRetorno().getRemessa().getArquivo().getNomeArquivo()));
					item.add(linkRetorno);
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getRetorno().getDataOcorrencia())));
				} else {
					linkRetorno.add(new Label("retorno", StringUtils.EMPTY));
					item.add(linkRetorno);
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getDataOcorrencia())));
				}
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}

	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return tituloMediator.buscarListaTitulos(dataInicio, dataFim, titulo, municipio, getUser());
			}
		};
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}
}
