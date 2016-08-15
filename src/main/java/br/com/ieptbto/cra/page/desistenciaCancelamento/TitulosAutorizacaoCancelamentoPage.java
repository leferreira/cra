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
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
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
public class TitulosAutorizacaoCancelamentoPage extends BasePage<AutorizacaoCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;
	@SpringBean
	TituloMediator tituloMediator;

	private AutorizacaoCancelamento autorizacaoCancelamento;
	private Instituicao cartorioDestino;

	public TitulosAutorizacaoCancelamentoPage(AutorizacaoCancelamento autorizacaoCancelamento) {
		this.autorizacaoCancelamento = autorizacaoCancelamento;
		this.cartorioDestino = instituicaoMediator.getCartorioPorCodigoIBGE(autorizacaoCancelamento.getCabecalhoCartorio().getCodigoMunicipio());
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		linkDownloadArquivo();
		informacoesAutorizacaoCancelamento();
		listaPedidosAutorizacaoCancelamentos();
	}

	private void linkDownloadArquivo() {
		add(new Link<AutorizacaoCancelamento>("downloadArquivo") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				File file = autorizacaoCancelamentoMediator.baixarAutorizacaoTXT(getUser(), autorizacaoCancelamento);
				IResourceStream resourceStream = new FileResourceStream(file);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
						autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
			}
		});
	}

	private void informacoesAutorizacaoCancelamento() {
		add(new Label("nomeArquivo", autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
		add(new Label("dataEnvio",
				DataUtil.localDateToString(autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getDataEnvio())));
		add(new Label("enviadoPor",
				autorizacaoCancelamento.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
		add(new Label("destino", cartorioDestino.getNomeFantasia()));
	}

	private void listaPedidosAutorizacaoCancelamentos() {
		add(new ListView<PedidoAutorizacaoCancelamento>("pedidosAC", carregarPedidosAutorizacaoCancelamento()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PedidoAutorizacaoCancelamento> item) {
				final PedidoAutorizacaoCancelamento pedidoAutorizacao = item.getModelObject();

				if (pedidoAutorizacao.getTitulo() == null) {
					item.add(new Label("nossoNumerno", pedidoAutorizacao.getCarteiraNossoNumero()));
					item.add(new Label("numeroTitulo", pedidoAutorizacao.getNumeroTitulo()));
					item.add(new Label("protocolo", pedidoAutorizacao.getNumeroProtocolo()));
					item.add(new Label("pracaProtesto", cartorioDestino.getMunicipio().getNomeMunicipio().toUpperCase()));
					item.add(new LabelValorMonetario<BigDecimal>("valor", pedidoAutorizacao.getValorTitulo()));

					Button botaoHistorico = new Button("linkHistorico");
					botaoHistorico.add(new Label("nomeDevedor", pedidoAutorizacao.getNomePrimeiroDevedor()));
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
					pedidoAutorizacao.getTitulo().setConfirmacao(tituloMediator.buscarConfirmacao(pedidoAutorizacao.getTitulo()));
					pedidoAutorizacao.getTitulo().setRetorno(tituloMediator.buscarRetorno(pedidoAutorizacao.getTitulo()));

					item.add(new Label("nossoNumerno", pedidoAutorizacao.getTitulo().getNossoNumero()));
					item.add(new Label("numeroTitulo", pedidoAutorizacao.getTitulo().getNumeroTitulo()));
					item.add(new Label("protocolo", pedidoAutorizacao.getNumeroProtocolo()));
					item.add(new Label("pracaProtesto", cartorioDestino.getMunicipio().getNomeMunicipio().toUpperCase()));
					item.add(new LabelValorMonetario<BigDecimal>("valor", pedidoAutorizacao.getTitulo().getSaldoTitulo()));
					item.add(new Label("situacaoTitulo", pedidoAutorizacao.getTitulo().getSituacaoTitulo()));

					if (pedidoAutorizacao.getTitulo().getConfirmacao() == null) {
						item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
						item.add(new Label("dataSituacao", StringUtils.EMPTY));

					} else if (pedidoAutorizacao.getTitulo().getConfirmacao() != null && pedidoAutorizacao.getTitulo().getRetorno() == null) {
						item.add(new Label("dataConfirmacao",
								DataUtil.localDateToString(pedidoAutorizacao.getTitulo().getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
						item.add(new Label("dataSituacao",
								DataUtil.localDateToString(pedidoAutorizacao.getTitulo().getConfirmacao().getDataOcorrencia())));

					} else if (pedidoAutorizacao.getTitulo().getRetorno() != null) {
						item.add(new Label("dataConfirmacao",
								DataUtil.localDateToString(pedidoAutorizacao.getTitulo().getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
						item.add(new Label("dataSituacao",
								DataUtil.localDateToString(pedidoAutorizacao.getTitulo().getRetorno().getDataOcorrencia())));
					}

					Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

						/***/
						private static final long serialVersionUID = 1L;

						public void onClick() {
							setResponsePage(new HistoricoPage(pedidoAutorizacao.getTitulo()));
						}
					};
					if (pedidoAutorizacao.getNomePrimeiroDevedor().length() > 25) {
						linkHistorico.add(new Label("nomeDevedor", pedidoAutorizacao.getNomePrimeiroDevedor().substring(0, 24)));
					} else {
						linkHistorico.add(new Label("nomeDevedor", pedidoAutorizacao.getNomePrimeiroDevedor()));
					}
					item.add(linkHistorico);
					Link<Arquivo> linkArquivoRemessa = new Link<Arquivo>("linkRemessa") {

						/***/
						private static final long serialVersionUID = 1L;

						public void onClick() {
							setResponsePage(new TitulosArquivoPage(pedidoAutorizacao.getTitulo().getRemessa()));
						}
					};
					linkArquivoRemessa.add(new Label("nomeRemessa", pedidoAutorizacao.getTitulo().getRemessa().getArquivo().getNomeArquivo()));
					item.add(linkArquivoRemessa);

					Link<Retorno> linkRetorno = new Link<Retorno>("linkRetorno") {

						/***/
						private static final long serialVersionUID = 1L;

						public void onClick() {
							setResponsePage(new TitulosArquivoPage(pedidoAutorizacao.getTitulo().getRetorno().getRemessa()));
						}
					};
					if (pedidoAutorizacao.getTitulo().getRetorno() != null) {
						linkRetorno.add(new Label("retorno", pedidoAutorizacao.getTitulo().getRetorno().getRemessa().getArquivo().getNomeArquivo()));
					} else {
						linkRetorno.add(new Label("retorno", StringUtils.EMPTY));
					}
					item.add(linkRetorno);
				}

			}
		});
	}

	private IModel<List<PedidoAutorizacaoCancelamento>> carregarPedidosAutorizacaoCancelamento() {
		return new LoadableDetachableModel<List<PedidoAutorizacaoCancelamento>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PedidoAutorizacaoCancelamento> load() {
				return autorizacaoCancelamentoMediator.buscarPedidosAutorizacaoCancelamento(autorizacaoCancelamento);
			}
		};
	}

	@Override
	protected IModel<AutorizacaoCancelamento> getModel() {
		return new CompoundPropertyModel<AutorizacaoCancelamento>(autorizacaoCancelamento);
	}

}
