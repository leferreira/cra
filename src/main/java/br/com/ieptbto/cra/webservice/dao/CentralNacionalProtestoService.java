package br.com.ieptbto.cra.webservice.dao;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServices;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.vo.*;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoService extends CnpWebService {

	@Autowired
	CentralNacionalProtestoMediator centralNacionalProtestoMediator;
	@Autowired
	MunicipioMediator municipioMediator;

	public String processar(Usuario usuario, String data) {
		ArquivoCnpVO arquivoCnp = new ArquivoCnpVO();
		LocalDate dataMoviemento = new LocalDate();
		
		try {
			if (usuario == null) {
				return usuarioInvalido();
			}
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				return usuarioNaoPermitidoConsultaArquivoCNP();
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (StringUtils.isBlank(data)) {
				return mensagemDataInvalida(usuario);
			} else {
				try {
					dataMoviemento = DataUtil.stringToLocalDate("yyyy-MM-dd", data);
				} catch (Exception ex) {
					logger.error(ex.getMessage());
					return mensagemDataInvalida(usuario);
				}
			}
			if (centralNacionalProtestoMediator.isLoteLiberadoConsultaPorData(dataMoviemento)) {
				arquivoCnp = centralNacionalProtestoMediator.processarLoteNacionalPorData(dataMoviemento);
			} else if (!dataMoviemento.equals(new LocalDate())) {
				return naoHaMovimentoParaEstaData(usuario, dataMoviemento);
			} else if (dataMoviemento.equals(new LocalDate())) {
				arquivoCnp = centralNacionalProtestoMediator.processarLoteNacional();
				if (arquivoCnp.getRemessasCnpVO().isEmpty()) {
					return naoHaRemessasParaArquivoCnp(usuario);
				}
			}
			loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Arquivo CNP do dia " + DataUtil.localDateToString(dataMoviemento) + ", enviado com sucesso para o IEPTB nacional.");
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex.getCause());
			loggerCra.error(usuario, CraAcao.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro interno ao construir o arquivo da CNP do dia " + DataUtil.localDateToString(dataMoviemento) + ".", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(ex.getMessage(), dataMoviemento), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(arquivoCnp, CONSTANTE_CNP_XML);
	}

	private String naoHaMovimentoParaEstaData(Usuario usuario, LocalDate dataMoviemnto) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(dataMoviemnto));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_NAO_HA_MOVIMENTO_NESSA_DATA.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_NAO_HA_MOVIMENTO_NESSA_DATA.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	/**
	 * @param usuario
	 * @return
	 */
	public String consultar(Usuario usuario) {
		try {
			if (usuario == null) {
				return usuarioInvalido();
			}
			List<Instituicao> cartorios = centralNacionalProtestoMediator.consultarCartoriosCentralNacionalProtesto();
			return gerarXmlConsultaCartoriosCnp(cartorios);

		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.CONSULTA_CARTORIOS_CENTRAL_NACIONAL_PROTESTO,
					"Erro interno ao informar os cartórios ativos na CNP.", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(), CONSTANTE_RELATORIO_XML);
		}
	}

	private String gerarXmlConsultaCartoriosCnp(List<Instituicao> cartorios) {
		CnpCartoriosConteudoVO conteudo = new CnpCartoriosConteudoVO();
		conteudo.setCartorios(new ArrayList<CnpCartorioVO>());

		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			for (Instituicao cartorio : cartorios) {
				cartorio.setMunicipio(municipioMediator.carregarMunicipio(cartorio.getMunicipio()));
				CnpCartorioVO cartorioCnp = new CnpCartorioVO();
				cartorioCnp.setCodigo("01");
				cartorioCnp.setNomeFantasia(cartorio.getNomeFantasia());
				cartorioCnp.setTabeliao(cartorio.getTabeliao());
				cartorioCnp.setResponsavel(cartorio.getResponsavel());
				cartorioCnp.setCnpj(cartorio.getCnpj());
				cartorioCnp.setEndereco(cartorio.getEndereco());
				cartorioCnp.setBairro(cartorio.getBairro());
				cartorioCnp.setUf(cartorio.getMunicipio().getUf());
				cartorioCnp.setTelefone(cartorio.getMunicipio().getUf());
				cartorioCnp.setParticipante("1");
				cartorioCnp.setUltimoEnvio(DataUtil.localDateToString(new LocalDate().minusDays(1)));

				CnpMunicipioCartorioVO municipio = new CnpMunicipioCartorioVO();
				municipio.setNome(cartorio.getMunicipio().getNomeMunicipio());
				municipio.setCodigo(cartorio.getMunicipio().getCodigoIBGE());
				cartorioCnp.setMunicipio(municipio);

				conteudo.getCartorios().add(cartorioCnp);
			}
			CnpCartoriosConsultaXmlVO xml = new CnpCartoriosConsultaXmlVO();
			xml.setConteudo(conteudo);

			context = JAXBContext.newInstance(xml.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF8");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName("consulta"), Object.class, xml);
			marshaller.marshal(element, writer);
			String msg = writer.toString();
			msg = msg.replace(" xsi:type=\"cnpCartoriosConsultaXml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			writer.close();
			return msg;

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			e.printStackTrace();
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			e.printStackTrace();
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}
}