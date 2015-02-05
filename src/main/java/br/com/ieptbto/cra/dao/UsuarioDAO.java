package br.com.ieptbto.cra.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Usuario;

/**
 * 
 * @author Leandro
 * 
 */
@SuppressWarnings("unchecked")
@Repository
public class UsuarioDAO extends AbstractBaseDAO {

	@Autowired
	GrupoUsuarioDAO grupoUsuarioDAO;

	public Usuario buscarUsuarioPorLogin(String login) {
		Criteria criteria = getCriteria(Usuario.class);
		criteria.add(Restrictions.like("login", login, MatchMode.EXACT));

		return Usuario.class.cast(criteria.uniqueResult());
	}

	public void incluirUsuarioDeTeste() {
		Usuario usuario = new Usuario();
		Transaction transaction = getBeginTransation();
		try {
			usuario.setEmail("teste@teste.com.br");
			usuario.setLogin("teste");
			usuario.setNome("Teste");
			usuario.setSenha(Usuario.cryptPass("teste1234"));
			usuario.setGrupoUsuario(grupoUsuarioDAO.buscarGrupoInicial("Super Administrador"));
			save(usuario);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
		}
	}

	@Transactional(readOnly = true)
	public Usuario criarUsuario(Usuario usuario) {
		Usuario novoUsuario = new Usuario();
		Transaction transaction = getBeginTransation();
		try {
			usuario.setSenha(Usuario.cryptPass(usuario.getSenha()));
			novoUsuario = save(usuario);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novoUsuario;
	}

	@Transactional(readOnly = true)
	public Usuario alterar(Usuario usuario) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(usuario);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return usuario;
	}

	public List<Usuario> buscarUsuario(String login, String nome) {
		Criteria criteria = getCriteria(Usuario.class);

		if (StringUtils.isNotBlank(login)) {
			criteria.add(Restrictions.eq("login", login));
		}

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.like("nome", nome, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc("nome"));

		List<Usuario> listaUsuario = criteria.list();

		return listaUsuario;
	}

}