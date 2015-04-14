package br.com.ieptbto.cra.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;

@Repository
public class TipoInstituicaoDAO extends AbstractBaseDAO {

	public TipoInstituicao salvar(TipoInstituicao tipoInstituicao, List<TipoArquivo> tiposPermitidos) {
		TipoInstituicao novo = new TipoInstituicao();
		PermissaoEnvio permissao = new PermissaoEnvio();
		List<PermissaoEnvio> listaPermissoes = new ArrayList<PermissaoEnvio>();
		Transaction transaction = getBeginTransation();
		try {
			novo = save(tipoInstituicao);
			permissao.setTipoInstituicao(tipoInstituicao);
			for (TipoArquivo tipoArquivo: tiposPermitidos){
				permissao.setTipoArquivo(tipoArquivo);
				listaPermissoes.add(permissao);
			}
			inserirLista(listaPermissoes);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novo;
	}

	public TipoInstituicao alterar(TipoInstituicao tipoInstituicao, List<TipoArquivo> tiposPermitidos) {
		PermissaoEnvio permissao = new PermissaoEnvio();
		Transaction transaction = getBeginTransation();
		try {
			update(tipoInstituicao);
			permissao.setTipoInstituicao(tipoInstituicao);
			for (TipoArquivo tipoArquivo: tiposPermitidos){
				permissao.setTipoArquivo(tipoArquivo);
				update(permissao);
			}
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return tipoInstituicao;
	}

	public void inserirTipoInstituicaoInicial(String tipo) {
		Transaction transaction = getBeginTransation();
		try {
			TipoInstituicao tipoInstituicao = new TipoInstituicao();
			tipoInstituicao.setTipoInstituicao(tipo);
			save(tipoInstituicao);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> buscarListaTipoInstituicao() {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.add(Restrictions.ne("tipoInstituicao", "Cartório"));
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<TipoInstituicao> listarTodos() {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return criteria.list();
	}

	public TipoInstituicao buscarTipoInstituicao(String tipoInstituicao) {
		Criteria criteria = getCriteria(TipoInstituicao.class);
		if (StringUtils.isNotBlank(tipoInstituicao)) {
			criteria.add(Restrictions.like("tipoInstituicao", tipoInstituicao, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc("tipoInstituicao"));
		return TipoInstituicao.class.cast(criteria.uniqueResult());
	}
	
	@SuppressWarnings("unchecked")
	public List<PermissaoEnvio> permissoesPorTipoInstituicao(TipoInstituicao tipo) {
		Criteria criteria = getCriteria(PermissaoEnvio.class);
		criteria.add(Restrictions.eq("tipoInstituicao", tipo));

		List<PermissaoEnvio> listaTipo = criteria.list();
		return listaTipo;
	}
}
