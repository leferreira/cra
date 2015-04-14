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
	
	public List<Remessa> remessasPorIntervaloDeDatas(Arquivo arquivo, Municipio pracaProtesto, Instituicao portador, LocalDate dataInicio,LocalDate dataFim, Usuario usuarioCorrente){
		Criteria criteria = getCriteria(Remessa.class);
		criteria.createAlias("arquivo", "a");
		criteria.createAlias("a.instituicaoEnvio", "instituicaoEnvio");
		criteria.createAlias("instituicaoDestino", "instituicaoDestino");
		criteria.createAlias("instituicaoDestino.municipio", "m");
		if (!usuarioCorrente.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("instituicaoDestino", usuarioCorrente.getInstituicao()))
					.add(Restrictions.eq("a.instituicaoEnvio", usuarioCorrente.getInstituicao())));
		}
		
		if (StringUtils.isNotBlank(arquivo.getNomeArquivo())) 
			criteria.add(Restrictions.ilike("a.nomeArquivo", arquivo.getNomeArquivo(), MatchMode.ANYWHERE));
		
		if (pracaProtesto != null)
			criteria.add(Restrictions.ilike("m.nomeMunicipio", arquivo.getInstituicaoEnvio().getMunicipio().getNomeMunicipio(), MatchMode.ANYWHERE));
		
		if (portador != null){
			criteria.add(Restrictions.ilike("instituicaoEnvio.nomeFantasia", arquivo.getInstituicaoEnvio().getNomeFantasia(), MatchMode.ANYWHERE));
			criteria.add(Restrictions.ilike("instituicaoDestino.nomeFantasia", arquivo.getInstituicaoEnvio().getNomeFantasia(), MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.between("a.dataEnvio", dataInicio, dataFim));
		
		criteria.addOrder(Order.desc("a.dataEnvio"));
		return criteria.list();
	}
}
