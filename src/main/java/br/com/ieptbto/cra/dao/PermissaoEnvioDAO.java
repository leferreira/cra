package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.exception.InfraException;

@Repository
public class PermissaoEnvioDAO extends AbstractBaseDAO {

	public PermissaoEnvio salvar(PermissaoEnvio permissao) {
		PermissaoEnvio novasPermissao = new PermissaoEnvio();
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			novasPermissao = save(permissao);
			transaction.commit();
			logger.info("As permissões para o tipo " + permissao.getTipoInstituicao().getTipoInstituicao() + " foram salvas no banco!");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível atribuir as permissões ao tipo "
			        + permissao.getTipoInstituicao().getTipoInstituicao() + ", informe a CRA!");
		}
		return novasPermissao;
	}

	public PermissaoEnvio alterar(PermissaoEnvio permissao) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			update(permissao);
			transaction.commit();

			logger.info("As permissões para o tipo " + permissao.getTipoInstituicao().getTipoInstituicao() + " foram alteradas no banco!");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível alterar as permissões ao tipo."
			        + permissao.getTipoInstituicao().getTipoInstituicao() + ", informe a CRA!");
		}
		return permissao;
	}

	@SuppressWarnings("unchecked")
	public List<PermissaoEnvio> permissoesPorTipoInstituicao(TipoInstituicao tipo) {
		Criteria criteria = getCriteria(PermissaoEnvio.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipo));

		List<PermissaoEnvio> listaTipo = criteria.list();
		return listaTipo;
	}
}
