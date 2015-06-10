package br.com.ieptbto.cra.arquivoDePara;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.AgenciaCAF;

/**
 * @author Thasso Araújo
 *
 */
public class ArquivoCAF extends AbstractDePara {
	
	private static final int POSICAO_STATUS_AGENCIA_INICIO = 186;
	private static final int POSICAO_STATUS_AGENCIA_FIM = 187;
	
	@Autowired
	ArquivoDeParaDAO deParaDAO;
	
	private List<AgenciaCAF> listaAgencias;
	
	@Override
	public List<AgenciaCAF> processar(FileUpload file) {
		this.listaAgencias = new ArrayList<AgenciaCAF>();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));
			String linha = "";

			while ((linha = reader.readLine()) != null) {
				
				if (linha.substring(POSICAO_STATUS_AGENCIA_INICIO, POSICAO_STATUS_AGENCIA_FIM).equals("S")) {
					AgenciaCAF arquivoCAF = new AgenciaCAF();
					
					arquivoCAF.setBanco(linha.substring(0, 3));
					arquivoCAF.setCodigoAgencia(linha.substring(3, 7));
					arquivoCAF.setNomeAgencia(linha.substring(57, 87));
					arquivoCAF.setCidade(linha.substring(107, 145));
					arquivoCAF.setUf(linha.substring(145, 147));
				
					listaAgencias.add(arquivoCAF);
				}
			}
			reader.close();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listaAgencias;
	}
}
