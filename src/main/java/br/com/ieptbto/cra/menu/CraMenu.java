package br.com.ieptbto.cra.menu;

import org.apache.wicket.markup.html.panel.Panel;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.PermissaoUsuario;
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
		menuPadrao.addItem("RelatorioArquivosTitulos", rolesPesquisar);
		
		/** Menus Administrador */
		MenuItem menuAdministrador = menu.addItem("menuAdministrador", rolesIncluir);
		menuAdministrador.setVisible(verificaPermissao(usuario));
		menuAdministrador.addItem("Batimento", rolesIncluir);
		menuAdministrador.addItem("ImportarExtrato", rolesIncluir);
		menuAdministrador.addItem("RetornoAguardando", rolesIncluir);
		menuAdministrador.addItem("RemessasConvenio", rolesIncluir);
		menuAdministrador.addItem("ConfirmacaoPage", rolesIncluir);
		menuAdministrador.addItem("RetornoPage", rolesIncluir);
		menuAdministrador.addItem("CartoriosPage", rolesIncluir);
		menuAdministrador.addItem("InstituicoesPage", rolesIncluir);
		menuAdministrador.addItem("MunicipiosPage", rolesIncluir);
		menuAdministrador.addItem("TipoInstituicoesPage", rolesIncluir);
		menuAdministrador.addItem("UsuariosPage", rolesIncluir);
		menuAdministrador.addItem("FiliadosPage", rolesIncluir);
		menuAdministrador.addItem("EmpresasLayout", rolesIncluir);
		menuAdministrador.addItem("InstrumentoDeProtesto", rolesIncluir);
		menuAdministrador.addItem("GerarSlip", rolesIncluir);
		menuAdministrador.addItem("ImportarArquivoDePara", rolesIncluir);
		menuAdministrador.addItem("BuscarInstrumentoProtesto", rolesIncluir);
		menuAdministrador.addItem("LiberarEnvelopes", rolesIncluir);
	}

	private boolean verificaPermissao(Usuario user) {
		
		if (user.getGrupoUsuario().getGrupo().equals(PermissaoUsuario.SUPER_ADMINISTRADOR.getLabel()) ||
				user.getGrupoUsuario().getGrupo().equals(PermissaoUsuario.ADMINISTRADOR.getLabel())) {
			return true;
		}
		return false;
	}
}
