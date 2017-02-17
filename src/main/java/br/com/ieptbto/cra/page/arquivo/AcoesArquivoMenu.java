package br.com.ieptbto.cra.page.arquivo;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RemessaMediator;

/**
 * @author Thasso Araújo
 *
 */
public class AcoesArquivoMenu extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;

	private Remessa remessa;
	private Usuario usuario;

	public AcoesArquivoMenu(String id, IModel<Remessa> model, Usuario usuario) {
		super(id, model);
		this.remessa = model.getObject();
		this.usuario = usuario;

		verificarPermissaoUsuario();
		add(labelMenuAcoes());
		add(linkBloquearDownloadArquivo());
	}

	private Label labelMenuAcoes() {
		return new Label("labelMenuAcoes", "Menu de Ações");
	}

	private Link<Remessa> linkBloquearDownloadArquivo() {
		Link<Remessa> linkBloquear = new Link<Remessa>("linkBloquear") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
					if (remessa.getDevolvidoPelaCRA().equals(true)) {
						info("Arquivo de Remessa " + remessa.getArquivo().getNomeArquivo() + ", enviado para "
										+ remessa.getInstituicaoDestino().getNomeFantasia() + " já foi bloqueado anteriormente!");
					} else {
						remessaMediator.alterarParaDevolvidoPelaCRA(remessa);
						remessa.setDevolvidoPelaCRA(true);
						success("Arquivo de Remessa " + remessa.getArquivo().getNomeArquivo() + ", enviado para "
										+ remessa.getInstituicaoDestino().getNomeFantasia() + " bloqueado com sucesso!");
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível bloquear o arquivo! Favor entrar em contato com a CRA...");
				}
			}
		};
		TipoArquivoFebraban tipoArquivo = remessa.getArquivo().getTipoArquivo().getTipoArquivo();
		if (!tipoArquivo.equals(TipoArquivoFebraban.REMESSA) || remessa.getDevolvidoPelaCRA()) {
			linkBloquear.setOutputMarkupId(true);
			linkBloquear.setEnabled(false);
		}
		return linkBloquear;
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
