package br.com.ieptbto.cra.page.titulo;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.ArquivoOcorrenciaBean;
import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.SolicitacaoCancelamento;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.SituacaoBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private TituloFiliadoMediator tituloFiliadoMediator;
	@SpringBean
	private ArquivoMediator arquivoMediator;
	@SpringBean
	private RemessaMediator remessaMediator;
	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private DesistenciaProtestoMediator desistenciaMediator;
	@SpringBean
	private CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@SpringBean
	private AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;
	@SpringBean
	private BatimentoMediator batimentoMediator;
	@SpringBean
	private InstrumentoProtestoMediator instrumentoMediator;
	@SpringBean
	private RetornoMediator retornoMediator;

	private TituloRemessa tituloRemessa;
	private List<ArquivoOcorrenciaBean> arquivosOcorrencias;

	public HistoricoPage(TituloRemessa titulo) {
		this.tituloRemessa = titulo;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarArquivosOcorrencias();
		carregarCampos();

	}

	private void carregarArquivosOcorrencias() {
		TituloRemessa titulo = getTituloRemessa();
		ArquivoOcorrenciaBean novaOcorrencia = null;

		if (new Integer(titulo.getCodigoPortador()) > 799) {
			TituloFiliado tituloFiliado = tituloFiliadoMediator.buscarTituloFiliadoProcessadoNaCra(titulo.getNossoNumero(), titulo.getNumeroTitulo());
			if (tituloFiliado != null) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToTituloFiliado(tituloFiliado);
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}

		novaOcorrencia = new ArquivoOcorrenciaBean();
		if (titulo.getRemessa() != null) {
			Remessa remessa = remessaMediator.carregarRemessaPorId(titulo.getRemessa());
			titulo.setRemessa(remessa);
		}
		novaOcorrencia.parseToRemessa(titulo.getRemessa());
		getArquivosOcorrencias().add(novaOcorrencia);

		if (titulo.getConfirmacao() != null) {
			Confirmacao confirmacao = tituloMediator.carregarTituloConfirmacao(titulo.getConfirmacao());
			getTituloRemessa().setConfirmacao(confirmacao);
			novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToRemessa(confirmacao.getRemessa());
			getArquivosOcorrencias().add(novaOcorrencia);

			if (confirmacao.getRemessa().getArquivo().getId() != confirmacao.getRemessa().getArquivoGeradoProBanco().getId()) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToArquivoGerado(confirmacao.getRemessa().getArquivoGeradoProBanco());
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}
		if (titulo.getRetorno() != null) {
			Retorno retorno = tituloMediator.carregarTituloRetorno(titulo.getRetorno());
			getTituloRemessa().setRetorno(retorno);
			novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToRemessa(retorno.getRemessa());
			getArquivosOcorrencias().add(novaOcorrencia);

			if (titulo.getRetorno().getRemessa().getBatimento() != null) {
				if (titulo.getRetorno().getRemessa().getSituacaoBatimentoRetorno().equals(SituacaoBatimentoRetorno.AGUARDANDO_LIBERACAO)
						|| titulo.getRetorno().getRemessa().getSituacaoBatimentoRetorno().equals(SituacaoBatimentoRetorno.CONFIRMADO)) {
					List<Deposito> depositos = batimentoMediator.buscarDepositosArquivoRetorno(titulo.getRetorno().getRemessa().getBatimento());
					novaOcorrencia = new ArquivoOcorrenciaBean();
					novaOcorrencia.parseToBatimento(titulo.getRetorno().getRemessa().getBatimento(), depositos,
							retornoMediator.buscarValorDeTitulosPagos(titulo.getRetorno().getRemessa()));
					getArquivosOcorrencias().add(novaOcorrencia);
				}
			}

			if (retorno.getTipoOcorrencia().equals(TipoOcorrencia.PROTESTADO.constante)) {
				InstrumentoProtesto instrumentoProtesto = instrumentoMediator.isTituloJaFoiGeradoInstrumento(retorno);
				if (instrumentoProtesto != null) {
					novaOcorrencia = new ArquivoOcorrenciaBean();
					novaOcorrencia.parseToInstrumentoProtesto(instrumentoProtesto);
					getArquivosOcorrencias().add(novaOcorrencia);
				}
			}

			if (retorno.getRemessa().getArquivo().getId() != retorno.getRemessa().getArquivoGeradoProBanco().getId()) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToArquivoGerado(retorno.getRemessa().getArquivoGeradoProBanco());
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}

		if (titulo.getPedidosDesistencia() != null) {
			List<PedidoDesistencia> pedidoDesistencias = desistenciaMediator.buscarPedidosDesistenciaProtestoPorTitulo(titulo);
			for (PedidoDesistencia pedido : pedidoDesistencias) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToDesistenciaProtesto(pedido.getDesistenciaProtesto());
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}

		if (titulo.getPedidosCancelamento() != null) {
			List<PedidoCancelamento> pedidoCancelamento = cancelamentoProtestoMediator.buscarPedidosCancelamentoProtestoPorTitulo(titulo);
			for (PedidoCancelamento pedido : pedidoCancelamento) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToCancelamentoProtesto(pedido.getCancelamentoProtesto());
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}

		if (titulo.getPedidosAutorizacaoCancelamento() != null) {
			List<PedidoAutorizacaoCancelamento> pedidosAC = autorizacaoCancelamentoMediator.buscarPedidosAutorizacaoCancelamentoPorTitulo(titulo);
			for (PedidoAutorizacaoCancelamento pedido : pedidosAC) {
				novaOcorrencia = new ArquivoOcorrenciaBean();
				novaOcorrencia.parseToAutorizacaoCanlamento(pedido.getAutorizacaoCancelamento());
				getArquivosOcorrencias().add(novaOcorrencia);
			}
		}

		SolicitacaoCancelamento solicitacaoCancelamento = cancelamentoProtestoMediator.buscarSolicitacaoCancelamentoPorTitulo(titulo);
		if (solicitacaoCancelamento != null) {
			novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToSolicitacaoCancelamento(solicitacaoCancelamento);
			getArquivosOcorrencias().add(novaOcorrencia);
		}

		Collections.sort(getArquivosOcorrencias());
		add(listaArquivoOcorrenciaBean());
	}

	private ListView<ArquivoOcorrenciaBean> listaArquivoOcorrenciaBean() {
		return new ListView<ArquivoOcorrenciaBean>("divListaHistorico", getArquivosOcorrencias()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ArquivoOcorrenciaBean> item) {
				final ArquivoOcorrenciaBean arquivoOcorrenciaBean = item.getModelObject();

				if (arquivoOcorrenciaBean.getTituloFiliado() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-success"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-edit")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setOutputMarkupId(true);
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", arquivoOcorrenciaBean.getMensagem()));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getRemessa() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);
					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosArquivoPage(arquivoOcorrenciaBean.getRemessa()));
						}
					};
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getRemessa().getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getBatimento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-warning"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check-square-o")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setVisible(false);
					item.add(link);
					item.add(new Label("acao", "").setVisible(false));
					item.add(new Label("mensagem", arquivoOcorrenciaBean.getMensagem()).setOutputMarkupId(true).setEscapeModelStrings(false));
				}

				if (arquivoOcorrenciaBean.getInstrumentoProtesto() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-warning"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-list-ul")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", "Instrumento de Protesto: "));
					link.setOutputMarkupId(true);
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", "").setVisible(false));
					item.add(new Label("mensagem", arquivoOcorrenciaBean.getMensagem()));
				}

				if (arquivoOcorrenciaBean.getArquivoLiberado() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-success"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-send-o")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getArquivoLiberado().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo liberado para instituição."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getDesistenciaProtesto() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosDesistenciaPage(arquivoOcorrenciaBean.getDesistenciaProtesto()));
						}
					};
					link.add(new Label("conteudoLink",
							arquivoOcorrenciaBean.getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getCancelamentoProtesto() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosCancelamentoPage(arquivoOcorrenciaBean.getCancelamentoProtesto()));
						}
					};
					link.add(new Label("conteudoLink",
							arquivoOcorrenciaBean.getCancelamentoProtesto().getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getAutorizacaoCancelamento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosAutorizacaoCancelamentoPage(arquivoOcorrenciaBean.getAutorizacaoCancelamento()));
						}
					};
					link.add(new Label("conteudoLink",
							arquivoOcorrenciaBean.getAutorizacaoCancelamento().getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getSolicitacaoCancelamento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-danger"));
					divIcon.add(new Label("icon").add(new AttributeAppender("class", "fa fa-times")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setOutputMarkupId(true);
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", arquivoOcorrenciaBean.getMensagem()));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				item.add(new Label("data", DataUtil.localDateToString(arquivoOcorrenciaBean.getData())));
				item.add(new Label("hora", DataUtil.localTimeToString("HH:mm:ss", arquivoOcorrenciaBean.getHora())));
				item.add(new Label("usuarioAcao", arquivoOcorrenciaBean.getNomeUsuario()));
			}
		};
	}

	private void carregarCampos() {
		add(numeroProtocoloCartorio());
		add(dataProtocolo());
		add(codigoCartorio());
		add(labelAgenciaCodigoCedente());
		add(dataOcorrencia());
		add(irregularidade());
		add(codigoMunicipio());
		add(pracaProtesto());
		add(status());
		add(nomeSacadorVendedor());
		add(documentoSacador());
		add(ufSacadorVendedor());
		add(cepSacadorVendedor());
		add(cidadeSacadorVendedor());
		add(enderecoSacadorVendedor());
		add(nomeDevedor());
		add(documentoDevedor());
		add(ufDevedor());
		add(cepDevedor());
		add(cidadeDevedor());
		add(enderecoDevedor());
		add(numeroTitulo());
		add(portador());
		add(agencia());
		add(nossoNumero());
		add(especieTitulo());
		add(dataEmissaoTitulo());
		add(dataVencimentoTitulo());
		add(valorTitulo());
		add(saldoTitulo());
		add(valorCustaCartorio());
		add(valorDemaisDespesas());
		add(numeroControleDevedor());
		add(linkAnexos());
		add(labelAlinea());
		add(campoAlinea());
		add(labelDocumentosAnexos());
	}

	private Component labelAlinea() {
		Label labelAlinea = new Label("labelAlinea", "ALÍNEA");
		labelAlinea.setVisible(false);
		if (getTituloRemessa().getComplementoRegistro() != null) {
			if (getTituloRemessa().getComplementoRegistro().trim().length() == 2) {
				labelAlinea.setVisible(true);
			}
		}
		return labelAlinea;
	}

	private Label campoAlinea() {
		Label campoAlinea = new Label("campoAlinea", "");
		campoAlinea.setVisible(false);
		if (getTituloRemessa().getComplementoRegistro() != null) {
			if (getTituloRemessa().getComplementoRegistro().trim().length() == 2) {
				campoAlinea = new Label("campoAlinea", getTituloRemessa().getComplementoRegistro().trim());
				campoAlinea.setVisible(true);
			}
		}
		return campoAlinea;
	}

	private Component labelDocumentosAnexos() {
		Label labelDocumentos = new Label("labelDocumentosAnexos", "DOCUMENTOS ANEXOS");
		labelDocumentos.setVisible(false);
		if (getTituloRemessa().getAnexo() != null) {
			labelDocumentos.setVisible(true);
		}
		return labelDocumentos;
	}

	private Link<Void> linkAnexos() {
		Link<Void> linkAnexos = new Link<Void>("linkAnexos") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					File file = remessaMediator.decodificarAnexoTitulo(getUser(), getTituloRemessa());
					IResourceStream resourceStream = new FileResourceStream(file);

					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
				}

			}
		};
		if (getTituloRemessa().getAnexo() == null) {
			linkAnexos.setVisible(false);
		}
		return linkAnexos;
	}

	private Label codigoCartorio() {
		Integer codigoCartorio = 0;
		if (getTituloRemessa().getConfirmacao() != null) {
			codigoCartorio = getTituloRemessa().getConfirmacao().getCodigoCartorio();
		}
		if (getTituloRemessa().getRetorno() != null) {
			codigoCartorio = getTituloRemessa().getRetorno().getCodigoCartorio();
		}
		return new Label("codigoCartorio", new Model<String>(codigoCartorio.toString()));
	}

	private Label dataOcorrencia() {
		LocalDate dataOcorrencia = getTituloRemessa().getRemessa().getCabecalho().getDataMovimento();

		if (getTituloRemessa().getConfirmacao() != null) {
			dataOcorrencia = getTituloRemessa().getConfirmacao().getDataOcorrencia();
		}
		if (getTituloRemessa().getRetorno() != null) {
			dataOcorrencia = getTituloRemessa().getRetorno().getDataOcorrencia();
		}
		return new Label("dataOcorrencia", DataUtil.localDateToString(dataOcorrencia));
	}

	private Label irregularidade() {
		CodigoIrregularidade codigoIrregularidade = null;
		String irregularidade = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			irregularidade = getTituloRemessa().getConfirmacao().getCodigoIrregularidade();
		}
		if (getTituloRemessa().getRetorno() != null) {
			if (getTituloRemessa().getRetorno().getTipoOcorrencia().equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
				irregularidade = getTituloRemessa().getRetorno().getCodigoIrregularidade();
			}
		}
		codigoIrregularidade = CodigoIrregularidade.getIrregularidade(irregularidade);
		if (codigoIrregularidade == null) {
			return new Label("irregularidade", new Model<String>("00"));
		}
		return new Label("irregularidade", new Model<String>(codigoIrregularidade.getMotivo().toUpperCase()));
	}

	private Label codigoMunicipio() {
		return new Label("codigoMunicipio", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getCodigoMunicipio()));
	}

	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			numeroProtocolo = getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio();
		}
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label dataProtocolo() {
		String dataProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			dataProtocolo = DataUtil.localDateToString(getTituloRemessa().getConfirmacao().getDataProtocolo());
		}
		return new Label("dataProtocolo", new Model<String>(dataProtocolo));
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(getTituloRemessa().getPracaProtesto()));
	}

	private Label status() {
		return new Label("situacaoTitulo", new Model<String>(getTituloRemessa().getSituacaoTitulo()));
	}

	private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(getTituloRemessa().getNumeroTitulo()));
	}

	private Label labelAgenciaCodigoCedente() {
		return new Label("agenciaCodigoCedente", getTituloRemessa().getAgenciaCodigoCedente());
	}

	private Label portador() {
		return new Label("portador", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getNomePortador()));
	}

	private Label agencia() {
		return new Label("agencia", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getAgenciaCentralizadora()));
	}

	private Label nossoNumero() {
		return new Label("nossoNumero", new Model<String>(getTituloRemessa().getNossoNumero()));
	}

	private Label especieTitulo() {
		return new Label("especieTitulo", new Model<String>(getTituloRemessa().getEspecieTitulo()));
	}

	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataEmissaoTitulo())));
	}

	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataVencimentoTitulo())));
	}

	public Label valorTitulo() {
		Label textField = new Label("valorTitulo", new Model<String>("R$ " + getTituloRemessa().getValorTitulo().toString()));
		return textField;
	}

	public Label saldoTitulo() {
		Label textField = new Label("saldoTitulo", new Model<String>("R$ " + getTituloRemessa().getSaldoTitulo().toString()));
		return textField;
	}

	public Label valorCustaCartorio() {
		BigDecimal valorCustaCartorio = BigDecimal.ZERO;
		if (getTituloRemessa().getConfirmacao() != null) {
			valorCustaCartorio = getTituloRemessa().getConfirmacao().getValorCustaCartorio();
		}
		if (getTituloRemessa().getRetorno() != null) {
			valorCustaCartorio = getTituloRemessa().getRetorno().getValorCustaCartorio();
		}
		return new LabelValorMonetario<BigDecimal>("valorCustaCartorio", valorCustaCartorio);
	}

	public Label valorDemaisDespesas() {
		BigDecimal valorDemaisDespesas = BigDecimal.ZERO;
		if (getTituloRemessa().getConfirmacao() != null) {
			valorDemaisDespesas = getTituloRemessa().getConfirmacao().getValorDemaisDespesas();
		}
		if (getTituloRemessa().getRetorno() != null) {
			valorDemaisDespesas = getTituloRemessa().getRetorno().getValorDemaisDespesas();
		}
		return new LabelValorMonetario<BigDecimal>("valorDemaisDespesas", valorDemaisDespesas);
	}

	private Label nomeSacadorVendedor() {
		return new Label("nomeSacadorVendedor", new Model<String>(getTituloRemessa().getNomeSacadorVendedor()));
	}

	private Label documentoSacador() {
		return new Label("documentoSacador", new Model<String>(getTituloRemessa().getDocumentoSacador()));
	}

	private Label ufSacadorVendedor() {
		return new Label("ufSacadorVendedor", new Model<String>(getTituloRemessa().getUfSacadorVendedor()));
	}

	private Label cepSacadorVendedor() {
		return new Label("cepSacadorVendedor", new Model<String>(getTituloRemessa().getCepSacadorVendedor()));
	}

	private Label cidadeSacadorVendedor() {
		return new Label("cidadeSacadorVendedor", new Model<String>(getTituloRemessa().getCidadeSacadorVendedor()));
	}

	private Label enderecoSacadorVendedor() {
		return new Label("enderecoSacadorVendedor", new Model<String>(getTituloRemessa().getEnderecoSacadorVendedor()));
	}

	private Label nomeDevedor() {
		return new Label("nomeDevedor", new Model<String>(getTituloRemessa().getNomeDevedor()));
	}

	private Label documentoDevedor() {
		return new Label("documentoDevedor", new Model<String>(getTituloRemessa().getNumeroIdentificacaoDevedor()));
	}

	private Label ufDevedor() {
		return new Label("ufDevedor", new Model<String>(getTituloRemessa().getUfDevedor()));
	}

	private Label cepDevedor() {
		return new Label("cepDevedor", new Model<String>(getTituloRemessa().getCepDevedor()));
	}

	private Label cidadeDevedor() {
		return new Label("cidadeDevedor", new Model<String>(getTituloRemessa().getCidadeDevedor()));
	}

	private Label enderecoDevedor() {
		return new Label("enderecoDevedor", new Model<String>(getTituloRemessa().getEnderecoDevedor()));
	}

	private Label numeroControleDevedor() {
		return new Label("numeroControleDevedor", new Model<String>(getTituloRemessa().getNumeroControleDevedor().toString()));
	}

	private TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}

	public List<ArquivoOcorrenciaBean> getArquivosOcorrencias() {
		if (arquivosOcorrencias == null) {
			arquivosOcorrencias = new ArrayList<ArquivoOcorrenciaBean>();
		}
		return arquivosOcorrencias;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}