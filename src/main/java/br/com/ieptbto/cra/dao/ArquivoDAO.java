package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public class ArquivoDAO extends AbstractBaseDAO {

	@Autowired
	InstituicaoMediator InstituicaoMediator;
	@Autowired
	TituloDAO tituloDAO;

	public List<Arquivo> buscarTodosArquivos() {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	@Transactional
	public Arquivo salvar(Arquivo arquivo, Usuario usuarioAcao) {
		Arquivo arquivoSalvo = new Arquivo();
		Session session = getSession();
		Transaction transaction = session.beginTransaction();

		try {
			arquivo.setStatusArquivo(save(arquivo.getStatusArquivo()));
			arquivoSalvo = save(arquivo);

			for (Remessa remessa : arquivo.getRemessas()) {
				remessa.setCabecalho(save(remessa.getCabecalho()));
				remessa.setRodape(save(remessa.getRodape()));
				remessa.setArquivo(arquivoSalvo);
				remessa.setDataRecebimento(new LocalDate());
				if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
					remessa.setSituacaoBatimento(false);
				}
				save(remessa);
				for (Titulo titulo : remessa.getTitulos()) {
					titulo.setRemessa(remessa);
					if (Retorno.class.isInstance(titulo)) {
						Retorno.class.cast(titulo).setCabecalho(remessa.getCabecalho());
					}
					Historico historico = new Historico();
					historico.setDataOcorrencia(new LocalDateTime());
					historico.setRemessa(remessa);
					historico.setTitulo(tituloDAO.salvar(titulo, transaction));
					historico.setUsuarioAcao(usuarioAcao);
					save(historico);
				}
			}
			transaction.commit();
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
			        + " foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			// if
			// (ex.getMessage().contains("duplicar valor da chave viola a restrição de unicidade"))
			// {
			// throw new InfraException("O registro já está salvo na CRA.");
			// }
			throw new InfraException("Não foi possível inserir esse arquivo na base de dados.");
		}
		return arquivoSalvo;

	}

	public StatusArquivo buscarStatusArquivo(SituacaoArquivo situacao) {
		Criteria criteria = getCriteria(StatusArquivo.class);
		criteria.add(Restrictions.eq("situacaoArquivo", situacao));
		criteria.addOrder(Order.asc("id"));
		return StatusArquivo.class.cast(criteria.uniqueResult());
	}

	public Arquivo buscarArquivosPorNome(String nomeArquivo) {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.add(Restrictions.eq("nomeArquivo", nomeArquivo));
		criteria.setMaxResults(1);
		return Arquivo.class.cast(criteria.uniqueResult());
	}
}
