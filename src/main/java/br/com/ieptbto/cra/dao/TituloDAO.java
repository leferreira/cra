package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Repository
public class TituloDAO extends AbstractBaseDAO  {

	private Instituicao instituicaoUsuario;
	
	@SuppressWarnings("unchecked")
	public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Usuario user){
		this.instituicaoUsuario = user.getInstituicao();
		
		Criteria criteria = getCriteria(TituloRemessa.class);
		criteria.createAlias("remessa", "remessa");
		criteria.createAlias("remessa.arquivo", "arquivo");
		if (!instituicaoUsuario.getTipoInstituicao().getTipoInstituicao().equals("CRA")) {
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("remessa.instituicaoDestino", instituicaoUsuario))
					.add(Restrictions.eq("arquivo.instituicaoEnvio", instituicaoUsuario)));
		}
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.ilike("codigoPortador", titulo.getCodigoPortador(), MatchMode.ANYWHERE))
				.add(Restrictions.ilike("numeroProtocoloCartorio", titulo.getNumeroProtocoloCartorio(), MatchMode.ANYWHERE))
				.add(Restrictions.ilike("numeroTitulo", titulo.getNumeroTitulo(), MatchMode.ANYWHERE))
				.add(Restrictions.ilike("nomeDevedor", titulo.getNomeDevedor(), MatchMode.ANYWHERE))
				.add(Restrictions.ilike("documentoDevedor", titulo.getDocumentoDevedor(), MatchMode.ANYWHERE))
				.add(Restrictions.eq("dataEmissaoTitulo", titulo.getDataEmissaoTitulo())) 
				.add(Restrictions.eq("dataOcorrencia", titulo.getDataOcorrencia())) 
				.add(Restrictions.ilike("nossoNumero", titulo.getNossoNumero(), MatchMode.ANYWHERE)));
		
		
		criteria.addOrder(Order.asc("nomeDevedor"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Historico> getHistoricoTitulo(TituloRemessa titulo){
		Criteria criteria = getCriteria(Historico.class);
		criteria.createAlias("titulo", "t");
		criteria.add(Restrictions.eq("t.id", titulo.getId()));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
}
