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

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.StatusRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

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
	private List<DesistenciaProtesto> desistenciaProtesto;

	public ListaCancelamentoDevolvidoPage(Arquivo arquivo, ArrayList<TipoArquivoEnum> tiposArquivo, Municipio municipio ,LocalDate dataInicio, LocalDate dataFim) {
		this.desistenciaProtesto = remessaMediator.buscarRemessaDesistenciaProtesto(arquivo, arquivo.getInstituicaoEnvio(), municipio, dataInicio, dataFim, tiposArquivo, getUser());
		add(carregarListaArquivos());
	}

	private ListView<DesistenciaProtesto> carregarListaArquivos() {
		return new ListView<DesistenciaProtesto>("dataTableRemessa", getRemessasCancelamentoDevolucao()) {

			@Override
			protected void populateItem(ListItem<DesistenciaProtesto> item) {
				final DesistenciaProtesto desistencia = item.getModelObject();
				item.add(new Label("tipoArquivo", desistencia.getRemessaDesistenciaProtesto().getArquivo().getTipoArquivo().getTipoArquivo().constante));
				item.add(new Label("nomeArquivo", desistencia.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
				item.add(new Label("dataEnvio", DataUtil.localDateToString(desistencia.getRemessaDesistenciaProtesto().getArquivo().getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(desistencia.getRemessaDesistenciaProtesto().getArquivo().getHoraEnvio())));
				item.add(new Label("instituicao", desistencia.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", instituicaoMediator.getCartorioPorCodigoIBGE(desistencia.getCabecalhoCartorio().getCodigoMunicipio()).getNomeFantasia()));
				item.add(new Label("status", verificaDownload(desistencia).getLabel().toUpperCase()).setMarkupId(verificaDownload(desistencia).getLabel()));
				item.add(downloadArquivoTXT(desistencia));
			}

			private Link<Remessa> downloadArquivoTXT(final DesistenciaProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					@Override
					public void onClick() {
						File file = remessaMediator.baixarRemessaTXT(getUser(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
			
			private StatusRemessa verificaDownload(DesistenciaProtesto desistencia) {
				if (desistencia.getDownload().equals(false)) {
					return StatusRemessa.AGUARDANDO;
				}
				return StatusRemessa.RECEBIDO;
			}
		};
	}

	public List<DesistenciaProtesto> getRemessasCancelamentoDevolucao() {
		return desistenciaProtesto;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
