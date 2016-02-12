package br.com.ieptbto.cra.menu;

import org.apache.wicket.markup.html.panel.Panel;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.PermissaoUsuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 */
public class CraMenu extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Usuario usuario;

	public CraMenu(String id, Usuario usuario) {
		super(id);
		Menu menu = new Menu("CraMenu");
		this.usuario = usuario;
		adicionarMenuLateral(menu);
		add(menu);
	}

	private void adicionarMenuLateral(Menu menu) {
		String[] rolesIncluir = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER };
		String[] rolesPesquisar = { CraRoles.USER };

		/** Menus Padrão */
		MenuItem menuPadrao = menu.addItem("menuPadrao", rolesPesquisar);
		menuPadrao.addItem("BuscarArquivo", rolesPesquisar);
		menuPadrao.addItem("EnviarArquivo", rolesPesquisar);
		menuPadrao.addItem("EnviarArquivoEmpresa", rolesPesquisar);
		menuPadrao.addItem("ArquivosCancelamentoDevolvidoPage", rolesPesquisar);
		menuPadrao.addItem("MonitorarTitulos", rolesPesquisar);
		
		/** Relatorio Padrão */
		MenuItem menuRelatorioPadrao = menu.addItem("menuRelatorioPadrao", rolesPesquisar);
		if (usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			menuRelatorioPadrao.setVisible(false);
		} else {
			menuRelatorioPadrao.setVisible(true);
		}
		menuRelatorioPadrao.addItem("RelatorioArquivosInstituicoesCartorios", rolesPesquisar);
		
		/** Relatorio Cra*/
		MenuItem menuRelatorioCra = menu.addItem("menuRelatorioCra", rolesPesquisar);
		if (usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			menuRelatorioCra.setVisible(true);
		} else {
			menuRelatorioCra.setVisible(false);
		}
		menuRelatorioCra.addItem("Relatorio", rolesIncluir);
		menuRelatorioCra.addItem("RelatorioCustasCra", rolesIncluir);
		menuRelatorioCra.addItem("RelatorioTitulos", rolesIncluir);
//		menuRelatorioCra.addItem("RelatorioBatimento", rolesIncluir);
		
		/** Menus Administrador */
		MenuItem menuAdministrador = menu.addItem("menuAdministrador", rolesIncluir);
		menuAdministrador.setVisible(verificaPermissao(usuario));
		menuAdministrador.addItem("Batimento", rolesIncluir);
		menuAdministrador.addItem("ImportarExtrato", rolesIncluir);
		menuAdministrador.addItem("BuscarDepositos", rolesIncluir);
		menuAdministrador.addItem("LiberarRetornos", rolesIncluir);
		
		menuAdministrador.addItem("GerarRemessasConvenio", rolesIncluir);
		menuAdministrador.addItem("GerarConfirmacao", rolesIncluir);
		menuAdministrador.addItem("GerarRetorno", rolesIncluir);
		
		menuAdministrador.addItem("Instituicoes", rolesIncluir);
		menuAdministrador.addItem("CartoriosProtesto", rolesIncluir);
		menuAdministrador.addItem("EmpresasFiliadas", rolesIncluir);
		menuAdministrador.addItem("LayoutsPersonalizados", rolesIncluir);
		menuAdministrador.addItem("Municipios", rolesIncluir);
		menuAdministrador.addItem("TipoInstituicoes", rolesIncluir);
		menuAdministrador.addItem("Usuarios", rolesIncluir);
		
		menuAdministrador.addItem("InstrumentoDeProtesto", rolesIncluir);
		menuAdministrador.addItem("GerarSlip", rolesIncluir);
//		menuAdministrador.addItem("BuscarInstrumentoProtesto", rolesIncluir);
//		menuAdministrador.addItem("LiberarEnvelopes", rolesIncluir);
		menuAdministrador.addItem("ImportarArquivoDePara", rolesIncluir);
	}

	private boolean verificaPermissao(Usuario user) {
		
		if (user.getGrupoUsuario().getGrupo().equals(PermissaoUsuario.SUPER_ADMINISTRADOR.getLabel()) ||
				user.getGrupoUsuario().getGrupo().equals(PermissaoUsuario.ADMINISTRADOR.getLabel())) {
			return true;
		}
		return false;
	}
}
