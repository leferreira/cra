package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;

/**
 * 
 * @author Lefer
 *
 */
@Repository
public class ArquivoDAO extends AbstractBaseDAO {

	@Autowired
	InstituicaoMediator InstituicaoMediator;

	@SuppressWarnings("unchecked")
	public List<Arquivo> buscarTodosArquivos() {
		Criteria criteria = getCriteria(Arquivo.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public Arquivo salvar(Arquivo arquivo) {
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
				remessa.setDataRecebimento(arquivoSalvo.getDataEnvio());
				remessa.setInstituicaoDestino(InstituicaoMediator.getInstituicao(remessa.getCabecalho().getCodigoMunicipio()));
				save(remessa);
				for (Titulo titulo : remessa.getTitulos()) {
					titulo.setRemessa(remessa);
					save(titulo);
				}
			}
			transaction.commit();
			logger.info("O arquivo " + arquivo.getNomeArquivo() + "enviado pelo usuário " + arquivo.getUsuarioEnvio().getLogin()
			        + " foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo na base de dados.");
		}
		return arquivoSalvo;

	}
}
