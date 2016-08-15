package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
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
public class TitulosCancelamentoPage extends BasePage<CancelamentoProtesto> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@SpringBean
	TituloMediator tituloMediator;

	private CancelamentoProtesto cancelamentoProtesto;
	private Instituicao cartorioDestino;

	public TitulosCancelamentoPage(CancelamentoProtesto cancelamentoProtesto) {
		this.cancelamentoProtesto = cancelamentoProtesto;
		this.cartorioDestino = instituicaoMediator.getCartorioPorCodigoIBGE(cancelamentoProtesto.getCabecalhoCartorio().getCodigoMunicipio());
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		linkDownloadArquivo();
		informacoesArquivoCancelamento();
		listaPedidosCancelamentos();

	}

	private void linkDownloadArquivo() {
		add(new Link<CancelamentoProtesto>("downloadArquivo") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				File file = cancelamentoProtestoMediator.baixarCancelamentoTXT(getUser(), getCancelamentoProtesto());
				IResourceStream resourceStream = new FileResourceStream(file);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
						getCancelamentoProtesto().getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
			}
		});
	}

	private void informacoesArquivoCancelamento() {
		add(new Label("nomeArquivo", getCancelamentoProtesto().getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
		add(new Label("dataEnvio",
				DataUtil.localDateToString(getCancelamentoProtesto().getRemessaCancelamentoProtesto().getArquivo().getDataEnvio())));
		add(new Label("enviadoPor", getCancelamentoProtesto().getRemessaCancelamentoProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
		add(new Label("destino", cartorioDestino.getNomeFantasia()));
	}

	private void listaPedidosCancelamentos() {
		add(new ListView<PedidoCancelamento>("pedidosCancelamento", carregarPedidosCancelamento()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PedidoCancelamento> item) {
				final PedidoCancelamento pedidoCancelamento = item.getModelObject();

				if (pedidoCancelamento.getTitulo() == null) {
					item.add(new Label("nossoNumerno", pedidoCancelamento.getCarteiraNossoNumero()));
					item.add(new Label("numeroTitulo", pedidoCancelamento.getNumeroTitulo()));
					item.add(new Label("protocolo", pedidoCancelamento.getNumeroProtocolo()));
					item.add(new Label("pracaProtesto", cartorioDestino.getMunicipio().getNomeMunicipio().toUpperCase()));
					item.add(new LabelValorMonetario<BigDecimal>("valor", pedidoCancelamento.getValorTitulo()));

					Button botaoHistorico = new Button("linkHistorico");
					botaoHistorico.add(new Label("nomeDevedor", pedidoCancelamento.getNomePrimeiroDevedor()));
					item.add(botaoHistorico);
					Button botaoRemessa = new Button("linkRemessa");
					botaoRemessa.add(new Label("nomeRemessa", StringUtils.EMPTY));
					item.add(botaoRemessa);
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					Button botaoRetorno = new Button("linkRetorno");
					botaoRetorno.add(new Label("retorno", StringUtils.EMPTY));
					item.add(botaoRetorno);
					item.add(new Label("dataSituacao", StringUtils.EMPTY));
					item.add(new Label("situacaoTitulo", "TÍTULO ANTIGO"));

				} else {
					pedidoCancelamento.getTitulo().setConfirmacao(tituloMediator.buscarConfirmacao(pedidoCancelamento.getTitulo()));
					pedidoCancelamento.getTitulo().setRetorno(tituloMediator.buscarRetorno(pedidoCancelamento.getTitulo()));

					item.add(new Label("nossoNumerno", pedidoCancelamento.getTitulo().getNossoNumero()));
					item.add(new Label("numeroTitulo", pedidoCancelamento.getTitulo().getNumeroTitulo()));
					item.add(new Label("protocolo", pedidoCancelamento.getNumeroProtocolo()));
					item.add(new Label("pracaProtesto", cartorioDestino.getMunicipio().getNomeMunicipio().toUpperCase()));
					item.add(new LabelValorMonetario<BigDecimal>("valor", pedidoCancelamento.getTitulo().getSaldoTitulo()));
					item.add(new Label("situacaoTitulo", pedidoCancelamento.getTitulo().getSituacaoTitulo()));

					if (pedidoCancelamento.getTitulo().getConfirmacao() == null) {
						item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
						item.add(new Label("dataSituacao", StringUtils.EMPTY));

					} else if (pedidoCancelamento.getTitulo().getConfirmacao() != null && pedidoCancelamento.getTitulo().getRetorno() == null) {
						item.add(new Label("dataConfirmacao", DataUtil
								.localDateToString(pedidoCancelamento.getTitulo().getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
						item.add(new Label("dataSituacao",
								DataUtil.localDateToString(pedidoCancelamento.getTitulo().getConfirmacao().getDataOcorrencia())));

					} else if (pedidoCancelamento.getTitulo().getRetorno() != null) {
						item.add(new Label("dataConfirmacao", DataUtil
								.localDateToString(pedidoCancelamento.getTitulo().getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
						item.add(new Label("dataSituacao",
								DataUtil.localDateToString(pedidoCancelamento.getTitulo().getRetorno().getDataOcorrencia())));
					}
					Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

						/***/
						private static final long serialVersionUID = 1L;

						public void onClick() {
							setResponsePage(new HistoricoPage(pedidoCancelamento.getTitulo()));
						}
					};
					if (pedidoCancelamento.getNomePrimeiroDevedor().length() > 25) {
						linkHistorico.add(new Label("nomeDevedor", pedidoCancelamento.getNomePrimeiroDevedor().substring(0, 24)));
					} else {
						linkHistorico.add(new Label("nomeDevedor", pedidoCancelamento.getNomePrimeiroDevedor()));
					}
					item.add(linkHistorico);
					Link<Arquivo> linkArquivoRemessa = new Link<Arquivo>("linkRemessa") {

						/***/
						private static final long serialVersionUID = 1L;

						public void onClick() {
							setResponsePage(new TitulosArquivoPage(pedidoCancelamento.getTitulo().getRemessa()));
						}
					};
					linkArquivoRemessa.add(new Label("nomeRemessa", pedidoCancelamento.getTitulo().getRemessa().getArquivo().getNomeArquivo()));
					item.add(linkArquivoRemessa);

					Link<Retorno> linkRetorno = new Link<Retorno>("linkRetorno") {

						/***/
						private static final long serialVersionUID = 1L;

						public void onClick() {
							setResponsePage(new TitulosArquivoPage(pedidoCancelamento.getTitulo().getRetorno().getRemessa()));
						}
					};
					if (pedidoCancelamento.getTitulo().getRetorno() != null) {
						linkRetorno.add(new Label("retorno", pedidoCancelamento.getTitulo().getRetorno().getRemessa().getArquivo().getNomeArquivo()));
					} else {
						linkRetorno.add(new Label("retorno", StringUtils.EMPTY));
					}
					item.add(linkRetorno);
				}
			}
		});
	}

	private IModel<List<PedidoCancelamento>> carregarPedidosCancelamento() {
		return new LoadableDetachableModel<List<PedidoCancelamento>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PedidoCancelamento> load() {
				return cancelamentoProtestoMediator.buscarPedidosCancelamentoProtesto(cancelamentoProtesto);
			}
		};
	}

	public CancelamentoProtesto getCancelamentoProtesto() {
		return cancelamentoProtesto;
	}

	@Override
	protected IModel<CancelamentoProtesto> getModel() {
		return new CompoundPropertyModel<CancelamentoProtesto>(cancelamentoProtesto);
	}

}
