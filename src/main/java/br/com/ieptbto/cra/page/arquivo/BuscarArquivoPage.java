package br.com.ieptbto.cra.page.arquivo;

import br.com.ieptbto.cra.beans.ArquivoBean;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoVisualizacaoArquivos;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
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
public class BuscarArquivoPage extends BasePage<Arquivo> {

	private static final long serialVersionUID = 1L;
	private Arquivo arquivo;
	private Usuario usuario;

	public BuscarArquivoPage() {
		this.arquivo = new Arquivo();
		this.usuario = getUser();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioBuscarArquivo();
	}

	private void formularioBuscarArquivo() {
		ArquivoBean arquivoFormBean = new ArquivoBean();
		Form<ArquivoBean> form = new Form<ArquivoBean>("form", new CompoundPropertyModel<ArquivoBean>(arquivoFormBean)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				ArquivoBean arquivoBean = getModelObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				try {
					if (arquivoBean.getNomeArquivo() == null && arquivoBean.getDataInicio() == null) {
						throw new InfraException("Por favor, informe o 'Nome do Arquivo' ou 'Período de datas' como parâmetro!");
					} else if (arquivoBean.getNomeArquivo() != null) {
						if (arquivoBean.getNomeArquivo().length() < 4) {
							throw new InfraException("Por favor, informe ao menos 4 caracteres!");
						}
					}

                    DataUtil.validarPeriodoDataInicialFinal(arquivoBean.getDataInicio(), arquivoBean.getDataFim());
					if (TipoVisualizacaoArquivos.ARQUIVOS_CARTORIOS.equals(arquivoBean.getTipoVisualizacaoArquivos())) {
						setResponsePage(new ListaArquivoCartorioPage(arquivoBean, usuario));
					} else if (TipoVisualizacaoArquivos.ARQUIVOS_BANCOS_CONVENIOS.equals(arquivoBean.getTipoVisualizacaoArquivos())) {
						setResponsePage(new ListaArquivoInstituicaoPage(arquivoBean, usuario));
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível buscar os arquivos ! Favor entrar em contato com a CRA...");
				}
			}
		};
		form.add(new BuscarArquivoInputPanel("buscarArquivoInputPanel", new CompoundPropertyModel<ArquivoBean>(arquivoFormBean), usuario));
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}