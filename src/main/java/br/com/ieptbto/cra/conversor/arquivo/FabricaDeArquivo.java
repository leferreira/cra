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
import br.com.ieptbto.cra.entidade.Cabecalho;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
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
	private File arquivoFisico;
	private Arquivo arquivo;

	public FabricaDeArquivo(File arquivoFisico, Arquivo arquivo) {
		this.arquivoFisico = arquivoFisico;
		this.arquivo = arquivo;
	}

	public Arquivo converter() {
		List<ArquivoException> erros = new ArrayList<ArquivoException>();
		validarArquivo(getArquivoFisico(), erros);

		return processarArquivoFisico(getArquivoFisico(), erros);
	}

	private Arquivo processarArquivoFisico(File arquivoFisico, List<ArquivoException> erros) {

		try {
			List<Remessa> remessas = new ArrayList<Remessa>();
			getArquivo().setRemessas(remessas);
			Remessa remessa = new Remessa();
			remessa.setTitulos(new ArrayList<Titulo>());
			remessa.setArquivo(getArquivo());

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoFisico)));
			String linha = "";
			while ((linha = reader.readLine()) != null) {
				setRegistro(linha, remessa);
				if (remessa.getRodape() != null) {
					remessas.add(remessa);
					remessa = new Remessa();
					remessa.setTitulos(new ArrayList<Titulo>());
					remessa.setArquivo(getArquivo());
				}
			}
			reader.close();

			return getArquivo();

		} catch (FileNotFoundException e) {
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		} catch (IOException e) {
			new InfraException("arquivoFisico não encontrado");
			logger.error(e.getMessage());
		}

		return null;
	}

	private void setRegistro(String linha, Remessa remessa) {
		AbstractArquivoVO registro = FabricaRegistro.getInstance(linha).criarRegistro();

		if (TipoRegistro.CABECALHO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			CabecalhoVO cabecalhoVO = CabecalhoVO.class.cast(registro);
			Cabecalho cabecalho = new CabecalhoConversor().converter(Cabecalho.class, cabecalhoVO);
			remessa.setCabecalho(cabecalho);
			cabecalho.setRemessa(remessa);
		} else if (TipoRegistro.TITULO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			TituloVO tituloVO = TituloVO.class.cast(registro);
			Titulo titulo = new TituloConversor().converter(Titulo.class, tituloVO);
			titulo.setRemessa(remessa);
			remessa.getTitulos().add(titulo);
		} else if (TipoRegistro.RODAPE.getConstante().equals(registro.getIdentificacaoRegistro())) {
			RodapeVO rodapeVO = RodapeVO.class.cast(registro);
			Rodape rodape = new RodapeConversor().converter(Rodape.class, rodapeVO);
			remessa.setRodape(rodape);
			rodape.setRemessa(remessa);
		} else {
			throw new InfraException("O Tipo do registro não foi encontrado: [" + registro.getIdentificacaoRegistro() + " ]");
		}

	}

	private void validarArquivo(File arquivo2, List<ArquivoException> erros) {
		// TODO Auto-generated method stub

	}

	public File getArquivoFisico() {
		return arquivoFisico;
	}

	public void setArquivoFisico(File arquivo) {
		this.arquivoFisico = arquivo;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

}
