package br.com.ieptbto.cra.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;

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
}
