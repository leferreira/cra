package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.exception.ArquivoException;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
public class FabricaDeArquivo {

	private static final Logger logger = Logger.getLogger(FabricaDeArquivo.class);

	public Arquivo processarArquivoFisico(File arquivoFisico, Arquivo arquivo, List<ArquivoException> erros) {

		String linha = getLinhaArquivo(arquivoFisico);

		if (LayoutArquivo.TXT.equals(LayoutArquivo.get(linha))) {
			return new FabricaDeArquivoTXT(arquivoFisico, arquivo, erros).converter();
		} else if (LayoutArquivo.XML.equals(LayoutArquivo.get(linha))) {
			return new FabricaDeArquivoXML(arquivoFisico, arquivo, erros).converter();
		} else {
			throw new InfraException("Layout Do arquivo [" + arquivo.getNomeArquivo() + "] inválido");
		}

	}

	private static String getLinhaArquivo(File arquivoFisico) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoFisico)));
			String linha = reader.readLine();
			reader.close();
			return linha;
		} catch (FileNotFoundException e) {
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		} catch (IOException e) {
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		} finally {
		}
		return null;
	}

}
