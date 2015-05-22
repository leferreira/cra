package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class InstituicaoDAO extends AbstractBaseDAO {

	private static final int CARTORIO = 2;
	@Autowired
	TipoInstituicaoDAO tipoInstituicaoDAO;
	@Autowired
	MunicipioDAO municipioDAO;

	public Instituicao salvar(Instituicao instituicao) {
		Instituicao nova = new Instituicao();
		Transaction transaction = getBeginTransation();
		try {
			nova = save(instituicao);
			transaction.commit();
			logger.info(instituicao.getTipoInstituicao().getTipoInstituicao() + " foi salvo na base de dados. ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esses dados na base.");
		}
		return nova;
	}

	public Instituicao alterar(Instituicao instituicao) {
		Session session = getSession();
		session.clear();
		session.flush();
		Transaction transaction = session.beginTransaction();
		try {
			update(instituicao);
			transaction.commit();
			logger.info(instituicao.getTipoInstituicao().getTipoInstituicao() + " foi alterado na base de dados. ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível alterar esses dados na base.");
		}
		return instituicao;
	}

	public Instituicao buscarCartorioPorMunicipio(String nomeMunicipio) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("tipoInstituicao", "t");
		criteria.createAlias("municipio", "m");

		criteria.add(Restrictions.and(Restrictions.eq("t.tipoInstituicao", "Cartório"), Restrictions.eq("m.nomeMunicipio", nomeMunicipio)));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	public boolean isInstituicaoAtiva(Instituicao instituicao) {
		if (instituicao.isSituacao()) {
			return true;
		} else {
			return false;
		}
	}

	public void inserirInstituicaoInicial(Municipio muMunicipio) {
		Transaction transaction = getBeginTransation();
		try {
			Instituicao instituicao = new Instituicao();
			instituicao.setNomeFantasia("CRA");
			instituicao.setSituacao(true);
			instituicao.setCnpj("123");
			instituicao.setMunicipio(muMunicipio);
			instituicao.setRazaoSocial("CRA");
			instituicao.setTipoInstituicao(tipoInstituicaoDAO.buscarTipoInstituicao("CRA"));
			save(instituicao);

			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * Busca uma instituicao ou cartório
	 * 
	 * @return Instituicao
	 * */
	public Instituicao buscarInstituicao(Instituicao instituicao) {
		return buscarInstituicao(instituicao.getNomeFantasia());
	}

	/**
	 * Busca uma instituicao ou cartório
	 * 
	 * @return Instituicao
	 * */
	public Instituicao buscarInstituicao(String nome) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("nomeFantasia", nome));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	/**
	 * Buscar todos as instituicões, ativas ou não, menos cartórios
	 * 
	 * @return List<Instituicao>
	 * */
	@SuppressWarnings("unchecked")
	public List<Instituicao> buscarListaInstituicao() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("id"));
		criteria.add(Restrictions.ne("tipoInstituicao.id", 2));
		return criteria.list();
	}

	/**
	 * Buscar todos as instituições ativas, menos cartórios
	 * 
	 * @return List<Instituicao>
	 * */
	@SuppressWarnings("unchecked")
	public List<Instituicao> buscarListaInstituicaoAtivas() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("id"));
		criteria.add(Restrictions.ne("tipoInstituicao.id", 2));
		criteria.add(Restrictions.eq("situacao", true));
		return criteria.list();
	}

	/**
	 * Buscar todas as instituicões e cartórios cadastrados
	 * 
	 * @return List<Instituicao>
	 * */
	@SuppressWarnings("unchecked")
	public List<Instituicao> buscarCartoriosInstituicoes() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("nomeFantasia"));
		return criteria.list();
	}

	/**
	 * Buscar todos os cartórios, ativos ou não e menos as instituicões
	 * 
	 * @return List<Instituicao>
	 * */
	@SuppressWarnings("unchecked")
	public List<Instituicao> buscarListaCartorio() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("id"));
		criteria.createAlias("tipoInstituicao", "t");
		criteria.add(Restrictions.eq("t.id", 2));
		return criteria.list();
	}

	/**
	 * Buscar todos instituicões financieiras, ativos ou não
	 * 
	 * @return List<Instituicao>
	 * */
	@SuppressWarnings("unchecked")
	public List<Instituicao> getInstituicoesFinanceiras() {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.addOrder(Order.asc("nomeFantasia"));
		criteria.createAlias("tipoInstituicao", "t");
		criteria.add(Restrictions.eq("t.id", 3));
		return criteria.list();
	}

	public Instituicao buscarInstituicaoInicial(String nomeFantasia) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("nomeFantasia", nomeFantasia));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	@Transactional(readOnly = true)
	public Instituicao getInstituicao(Integer codigoMunicipio) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("municipio", "municipio");
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		criteria.add(Restrictions.eq("municipio.codigoIBGE", codigoMunicipio));
		criteria.add(Restrictions.eq("tipoInstituicao.id", CARTORIO));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	@Transactional(readOnly = true)
	public Instituicao getInstituicaoPorCodigo(String codigoCompensacao) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("codigoCompensacao", codigoCompensacao));

		return Instituicao.class.cast(criteria.uniqueResult());
	}
}
