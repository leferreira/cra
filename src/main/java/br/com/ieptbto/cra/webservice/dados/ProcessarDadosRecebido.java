package br.com.ieptbto.cra.webservice.dados;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.DadosArquivoRecebido;
import br.com.ieptbto.cra.mediator.DadosArquivoRecebidoMediator;

/**
 * 
 * @author Leandro
 *
 */
@Service
public class ProcessarDadosRecebido {

	@Autowired
	DadosArquivoRecebidoMediator dadosArquivoRecebidoMediator;

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public void salvarDados(DadosArquivoRecebido dadosArquivoRecebido) {
		dadosArquivoRecebidoMediator.salvarDados(dadosArquivoRecebido);

	}

}