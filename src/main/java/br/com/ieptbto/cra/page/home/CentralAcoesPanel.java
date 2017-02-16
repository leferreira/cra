package br.com.ieptbto.cra.page.home;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;
import br.com.ieptbto.cra.mediator.LoggerMediator;
import br.com.ieptbto.cra.page.centralDeAcoes.LogCraPage;

public class CentralAcoesPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	LoggerMediator loggerMediator;

	private Usuario usuario;

	public CentralAcoesPanel(String id, Usuario usuario) {
		super(id);
		this.usuario = usuario;

		verificarVisibilidade();
		add(listaUltimasAcoesErros());
	}

	private ListView<LogCra> listaUltimasAcoesErros() {
		return new ListView<LogCra>("listUltimosErrosLog", loggerMediator.buscarUltimosLogDeErros()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<LogCra> item) {
				final LogCra log = item.getModelObject();
				item.add(new Label("instituicao", log.getInstituicao()));
				item.add(new Label("tipoLog", log.getTipoLog().getLabel()).setOutputMarkupId(true)
								.setMarkupId(log.getTipoLog().getIdHtml()));

				if (log.getDescricao() != null) {
					if (log.getDescricao().length() > 80) {
						item.add(new Label("descricao", log.getDescricao().substring(0, 79)).setEscapeModelStrings(false));
					} else {
						item.add(new Label("descricao", log.getDescricao()).setEscapeModelStrings(false));
					}
				} else {
					item.add(new Label("descricao", StringUtils.EMPTY));
				}
				item.add(new Link<LogCra>("descricaoGeral") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new LogCraPage(log));
					}
				});
			}
		};
	}

	private void verificarVisibilidade() {
		TipoInstituicaoSistema tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (!tipoInstituicao.equals(TipoInstituicaoSistema.CRA)) {
			this.setOutputMarkupId(true);
			this.setVisible(false);
		}
	}
}