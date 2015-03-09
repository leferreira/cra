package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Municipio;

@Repository
public class MunicipioDAO extends AbstractBaseDAO{

	@SuppressWarnings("unchecked")
	public List<Municipio> listarTodos(){
		Criteria criteria = getCriteria(Municipio.class);
		criteria.addOrder(Order.asc("nomeMunicipio"));
		return criteria.list();
	}
}
