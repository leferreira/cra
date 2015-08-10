package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class CancelamentoDevolvidoCraPanel extends Panel {

	@SpringBean
	RemessaMediator remessasMediator;
	@SpringBean
	DownloadMediator downloadMediator;
	private Instituicao instituicao;
	private TextField<String> dataEnvioInicio;
	private TextField<String> dataEnvioFinal;
	private ArrayList<TipoArquivoEnum> tiposSelect = new ArrayList<TipoArquivoEnum>();
	
	public CancelamentoDevolvidoCraPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao=instituicao;
		add(new Button("botaoEnviar"){

			@Override
			public void onSubmit() {
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				
				try {
					if (dataEnvioInicio.getDefaultModelObject() != null){
						if (dataEnvioFinal.getDefaultModelObject() != null){
							dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						} else
							throw new InfraException("As duas datas devem ser preenchidas.");
					} 
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		});
		add(listViewArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(comboTipoArquivos());
	}
	
	private ListView<Remessa> listViewArquivos(){
		return new ListView<Remessa>("listView", buscarRemessas()) {

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo().getTipoArquivo().getTipoArquivo().constante));
				
				Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {

					public void onClick() {
		            	setResponsePage(new TitulosDoArquivoPage(remessa));  
		            }
		        };
		        linkArquivo.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
		        item.add(linkArquivo);
		        
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getDataRecebimento())));
				item.add(new Label("instituicao", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
				item.add(new LabelValorMonetario<BigDecimal>("valor", remessa.getRodape().getSomatorioValorRemessa()));
				item.add(new Label("status", remessa.getStatusRemessa().getLabel().toUpperCase()).setMarkupId(remessa.getStatusRemessa().getLabel()));
				item.add(downloadArquivo(remessa));
			}
			
			private Link<Arquivo> downloadArquivo(final Remessa remessa) {
				return new Link<Arquivo>("downloadArquivo") {

					@Override
					public void onClick() {
						File file = remessasMediator.baixarRemessaTXT(instituicao, remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
		};
	}

	private IModel<List<Remessa>> buscarRemessas() {
		return new LoadableDetachableModel<List<Remessa>>() {

			@Override
			protected List<Remessa> load() {
				return null;
			}
		};
	}
	
	private CheckBoxMultipleChoice<TipoArquivoEnum> comboTipoArquivos() {
		IChoiceRenderer<TipoArquivoEnum> renderer = new ChoiceRenderer<>("constante");
		List<TipoArquivoEnum> choices = new ArrayList<TipoArquivoEnum>();
		choices.add(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO);
		choices.add(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO);
		choices.add(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO);
		return new CheckBoxMultipleChoice<TipoArquivoEnum>("tipoArquivos",new Model<ArrayList<TipoArquivoEnum>>(tiposSelect), choices, renderer);
	}

	private TextField<String> dataEnvioInicio() {
		dataEnvioInicio = new TextField<String>("dataEnvioInicio", new Model<String>());
		if (dataEnvioInicio.getModelObject() != null) {
			dataEnvioInicio = new TextField<String>("dataEnvioInicio", new Model<String>(dataEnvioInicio.getModelObject()));
		}
		dataEnvioInicio.setRequired(true);
		dataEnvioInicio.setLabel(new Model<String>("intervalo da data do envio"));
		return dataEnvioInicio;
	}
	
	private TextField<String> dataEnvioFinal() {
		dataEnvioFinal = new TextField<String>("dataEnvioFinal", new Model<String>());
		if (dataEnvioFinal.getModelObject() != null) {
			dataEnvioFinal = new TextField<String>("dataEnvioFinal", new Model<String>(dataEnvioFinal.getModelObject()));
		}
		return dataEnvioFinal;
	}
}
