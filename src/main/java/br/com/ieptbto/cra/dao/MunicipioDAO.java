package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;

@Repository
public class MunicipioDAO extends AbstractBaseDAO {
	
	private static final int CARTORIO = 2;

	public Municipio salvar(Municipio municipio) {
		Municipio novoMunicipio = new Municipio();
		Transaction transaction = getBeginTransation();
		try {
			novoMunicipio = save(municipio);
			transaction.commit();
		} catch (Exception ex) {
			transaction.rollback();
		}
		return novoMunicipio;
	}

	public Municipio alterar(Municipio municipio) {
		Transaction transaction = getBeginTransation();
		try {
			update(municipio);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return municipio;
	}

	public Municipio buscarMunicipioPorNome(String nomeMunicipio) {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.add(Restrictions.like("nomeMunicipio", nomeMunicipio, MatchMode.EXACT));

		return Municipio.class.cast(criteria.uniqueResult());
	}

	@SuppressWarnings("unchecked")
	public List<Municipio> listarTodos() {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.addOrder(Order.asc("nomeMunicipio"));
		return criteria.list();
	}

	public Municipio buscaMunicipioPorCodigoIBGE(Integer codigoMunicipio) {
		Criteria criteria = getCriteria(Municipio.class);
		criteria.add(Restrictions.eq("codigoIBGE", codigoMunicipio));
		return Municipio.class.cast(criteria.uniqueResult());
	}
	
	public Municipio buscarMunicipioDoCartorio(Instituicao cartorio) {
		Criteria criteria = getCriteria(Instituicao.class);
		criteria.createAlias("municipio", "municipio");
		criteria.createAlias("tipoInstituicao", "tipoInstituicao");
		
		criteria.add(Restrictions.eq("tipoInstituicao.id", CARTORIO));
		criteria.add(Restrictions.eq("nomeFantasia", cartorio.getNomeFantasia()));
		criteria.setMaxResults(1);
		Instituicao cartorioBuscado = Instituicao.class.cast(criteria.uniqueResult());
		return cartorioBuscado.getMunicipio();
	}
}
