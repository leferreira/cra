package br.com.ieptbto.cra.page.cnp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.CustomFeedbackPanel;
import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.entidade.TituloCnp;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;

/**
 * @author Thasso Araújo
 *
 */
public class CentralNacionalProtestoPage extends WebPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	CentralNacionalProtestoMediator cnpMediator;

	private TituloCnp tituloCnp;
	private List<String> titulosProtesto;
	private TextField<String> textFieldDocumentoDevedor;
	private boolean visibilidadeDivResultado;

	public CentralNacionalProtestoPage() {
		this.tituloCnp = new TituloCnp();
		this.visibilidadeDivResultado = false;
		carregar();
	}

	public CentralNacionalProtestoPage(TituloCnp titulo, List<String> titulosProtesto) {
		this.tituloCnp = titulo;
		this.titulosProtesto = titulosProtesto;
		this.visibilidadeDivResultado = true;
		carregar();
	}

	private void carregar() {
		addFeedbackPanel();
		formularioConsultaProtesto();
		labelNaoValidoComoCertidao();
		carregarDivResultadoConsulta();
	}

	private void addFeedbackPanel() {
		CustomFeedbackPanel feedBackPanel = new CustomFeedbackPanel("feedback");
		feedBackPanel.setOutputMarkupId(true);
		add(feedBackPanel);
	}

	private void formularioConsultaProtesto() {
		Form<TituloCnp> form = new Form<TituloCnp>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				String documentoDevedor = StringUtils.EMPTY;

				try {
					if (textFieldDocumentoDevedor.getModelObject() != null) {
						documentoDevedor = textFieldDocumentoDevedor.getModelObject();
					}
					if (documentoDevedor.contains(".")) {
						documentoDevedor = documentoDevedor.replace(".", "");
					}
					if (documentoDevedor.contains("/")) {
						documentoDevedor = documentoDevedor.replace("/", "");
					}
					if (documentoDevedor.contains("-")) {
						documentoDevedor = documentoDevedor.replace("-", "");
					}

					setResponsePage(new CentralNacionalProtestoPage(tituloCnp, cnpMediator.consultarProtestos(documentoDevedor)));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível realizar a consulta de protesto ! \n Entre em contato com o IEPTB-TO. ");
				}
			}

		};
		form.add(campoDocumentoDevedor());
		add(form);
	}

	private TextField<String> campoDocumentoDevedor() {
		textFieldDocumentoDevedor = new TextField<String>("documentoDevedor", new Model<String>());
		textFieldDocumentoDevedor.setLabel(new Model<String>("Documento"));
		textFieldDocumentoDevedor.setRequired(true);
		return textFieldDocumentoDevedor;
	}

	private void carregarDivResultadoConsulta() {
		WebMarkupContainer divResultadoConsulta = new WebMarkupContainer("resultado");
		divResultadoConsulta.setOutputMarkupId(true);

		Label labelNaoHaProtestos = new Label("labelNaoHaProtestos",
				"Não constam protestos, por falta de pagamento, para este CPF/CNPJ nos tabelionatos do estado do Tocantins!".toUpperCase());
		labelNaoHaProtestos.setOutputMarkupId(true);
		labelNaoHaProtestos.setVisible(false);
		divResultadoConsulta.add(labelNaoHaProtestos);

		ListView<String> pageableListView = new ListView<String>("resultados", getTitulosProtesto()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				String municipioGuarai = item.getModelObject();
				item.add(new Label("protesto", "CONSTA(M) PROTESTO(S) NO TABELIONATO DE PROTESTO DE TÍTULOS DE " + municipioGuarai + "."));
			}
		};
		pageableListView.setOutputMarkupId(true);
		pageableListView.setVisible(false);
		divResultadoConsulta.add(pageableListView);

		Label labelMensagemDevedor = new Label("prazo",
				"OBS: O prazo para exclusão do registro de protesto da base é de 5 dias após o cancelamento do protesto no cartório".toUpperCase());
		labelMensagemDevedor.setOutputMarkupId(true);
		labelMensagemDevedor.setVisible(false);
		divResultadoConsulta.add(labelMensagemDevedor);

		Label atualizacao = new Label("atualizacao", DataUtil.localDateToString(new LocalDate().minusDays(1)));
		divResultadoConsulta.add(atualizacao);

		if (titulosProtesto != null) {
			if (getTitulosProtesto().isEmpty()) {
				labelNaoHaProtestos.setVisible(true);
			} else {
				labelMensagemDevedor.setVisible(true);
				pageableListView.setVisible(true);
			}
		}
		divResultadoConsulta.setVisible(visibilidadeDivResultado);
		add(divResultadoConsulta);
	}

	private void labelNaoValidoComoCertidao() {
		add(new Label("certidao", "*As informações a seguir referem-se apenas a pesquisa, não tendo validade como certidão!".toUpperCase()));
	}

	public List<String> getTitulosProtesto() {
		if (titulosProtesto == null) {
			titulosProtesto = new ArrayList<String>();
		}
		return titulosProtesto;
	}
}
