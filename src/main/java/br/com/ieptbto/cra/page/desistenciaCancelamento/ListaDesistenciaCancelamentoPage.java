package br.com.ieptbto.cra.page.desistenciaCancelamento;

import br.com.ieptbto.cra.beans.DesistenciaCancelamentoBean;
import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.enumeration.StatusDownload;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.mediator.*;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaDesistenciaCancelamentoPage extends BasePage<Arquivo> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	DownloadMediator downloadMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	DesistenciaProtestoMediator desistenciaMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoMediator;
	@SpringBean
	AutorizacaoCancelamentoMediator autorizacaoMediator;

	private List<DesistenciaCancelamentoBean> arquivosDesistenciasCancelamentos;
	private List<DesistenciaProtesto> desistencias;
	private List<CancelamentoProtesto> cancelamentos;
	private List<AutorizacaoCancelamento> autorizacoes;

	public ListaDesistenciaCancelamentoPage(String nomeArquivo, Instituicao bancoConvenio, List<TipoArquivoFebraban> tiposArquivo,
			Instituicao cartorio, LocalDate dataInicio, LocalDate dataFim) {
		Usuario user = getUser();
		this.desistencias = desistenciaMediator.consultarDesistencias(nomeArquivo, bancoConvenio, cartorio, dataInicio, dataFim, tiposArquivo, user);
		this.cancelamentos = cancelamentoMediator.consultarCancelamentos(nomeArquivo, bancoConvenio, cartorio, dataInicio, dataFim, tiposArquivo, user);
		this.autorizacoes = autorizacaoMediator.consultarAutorizacoes(nomeArquivo, bancoConvenio, cartorio, dataInicio, dataFim, tiposArquivo, user);
		converterDesistenciasCancelamentos();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(listaArquivosDesistenciasCancelamento());
	}

	private ListView<DesistenciaCancelamentoBean> listaArquivosDesistenciasCancelamento() {
		return new ListView<DesistenciaCancelamentoBean>("dataTableRemessa", getArquivosDesistenciasCancelamentos()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<DesistenciaCancelamentoBean> item) {
				final DesistenciaCancelamentoBean arquivo = item.getModelObject();

				if (arquivo.getTipoArquivo().equals(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO)) {
					item.add(downloadArquivoTXT(arquivo.getDesistenciaProtesto()));
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosDesistenciaPage(arquivo.getDesistenciaProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
					item.add(linkArquivo);
				} else if (arquivo.getTipoArquivo().equals(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO)) {
					item.add(downloadArquivoTXT(arquivo.getCancelamentoProtesto()));
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosCancelamentoPage(arquivo.getCancelamentoProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
					item.add(linkArquivo);
				} else if (arquivo.getTipoArquivo().equals(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO)) {
					item.add(downloadArquivoTXT(arquivo.getAutorizacaoCancelamento()));
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosAutorizacaoCancelamentoPage(arquivo.getAutorizacaoCancelamento()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
					item.add(linkArquivo);
				}

				item.add(new Label("dataEnvio", arquivo.getDataEnvio()));
				item.add(new Label("instituicao", arquivo.getInstituicao()));
				item.add(new Label("envio", arquivo.getEnvio()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(arquivo.getCodigoMunicipioDestino()).getNomeFantasia()));
				item.add(new Label("horaEnvio", arquivo.getHoraEnvio()));
				item.add(new Label("status", verificaDownload(arquivo.getStatus()).getLabel().toUpperCase())
						.setMarkupId(verificaDownload(arquivo.getStatus()).getLabel()));
			}

			private Link<DesistenciaProtesto> downloadArquivoTXT(final DesistenciaProtesto remessa) {
				return new Link<DesistenciaProtesto>("downloadArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = downloadMediator.baixarDesistenciaTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
								remessa.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}

			private Link<CancelamentoProtesto> downloadArquivoTXT(final CancelamentoProtesto remessa) {
				return new Link<CancelamentoProtesto>("downloadArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = downloadMediator.baixarCancelamentoTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
								remessa.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}

			private Link<Remessa> downloadArquivoTXT(final AutorizacaoCancelamento remessa) {
				return new Link<Remessa>("downloadArquivo") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = downloadMediator.baixarAutorizacaoTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
								remessa.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					}
				};
			}

			private StatusDownload verificaDownload(Boolean download) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao()
						.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusDownload.ENVIADO;
				}
				if (download.equals(false)) {
					return StatusDownload.AGUARDANDO;
				}
				return StatusDownload.RECEBIDO;
			}
		};
	}

	private void converterDesistenciasCancelamentos() {
		if (!getDesistenciaProtesto().isEmpty()) {
			for (DesistenciaProtesto desistenciaProtesto : getDesistenciaProtesto()) {
				DesistenciaCancelamentoBean arquivo = new DesistenciaCancelamentoBean();
				arquivo.parseDesistenciaProtesto(desistenciaProtesto);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		}

		if (!getCancelamentoProtestos().isEmpty()) {
			for (CancelamentoProtesto cancelamento : getCancelamentoProtestos()) {
				DesistenciaCancelamentoBean arquivo = new DesistenciaCancelamentoBean();
				arquivo.parseCancelamentoProtesto(cancelamento);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		}

		if (!getAutorizacaoCancelamentos().isEmpty()) {
			for (AutorizacaoCancelamento ac : getAutorizacaoCancelamentos()) {
				DesistenciaCancelamentoBean arquivo = new DesistenciaCancelamentoBean();
				arquivo.parseAutorizacaoCancelamento(ac);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		}
	}

	public List<DesistenciaProtesto> getDesistenciaProtesto() {
		if (desistencias == null) {
			desistencias = new ArrayList<DesistenciaProtesto>();
		}
		return desistencias;
	}

	public List<CancelamentoProtesto> getCancelamentoProtestos() {
		if (cancelamentos == null) {
			cancelamentos = new ArrayList<CancelamentoProtesto>();
		}
		return cancelamentos;
	}

	public List<AutorizacaoCancelamento> getAutorizacaoCancelamentos() {
		if (autorizacoes == null) {
			autorizacoes = new ArrayList<AutorizacaoCancelamento>();
		}
		return autorizacoes;
	}

	public List<DesistenciaCancelamentoBean> getArquivosDesistenciasCancelamentos() {
		if (arquivosDesistenciasCancelamentos == null) {
			arquivosDesistenciasCancelamentos = new ArrayList<DesistenciaCancelamentoBean>();
		}
		return arquivosDesistenciasCancelamentos;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return null;
	}
}
