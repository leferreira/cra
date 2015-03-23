package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivo;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.exception.ArquivoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.FabricaRegistro;

/**
 * 
 * @author Lefer
 *
 */
public class FabricaDeArquivo {

	private static final Logger logger = Logger.getLogger(FabricaDeArquivo.class);
	private File arquivo;

	public FabricaDeArquivo(File arquivoFisico) {
		setArquivo(arquivoFisico);
	}

	public Arquivo converter() {
		List<ArquivoException> erros = new ArrayList<ArquivoException>();
		validarArquivo(getArquivo(), erros);

		return processarArquivo(getArquivo(), erros);
	}

	private Arquivo processarArquivo(File arquivoFisico, List<ArquivoException> erros) {
		return processarArquivoFisico(arquivoFisico, erros);
	}

	private Arquivo processarArquivoFisico(File arquivoFisico, List<ArquivoException> erros) {

		try {
			ArquivoVO arquivo = new ArquivoVO();
			arquivo.setTitulos(new ArrayList<TituloVO>());
			arquivo.setCabecalhos(new ArrayList<CabecalhoVO>());
			arquivo.setRodapes(new ArrayList<RodapeVO>());

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoFisico)));
			String linha = "";

			while ((linha = reader.readLine()) != null) {
				setRegistro(FabricaRegistro.getInstance(linha).criarRegistro(), arquivo);
			}
			reader.close();

			return parseArquivoVo(arquivo);

		} catch (FileNotFoundException e) {
			new InfraException("arquivo não encontrado");
			logger.error(e.getMessage());
		} catch (IOException e) {
			new InfraException("arquivo não encontrado");
			logger.error(e.getMessage());
		}

		return null;
	}

	private Arquivo parseArquivoVo(ArquivoVO arquivo2) {
		Arquivo arquivo = new Arquivo();
		return arquivo;
	}

	private void setRegistro(AbstractArquivo registro, ArquivoVO arquivo) {
		if (TipoRegistro.TITULO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			TituloVO titulo = TituloVO.class.cast(registro);
			arquivo.getTitulos().add(titulo);
		} else if (TipoRegistro.CABECALHO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			CabecalhoVO cabecalho = CabecalhoVO.class.cast(registro);
			arquivo.getCabecalhos().add(cabecalho);
		} else if (TipoRegistro.RODAPE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeVO rodape = RodapeVO.class.cast(registro);
			arquivo.getRodapes().add(rodape);
		}

	}

	private void validarArquivo(File arquivo2, List<ArquivoException> erros) {
		// TODO Auto-generated method stub

	}

	public File getArquivo() {
		return arquivo;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
	}

}
