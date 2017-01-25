package br.com.ieptbto.cra.relatorio;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.NivelDetalhamentoRelatorio;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.enumeration.regra.TipoInstituicaoSistema;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.page.base.BasePage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Thasso Aráujo
 *
 */
public class RelatorioUtil implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(BasePage.class);

	private HashMap<String, Object> params;
	private Instituicao instituicao;
	private LocalDate dataInicio;
	private LocalDate dataFim;

	private void initParams() throws IOException {
		this.params = new HashMap<String, Object>();
		this.params.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_PATH);
		this.params.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_PATH + "ieptb.gif")));
		this.params.put("INSTIUICAO_ID", getInstituicao().getId());
		this.params.put("DATA_INICIO", new java.sql.Date(getDataInicio().toDate().getTime()));
		this.params.put("DATA_FIM", new java.sql.Date(getDataFim().toDate().getTime()));
	}

	public JasperPrint relatorioArquivoCartorio(Remessa remessa) throws JRException {
		this.params = new HashMap<String, Object>();
		this.params.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_PATH);
		this.params.put("ID_REMESSA", Long.parseLong(Integer.toString(remessa.getId())));
		this.params.put("INSTITUICAO_ENVIO", remessa.getInstituicaoOrigem().getNomeFantasia());
		this.params.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
		TipoArquivoFebraban tipoArquivo = remessa.getArquivo().getTipoArquivo().getTipoArquivo();
		JasperReport jasperReport = null;

		try {
			this.params.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_PATH + "ieptb.gif")));

			if (tipoArquivo.equals(TipoArquivoFebraban.REMESSA)) {
			} else if (tipoArquivo.equals(TipoArquivoFebraban.CONFIRMACAO)) {
			} else if (tipoArquivo.equals(TipoArquivoFebraban.RETORNO)) {
				jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioRetornoCartorio.jrxml"));
			}
		} catch (IOException | JRException e) {
			throw new InfraException("Não foi possível localizar o arquivo passado como parâmetro!");
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	public JasperPrint relatorioArquivoInstiuicao(Arquivo arquivo) {
		return null;
	}

	public JasperPrint gerarRelatorioArquivos(TipoArquivoFebraban tipoArquivo, NivelDetalhamentoRelatorio tipoRelatorio, Instituicao instituicao, LocalDate dataInicio,
			LocalDate dataFim) throws JRException {
		this.instituicao = instituicao;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;

		try {
			initParams();

			if (tipoArquivo.equals(TipoArquivoFebraban.REMESSA)) {
				return relatorioArquivoRemessa(tipoRelatorio);
			} else if (tipoArquivo.equals(TipoArquivoFebraban.CONFIRMACAO)) {
				return relatorioArquivoConfirmacao(tipoRelatorio);
			} else if (tipoArquivo.equals(TipoArquivoFebraban.RETORNO)) {
				return relatorioArquivoRetorno(tipoRelatorio);
			}
		} catch (IOException e) {
			throw new InfraException("Não foi possível localizar o arquivo passado como parâmetro!");
		}
		return null;
	}

	public JasperPrint gerarRelatorioTitulosPorSituacao(SituacaoTituloRelatorio situacaoTitulosRelatorio, Instituicao instituicao,
			LocalDate dataInicio, LocalDate dataFim) throws JRException {
		this.instituicao = instituicao;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		JasperReport jasperReport = null;

		try {
			initParams();
			TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

			if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.GERAL)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioTitulosGeral_Instituicao.jrxml"));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.SEM_CONFIRMACAO)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.COM_CONFIRMACAO)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.COM_RETORNO)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.PAGOS)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.PROTESTADOS)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.DESISTÊNCIA_DE_PROTESTO)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}

			} else if (situacaoTitulosRelatorio.equals(SituacaoTituloRelatorio.AUTORIZACAO_CANCELAMENTO)) {
				if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
						|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
					jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
				}
			}

		} catch (IOException e) {
			throw new InfraException("Não foi possível localizar o arquivo passado como parâmetro!");
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioArquivoRemessa(NivelDetalhamentoRelatorio tipoRelatorio) throws JRException {

		if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.SINTETICO)) {
			return relatorioSinteticoRemessa();
		} else if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.ANALITICO)) {
			return relatorioAnaliticoRemessa();
		} else if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.DETALHADO)) {
			return relatorioDetalhadoRemessa();
		}
		return null;
	}

	private JasperPrint relatorioArquivoConfirmacao(NivelDetalhamentoRelatorio tipoRelatorio) throws JRException {

		if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.SINTETICO)) {
			return relatorioSinteticoConfirmacao();
		} else if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.ANALITICO)) {
			return relatorioAnaliticoConfirmacao();
		} else if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.DETALHADO)) {
			return relatorioDetalhadoConfirmacao();
		}
		return null;
	}

	private JasperPrint relatorioArquivoRetorno(NivelDetalhamentoRelatorio tipoRelatorio) throws JRException {

		if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.SINTETICO)) {
			return relatorioSinteticoRetorno();
		} else if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.ANALITICO)) {
			return relatorioAnaliticoRetorno();
		} else if (tipoRelatorio.equals(NivelDetalhamentoRelatorio.DETALHADO)) {
			return relatorioDetalhadoRetorno();
		}
		return null;
	}

	private JasperPrint relatorioSinteticoRemessa() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioAnaliticoRemessa() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioDetalhadoRemessa() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioSinteticoConfirmacao() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioAnaliticoConfirmacao() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioDetalhadoConfirmacao() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioSinteticoRetorno() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioAnaliticoRetorno() throws JRException {
		JasperReport jasperReport = null;
		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();

		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private JasperPrint relatorioDetalhadoRetorno() throws JRException {
		JasperReport jasperReport = null;

		TipoInstituicaoSistema tipoInstituicaoParametro = getInstituicao().getTipoInstituicao().getTipoInstituicao();
		if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.INSTITUICAO_FINANCEIRA)
				|| tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CONVENIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		} else if (tipoInstituicaoParametro.equals(TipoInstituicaoSistema.CARTORIO)) {
			jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(""));
		}
		return JasperFillManager.fillReport(jasperReport, params, getConnection());
	}

	private Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");
			// return
			// DriverManager.getConnection("jdbc:postgresql://localhost:5432/nova_cra",
			// "postgres", "1234");
		} catch (Exception e) {
			throw new InfraException("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
		}
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}
}