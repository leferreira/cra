package br.com.ieptbto.cra.conversor.arquivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.exception.ArquivoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.processador.FabricaRegistro;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivoTXT extends AbstractFabricaDeArquivo {

	@Autowired
	InstituicaoMediator instituicaoMediator;

	public FabricaDeArquivoTXT fabrica(File arquivoFisico, Arquivo arquivo, List<ArquivoException> erros) {
		this.arquivoFisico = arquivoFisico;
		this.arquivo = arquivo;
		this.erros = erros;
		return this;
	}

	public Arquivo converter() {
		try {
			List<Remessa> remessas = new ArrayList<Remessa>();
			getArquivo().setRemessas(remessas);
			Remessa remessa = new Remessa();
			remessa.setTitulos(new ArrayList<TituloRemessa>());
			remessa.setArquivo(getArquivo());

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getArquivoFisico())));
			String linha = "";
			while ((linha = reader.readLine()) != null) {
				setRegistro(linha, remessa);
				if (remessa.getRodape() != null) {
					remessas.add(remessa);
					remessa = new Remessa();
					remessa.setTitulos(new ArrayList<TituloRemessa>());
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
			CabecalhoRemessa cabecalho = new CabecalhoConversor().converter(CabecalhoRemessa.class, cabecalhoVO);
			remessa.setCabecalho(cabecalho);
			cabecalho.setRemessa(remessa);
			remessa.setInstituicaoDestino(getInstituicaoDeDestino(cabecalho));
			remessa.setInstituicaoOrigem(getArquivo().getInstituicaoEnvio());
		} else if (TipoRegistro.TITULO.getConstante().equals(registro.getIdentificacaoRegistro())) {
			TituloVO tituloVO = TituloVO.class.cast(registro);
			TituloRemessa titulo = new TituloConversor().converter(TituloRemessa.class, tituloVO);
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

	private Instituicao getInstituicaoDeDestino(CabecalhoRemessa cabecalho) {
		if (TipoArquivoEnum.CONFIRMACAO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))
		        || TipoArquivoEnum.RETORNO.equals(TipoArquivoEnum.getTipoArquivoEnum(getArquivo().getNomeArquivo()))) {
			return instituicaoMediator.getInstituicaoPorCodigoPortador(cabecalho.getNumeroCodigoPortador());
		} else {
			return instituicaoMediator.getInstituicaoPorCodigoIBGE(cabecalho.getCodigoMunicipio());
		}
	}

	@Override
	public void validar() {
		// TODO Auto-generated method stub

	}
}
