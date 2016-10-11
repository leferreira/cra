package br.com.ieptbto.cra.page.administracao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.entidade.DadosArquivoRecebido;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DadosArquivoRecebidoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class DadosRequisicoesPage extends BasePage<DadosArquivoRecebido> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private DadosArquivoRecebidoMediator dadosArquivoRecebidoMediator;

	private DadosArquivoRecebido dadosArquivosRecebido;
	private Usuario user;
	private Form<DadosArquivoRecebido> form;
	private DateTextField dateFieldDataInicio;
	private DateTextField dateFieldDataFim;
	private DropDownChoice<CraAcao> dropDownCraAcao;
	private List<DadosArquivoRecebido> dadosRequisicoes;

	public DadosRequisicoesPage() {
		this.dadosArquivosRecebido = new DadosArquivoRecebido();
		this.user = getUser();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formBuscarDados();
		listaRequisicoes();
	}

	private void formBuscarDados() {
		this.form = new Form<DadosArquivoRecebido>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					Date dataInicio = dateFieldDataInicio.getModelObject();
					Date dataFim = dateFieldDataFim.getModelObject();
					if (!dataInicio.before(dataFim)) {
						if (!dataInicio.equals(dataFim)) {
							throw new InfraException("A data de início deve ser antes da data fim.");
						}
					}
					CraAcao acao = dropDownCraAcao.getModelObject();
					List<DadosArquivoRecebido> requisicoes = dadosArquivoRecebidoMediator.buscarDados(acao, dataInicio, dataFim);
					getDadosRequisicoes().clear();
					getDadosRequisicoes().addAll(requisicoes);

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					error("Não foi possível buscar as requisições recebidas ! Favor entrar em contato com a CRA...");
				}
			}
		};
		this.form.add(dateFieldDataInicio());
		this.form.add(dateFieldDataFim());
		this.form.add(dropDownCraAcao());
		add(this.form);
	}

	private DateTextField dateFieldDataInicio() {
		this.dateFieldDataInicio = new DateTextField("dataInicio", new Model<Date>(), "dd/MM/yyyy");
		this.dateFieldDataInicio.setLabel(new Model<String>("Período de datas"));
		this.dateFieldDataInicio.setMarkupId("date");
		this.dateFieldDataInicio.setRequired(true);
		return dateFieldDataInicio;
	}

	private DateTextField dateFieldDataFim() {
		this.dateFieldDataFim = new DateTextField("dataFim", new Model<Date>(), "dd/MM/yyyy");
		this.dateFieldDataFim.setMarkupId("date1");
		return dateFieldDataFim;
	}

	private DropDownChoice<CraAcao> dropDownCraAcao() {
		ChoiceRenderer<CraAcao> renderer = new ChoiceRenderer<CraAcao>("label");
		this.dropDownCraAcao = new DropDownChoice<CraAcao>("craAcao", new Model<CraAcao>(), Arrays.asList(CraAcao.values()), renderer);
		this.dropDownCraAcao.setLabel(new Model<String>("Ação"));
		this.dropDownCraAcao.setRequired(true);
		return dropDownCraAcao;
	}

	private void listaRequisicoes() {
		add(new ListView<DadosArquivoRecebido>("dataTableRequisicoes", getDadosRequisicoes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<DadosArquivoRecebido> item) {
				final DadosArquivoRecebido requisicao = item.getModelObject();
				item.add(new Label("dataHora", DataUtil.localDateToString(new LocalDate(requisicao.getDataRecebimento())) + " ás "
						+ DataUtil.localTimeToString("HH:mm:ss", new LocalTime(requisicao.getDataRecebimento()))));
				item.add(new Label("servico", CraAcao.getCraAcao(requisicao.getServico()).getLabel()));
				item.add(new Label("loginInformado", requisicao.getLogin()));
				item.add(new Label("nomeArquivo", requisicao.getNomeArquivo()));
				item.add(downloadConteudo(requisicao));
			}

			private Link<DadosArquivoRecebido> downloadConteudo(final DadosArquivoRecebido requisicao) {
				return new Link<DadosArquivoRecebido>("downloadConteudo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							if (requisicao.getDados() == null) {
								throw new InfraException("Não há conteúdo no arquivo enviado ou não foi um serviço de envio de arquivos e sim de consulta!");
							}
							File file = dadosArquivoRecebidoMediator.downloadConteudoRequisicao(user, requisicao);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));

						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							logger.info(e.getMessage(), e);
							error("Não foi possível favor o download do contéudo da requisição! Favor entrar em contato com a CRA...");
						}
					}
				};
			}
		});
	}

	public List<DadosArquivoRecebido> getDadosRequisicoes() {
		if (dadosRequisicoes == null) {
			dadosRequisicoes = new ArrayList<DadosArquivoRecebido>();
		}
		return dadosRequisicoes;
	}

	@Override
	protected IModel<DadosArquivoRecebido> getModel() {
		return new CompoundPropertyModel<DadosArquivoRecebido>(dadosArquivosRecebido);
	}

}