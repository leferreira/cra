package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivoXML extends AbstractFabricaDeArquivo {

	private ArquivoVO arquivoVO;
	@Autowired
	private InstituicaoMediator instituicaoMediator;

	public Arquivo fabrica(ArquivoVO arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		this.arquivoVO = arquivoFisico;
		this.arquivo = arquivo;
		this.erros = erros;

		return this.converter();
	}

	public Arquivo converter() {
		Arquivo arquivo = new Arquivo();
		arquivo.setRemessas(new ArrayList<Remessa>());
		List<CabecalhoVO> cabecalhosVO = getArquivoVO().getCabecalhos();
		List<RodapeVO> rodapesVO = getArquivoVO().getRodapes();
		List<TituloVO> titulosVO = getArquivoVO().getTitulos();

		for (int i = 0; i < cabecalhosVO.size(); i++) {
			Remessa remessa = new Remessa();
			remessa.setArquivo(getArquivo());
			remessa.setCabecalho(getCabecalho(cabecalhosVO.get(i)));
			remessa.getCabecalho().setRemessa(remessa);
			remessa.setRodape(getRodape(rodapesVO.get(i)));
			remessa.getRodape().setRemessa(remessa);
			remessa.setArquivo(arquivo);
			remessa.setInstituicaoDestino(getInstituicaoDestino(cabecalhosVO.get(i).getCodigoMunicipio()));
			remessa.setInstituicaoOrigem(getInstituicaoOrigem(cabecalhosVO.get(i).getNumeroCodigoPortador()));
			remessa.setDataRecebimento(getDataRecebimento(cabecalhosVO.get(i).getDataMovimento()));
			remessa.setTitulos(getTitulos(titulosVO.subList(0, remessa.getCabecalho().getQtdTitulosRemessa()), remessa));
			titulosVO.removeAll(titulosVO.subList(0, remessa.getCabecalho().getQtdTitulosRemessa()));

			arquivo.getRemessas().add(remessa);
		}

		return arquivo;
	}

	private LocalDate getDataRecebimento(String dataMovimento) {
		return DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, dataMovimento);
	}

	@SuppressWarnings("rawtypes")
	private List<Titulo> getTitulos(List<TituloVO> titulosVO, Remessa remessa) {
		List<Titulo> titulos = new ArrayList<Titulo>();
		for (TituloVO tituloVO : titulosVO) {
			TituloRemessa titulo = TituloRemessa.parseTituloVO(tituloVO);
			titulo.setRemessa(remessa);
			titulos.add(titulo);
		}
		return titulos;
	}

	private Instituicao getInstituicaoOrigem(String numeroCodigoPortador) {
		return instituicaoMediator.getInstituicaoPorCodigoPortador(numeroCodigoPortador);
	}

	private Instituicao getInstituicaoDestino(String codigoMunicipio) {
		return instituicaoMediator.getInstituicaoPorCodigoIBGE(Integer.parseInt(codigoMunicipio));
	}

	private Rodape getRodape(RodapeVO rodapeVO) {
		return Rodape.parseRodapeVO(rodapeVO);
	}

	private CabecalhoRemessa getCabecalho(CabecalhoVO cabecalhoVO) {
		return CabecalhoRemessa.parseCabecalhoVO(cabecalhoVO);
	}

	@Override
	public void validar() {
		// TODO Auto-generated method stub

	}

	public ArquivoVO getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(ArquivoVO arquivoVO) {
		this.arquivoVO = arquivoVO;
	}

}
