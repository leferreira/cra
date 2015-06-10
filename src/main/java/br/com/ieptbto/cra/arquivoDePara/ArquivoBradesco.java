package br.com.ieptbto.cra.arquivoDePara;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

/**
 * @author Thasso Araújo
 *
 */
public class ArquivoBradesco extends AbstractDePara {

	private static final Logger logger = Logger.getLogger(ArquivoBradesco.class);
	
	@Autowired
	ArquivoDeParaDAO deParaDAO;
	
	private File arquivoFisico;
	private FileUpload file;
	
	@Override
	public List<AgenciaBradesco> processar(FileUpload planilha) {
		this.file = planilha;
		
		if (getFile() != null) {

			logger.info("Início do processamento do arquivo De/Para Bradesco " + getFile().getClientFileName());
			verificaDiretorioDePara();
			copiarArquivoParaDiretorioDoUsuarioTemporario(getFile().getClientFileName());
			return converterArquivoDeParaBradesco();
			
		} else {
			throw new InfraException("O arquivoFisico " + getFile().getClientFileName() + "enviado não pode ser processado.");
		}
	}
	
	public List<AgenciaBradesco> converterArquivoDeParaBradesco() {
		
		try {
			File file = new File(ConfiguracaoBase.DIRETORIO_BASE_DE_PARA_TEMP + getFile().getClientFileName());
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			fileOut.write(getFile().getBytes());
			fileOut.close();
			FileInputStream fileIn = new FileInputStream(file);
			String name = file.getName();
			int pos = name.lastIndexOf('.');
			String ext = name.substring(pos + 1);

			Workbook plan = null;

			if (ext.equals("xlsx")) {
				try {
					plan = new XSSFWorkbook(fileIn);
					fileIn.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			} else if (ext.equals("xls")) {
				try {
					plan = new HSSFWorkbook(fileIn);
					fileIn.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			} 
			file.delete();
			return processarPlanilha(plan);

		} catch (FileNotFoundException ex) {
			System.out.println("Arquivo não encontrado.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<AgenciaBradesco> processarPlanilha(Workbook obj) {
		List<AgenciaBradesco> listaAgencias = new ArrayList<AgenciaBradesco>();

		Sheet worksheet = obj.getSheetAt(0);
		Row row;
		for (int i = 1; i <= worksheet.getLastRowNum(); i++) {
			row = worksheet.getRow(i);

			AgenciaBradesco agencia = new AgenciaBradesco();
			agencia.setNomeCedente(getValorCelula(row, 0));
			agencia.setCnpj(getValorCelula(row, 1));
			agencia.setCodigoAgenciaCedente(getValorCelula(row, 2));
			agencia.setAgenciaDestino(getValorCelula(row, 3));
			agencia.setOrientacao(getValorCelula(row, 4));

			listaAgencias.add(agencia);
		}
		logger.info("Fim do processamento do arquivo De/Para Bradesco!");
		return listaAgencias;
	}
	
	private static String getValorCelula(Row row, int posicao) {
		Cell cell = row.getCell(posicao);
		String linha = "";
		if (cell.getCellType() == 1)
			linha = cell.getStringCellValue();
		return linha;
	}

	private void copiarArquivoParaDiretorioDoUsuarioTemporario(String nomeArquivo) {
		setArquivoFisico(new File(ConfiguracaoBase.DIRETORIO_BASE_DE_PARA_TEMP + nomeArquivo));
		try {
			getFile().writeTo(getArquivoFisico());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + getFile().getClientFileName());
		}

	}
	
	public File getArquivoFisico() {
		if (!arquivoFisico.exists()) {
			try {
				arquivoFisico.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e.getCause());
				throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + arquivoFisico.getName());
			}
		}
		return arquivoFisico;
	}
	
	public void setArquivoFisico(File arquivo) {
		if (this.arquivoFisico != null && this.arquivoFisico.exists()) {
			this.arquivoFisico.delete();
		}
		this.arquivoFisico = arquivo;
	}

	private void verificaDiretorioDePara() {
		File diretorioTemp = new File(ConfiguracaoBase.DIRETORIO_BASE_DE_PARA_TEMP);

		if (!diretorioTemp.exists()) {
			diretorioTemp.mkdirs();
		}
	}

	public FileUpload getFile() {
		return file;
	}

	public void setFile(FileUpload file) {
		this.file = file;
	}
}
