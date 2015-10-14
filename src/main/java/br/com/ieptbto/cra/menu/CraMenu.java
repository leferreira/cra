package br.com.ieptbto.cra.menu;

import org.apache.wicket.markup.html.panel.Panel;

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

	public CraMenu(String id) {
		super(id);
		Menu menu = new Menu("CraMenu");
		adicionarMenuLateral(menu);
		// adicionarMenuAgenda(menu);
		// adicionarMenuClinico(menu);
		// adicionarMenuRelatorio(menu);

		add(menu);
	}

	private void adicionarMenuLateral(Menu menu) {
		String[] rolesIncluir = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER };
		String[] rolesPesquisar = { CraRoles.USER };

		MenuItem menuLateral = menu.addItem("menuLateral", rolesPesquisar);
		/** Menus arquivos */
		menuLateral.addItem("BuscarArquivo", rolesPesquisar);
		menuLateral.addItem("EnviarArquivo", rolesPesquisar);
		menuLateral.addItem("ArquivosCancelamentoDevolvidoPage", rolesPesquisar);

		/** Menus titulos */
		menuLateral.addItem("MonitorarTitulos", rolesPesquisar);

		/** Menus Relatorios */
//		menuLateral.addItem("RelatorioSintetico", rolesIncluir);
		menuLateral.addItem("RelatorioArquivosTitulos", rolesIncluir);

		/** Menus Admins */
		menuLateral.addItem("Batimento", rolesIncluir);
		menuLateral.addItem("RemessasConvenio", rolesIncluir);
		menuLateral.addItem("ConfirmacaoPage", rolesIncluir);
		menuLateral.addItem("RetornoPage", rolesIncluir);
		// menuLateral.addItem("TipoArquivoPage", rolesIncluir);
		menuLateral.addItem("CartoriosPage", rolesIncluir);
		menuLateral.addItem("InstituicoesPage", rolesIncluir);
		menuLateral.addItem("MunicipiosPage", rolesIncluir);
		menuLateral.addItem("TipoInstituicoesPage", rolesIncluir);
		menuLateral.addItem("UsuariosPage", rolesIncluir);
		menuLateral.addItem("FiliadosPage", rolesIncluir);

		/** Instrumento de Protesto */
		menuLateral.addItem("InstrumentoDeProtesto", rolesIncluir);
		menuLateral.addItem("GerarSlip", rolesIncluir);
		menuLateral.addItem("ImportarArquivoDePara", rolesIncluir);
		menuLateral.addItem("BuscarInstrumentoProtesto", rolesIncluir);
		menuLateral.addItem("LiberarEnvelopes", rolesIncluir);
	}

}
