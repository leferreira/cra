package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.dao.UsuarioDAO;
import br.com.ieptbto.cra.entidade.Usuario;

@Service
public class UsuarioMediator {

	@Autowired
	UsuarioDAO usuarioDao;

	public Usuario autenticar(String login, String senha) {
		Usuario usuario = usuarioDao.buscarUsuarioPorLogin(login);
		if (usuario != null && usuario.isSenha(senha)) {
			return usuario;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<Usuario> buscarUsuario(String login, String nome) {
		return usuarioDao.buscarUsuario(login, nome);
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

	public void remover(Usuario usuario) {
	}

	/**
	 * Verifica se o login que será criado não existe no sistema
	 * 
	 * @param usuario
	 * @return
	 */
	public boolean isLoginNaoExiste(Usuario usuario) {
		Usuario usuarioPesquisado = usuarioDao.buscarUsuarioPorLogin(usuario
				.getLogin());
		if (usuarioPesquisado == null) {
			return true;
		}
		return false;
	}

	public boolean trocarSenha(String senha, String novaSenha,
			String confirmaSenha, Usuario usuario) {
		return false;

	}

	public void cargaInicial() {
		usuarioDao.incluirUsuarioDeTeste();

	}

	public List<Usuario> listarTodos() {
		return usuarioDao.listarTodosUsuarios();
	}

}
