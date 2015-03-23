package br.com.ieptbto.cra.validacao.regra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

public class RegraValidaTipoArquivoTXT extends RegrasDeEntrada {

	private static final Logger logger = Logger.getLogger(RegraValidaTipoArquivoTXT.class);
	private File arquivo;

	public void validar(File arquivo, Usuario usuario) {
		this.arquivo = arquivo;
		executar();
	}

	@Override
	protected void executar() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)));
			String linha = reader.readLine();
			reader.close();
			if (!linha.startsWith(ConfiguracaoBase.CARACTER_INICIO_TXT)) {
				throw new InfraException("O arquivo não é um tipo TXT válido.");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new InfraException("Não foi possível verificar a primeira linha do arquivo.");
		}

	}

}
