package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;

@Repository
public class PermissaoEnvioDAO extends AbstractBaseDAO{

	@Transactional(readOnly = true)
	public PermissaoEnvio salvar(PermissaoEnvio permissao ) {
		PermissaoEnvio novasPermissao = new PermissaoEnvio();
		Transaction transaction = getBeginTransation();
		try {
			novasPermissao = save(permissao);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novasPermissao;
	}

	@Transactional(readOnly = true)
	public PermissaoEnvio alterar(PermissaoEnvio permissao) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(permissao);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return permissao;
	}
	
	@SuppressWarnings("unchecked")
	public List<PermissaoEnvio> permissoesPorTipoInstituicao(TipoInstituicao tipo){
		Criteria criteria = getCriteria(PermissaoEnvio.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipo));

		List<PermissaoEnvio> listaTipo = criteria.list();
		flush();
		return listaTipo;
	}
}
