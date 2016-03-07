package br.com.ieptbto.cra.page.instrumentoProtesto;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.DateTextField;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
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
public class BuscarInstrumentoProtestoPage extends BasePage<InstrumentoProtesto> {

    /***/
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BuscarInstrumentoProtestoPage.class);

    @SpringBean
    private InstituicaoMediator instituicaoMediator;
    @SpringBean
    private MunicipioMediator municipioMediator;
    private InstrumentoProtesto instrumento;
    private DateTextField dataEntrada;
    private TextField<String> codigoInstrumento;
    private TextField<String> codigoEnvelope;
    private TextField<String> protocoloCartorio;
    private DropDownChoice<Municipio> codigoIbge;

    public BuscarInstrumentoProtestoPage() {
	this.instrumento = new InstrumentoProtesto();

	add(adicionarFormularioCodigoBarra());
	add(adicionarFormularioCodigoEnvelope());
	add(adicionarFormularioMunicipioProtocolo());
	add(adicionarFormularioDataEntrada());
    }

    private Form<InstrumentoProtesto> adicionarFormularioCodigoBarra() {
	Form<InstrumentoProtesto> form = new Form<InstrumentoProtesto>("form") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @SuppressWarnings("unused")
	    @Override
	    protected void onSubmit() {
		String numeroProtocolo = null;
		String codigoBarraMunicipio = null;

		try {
		    if (codigoInstrumento.getModelObject() != null) {
			codigoBarraMunicipio = codigoInstrumento.getModelObject().substring(1, 8);
			numeroProtocolo = codigoInstrumento.getModelObject().substring(8, 18);
		    }

		} catch (InfraException ex) {
		    logger.error(ex.getMessage(), ex);
		    error(ex.getMessage());
		} catch (Exception ex) {
		    error("Não foi possível buscar SLIP ! Entre em contato com a CRA !");
		    logger.error(ex.getMessage(), ex);
		}
	    }
	};
	form.add(campoCodigoDeBarra());
	return form;
    }

    private Form<InstrumentoProtesto> adicionarFormularioCodigoEnvelope() {
	Form<InstrumentoProtesto> form = new Form<InstrumentoProtesto>("formCodigoEnvelope") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @SuppressWarnings("unused")
	    @Override
	    protected void onSubmit() {
		String envelope = null;

		try {
		    if (codigoEnvelope.getModelObject() != null) {
			envelope = codigoEnvelope.getModelObject();
		    }

		} catch (InfraException ex) {
		    logger.error(ex.getMessage(), ex);
		    error(ex.getMessage());
		} catch (Exception ex) {
		    error("Não foi possível buscar SLIP ! Entre em contato com a CRA !");
		    logger.error(ex.getMessage(), ex);
		}
	    }
	};
	form.add(codigoEnvelope());
	return form;
    }

    private Form<InstrumentoProtesto> adicionarFormularioMunicipioProtocolo() {
	Form<InstrumentoProtesto> form = new Form<InstrumentoProtesto>("formMunicipioProtocolo") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @SuppressWarnings("unused")
	    @Override
	    protected void onSubmit() {
		String numeroProtocolo = null;
		String inputMunicipio = null;

		try {
		    if (protocoloCartorio.getModelObject() != null) {
			numeroProtocolo = protocoloCartorio.getModelObject();
		    }

		    if (codigoIbge.getModelObject() != null) {
			inputMunicipio = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();
		    }

		} catch (InfraException ex) {
		    logger.error(ex.getMessage(), ex);
		    error(ex.getMessage());
		} catch (Exception ex) {
		    error("Não foi possível buscar SLIP ! Entre em contato com a CRA !");
		    logger.error(ex.getMessage(), ex);
		}
	    }
	};
	form.add(codigoIbgeCartorio());
	form.add(numeroProtocoloCartorio());
	return form;
    }

    private Form<InstrumentoProtesto> adicionarFormularioDataEntrada() {
	Form<InstrumentoProtesto> form = new Form<InstrumentoProtesto>("formDataEntrada") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @SuppressWarnings("unused")
	    @Override
	    protected void onSubmit() {
		LocalDate dataEntradaInstrumentos = null;

		try {
		    if (codigoIbge.getModelObject() != null) {
			dataEntradaInstrumentos = LocalDate.fromDateFields(dataEntrada.getModelObject());
		    }

		} catch (InfraException ex) {
		    logger.error(ex.getMessage(), ex);
		    error(ex.getMessage());
		} catch (Exception ex) {
		    error("Não foi possível buscar SLIP ! Entre em contato com a CRA !");
		    logger.error(ex.getMessage(), ex);
		}
	    }
	};
	form.add(campoDataEntrada());
	return form;
    }

    private DateTextField campoDataEntrada() {
	dataEntrada = new DateTextField("date", "dd/MM/yyyy");
	dataEntrada.setRequired(true);
	dataEntrada.setLabel(new Model<String>("Data Entrada"));
	return dataEntrada;
    }

    private TextField<String> campoCodigoDeBarra() {
	codigoInstrumento = new TextField<String>("codigoInstrumento", new Model<String>());
	codigoInstrumento.setRequired(true);
	codigoInstrumento.setLabel(new Model<String>("Código de Barra do Instrumento de Protesto"));
	return codigoInstrumento;
    }

    private TextField<String> codigoEnvelope() {
	codigoEnvelope = new TextField<String>("codigoEnvelope", new Model<String>());
	codigoEnvelope.setRequired(true);
	codigoEnvelope.setLabel(new Model<String>("Código do Envelope"));
	return codigoEnvelope;
    }

    private DropDownChoice<Municipio> codigoIbgeCartorio() {
	IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
	codigoIbge = new DropDownChoice<Municipio>("codigoIbge", new Model<Municipio>(), municipioMediator.getMunicipiosTocantins(), renderer);
	codigoIbge.setLabel(new Model<String>("Município"));
	codigoIbge.setRequired(true);
	return codigoIbge;
    }

    private TextField<String> numeroProtocoloCartorio() {
	protocoloCartorio = new TextField<String>("protocoloCartorio", new Model<String>());
	protocoloCartorio.setRequired(true);
	protocoloCartorio.setLabel(new Model<String>("Número de Protocolo"));
	return protocoloCartorio;
    }

    @Override
    protected IModel<InstrumentoProtesto> getModel() {
	return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
    }
}