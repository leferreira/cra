package br.com.ieptbto.cra.page.titulo.historico;

import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoOcorrencia;
import br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao.EnviarSolicitacaoDesistenciaCancelamentoPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author Thasso Araújo
 *
 */
public class AcoesTituloMenu extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloRemessa tituloRemessa;
	private Usuario usuario;

	public AcoesTituloMenu(String id, IModel<TituloRemessa> model, Usuario usuario) {
		super(id, model);
		this.tituloRemessa = model.getObject();
		this.usuario = usuario;

		verificarPermissaoUsuario();
		add(labelMenuAcoes());
		add(linkSolicitarDesistenciaCancelamento());
	}

	private Label labelMenuAcoes() {
		return new Label("labelMenuAcoes", "Menu de Ações");
	}

	private Link<SolicitacaoDesistenciaCancelamento> linkSolicitarDesistenciaCancelamento() {
		Link<SolicitacaoDesistenciaCancelamento> linkSolicitacao = new Link<SolicitacaoDesistenciaCancelamento>("linkSolicitacao") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new EnviarSolicitacaoDesistenciaCancelamentoPage(tituloRemessa));
			}
		};
		String situacaoTitulo = tituloRemessa.getSituacaoTitulo();
		if (situacaoTitulo.equals("ABERTO") || situacaoTitulo.equals(TipoOcorrencia.PROTESTADO.getLabel())) {
		} else {
			linkSolicitacao.setOutputMarkupId(true);
			linkSolicitacao.setEnabled(false);
		}
		return linkSolicitacao;
	}

	private void verificarPermissaoUsuario() {
		TipoInstituicaoCRA tipoInstituicaoUsuario = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicaoUsuario.equals(TipoInstituicaoCRA.CRA)) {
			this.setVisible(true);
		} else {
			this.setVisible(false);
		}
	}
}
