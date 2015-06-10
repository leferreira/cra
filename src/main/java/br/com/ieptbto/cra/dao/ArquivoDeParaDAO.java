package br.com.ieptbto.cra.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
@Repository
public class ArquivoDeParaDAO extends AbstractBaseDAO{

	public void salvarArquivoCAF(List<AgenciaCAF> listaAgencias) {
		Transaction transaction = getBeginTransation();
		
		try {
		
			for (AgenciaCAF agencia: listaAgencias){
				save(agencia);
			}
			transaction.commit();
			
			logger.info("O arquivo CAF enviado foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo CAF na base de dados.");
		}
	}

	public void salvarArquivoBradesco(List<AgenciaBradesco> listaAgencias) {
		Transaction transaction = getBeginTransation();
		
		try {
			
			for (AgenciaBradesco agencia: listaAgencias){
				save(agencia);
			}
			transaction.commit();
			
			logger.info("O arquivo Bradesco enviado foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo Bradesco na base de dados.");
		}
	}
	
	public void salvarArquivoBancoDoBrasil(List<AgenciaBancoDoBrasil> listaAgencias) {
		Transaction transaction = getBeginTransation();
		
		try {
			
			for (AgenciaBancoDoBrasil agencia: listaAgencias){
				save(agencia);
			}
			transaction.commit();
			
			logger.info("O arquivo Bradesco enviado foi inserido na base ");
		} catch (Exception ex) {
			transaction.rollback();
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível inserir esse arquivo Bradesco na base de dados.");
		}
	}
}
