package br.com.ieptbto.cra.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings({ "unchecked" })
@Repository
public class RemessaDAO extends AbstractBaseDAO {

	public List<Remessa> listarRemessasPorInstituicao(Instituicao instituicao) {
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");
		if (!instituicao.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("instituicaoDestino", instituicao))
					.add(Restrictions.eq("a.instituicaoEnvio", instituicao)));
		}
		criteria.addOrder(Order.desc("a.dataEnvio"));
		return criteria.list();
	}
	
	/**
	 * Buscar 
	 * */
	public List<Remessa> buscarRemessas(Arquivo arquivo, Municipio pracaProtesto, Instituicao portador, LocalDate dataInicio,LocalDate dataFim, Usuario usuarioCorrente){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");
		criteria.createAlias("instituicaoDestino", "d");
		criteria.createAlias("instituicaoOrigem", "o");
		
		if (!usuarioCorrente.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("instituicaoDestino", usuarioCorrente.getInstituicao()))
					.add(Restrictions.eq("instituicaoOrigem", usuarioCorrente.getInstituicao())));
		}
		if (StringUtils.isNotBlank(arquivo.getNomeArquivo())){ 
			criteria.add(Restrictions.ilike("a.nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		}
		if (portador != null){
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("instituicaoOrigem", portador)).add(Restrictions.eq("instituicaoDestino", portador)));
		}
		if (pracaProtesto != null){
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("d.municipio", pracaProtesto)).add(Restrictions.eq("o.municipio", pracaProtesto)));
		}
		criteria.add(Restrictions.between("a.dataEnvio", dataInicio, dataFim));
		criteria.addOrder(Order.desc("a.dataEnvio"));
		return criteria.list();
	}
}
