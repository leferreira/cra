package br.com.ieptbto.cra.webservice.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.mediator.RemessaMediator;

/**
 * 
 * @author Lefer
 *
 */
@WebServlet(urlPatterns = { "/RemessaService" })
public class RemessaServiceImpl extends HttpServlet {

	@SpringBean
	RemessaMediator remessaMediator;

	/*** */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		gerarErro(request, response, gerarXML(), "Leandro");
	}

	private String gerarXML() {
		ArquivoVO arquivo = getArquivo("");
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<ArquivoVO> element = new JAXBElement<ArquivoVO>(new QName("remessa"), ArquivoVO.class, arquivo);
			marshaller.marshal(element, writer);

			return writer.toString();

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArquivoVO getArquivo(String nome) {
		return remessaMediator.buscarArquivos(nome);
	}

	// private RodapeVO getRodape() {
	// RodapeVO rodape = new RodapeVO();
	// rodape.setIdentificacaoRegistro("9");
	// rodape.setNumeroCodigoPortador("237");
	// rodape.setNomePortador("Banco Bradesco");
	// rodape.setDataMovimento("04022015");
	// rodape.setNumeroSequencialRegistroArquivo("1");
	// rodape.setSomatorioQtdRemessa("1");
	// rodape.setSomatorioValorRemessa("1");
	// rodape.setComplementoRegistro("");
	// return rodape;
	// }

	public void gerarErro(HttpServletRequest request, HttpServletResponse response, String erroXmlParaDownload, String user_arq) {

		File caminho = new File("/logs_de_transmissao");
		caminho.mkdirs();

		String filename = "erro_envio_" + user_arq + ".xml";
		String localCompleto = caminho + "/" + filename;
		File file = new File(localCompleto);

		try {
			FileWriter arquivo = new FileWriter(new File(localCompleto));
			arquivo.write(erroXmlParaDownload);
			arquivo.close();

			FileInputStream in = new FileInputStream(file);

			response.setContentLength((int) file.length());

			OutputStream out = response.getOutputStream();
			byte[] buf = new byte[(int) file.length()];
			int count;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			in.close();
			out.flush();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
