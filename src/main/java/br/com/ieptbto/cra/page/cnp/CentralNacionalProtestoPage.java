package br.com.ieptbto.cra.page.cnp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.bean.TituloProtestoBean;
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
	private List<TituloProtestoBean> titulosProtesto;

	private String randomText;
	private TextField<String> textFieldDocumentoDevedor;
	private CaptchaImageResource captchaImageResource;
	private TextField<String> textFieldCaptchaText;
	private boolean visibilidadeDivResultado;

	public CentralNacionalProtestoPage() {
		this.tituloCnp = new TituloCnp();
		this.visibilidadeDivResultado = false;
		carregar();
	}

	public CentralNacionalProtestoPage(TituloCnp titulo, List<TituloProtestoBean> titulosProtesto) {
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
		FeedbackPanel feedBackPanel = new FeedbackPanel("feedback");
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
				String captchaText = StringUtils.EMPTY;

				try {
					if (textFieldDocumentoDevedor.getModelObject() != null) {
						documentoDevedor = textFieldDocumentoDevedor.getModelObject();
					}
					if (textFieldCaptchaText.getModelObject() != null) {
						captchaText = textFieldCaptchaText.getModelObject();
					}

					System.out.println(captchaText);
					System.out.println(randomText);
					if (!randomText.equalsIgnoreCase(captchaText)) {
						throw new InfraException("Texto de Verificação Incorreto. Por favor digite novamente!");
					} else {
						setResponsePage(new CentralNacionalProtestoPage(tituloCnp, cnpMediator.consultarProtestos(documentoDevedor)));
					}
					captchaImageResource.invalidate();
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível realizar a consulta de protesto ! \n Entre em contato com o IEPTB-TO. ");
				}
			}

		};
		form.add(campoDocumentoDevedor());

		captchaImageResource = new CaptchaImageResource() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected byte[] render() {
				randomText = CaptchaGenerate.randomString(6, 6);
				getChallengeIdModel().setObject(randomText);
				return super.render();
			}
		};

		final Image captchaImage = new Image("image", captchaImageResource);
		captchaImage.setOutputMarkupId(true);
		form.add(captchaImage);

		AjaxLink<Void> changeCaptchaLink = new AjaxLink<Void>("changeLink") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				captchaImageResource.invalidate();
				target.add(captchaImage);
			}
		};
		form.add(changeCaptchaLink);

		form.add(captchTextField());
		add(form);
	}

	private TextField<String> captchTextField() {
		textFieldCaptchaText = new TextField<String>("text", new Model<String>());
		textFieldCaptchaText.setRequired(true);
		textFieldCaptchaText.setLabel(new Model<String>("Texto de Verificação"));
		return textFieldCaptchaText;
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

		Label labelNaoHaProtestos = new Label("labelNaoHaProtestos", "Não constam protestos por falta de pagamento nos tabelionatos de protestos do Tocantins!".toUpperCase());
		labelNaoHaProtestos.setOutputMarkupId(true);
		labelNaoHaProtestos.setVisible(false);

		PageableListView<TituloProtestoBean> pageableListView = new PageableListView<TituloProtestoBean>("resultados", getTitulosProtesto(), 5) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloProtestoBean> item) {
				TituloProtestoBean tituloProtesto = item.getModelObject();
				item.add(new Label("nomeDevedor", tituloProtesto.getNomeDevedor()));
			}
		};
		pageableListView.setOutputMarkupId(true);
		pageableListView.setVisible(false);
		divResultadoConsulta.add(new PagingNavigator("pager", pageableListView));
		divResultadoConsulta.add(pageableListView);

		if (titulosProtesto != null) {
			if (getTitulosProtesto().isEmpty()) {
				labelNaoHaProtestos.setVisible(true);
			} else {
				pageableListView.setVisible(true);
			}
		}
		divResultadoConsulta.setVisible(visibilidadeDivResultado);
		divResultadoConsulta.add(labelNaoHaProtestos);
		add(divResultadoConsulta);
	}

	private void labelNaoValidoComoCertidao() {
		Label labelCertidao = new Label("certidao", "As informações a seguir referem-se apenas a pesquisa, não tendo validade como certidão!".toUpperCase());
		add(labelCertidao);
	}

	public List<TituloProtestoBean> getTitulosProtesto() {
		if (titulosProtesto == null) {
			titulosProtesto = new ArrayList<TituloProtestoBean>();
		}
		return titulosProtesto;
	}
}
