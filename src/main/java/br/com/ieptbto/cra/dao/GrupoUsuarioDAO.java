package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Leandro
 * 
 */
@SuppressWarnings({ "unchecked" })
@Repository
public class GrupoUsuarioDAO extends AbstractBaseDAO {

	@Transactional(readOnly = true)
	public List<GrupoUsuario> buscarTodosPorPermissao(Usuario usuario) {
		Criteria criteria = getCriteria(GrupoUsuario.class);
		if (usuario.getGrupoUsuario() != null) {
			if (usuario.getGrupoUsuario().getGrupo().equals(CraRoles.ADMIN)) {
				criteria.add(Restrictions.not(Restrictions.eq("grupo", CraRoles.SUPER)));
			}

		} else {
			return criteria.list();
		}

		return criteria.list();
	}

	public void inserirGruposCargaInicial(GrupoUsuario grupo) {
		Transaction transaction = getBeginTransation();
		try {
			save(grupo);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}

	}

	public <t> void inserirLista(List<t> lista) {
		// TODO Auto-generated method stub

	}

	public GrupoUsuario buscarGrupo(String grupo) {
		Criteria criteria = getCriteria(GrupoUsuario.class);
		criteria.add(Restrictions.eq("grupo", grupo));
		return GrupoUsuario.class.cast(criteria.uniqueResult());
	}

	public GrupoUsuario buscarGrupoInicial(String grupo) {
		Criteria criteria = getCriteria(GrupoUsuario.class);
		criteria.add(Restrictions.eq("grupo", grupo));
		return GrupoUsuario.class.cast(criteria.uniqueResult());
	}
}
