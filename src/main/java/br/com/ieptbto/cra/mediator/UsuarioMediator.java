package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.GrupoUsuarioDAO;
import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.dao.TipoInstituicaoDAO;
import br.com.ieptbto.cra.dao.UsuarioDAO;
import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.security.CraRoles;

@Service
public class UsuarioMediator {

	private static final Logger logger = Logger.getLogger(UsuarioMediator.class);
	@Autowired
	UsuarioDAO usuarioDao;
	@Autowired
	InstituicaoDAO instituicaoDao;
	@Autowired
	TipoInstituicaoDAO tipoInstituicaoDao;
	@Autowired
	GrupoUsuarioDAO grupoUsuarioDao;
	@Autowired
	TipoArquivoDAO tipoArquivoDao;
	@Autowired
	MunicipioMediator municipioMediator;

	public Usuario autenticar(String login, String senha) {
		Usuario usuario = usuarioDao.buscarUsuarioPorLogin(login);
		if (usuario != null && usuario.isSenha(senha)) {
			if (instituicaoDao.isInstituicaoAtiva(usuario.getInstituicao())) {
				if (usuario.isStatus() == true) {
					logger.info("O usuário <<" + usuario.getLogin() + ">> entrou na CRA.");
					return usuario;
				}
			}
		}
		return null;
	}

	public Usuario alterar(Usuario usuario) {
		return usuarioDao.alterar(usuario);
	}

	public Usuario salvar(Usuario usuario) {
		if (isLoginNaoExiste(usuario)) {
			return usuarioDao.criarUsuario(usuario);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<Usuario> buscarUsuario(String login, String nome) {
		return usuarioDao.buscarUsuario(login, nome);
	}

	/**
	 * Verificar se as senha e a confirmação da senha coincidem
	 * 
	 * @param usuario
	 * @return
	 * */
	public boolean isSenhasIguais(Usuario usuario) {
		if (usuario.getSenha().equals(usuario.getConfirmarSenha())) {
			System.out.println("true");
			return true;
		} else {
			System.out.println("false");
			return false;
		}
	}

	/**
	 * Verifica se o login que será criado não existe no sistema
	 * 
	 * @param usuario
	 * @return
	 */
	public boolean isLoginNaoExiste(Usuario usuario) {
		Usuario usuarioPesquisado = usuarioDao.buscarUsuarioPorLogin(usuario.getLogin());
		if (usuarioPesquisado == null) {
			return true;
		}
		return false;
	}

	public boolean trocarSenha(String senha, String novaSenha, String confirmaSenha, Usuario usuario) {
		return false;

	}

	public void cargaInicial() {
		/*
		 * Inserindo os Tipos da Instituição
		 */
		tipoInstituicaoDao.inserirTipoInstituicaoInicial("CRA");
		tipoInstituicaoDao.inserirTipoInstituicaoInicial("Cartório");
		tipoInstituicaoDao.inserirTipoInstituicaoInicial("Instituições Financeiras");

		/*
		 * Inserindo os Tipos da Instituição
		 */
		tipoArquivoDao.inserirTipoArquivo("B");
		tipoArquivoDao.inserirTipoArquivo("C");
		tipoArquivoDao.inserirTipoArquivo("R");
		tipoArquivoDao.inserirTipoArquivo("CP");
		tipoArquivoDao.inserirTipoArquivo("DP");
		tipoArquivoDao.inserirTipoArquivo("AC");

		/*
		 * Inserindo a instituição CRA
		 */
//		Municipio m = new Municipio();
//		m.setNomeMunicipio("Palmas");
//		m.setUf("TO");
//		m.setCodigoIBGE(1721000);
//		m.setSituacao(true);
//		Municipio m = municipioMediator.buscarMunicipio("Palmas");
//		instituicaoDao.inserirInstituicaoInicial(m);

		/*
		 * Inserindo os Grupos dos Usuário e as Permissões
		 */
		GrupoUsuario grupo1 = new GrupoUsuario();
		grupo1.setGrupo("Super Administrador");
		String[] roles = { CraRoles.ADMIN, CraRoles.USER, CraRoles.SUPER };
		grupo1.setRoles(new Roles(roles));
		grupoUsuarioDao.inserirGruposCargaInicial(grupo1);

		GrupoUsuario grupo2 = new GrupoUsuario();
		grupo2.setGrupo("Administrador");
		String[] roles1 = { Roles.ADMIN, CraRoles.USER };
		grupo2.setRoles(new Roles(roles1));
		grupoUsuarioDao.inserirGruposCargaInicial(grupo2);

		GrupoUsuario grupo3 = new GrupoUsuario();
		grupo3.setGrupo("Usuário");
		String[] roles2 = { CraRoles.USER };
		grupo3.setRoles(new Roles(roles2));
		grupoUsuarioDao.inserirGruposCargaInicial(grupo3);

		/*
		 * Inserindo o usuário de teste
		 */
//		usuarioDao.incluirUsuarioDeTeste();
	}

	public List<Usuario> listarTodos() {
		return usuarioDao.listarTodosUsuarios();
	}

	public boolean isUsuatioAtivo(Usuario usuario) {
		return usuario.isStatus();
	}

}
