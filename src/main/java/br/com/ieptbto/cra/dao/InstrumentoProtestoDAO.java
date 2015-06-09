package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.InstrumentoProtesto;

/**
 * @author Thasso Araújo
 *
 */

@Repository
public class InstrumentoProtestoDAO extends AbstractBaseDAO {

	public InstrumentoProtesto salvarInstrumento(InstrumentoProtesto instrumento) {
		InstrumentoProtesto instrumentoSalvo = new InstrumentoProtesto();
		Transaction transaction = getBeginTransation();
		
		try {
			instrumentoSalvo = save(instrumento);
			transaction.commit();

		} catch (Exception ex) {
			transaction.rollback();
			System.out.println(ex.getMessage());
		}
		return instrumentoSalvo;
	}

	@SuppressWarnings("unchecked")
	public List<InstrumentoProtesto> buscarInstrumentosParaSlip() {
		Criteria criteria = getCriteria(InstrumentoProtesto.class);
		criteria.createAlias("titulo", "titulo");
		criteria.createAlias("titulo.remessa", "remessa");
		criteria.add(Restrictions.eq("situacao", false));
		criteria.addOrder(Order.asc("remessa.instituicaoOrigem"));
		criteria.addOrder(Order.asc("titulo.agenciaCodigoCedente"));
		return criteria.list();
	}

}
