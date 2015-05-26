package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaDeArquivoXML extends AbstractFabricaDeArquivo {

	private List<RemessaVO> arquivoVO;
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	private Instituicao instituicaoEnvio;

	public void fabrica(List<RemessaVO> arquivoFisico, Arquivo arquivo, List<Exception> erros) {
		this.arquivoVO = arquivoFisico;
		this.arquivo = arquivo;
		this.erros = erros;
		converter();
	}

	public Arquivo converter() {
		for (RemessaVO remessaVO : getArquivoVO()) {
			if (validar(remessaVO)) {
				Remessa remessa = new Remessa();
				remessa.setArquivo(getArquivo());
				remessa.setCabecalho(getCabecalho(remessaVO.getCabecalho()));
				remessa.getCabecalho().setRemessa(remessa);
				remessa.setRodape(getRodape(remessaVO.getRodape()));
				remessa.getRodape().setRemessa(remessa);
				remessa.setArquivo(getArquivo());
				remessa.setInstituicaoDestino(getInstituicaoDestino(remessaVO.getCabecalho().getCodigoMunicipio()));
				remessa.setInstituicaoOrigem(getInstituicaoOrigem(remessaVO.getCabecalho().getNumeroCodigoPortador()));
				remessa.setDataRecebimento(getDataRecebimento(remessaVO.getCabecalho().getDataMovimento()));
				remessa.setTitulos(getTitulos(remessaVO.getTitulos(), remessa));
				getArquivo().getRemessas().add(remessa);
				getArquivo().setInstituicaoEnvio(getInstituicaoEnvio());
			}
		}
		return getArquivo();
	}

	private boolean validar(RemessaVO remessaVO) {

		validarCodigoMunicipio(remessaVO);
		validarMunicipioAtivo(remessaVO);

		if (getErros().isEmpty()) {
			return true;
		}
		return false;
	}

	private void validarMunicipioAtivo(RemessaVO remessaVO) {
		try {
			getInstituicaoDestino(remessaVO.getCabecalho().getCodigoMunicipio());

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			getErros().add(
			        new XmlCraException(ex.getMessage(), remessaVO.getCabecalho().getCodigoMunicipio(), remessaVO.getCabecalho()
			                .getCodigoMunicipio(), CodigoErro.MUNICIPIO_NAO_CADASTRADO_NA_CRA));
		}
	}

	private void validarCodigoMunicipio(RemessaVO remessaVO) {
		if (StringUtils.isEmpty(remessaVO.getCabecalho().getCodigoMunicipio())) {
			logger.error(CodigoErro.CODIGO_DO_MUNICIPIO_NAO_INFORMADO.getDescricao());
			getErros().add(
			        new XmlCraException(CodigoErro.CODIGO_DO_MUNICIPIO_NAO_INFORMADO.getDescricao(), remessaVO.getCabecalho()
			                .getCodigoMunicipio(), remessaVO.getCabecalho().getCodigoMunicipio(),
			                CodigoErro.CODIGO_DO_MUNICIPIO_NAO_INFORMADO));
		}
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
		if (getInstituicaoEnvio() == null) {
			setInstituicaoEnvio(instituicaoMediator.getInstituicaoPorCodigoPortador(numeroCodigoPortador));
		}
		return getInstituicaoEnvio();
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

	public List<RemessaVO> getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(List<RemessaVO> arquivoVO) {
		this.arquivoVO = arquivoVO;
	}

	public Instituicao getInstituicaoEnvio() {
		return instituicaoEnvio;
	}

	public void setInstituicaoEnvio(Instituicao instituicaoEnvio) {
		this.instituicaoEnvio = instituicaoEnvio;
	}

}
