package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
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
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
public class ArquivosInstituicaoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	ArquivoMediator arquivoMediator;

	private Instituicao instituicao;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private TextField<String> dataEnvioInicio;
	private TextField<String> dataEnvioFinal;
	private ArrayList<String> tiposSelect = new ArrayList<String>();
	private ArrayList<String> statusSelect = new ArrayList<String>();

	public ArquivosInstituicaoPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		add(new Button("botaoEnviar") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				try {
					if (dataEnvioInicio.getDefaultModelObject() != null) {
						if (dataEnvioFinal.getDefaultModelObject() != null) {
							dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									error("A data de início deve ser antes da data fim.");
						} else
							error("As duas datas devem ser preenchidas.");
					}
				} catch (Exception e) {
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		});
		add(listViewArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
		add(comboTipoArquivos());
		add(comboStatus());
	}

	@SuppressWarnings("rawtypes")
	private ListView<Arquivo> listViewArquivos() {
		return new ListView<Arquivo>("listView", buscarArquivos()) {
			/** */
			private static final long serialVersionUID = -3365063971696545653L;

			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				final Arquivo arquivo = item.getModelObject();
				item.add(new Label("tipoArquivo", arquivo.getTipoArquivo().getTipoArquivo().constante));

				Link linkArquivo = new Link("linkArquivo") {
					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new TitulosDoArquivoPage(arquivo));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
				item.add(linkArquivo);

				item.add(new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio())));
				item.add(new Label("instituicao", arquivo.getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("instituicaoRecebe", arquivo.getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("status", arquivo.getStatusArquivo().getSituacaoArquivo().getLabel()));
				item.add(downloadArquivo(arquivo));
			}

			@SuppressWarnings("serial")
			private Link downloadArquivo(final Arquivo file) {
				return new Link<Arquivo>("downloadArquivo") {

					@Override
					public void onClick() {

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
				return arquivoMediator.buscarArquivosPorInstituicao(instituicao, tiposSelect, statusSelect, dataInicio, dataFim);
			}
		};
	}

	private Component comboTipoArquivos() {
		List<String> choices = new ArrayList<String>();
		List<TipoArquivoEnum> enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.constante);
		}
		return new CheckBoxMultipleChoice<String>("tipoArquivos", new Model<ArrayList<String>>(tiposSelect), choices);
	}

	private Component comboStatus() {
		List<String> choices = new ArrayList<String>(Arrays.asList(new String[] { "Enviado", "Recebido", "Aguardando" }));
		return new CheckBoxMultipleChoice<String>("statusArquivos", new Model<ArrayList<String>>(statusSelect), choices);
	}

	private TextField<String> dataEnvioInicio() {
		if (dataInicio != null)
			dataEnvioInicio = new TextField<String>("dataEnvioInicio", new Model<String>(DataUtil.localDateToString(dataInicio)));
		else
			dataEnvioInicio = new TextField<String>("dataEnvioInicio", new Model<String>());

		dataEnvioInicio.setRequired(true);
		dataEnvioInicio.setLabel(new Model<String>("intervalo da data do envio"));
		return dataEnvioInicio;
	}

	private TextField<String> dataEnvioFinal() {
		if (dataFim != null)
			dataEnvioFinal = new TextField<String>("dataEnvioFinal", new Model<String>(DataUtil.localDateToString(dataFim)));
		else
			dataEnvioFinal = new TextField<String>("dataEnvioFinal", new Model<String>());
		return dataEnvioFinal;
	}
}
