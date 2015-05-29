package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivo {

	private static final Logger logger = Logger.getLogger(FabricaDeArquivo.class);
	@Autowired
	private FabricaDeArquivoTXT fabricaDeArquivoTXT;
	@Autowired
	private FabricaDeArquivoXML fabricaDeArquivoXML;
	@Autowired
	private ConversorRemessaArquivo conversorRemessaArquivo;
	@Autowired
	private TipoArquivoMediator tipoArquivoMediator;

	public Arquivo processarArquivoFisico(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {

		String linha = getLinhaArquivo(arquivoFisico);

		if (LayoutArquivo.TXT.equals(LayoutArquivo.get(linha))) {
			return fabricaDeArquivoTXT.fabrica(arquivoFisico, arquivo, erros).converter();
		} else if (LayoutArquivo.XML.equals(LayoutArquivo.get(linha))) {
			return converterXML(arquivoFisico, arquivo, erros);
		}

		else {
			throw new InfraException("Layout Do arquivo [" + arquivo.getNomeArquivo() + "] inválido");
		}
	}

	private Arquivo converterXML(File arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		JAXBContext context;
		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlGerado = "";
			Scanner scanner = new Scanner(new FileInputStream(arquivoFisico));
			while (scanner.hasNext()) {

				xmlGerado = xmlGerado + scanner.nextLine().replaceAll("& ", "&amp;");
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlGerado.getBytes());
			arquivoVO = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.ARQUIVO_CORROMPIDO.getDescricao());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(CodigoErro.ARQUIVO_CORROMPIDO.getDescricao());
		}
		arquivo = conversorRemessaArquivo.converter(arquivoVO);
		return arquivo;
	}

	private static String getLinhaArquivo(File arquivoFisico) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoFisico)));
			String linha = reader.readLine();
			reader.close();
			return linha;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("arquivoFisico não encontrado");
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException("arquivoFisico não encontrado");
		}
	}

	public void processarArquivoXML(List<RemessaVO> arquivoRecebido, Usuario usuario, String nomeArquivo, Arquivo arquivo,
	        List<Exception> erros) {
		fabricaDeArquivoXML.fabrica(arquivoRecebido, arquivo, erros);
		arquivo.setNomeArquivo(nomeArquivo);
		arquivo.setDataEnvio(new LocalDate());
		arquivo.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo)));
		arquivo.setUsuarioEnvio(usuario);
		arquivo.setStatusArquivo(getStatusEnviado());

	}

	private StatusArquivo getStatusEnviado() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new Date());
		status.setStatus("Enviado");
		return status;
	}

	public void processarArquivoPersistente(Remessa remessa, File remessaTXT, List<Exception> erros) {
		fabricaDeArquivoTXT.fabricaTXT(remessaTXT, remessa, erros).converterParaTXT();
	}
}
