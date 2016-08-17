package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.io.File;
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
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class TitulosDesistenciaPage extends BasePage<DesistenciaProtesto> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	DesistenciaProtestoMediator desistenciaProtestoMediator;
	@SpringBean
	DownloadMediator downloadMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	TituloMediator tituloMediator;

	private DesistenciaProtesto desistenciaProtesto;
	private Instituicao cartorioDestino;

	public TitulosDesistenciaPage(DesistenciaProtesto deistenciaProtesto) {
		this.desistenciaProtesto = deistenciaProtesto;
		this.cartorioDestino = instituicaoMediator.getCartorioPorCodigoIBGE(getDesistenciaProtesto().getCabecalhoCartorio().getCodigoMunicipio());

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		linkDownloadArquivo();
		informacoesArquivoDesistencia();
		listaPedidosDesistencia();

	}

	private void linkDownloadArquivo() {
		add(new Link<DesistenciaProtesto>("downloadArquivo") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				File file = downloadMediator.baixarDesistenciaTXT(getUser(), getDesistenciaProtesto());
				IResourceStream resourceStream = new FileResourceStream(file);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
						getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
			}
		});
	}

	private void informacoesArquivoDesistencia() {
		add(new Label("nomeArquivo", getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
		add(new Label("dataEnvio", DataUtil.localDateToString(getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getDataEnvio())));
		add(new Label("enviadoPor", getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
		add(new Label("destino", cartorioDestino.getNomeFantasia()));
	}

	private void listaPedidosDesistencia() {
		add(new ListView<PedidoDesistencia>("pedidosDesistencia", carregarPedidosDesistencia()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PedidoDesistencia> item) {
				final PedidoDesistencia pedidoDesistencia = item.getModelObject();
				pedidoDesistencia.getTitulo().setConfirmacao(tituloMediator.buscarConfirmacao(pedidoDesistencia.getTitulo()));
				pedidoDesistencia.getTitulo().setRetorno(tituloMediator.buscarRetorno(pedidoDesistencia.getTitulo()));

				item.add(new Label("nossoNumerno", pedidoDesistencia.getTitulo().getNossoNumero()));
				item.add(new Label("numeroTitulo", pedidoDesistencia.getTitulo().getNumeroTitulo()));
				item.add(new Label("protocolo", pedidoDesistencia.getNumeroProtocolo()));
				item.add(new Label("pracaProtesto", cartorioDestino.getMunicipio().getNomeMunicipio().toUpperCase()));
				item.add(new LabelValorMonetario<BigDecimal>("valor", pedidoDesistencia.getTitulo().getSaldoTitulo()));
				item.add(new Label("situacaoTitulo", pedidoDesistencia.getTitulo().getSituacaoTitulo()));

				if (pedidoDesistencia.getTitulo().getConfirmacao() == null) {
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("dataSituacao", StringUtils.EMPTY));

				} else if (pedidoDesistencia.getTitulo().getConfirmacao() != null && pedidoDesistencia.getTitulo().getRetorno() == null) {
					item.add(new Label("dataConfirmacao",
							DataUtil.localDateToString(pedidoDesistencia.getTitulo().getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
					item.add(new Label("dataSituacao",
							DataUtil.localDateToString(pedidoDesistencia.getTitulo().getConfirmacao().getDataOcorrencia())));

				} else if (pedidoDesistencia.getTitulo().getRetorno() != null) {
					item.add(new Label("dataConfirmacao",
							DataUtil.localDateToString(pedidoDesistencia.getTitulo().getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
					item.add(new Label("dataSituacao", DataUtil.localDateToString(pedidoDesistencia.getTitulo().getRetorno().getDataOcorrencia())));
				}

				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(pedidoDesistencia.getTitulo()));
					}
				};
				if (pedidoDesistencia.getTitulo().getNomeDevedor().length() > 25) {
					linkHistorico.add(new Label("nomeDevedor", pedidoDesistencia.getTitulo().getNomeDevedor().substring(0, 24)));
				} else {
					linkHistorico.add(new Label("nomeDevedor", pedidoDesistencia.getTitulo().getNomeDevedor()));
				}
				item.add(linkHistorico);

				Link<Arquivo> linkArquivoRemessa = new Link<Arquivo>("linkRemessa") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(pedidoDesistencia.getTitulo().getRemessa()));
					}
				};
				linkArquivoRemessa.add(new Label("nomeRemessa", pedidoDesistencia.getTitulo().getRemessa().getArquivo().getNomeArquivo()));
				item.add(linkArquivoRemessa);

				Link<Retorno> linkRetorno = new Link<Retorno>("linkRetorno") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(pedidoDesistencia.getTitulo().getRetorno().getRemessa()));
					}
				};
				if (pedidoDesistencia.getTitulo().getRetorno() != null) {
					linkRetorno.add(new Label("retorno", pedidoDesistencia.getTitulo().getRetorno().getRemessa().getArquivo().getNomeArquivo()));
				} else {
					linkRetorno.add(new Label("retorno", StringUtils.EMPTY));
				}
				item.add(linkRetorno);
			}
		});
	}

	public IModel<List<PedidoDesistencia>> carregarPedidosDesistencia() {
		return new LoadableDetachableModel<List<PedidoDesistencia>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PedidoDesistencia> load() {
				return desistenciaProtestoMediator.buscarPedidosDesistenciaProtesto(desistenciaProtesto);
			}
		};
	}

	public DesistenciaProtesto getDesistenciaProtesto() {
		return desistenciaProtesto;
	}

	@Override
	protected IModel<DesistenciaProtesto> getModel() {
		return new CompoundPropertyModel<DesistenciaProtesto>(desistenciaProtesto);
	}

}
