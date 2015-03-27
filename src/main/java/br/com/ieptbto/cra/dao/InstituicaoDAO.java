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

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class InstituicaoDAO extends AbstractBaseDAO {

	@Autowired
	TipoInstituicaoDAO tipoInstituicaoDAO;
	@Autowired
	MunicipioDAO municipioDAO;

	@Transactional(readOnly = true)
	public Instituicao salvar(Instituicao instituicao) {
		Instituicao nova = new Instituicao();
		Transaction transaction = getBeginTransation();
		try {
			nova = save(instituicao);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return nova;
	}

	@Transactional(readOnly = true)
	public Instituicao alterar(Instituicao instituicao) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(instituicao);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return instituicao;
	}

	public Instituicao buscarCartorioPorMunicipio(String nomeMunicipio) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("municipioCartorio", nomeMunicipio));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	public boolean isInstituicaoAtiva(Instituicao instituicao) {
		if (instituicao.isSituacao()) {
			return true;
		} else {
			return false;
		}
	}

	public void inserirInstituicaoInicial(Municipio municipio) {
		Transaction transaction = getBeginTransation();
		try {
			Instituicao instituicao = new Instituicao();
			instituicao.setNomeFantasia("CRA");
			instituicao.setSituacao(true);
			instituicao.setCnpj("123");
			instituicao.setMunicipio(municipio);
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
		criteria.add(Restrictions.eq("tipoInstituicao.id", 2));
		return criteria.list();
	}

	public Instituicao buscarInstituicaoInicial(String nomeFantasia) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.add(Restrictions.eq("nomeFantasia", nomeFantasia));
		return Instituicao.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public Instituicao getInstituicao(Integer codigoMunicipio) {
		Municipio municipio = municipioDAO.buscaMunicipioPorCodigoIBGE(codigoMunicipio);
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("municipios", "municipio");

		criteria.add(Restrictions.disjunction().add(Restrictions.eq("municipio.id", municipio.getId())));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<Instituicao> result = (List<Instituicao>) criteria.list();

		return result.get(1);
	}
}
