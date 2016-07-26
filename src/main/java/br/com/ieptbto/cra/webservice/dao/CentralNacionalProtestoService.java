package br.com.ieptbto.cra.webservice.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.CnpCartorio;
import br.com.ieptbto.cra.webservice.VO.CnpCartoriosConsultaXml;
import br.com.ieptbto.cra.webservice.VO.CnpCartoriosConteudo;
import br.com.ieptbto.cra.webservice.VO.CnpMunicipioCartorio;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

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

	public String processar(Usuario usuario) {
		ArquivoCnpVO arquivoCnp = new ArquivoCnpVO();

		try {
			if (usuario == null) {
				return usuarioInvalido();
			}
			if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
				return usuarioNaoPermitidoConsultaArquivoCNP();
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (centralNacionalProtestoMediator.isLoteLiberadoConsultaPorData(new LocalDate())) {
				arquivoCnp = centralNacionalProtestoMediator.processarLoteNacionalPorData(new LocalDate());
			} else {
				arquivoCnp = centralNacionalProtestoMediator.processarLoteNacional();
				if (arquivoCnp.getRemessasCnpVO().isEmpty()) {
					return naoHaRemessasParaArquivoCnp(usuario);
				}
			}
			loggerCra.sucess(usuario, CraAcao.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Arquivo CNP do dia " + DataUtil.localDateToString(new LocalDate()) + ", enviado com sucesso para o IEPTB nacional.");
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex.getCause());
			loggerCra.error(usuario, CraAcao.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro interno ao construir o arquivo da CNP do dia " + DataUtil.localDateToString(new LocalDate()) + ".", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(arquivoCnp, CONSTANTE_CNP_XML);
	}

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
		CnpCartoriosConteudo conteudo = new CnpCartoriosConteudo();
		conteudo.setCartorios(new ArrayList<CnpCartorio>());

		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			for (Instituicao cartorio : cartorios) {
				cartorio.setMunicipio(municipioMediator.carregarMunicipio(cartorio.getMunicipio()));
				CnpCartorio cartorioCnp = new CnpCartorio();
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

				CnpMunicipioCartorio municipio = new CnpMunicipioCartorio();
				municipio.setNome(cartorio.getMunicipio().getNomeMunicipio());
				municipio.setCodigo(cartorio.getMunicipio().getCodigoIBGE());
				cartorioCnp.setMunicipio(municipio);

				conteudo.getCartorios().add(cartorioCnp);
			}
			CnpCartoriosConsultaXml xml = new CnpCartoriosConsultaXml();
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