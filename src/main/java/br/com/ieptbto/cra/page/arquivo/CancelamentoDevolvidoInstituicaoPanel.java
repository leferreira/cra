package br.com.ieptbto.cra.page.arquivo;

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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings( {"serial","unused" })
public class CancelamentoDevolvidoInstituicaoPanel extends Panel {

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	ArquivoMediator arquivoMediator;
	private Instituicao instituicao;
	private TextField<String> dataEnvioInicio;
	private TextField<String> dataEnvioFinal;
	private ArrayList<TipoArquivoEnum> tiposSelect = new ArrayList<TipoArquivoEnum>();

	public CancelamentoDevolvidoInstituicaoPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		add(new Button("botaoEnviar") {

			@Override
			public void onSubmit() {
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				try {
					if (dataEnvioInicio.getDefaultModelObject() != null) {
						if (dataEnvioFinal.getDefaultModelObject() != null) {
							dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									new InfraException("A data de início deve ser antes da data fim.");
						} else
							new InfraException("As duas datas devem ser preenchidas.");
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

	@SuppressWarnings("rawtypes")
	private ListView<Arquivo> listViewArquivos() {
		return new ListView<Arquivo>("listView", buscarArquivos()) {

			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				final Arquivo arquivo = item.getModelObject();
				item.add(new Label("tipoArquivo", arquivo.getTipoArquivo().getTipoArquivo().constante));
				Link linkArquivo = new Link("linkArquivo") {

					public void onClick() {
//						setResponsePage(new TitulosDoArquivoPage(arquivo));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio())));
				item.add(new Label("instituicao", arquivo.getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("instituicaoRecebe", arquivo.getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("status", arquivo.getStatusArquivo().getSituacaoArquivo().getLabel().toUpperCase()).setMarkupId(arquivo.getStatusArquivo().getSituacaoArquivo().getLabel()));
				item.add(downloadArquivo(arquivo));
			}

			private Link downloadArquivo(final Arquivo file) {
				return new Link<Arquivo>("downloadArquivo") {

					@Override
					public void onClick() {
//						File file = remessaMediator.baixarArquivoTXT(instituicao, arquivo);
//						IResourceStream resourceStream = new FileResourceStream(file);
//
//						getRequestCycle().scheduleRequestHandlerAfterCurrent(
//						        new ResourceStreamRequestHandler(resourceStream, file.getName()));
					}
				};
			}
		};
	}

	private IModel<List<Arquivo>> buscarArquivos() {
		return new LoadableDetachableModel<List<Arquivo>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Arquivo> load() {
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
