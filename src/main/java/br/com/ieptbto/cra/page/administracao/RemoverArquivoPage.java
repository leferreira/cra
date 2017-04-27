package br.com.ieptbto.cra.page.administracao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.AdministracaoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class RemoverArquivoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	AdministracaoMediator administracaoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;

	private Arquivo arquivo;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private DropDownChoice<Municipio> comboMunicipio;
	private ArrayList<TipoArquivoFebraban> tiposArquivo;

	public RemoverArquivoPage() {
		this.arquivo = new Arquivo();
		this.tiposArquivo = new ArrayList<TipoArquivoFebraban>();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		fomularioConsultarArquivosRemover();
	}

	private void fomularioConsultarArquivosRemover() {
		Form<Arquivo> form = new Form<Arquivo>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Arquivo arquivo = getModelObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;
				Municipio municipio = null;

				try {
					if (arquivo.getNomeArquivo() == null && dataEnvioInicio.getDefaultModelObject() == null) {
						throw new InfraException("Por favor, informe o 'Nome do Arquivo' ou 'Intervalo de datas'!");
					} else if (arquivo.getNomeArquivo() != null) {
						if (arquivo.getNomeArquivo().length() < 4) {
							throw new InfraException("Por favor, informe ao menos 4 caracteres!");
						}
					}

					if (dataEnvioInicio.getDefaultModelObject() != null) {
						if (dataEnvioFinal.getDefaultModelObject() != null) {
							dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						} else
							throw new InfraException("As duas datas devem ser preenchidas.");
					}
					if (comboMunicipio.getModelObject() != null) {
						municipio = comboMunicipio.getModelObject();
					}

					List<Arquivo> arquivos =
							administracaoMediator.consultarArquivosParaRemover(arquivo, municipio, dataInicio, dataFim, getTiposArquivo());
					setResponsePage(new ListaRemoverArquivoPage(arquivos));
				} catch (InfraException ex) {
					logger.info(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
					error("Não foi possível buscar os arquivos ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(comboTipoArquivos());
		form.add(dataEnvioInicio());
		form.add(dataEnvioFinal());
		form.add(nomeArquivo());
		form.add(comboPortador());
		form.add(pracaProtesto());
		add(form);
	}

	private TextField<String> nomeArquivo() {
		return new TextField<String>("nomeArquivo");
	}

	private CheckBoxMultipleChoice<TipoArquivoFebraban> comboTipoArquivos() {
		List<TipoArquivoFebraban> listaTipos = new ArrayList<TipoArquivoFebraban>();
		listaTipos.add(TipoArquivoFebraban.REMESSA);
		listaTipos.add(TipoArquivoFebraban.CONFIRMACAO);
		listaTipos.add(TipoArquivoFebraban.RETORNO);
		CheckBoxMultipleChoice<TipoArquivoFebraban> tipos =
				new CheckBoxMultipleChoice<TipoArquivoFebraban>("tipoArquivos", new Model<ArrayList<TipoArquivoFebraban>>(tiposArquivo), listaTipos);
		tipos.setLabel(new Model<String>("Tipo do Arquivo"));
		return tipos;
	}

	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		return dataEnvioInicio;
	}

	private TextField<LocalDate> dataEnvioFinal() {
		return dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}

	private DropDownChoice<Instituicao> comboPortador() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		DropDownChoice<Instituicao> comboPortador =
				new DropDownChoice<Instituicao>("instituicaoEnvio", instituicaoMediator.getInstituicoesFinanceiras(), renderer);
		return comboPortador;
	}

	private DropDownChoice<Municipio> pracaProtesto() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		this.comboMunicipio =
				new DropDownChoice<Municipio>("municipio", new Model<Municipio>(), municipioMediator.getMunicipiosTocantins(), renderer);
		return comboMunicipio;
	}

	public ArrayList<TipoArquivoFebraban> getTiposArquivo() {
		return tiposArquivo;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}