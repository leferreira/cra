package br.com.ieptbto.cra.page.titulo.historico;

import br.com.ieptbto.cra.beans.TituloOcorrenciaBean;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.HistoricoMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Thasso Araújo
 *
 */
public class OcorrenciasTituloPanel extends Panel {

	@SpringBean
	HistoricoMediator historicoMediator;

	private static final long serialVersionUID = 1L;
	private TituloRemessa titulo;

	public OcorrenciasTituloPanel(String id, IModel<TituloRemessa> model) {
		super(id, model);
		this.titulo = model.getObject();
		add(timeLineArquivosOcorrencias());
	}

	private ListView<TituloOcorrenciaBean> timeLineArquivosOcorrencias() {
		return new ListView<TituloOcorrenciaBean>("divListaHistorico", historicoMediator.carregarOrrenciasTitulo(titulo)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloOcorrenciaBean> item) {
				final TituloOcorrenciaBean arquivoOcorrenciaBean = item.getModelObject();

				if (arquivoOcorrenciaBean.getTituloFiliado() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-success"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-edit")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setOutputMarkupId(true);
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", arquivoOcorrenciaBean.getMensagem()));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getRemessa() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);
					Link<Remessa> link = new Link<Remessa>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosArquivoPage(arquivoOcorrenciaBean.getRemessa()));
						}
					};
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getRemessa().getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getBatimento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-warning"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-check-square-o")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setVisible(false);
					item.add(link);
					item.add(new Label("acao", "").setVisible(false));
					item.add(new Label("mensagem", arquivoOcorrenciaBean.getMensagem()).setOutputMarkupId(true)
									.setEscapeModelStrings(false));
				}

				if (arquivoOcorrenciaBean.getInstrumentoProtesto() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-warning"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-list-ul")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", "Instrumento de Protesto: "));
					link.setOutputMarkupId(true);
					link.setVisible(false);
					item.add(link);
					item.add(new Label("acao", "").setVisible(false));
					item.add(new Label("mensagem", arquivoOcorrenciaBean.getMensagem()));
				}

				if (arquivoOcorrenciaBean.getArquivo() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-success"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-send-o")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo liberado para instituição."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getDesistenciaProtesto() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosDesistenciaPage(arquivoOcorrenciaBean.getDesistenciaProtesto()));
						}
					};
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getDesistenciaProtesto().getRemessaDesistenciaProtesto()
									.getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getCancelamentoProtesto() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosCancelamentoPage(arquivoOcorrenciaBean.getCancelamentoProtesto()));
						}
					};
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getCancelamentoProtesto().getRemessaCancelamentoProtesto()
									.getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getAutorizacaoCancelamento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-primary"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-check")));
					item.add(divIcon);

					Link<Arquivo> link = new Link<Arquivo>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
							setResponsePage(new TitulosAutorizacaoCancelamentoPage(arquivoOcorrenciaBean.getAutorizacaoCancelamento()));
						}
					};
					link.add(new Label("conteudoLink", arquivoOcorrenciaBean.getAutorizacaoCancelamento()
									.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					item.add(link);
					item.add(new Label("acao", " Arquivo postado na CRA-TO."));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				if (arquivoOcorrenciaBean.getSolicitacaoCancelamento() != null) {
					WebMarkupContainer divIcon = new WebMarkupContainer("div-icon");
					divIcon.add(new AttributeAppender("class", "timeline-icon bg-danger"));
					divIcon.add(new Label("icon", "").add(new AttributeAppender("class", "fa fa-times")));
					item.add(divIcon);

					Link<Remessa> link = new Link<Remessa>("link") {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClick() {
						}
					};
					link.add(new Label("conteudoLink", ""));
					link.setOutputMarkupId(true);
					link.setEnabled(false);
					item.add(link);
					item.add(new Label("acao", arquivoOcorrenciaBean.getMensagem()).setEscapeModelStrings(false));
					item.add(new Label("mensagem", "").setVisible(false));
				}

				item.add(new Label("data", DataUtil.localDateToString(arquivoOcorrenciaBean.getData())));
				item.add(new Label("hora", DataUtil.localTimeToString("HH:mm:ss", arquivoOcorrenciaBean.getHora())));
				item.add(new Label("usuarioAcao", arquivoOcorrenciaBean.getNomeUsuario()));
			}
		};
	}
}