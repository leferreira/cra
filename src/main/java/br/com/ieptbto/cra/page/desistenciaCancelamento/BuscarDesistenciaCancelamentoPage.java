package br.com.ieptbto.cra.page.desistenciaCancelamento;

import br.com.ieptbto.cra.beans.ArquivoBean;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoVisualizacaoArquivos;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class BuscarDesistenciaCancelamentoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private Arquivo arquivo;
	private Usuario usuario;

	public BuscarDesistenciaCancelamentoPage() {
		this.arquivo = new Arquivo();
		this.usuario = getUser();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioBuscarDesistenciaCancelamento();
	}

	private void formularioBuscarDesistenciaCancelamento() {
		ArquivoBean arquivoFormBean = new ArquivoBean();

		Form<ArquivoBean> form = new Form<ArquivoBean>("form", new CompoundPropertyModel<ArquivoBean>(arquivoFormBean)) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				ArquivoBean arquivoFormBean = getModelObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				try {
					if (arquivoFormBean.getNomeArquivo() == null && arquivoFormBean.getDataInicio() == null) {
						throw new InfraException("Por favor, informe o 'Nome do Arquivo' ou 'Período de datas' como parâmetro!");
					} else if (arquivoFormBean.getNomeArquivo() != null) {
						if (arquivoFormBean.getNomeArquivo().length() < 4) {
							throw new InfraException("Por favor, informe ao menos 5 caracteres!");
						}
					}

					if (arquivoFormBean.getDataInicio() != null) {
						if (arquivoFormBean.getDataFim() != null) {
							dataInicio = new LocalDate(arquivoFormBean.getDataInicio());
							dataFim = new LocalDate(arquivoFormBean.getDataFim());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						} else {
							throw new InfraException("As duas datas devem ser preenchidas.");
						}
					}

					if (TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS.equals(arquivoFormBean.getTipoVisualizacaoArquivos())) {
						setResponsePage(
								new ListaDesistenciaCancelamentoPage(arquivoFormBean.getNomeArquivo(), arquivoFormBean.getBancoConvenio(),
										arquivoFormBean.getTiposArquivos(), arquivoFormBean.getCartorio(), dataInicio, dataFim));
					} else if (TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS.equals(arquivoFormBean.getTipoVisualizacaoArquivos())) {
						setResponsePage(new ListaDesistenciaCancelamentoInstituicaoPage(arquivoFormBean, usuario));
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível buscar os arquivos ! Favor entrar em contato com a CRA...");
				}
			}
		};
		form.add(new BuscarDesistenciaCancelamentoInputPanel("buscarDesistenciaCancelamentoInputPanel",
				new CompoundPropertyModel<ArquivoBean>(arquivoFormBean), usuario));
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}