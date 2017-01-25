package br.com.ieptbto.cra.page.home;

import org.apache.wicket.markup.html.panel.Panel;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;

public class InformacoesCraPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	private Usuario usuario;

	public InformacoesCraPanel(String id, Usuario usuario) {
		super(id);
		this.usuario = usuario;

		verificarVisibilidade();
	}

	private void verificarVisibilidade() {
		TipoInstituicaoSistema tipoInstituicao = usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicao.equals(TipoInstituicaoSistema.CRA)) {
			this.setOutputMarkupId(true);
			this.setVisible(false);
		}
	}
}