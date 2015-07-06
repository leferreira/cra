package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.math.BigDecimal;
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

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaArquivosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	@SpringBean
	DownloadMediator downloadMediator;

	private Arquivo arquivo;
	private List<Remessa> remessas;

	public ListaArquivosPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<String> tiposArquivo) {
		this.arquivo = arquivo;
		this.remessas = remessaMediator.buscarRemessaAvancado(arquivo, municipio,dataInicio, dataFim, tiposArquivo, getUser());
		add(carregarListaArquivos());
	}

	private ListView<Remessa> carregarListaArquivos() {
		return new ListView<Remessa>("dataTableRemessa", remessas) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo().getTipoArquivo().getTipoArquivo().constante));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {
					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosDoArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
				item.add(new Label("instituicao", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
				item.add(new LabelValorMonetario<BigDecimal>("valor", remessa.getRodape().getSomatorioValorRemessa()));
				item.add(new Label("status", remessa.getStatusRemessa().getLabel()).setMarkupId(remessa.getStatusRemessa().getLabel()));
				item.add(downloadArquivoTXT(remessa));
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {
					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = remessaMediator.baixarRemessaTXT(getUser().getInstituicao(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
		};
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
