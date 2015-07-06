package br.com.ieptbto.cra.page.instrumentoDeProtesto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoDeProtestoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.relatorio.ExportToPdf;
import br.com.ieptbto.cra.relatorio.SlipUtils;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class InstrumentoDeProtestoPage extends BasePage<InstrumentoProtesto> {

	@SpringBean
	InstrumentoDeProtestoMediator instrumentoMediator;
	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private InstrumentoProtesto instrumento;
	private List<Retorno> listaRetornoSlip;

	private TextField<String> codigoInstrumento;
	private TextField<String> protocoloCartorio;
	private DropDownChoice<Municipio> codigoIbge;

	public InstrumentoDeProtestoPage() {
		this.listaRetornoSlip = new ArrayList<Retorno>();
		adicionarFormularioCodigo();
		adicionarFormularioManual();
		
		add(carregarListaSlips());
		add(new Form<InstrumentoProtesto> ("formSlips", getModel()){
			@Override
			protected void onSubmit() {
				
				try {
					List<InstrumentoProtesto> listaSlip = instrumentoMediator.processarInstrumentos(getListaRetornoSlip());
					SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("dd-MM-yy");
					
					JasperPrint jasperPrint = getSlipUtils().gerarSlipLista(listaSlip);
					new ExportToPdf((HttpServletResponse)getResponse().getContainerResponse(), jasperPrint, "INSTRUMENTOS-" 
							+ dataPadraArquivo.format(new Date()).toString() + ".pdf"); 
				
				} catch (JRException e) {
					e.printStackTrace();
					error("Não foi possível gerar a lista de SLIP ! Entre em contato com a CRA !");
				}
			}
		});
		add(new Form<InstrumentoProtesto> ("formEtiquetas", getModel()){
			@Override
			protected void onSubmit() {
				// TODO Auto-generated method stub
				super.onSubmit();
			}
		});
	}

	private void adicionarFormularioManual() {
		Form<Retorno> formManual = new Form<Retorno>("formManual") {

			@Override
			protected void onSubmit() {
				String numeroProtocolo = protocoloCartorio.getModelObject();
				String codigoIBGE = Municipio.class.cast(codigoIbge.getDefaultModelObject()).getCodigoIBGE();
				Retorno tituloProtestado = instrumentoMediator.buscarTituloProtestado(numeroProtocolo, codigoIBGE);

				if (tituloProtestado != null) {
					if (!getListaRetornoSlip().contains(tituloProtestado)) {
						getListaRetornoSlip().add(tituloProtestado);
					} else
						error("A lista já contem o título!");
				} else
					error("Titulo não encontrado!");
			}
		};
		formManual.add(codigoIbgeCartorio());
		formManual.add(numeroProtocoloCartorio());
		formManual.add(new Button("addManual"));
		add(formManual);
	}

	private void adicionarFormularioCodigo() {
		Form<Retorno> formCodigo = new Form<Retorno>("formCodigo") {

			@Override
			protected void onSubmit() {
				String codigoIbge = codigoInstrumento.getModelObject().substring(1, 8);
				String protocolo = codigoInstrumento.getModelObject().substring(8, 18);
				Retorno tituloProtestado = instrumentoMediator.buscarTituloProtestado(protocolo, codigoIbge);

				if (tituloProtestado != null) {
					if (!getListaRetornoSlip().contains(tituloProtestado)) {
						getListaRetornoSlip().add(tituloProtestado);
					} else
						error("A lista já contem o título!");
				} else 
					error("Titulo não encontrado!");
			}
		};
		formCodigo.add(campoCodigoDeBarra());
		formCodigo.add(new Button("addCodigo"));
		add(formCodigo);
	}

	private ListView<Retorno> carregarListaSlips() {
		return new ListView<Retorno>("instrumentos", getListaRetornoSlip()) {

			@Override
			protected void populateItem(ListItem<Retorno> item) {
				final Retorno retorno = item.getModelObject();
				item.add(new Label("numeroTitulo", retorno.getTitulo().getNumeroTitulo()));
				item.add(new Label("protocolo", retorno.getNumeroProtocoloCartorio()));
				item.add(new Label("pracaProtesto", retorno.getTitulo().getPracaProtesto()));

				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {
					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(retorno.getTitulo()));
					}
				};
				linkHistorico.add(new Label("nomeDevedor", retorno.getTitulo().getNomeDevedor()));
				item.add(linkHistorico);

				item.add(new Label("portador", instituicaoMediator.getInstituicaoPorCodigoPortador(retorno.getTitulo().getCodigoPortador()).getNomeFantasia()));
				item.add(new Label("especie", retorno.getTitulo().getEspecieTitulo()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", retorno.getTitulo().getValorTitulo()));
				item.add(new LabelValorMonetario<BigDecimal>("valorSaldo", retorno.getTitulo().getSaldoTitulo()));
			}
		};
	}

	private TextField<String> campoCodigoDeBarra() {
		return codigoInstrumento = new TextField<String>("codigoInstrumento", new Model<String>());
	}

	private DropDownChoice<Municipio> codigoIbgeCartorio() {
		IChoiceRenderer<Municipio> renderer = new ChoiceRenderer<Municipio>("nomeMunicipio");
		codigoIbge = new DropDownChoice<Municipio>("codigoIbge", new Model<Municipio>(), municipioMediator.listarTodos(), renderer);
		codigoIbge.setRequired(true);
		codigoIbge.setLabel(new Model<String>("Município"));
		return codigoIbge;
	}

	private TextField<String> numeroProtocoloCartorio() {
		protocoloCartorio = new TextField<String>("protocoloCartorio", new Model<String>());
		protocoloCartorio.setRequired(true);
		protocoloCartorio.setLabel(new Model<String>("Número de Protocolo"));
		return protocoloCartorio;
	}

	private SlipUtils getSlipUtils() {
		return new SlipUtils();
	}

	public List<Retorno> getListaRetornoSlip() {
		return listaRetornoSlip;
	}
	
	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumento);
	}
}