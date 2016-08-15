package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.ArquivoDesistenciaCancelamentoBean;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaDesistenciaCancelamentoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	DesistenciaProtestoMediator desistenciaProtestoMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoMediator;
	@SpringBean
	AutorizacaoCancelamentoMediator autorizacaoMediator;

	private Arquivo arquivo;

	private List<ArquivoDesistenciaCancelamentoBean> arquivosDesistenciasCancelamentos;
	private List<DesistenciaProtesto> desistenciaProtesto;
	private List<CancelamentoProtesto> cancelamentoProtestos;
	private List<AutorizacaoCancelamento> autorizacaoCancelamentos;

	public ListaDesistenciaCancelamentoPage(Arquivo arquivo, ArrayList<TipoArquivoEnum> tiposArquivo, Municipio municipio,
			LocalDate dataInicio, LocalDate dataFim) {
		this.arquivo = arquivo;
		this.desistenciaProtesto = desistenciaProtestoMediator.buscarDesistenciaProtesto(arquivo, arquivo.getInstituicaoEnvio(), municipio, dataInicio, dataFim, tiposArquivo, getUser());
		this.cancelamentoProtestos = cancelamentoMediator.buscarCancelamentoProtesto(arquivo, arquivo.getInstituicaoEnvio(), municipio, dataInicio, dataFim, tiposArquivo, getUser());
		this.autorizacaoCancelamentos = autorizacaoMediator.buscarAutorizacaoCancelamento(arquivo, arquivo.getInstituicaoEnvio(), municipio, dataInicio, dataFim, tiposArquivo, getUser());

		converterDesistenciasCancelamentos();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaArquivosDevolucaoCancelamento();
	}

	private void listaArquivosDevolucaoCancelamento() {
		add(new ListView<ArquivoDesistenciaCancelamentoBean>("dataTableRemessa", getArquivosDesistenciasCancelamentos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ArquivoDesistenciaCancelamentoBean> item) {
				final ArquivoDesistenciaCancelamentoBean arquivo = item.getModelObject();

				if (arquivo.getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
					item.add(downloadArquivoTXT(arquivo.getDesistenciaProtesto()));
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosDesistenciaPage(arquivo.getDesistenciaProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
					item.add(linkArquivo);
				} else if (arquivo.getTipoArquivo().equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)) {
					item.add(downloadArquivoTXT(arquivo.getCancelamentoProtesto()));
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						/***/
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosCancelamentoPage(arquivo.getCancelamentoProtesto()));
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
					item.add(linkArquivo);
				} else if (arquivo.getTipoArquivo().equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
					item.add(downloadArquivoTXT(arquivo.getAutorizacaoCancelamento()));
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

						/***/
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
				item.add(new Label("status", verificaDownload(arquivo.getStatus()).getLabel().toUpperCase()).setMarkupId(verificaDownload(arquivo.getStatus()).getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final DesistenciaProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = desistenciaProtestoMediator.baixarDesistenciaTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}

			private Link<Remessa> downloadArquivoTXT(final CancelamentoProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = cancelamentoMediator.baixarCancelamentoTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}

			private Link<Remessa> downloadArquivoTXT(final AutorizacaoCancelamento remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = autorizacaoMediator.baixarAutorizacaoTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					}
				};
			}

			private StatusRemessa verificaDownload(Boolean download) {
				if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					return StatusRemessa.ENVIADO;
				}
				if (download.equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		});
	}

	private void converterDesistenciasCancelamentos() {
		if (!getDesistenciaProtesto().isEmpty()) {
			for (DesistenciaProtesto desistenciaProtesto : getDesistenciaProtesto()) {
				ArquivoDesistenciaCancelamentoBean arquivo = new ArquivoDesistenciaCancelamentoBean();
				arquivo.parseDesistenciaProtesto(desistenciaProtesto);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		}

		if (!getCancelamentoProtestos().isEmpty()) {
			for (CancelamentoProtesto cancelamento : getCancelamentoProtestos()) {
				ArquivoDesistenciaCancelamentoBean arquivo = new ArquivoDesistenciaCancelamentoBean();
				arquivo.parseCancelamentoProtesto(cancelamento);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		}

		if (!getAutorizacaoCancelamentos().isEmpty()) {
			for (AutorizacaoCancelamento ac : getAutorizacaoCancelamentos()) {
				ArquivoDesistenciaCancelamentoBean arquivo = new ArquivoDesistenciaCancelamentoBean();
				arquivo.parseAutorizacaoCancelamento(ac);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		}
	}

	public List<DesistenciaProtesto> getDesistenciaProtesto() {
		if (desistenciaProtesto == null) {
			desistenciaProtesto = new ArrayList<DesistenciaProtesto>();
		}
		return desistenciaProtesto;
	}

	public List<CancelamentoProtesto> getCancelamentoProtestos() {
		if (cancelamentoProtestos == null) {
			cancelamentoProtestos = new ArrayList<CancelamentoProtesto>();
		}
		return cancelamentoProtestos;
	}

	public List<AutorizacaoCancelamento> getAutorizacaoCancelamentos() {
		if (autorizacaoCancelamentos == null) {
			autorizacaoCancelamentos = new ArrayList<AutorizacaoCancelamento>();
		}
		return autorizacaoCancelamentos;
	}

	public List<ArquivoDesistenciaCancelamentoBean> getArquivosDesistenciasCancelamentos() {
		if (arquivosDesistenciasCancelamentos == null) {
			arquivosDesistenciasCancelamentos = new ArrayList<ArquivoDesistenciaCancelamentoBean>();
		}
		return arquivosDesistenciasCancelamentos;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
