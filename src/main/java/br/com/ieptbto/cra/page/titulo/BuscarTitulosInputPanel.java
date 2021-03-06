package br.com.ieptbto.cra.page.titulo;

import br.com.ieptbto.cra.beans.TituloBean;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
public class BuscarTitulosInputPanel extends Panel {

	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;

	private static final long serialVersionUID = 1L;
	private List<Instituicao> listaInstituicoes;
	private DropDownChoice<Instituicao> dropDownInstituicao;
	private Label labelMunicipio;
	private DropDownChoice<Instituicao> dropDownCartorio;
	private TipoInstituicaoCRA tipoInstituicao;

	public BuscarTitulosInputPanel(String id, IModel<TituloBean> model, Usuario user) {
		super(id, model);
		this.tipoInstituicao = user.getInstituicao().getTipoInstituicao().getTipoInstituicao();
		adicionarCampos();
	}

	private void adicionarCampos() {
		add(textFieldNossoNumero());
		add(textFieldNumeroTitulo());
		add(dateFieldDataInicio());
		add(dateFieldDataFinal());
		add(textFieldNumeroProtocoloCartorio());
		add(labelBancoCovenio());
		add(dropDownTipoInstituicao());
		add(dropDownBancoConvenios());
		add(labelMunicipio());
		add(dropDownCartorio());
		add(nomeDevedor());
		add(documentoDevedor());
		add(nomeSacador());
		add(documentoSacador());
	}

	private TextField<String> textFieldNossoNumero() {
		return new TextField<String>("nossoNumero");
	}

	private TextField<String> textFieldNumeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}

	private DateTextField dateFieldDataInicio() {
		DateTextField dataInicioTextField = new DateTextField("dataInicio", "dd/MM/yyyy");
		dataInicioTextField.setLabel(new Model<String>("Período de datas"));
		dataInicioTextField.setMarkupId("date");
		return dataInicioTextField;
	}

	private DateTextField dateFieldDataFinal() {
		DateTextField dataFimTextField = new DateTextField("dataFim", "dd/MM/yyyy");
		dataFimTextField.setMarkupId("date1");
		return dataFimTextField;
	}

	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}

	private TextField<String> documentoDevedor() {
		return new TextField<String>("numeroIdentificacaoDevedor");
	}

	private TextField<String> nomeSacador() {
		return new TextField<String>("nomeCredor");
	}

	private TextField<String> documentoSacador() {
		return new TextField<String>("documentoCredor");
	}

	private TextField<String> textFieldNumeroProtocoloCartorio() {
		return new TextField<String>("numeroProtocoloCartorio");
	}

	private Label labelBancoCovenio() {
		Label labelBancoCovenio = new Label("labelBancoCovenio", "Banco/Convênio");
		labelBancoCovenio.setOutputMarkupId(true);
		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(tipoInstituicao) || TipoInstituicaoCRA.CONVENIO.equals(tipoInstituicao)) {
			labelBancoCovenio.setVisible(false);
		}
		return labelBancoCovenio;
	}

	private DropDownChoice<Instituicao> dropDownBancoConvenios() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		dropDownInstituicao = new DropDownChoice<Instituicao>("bancoConvenio", getListaInstituicoes(), renderer);
		dropDownInstituicao.setLabel(new Model<String>("Banco/Convênio"));
		dropDownInstituicao.setOutputMarkupId(true);
		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(tipoInstituicao) || TipoInstituicaoCRA.CONVENIO.equals(tipoInstituicao)) {
			dropDownInstituicao.setVisible(false);
		}
		return dropDownInstituicao;
	}

	private DropDownChoice<TipoInstituicaoCRA> dropDownTipoInstituicao() {
		IChoiceRenderer<TipoInstituicaoCRA> renderer = new ChoiceRenderer<TipoInstituicaoCRA>("label");
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CONVENIO);
		choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
		final DropDownChoice<TipoInstituicaoCRA> dropDowntipoInstituicao =
				new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao", choices, renderer);
		dropDowntipoInstituicao.add(new OnChangeAjaxBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TipoInstituicaoCRA tipo = dropDowntipoInstituicao.getModelObject();

				if (dropDowntipoInstituicao.getModelObject() != null) {
					if (tipo.equals(TipoInstituicaoCRA.CONVENIO)) {
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getConvenios());
					} else if (tipo.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
						getListaInstituicoes().clear();
						getListaInstituicoes().addAll(instituicaoMediator.getInstituicoesFinanceiras());
					}
					dropDownInstituicao.setEnabled(true);
				} else {
					dropDownInstituicao.setEnabled(false);
					getListaInstituicoes().clear();
				}
				target.add(dropDownInstituicao);
			}
		});
		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(tipoInstituicao) || TipoInstituicaoCRA.CONVENIO.equals(tipoInstituicao)) {
			dropDowntipoInstituicao.setVisible(false);
		}
		dropDowntipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		return dropDowntipoInstituicao;
	}

	private Label labelMunicipio() {
		labelMunicipio = new Label("labelMunicipio", "Município");
		labelMunicipio.setOutputMarkupId(true);
		if (TipoInstituicaoCRA.CARTORIO.equals(tipoInstituicao)) {
			labelMunicipio.setVisible(false);
		}
		return labelMunicipio;
	}

	private DropDownChoice<Instituicao> dropDownCartorio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio");
		dropDownCartorio = new DropDownChoice<Instituicao>("cartorio", instituicaoMediator.getCartorios(), renderer);
		dropDownCartorio.setOutputMarkupId(true);
		if (TipoInstituicaoCRA.CARTORIO.equals(tipoInstituicao)) {
			dropDownCartorio.setVisible(false);
		}
		return dropDownCartorio;
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			this.listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}
}
