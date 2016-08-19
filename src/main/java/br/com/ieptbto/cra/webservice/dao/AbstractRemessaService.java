package br.com.ieptbto.cra.webservice.dao;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.DadosArquivoRecebido;
import br.com.ieptbto.cra.webservice.dados.ProcessarDadosRecebido;

/**
 * 
 * @author Leandro
 *
 */
public class AbstractRemessaService {

    public static Logger logger = Logger.getLogger(AbstractRemessaService.class);

    private ProcessarDadosRecebido processarDadosRecebido;
    private DadosArquivoRecebido dadosArquivoRecebido;

    public DadosArquivoRecebido getDadosArquivoRecebido() {
        return dadosArquivoRecebido;
    }

    /**
     * 
     * @param login
     * @param senha
     * @param nomeArquivo
     * @param dados
     * @param servico
     */
    protected void setDadosArquivoRecebido(String login, String senha, String nomeArquivo, String dados, String servico) {
        try {
            this.dadosArquivoRecebido = DadosArquivoRecebido.set(login, senha, nomeArquivo, dados, servico);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 
     * @param login
     * @param senha
     * @param nomeArquivo
     * @param servico
     */
    protected void setDadosArquivoRecebido(String login, String senha, String nomeArquivo, String servico) {
        try {
            this.dadosArquivoRecebido = DadosArquivoRecebido.set(login, senha, nomeArquivo, StringUtils.EMPTY, servico);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void salvarDadosRecebidos() {
        logger.info("Salvando dados recebidos pelo ws.");
        processarDadosRecebido.salvarDados(getDadosArquivoRecebido());
        logger.info("Dados recebidos salvos com sucesso.");
    }

    public void setProcessarDadosRecebido(ProcessarDadosRecebido processarDadosRecebido) {
        this.processarDadosRecebido = processarDadosRecebido;
    }

    public ProcessarDadosRecebido getProcessarDadosRecebido() {
        return processarDadosRecebido;
    }

}
