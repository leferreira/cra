package br.com.ieptbto.cra.webservice.dados;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private DadosArquivoRecebidoMediator dadosArquivoRecebidoMediator;

    public void salvarDados(DadosArquivoRecebido dadosArquivoRecebido) {
        dadosArquivoRecebidoMediator.salvarDados(dadosArquivoRecebido);

    }

}
