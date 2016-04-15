package br.com.ieptbto.cra.page.desistenciaCancelamento;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class BuscarTituloSolicitacaoCancelamentoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private TituloRemessa tituloRemessa;

	private DropDownChoice<Municipio> dropDownMunicipio;
	private DropDownChoice<Instituicao> dropDownBancosConvenios;

	public BuscarTituloSolicitacaoCancelamentoPage() {
		this.tituloRemessa = new TituloRemessa();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioTituloCancelamento();
	}

	private void formularioTituloCancelamento() {
		Form<TituloRemessa> form = new Form<TituloRemessa>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				TituloRemessa tituloRemessa = getModelObject();
				Instituicao bancoConvenio = null;
				Municipio municipio = null;

				try {
					if (StringUtils.isBlank(tituloRemessa.getNumeroTitulo()) && StringUtils.isBlank(tituloRemessa.getNumeroTitulo())
							&& StringUtils.isBlank(tituloRemessa.getNumeroTitulo()) && StringUtils.isBlank(tituloRemessa.getNumeroTitulo())
							&& dropDownMunicipio.getModelObject() == null) {
						throw new InfraException("Campos em branco! Por favor preencha ao menos um dos campos!");
					}
					if (dropDownMunicipio.getModelObject() != null) {
						municipio = dropDownMunicipio.getModelObject();
					}
					if (dropDownBancosConvenios.getModelObject() != null) {
						bancoConvenio = dropDownBancosConvenios.getModelObject();
					}

					setResponsePage(new ListaTituloSolicitacaoCancelamentoPage(tituloRemessa, bancoConvenio, municipio));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca de títulos para cancelamento ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(textFieldNomeDevedor());
		form.add(textFieldDocumentoDevedor());
		form.add(textFieldNumeroProtocoloCartorio());
		form.add(textFieldNumeroTitulo());
		form.add(labelBancosConvenios());
		form.add(dropDownBancoConvenio());
		form.add(dropDownMunicipio());
		add(form);
	}

	private TextField<String> textFieldNumeroTitulo() {
		TextField<String> textField = new TextField<String>("numeroTitulo");
		textField.setLabel(new Model<String>("Número do Título/Documento"));
		textField.setRequired(true);
		return textField;
	}

	private TextField<String> textFieldNomeDevedor() {
		TextField<String> textField = new TextField<String>("nomeDevedor");
		textField.setLabel(new Model<String>("Nome do Devedor"));
		return textField;
	}

	private TextField<String> textFieldDocumentoDevedor() {
		TextField<String> textField = new TextField<String>("numeroIdentificacaoDevedor");
		textField.setLabel(new Model<String>("Documento Devedor"));
		return textField;
	}

	private TextField<String> textFieldNumeroProtocoloCartorio() {
		TextField<String> textField = new TextField<String>("numeroProtocoloCartorio");
		textField.setLabel(new Model<String>("Número Protocolo"));
		return textField;
	}

	private DropDownChoice<Municipio> dropDownMunicipio() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		dropDownMunicipio = new DropDownChoice<Municipio>("municipio", new Model<Municipio>(), municipioMediator.getMunicipiosTocantins(), renderer);
		dropDownMunicipio.setLabel(new Model<String>("Município"));
		return dropDownMunicipio;
	}

	private Label labelBancosConvenios() {
		Label label = new Label("labelBancosConvenios", "BANCOS/CONVENIOS:");
		label.setOutputMarkupId(true);
		label.setVisible(false);
		if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			label.setVisible(true);
		}
		return label;
	}

	private DropDownChoice<Instituicao> dropDownBancoConvenio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		dropDownBancosConvenios = new DropDownChoice<Instituicao>("bancoConvenio", new Model<Instituicao>(),
				instituicaoMediator.getInstituicoesFinanceirasEConvenios(), renderer);
		dropDownBancosConvenios.setLabel(new Model<String>("Banco/Convênio"));
		dropDownBancosConvenios.setOutputMarkupId(true);
		dropDownBancosConvenios.setVisible(false);
		if (getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			dropDownBancosConvenios.setVisible(true);
		}
		return dropDownBancosConvenios;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}
