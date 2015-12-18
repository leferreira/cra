package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

import br.com.ieptbto.cra.bean.ArquivoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class ListaCancelamentoDevolvidoPage extends BasePage<Arquivo> {

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	private Arquivo arquivo;
	private List<ArquivoDesistenciaCancelamento> arquivosDesistenciasCancelamentos;
	private List<DesistenciaProtesto> desistenciaProtesto;
	private List<CancelamentoProtesto> cancelamentoProtestos;
	private List<AutorizacaoCancelamento> autorizacaoCancelamentos;

	public ListaCancelamentoDevolvidoPage(Arquivo arquivo, ArrayList<TipoArquivoEnum> tiposArquivo, Municipio municipio ,LocalDate dataInicio, LocalDate dataFim) {
		this.desistenciaProtesto = remessaMediator.buscarDesistenciaProtesto(arquivo, arquivo.getInstituicaoEnvio(), municipio, dataInicio, dataFim, tiposArquivo, getUser());
		this.cancelamentoProtestos = remessaMediator.buscarCancelamentoProtesto(arquivo, arquivo.getInstituicaoEnvio(), municipio, dataInicio, dataFim, tiposArquivo, getUser());
		this.autorizacaoCancelamentos = remessaMediator.buscarAutorizacaoCancelamento(arquivo, arquivo.getInstituicaoEnvio(), municipio, dataInicio, dataFim, tiposArquivo, getUser());
		
		converterDesistenciasCancelamentos();
		add(carregarListaArquivos());
	}

	private ListView<ArquivoDesistenciaCancelamento> carregarListaArquivos() {
		return new ListView<ArquivoDesistenciaCancelamento>("dataTableRemessa", getArquivosDesistenciasCancelamentos()) {

			@Override
			protected void populateItem(ListItem<ArquivoDesistenciaCancelamento> item) {
				final ArquivoDesistenciaCancelamento arquivoDesistenciaCancelamento = item.getModelObject();
				
				if (arquivoDesistenciaCancelamento.getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
					item.add(downloadArquivoTXT(arquivoDesistenciaCancelamento.getDesistenciaProtesto()));
				} else if (arquivoDesistenciaCancelamento.getTipoArquivo().equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)) {
					item.add(downloadArquivoTXT(arquivoDesistenciaCancelamento.getCancelamentoProtesto()));
				} else if (arquivoDesistenciaCancelamento.getTipoArquivo().equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
					item.add(downloadArquivoTXT(arquivoDesistenciaCancelamento.getAutorizacaoCancelamento()));
				}
				
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					@Override
					public void onClick() {
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivoDesistenciaCancelamento.getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", arquivoDesistenciaCancelamento.getDataEnvio()));
				item.add(new Label("instituicao", arquivoDesistenciaCancelamento.getInstituicao()));
				item.add(new Label("envio", arquivoDesistenciaCancelamento.getEnvio()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(arquivoDesistenciaCancelamento.getCodigoMunicipioDestino()).getNomeFantasia()));
				item.add(new Label("horaEnvio", arquivoDesistenciaCancelamento.getHoraEnvio()));
				item.add(new Label("status", verificaDownload(arquivoDesistenciaCancelamento.getStatus()).getLabel().toUpperCase()).setMarkupId(verificaDownload(arquivoDesistenciaCancelamento.getStatus()).getLabel()));
			}

			private Link<Remessa> downloadArquivoTXT(final DesistenciaProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					@Override
					public void onClick() {
						File file = remessaMediator.baixarDesistenciaTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				}; 
			}
			
			private Link<Remessa> downloadArquivoTXT(final CancelamentoProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					@Override
					public void onClick() {
						File file = remessaMediator.baixarCancelamentoTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
			
			private Link<Remessa> downloadArquivoTXT(final AutorizacaoCancelamento remessa) {
				return new Link<Remessa>("downloadArquivo") {

					@Override
					public void onClick() {
						File file = remessaMediator.baixarAutorizacaoTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
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
		};
	}
	
	private void converterDesistenciasCancelamentos() {
		
		if (!getDesistenciaProtesto().isEmpty()) {
			
			for (DesistenciaProtesto desistenciaProtesto : getDesistenciaProtesto()) {
				ArquivoDesistenciaCancelamento arquivo = new ArquivoDesistenciaCancelamento();
				arquivo.parseDesistenciaProtesto(desistenciaProtesto);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		} 
		
		if (!getCancelamentoProtestos().isEmpty()) {
			
			for (CancelamentoProtesto cancelamento : getCancelamentoProtestos()) {
				ArquivoDesistenciaCancelamento arquivo = new ArquivoDesistenciaCancelamento();
				arquivo.parseCancelamentoProtesto(cancelamento);
				getArquivosDesistenciasCancelamentos().add(arquivo);
			}
		} 
		
		if (!getAutorizacaoCancelamentos().isEmpty()) {
			
			for (AutorizacaoCancelamento ac : getAutorizacaoCancelamentos()) {
				ArquivoDesistenciaCancelamento arquivo = new ArquivoDesistenciaCancelamento();
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
	
	public List<ArquivoDesistenciaCancelamento> getArquivosDesistenciasCancelamentos() {
		if (arquivosDesistenciasCancelamentos == null) {
			arquivosDesistenciasCancelamentos = new ArrayList<ArquivoDesistenciaCancelamento>();
		}
		return arquivosDesistenciasCancelamentos;
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
