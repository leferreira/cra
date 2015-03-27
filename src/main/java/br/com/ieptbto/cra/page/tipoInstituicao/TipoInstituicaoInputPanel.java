package br.com.ieptbto.cra.page.tipoInstituicao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.PermissaoEnvioMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;

public class TipoInstituicaoInputPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;
	@SpringBean
	TipoArquivoMediator tipoArquivoMediator;
	@SpringBean
	PermissaoEnvioMediator permissaoEnvioMediator;

	private TipoInstituicao tipoInstituicao = new TipoInstituicao();
	private PermissaoEnvio permitidos = new PermissaoEnvio();
	TipoArquivoEnum arquivoEnum;

	private Component button;
	private List<PermissaoEnvio> listaPermissao = new ArrayList<PermissaoEnvio>();
	private List<TipoArquivoEnum> enumLista = new ArrayList<TipoArquivoEnum>();
	private TextField<String> nomeTipo;
	private CheckBoxMultipleChoice<String> tipoPermitidos;
	private List<String> choices = new ArrayList<String>();

	public TipoInstituicaoInputPanel(String id, IModel<TipoInstituicao> model,TipoInstituicao tipoInstituicao) {
		super(id, model);
		adicionarCampos();
	}

	public TipoInstituicaoInputPanel(String id) {
		super(id);
		adicionarCampos();
	}

	public void adicionarCampos() {
		add(campoTipoInstituicao());
		add(comboTipoArquivos());
		add(botaoSalvar());
	}

	private TextField<String> campoTipoInstituicao() {
		nomeTipo = new TextField<String>("tipoInstituicao");
		nomeTipo.setLabel(new Model<String>("Tipo Instituição"));
		nomeTipo.setRequired(true);
		nomeTipo.setOutputMarkupId(true);
		return nomeTipo;
	}

	public Component comboTipoArquivos() {
		enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.label);
		}
		return tipoPermitidos = new CheckBoxMultipleChoice<String>("arquivosEnvioPermitido", choices);
	}

	private Component botaoSalvar() {
		button = new Button("botaoSalvar") {
			/** */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			public void onSubmit() {
				
				String tipo = (String)nomeTipo.getDefaultModelObject();
				tipoInstituicao.setTipoInstituicao(tipo);
				List<String> selects = (List<String>) tipoPermitidos.getDefaultModelObject();
				if (tipoInstituicao.getId() != 0) {
					TipoInstituicao tipoSalvo = tipoInstituicaoMediator.alterar(tipoInstituicao);
					permitidos.setTipoInstituicao(tipoInstituicao);
					listaPermissao = permissaoEnvioMediator.permissoesPorTipoInstituicao(tipoSalvo);
					for (String s : selects) {
						arquivoEnum = TipoArquivoEnum.valueOf(s);
						if (!listaPermissao.isEmpty()) {
							for (PermissaoEnvio p : listaPermissao) {
								if (p.getTipoArquivo().getTipoArquivo().equals(arquivoEnum)) {
									permitidos.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(arquivoEnum));
									permissaoEnvioMediator.alterar(permitidos);
								}
							}
						} else {
							permitidos.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(arquivoEnum));
							permissaoEnvioMediator.salvar(permitidos);
						}
						if (tipoSalvo != null)
							info("Tipo instituição alterado com sucesso.");
						else
							error("Tipo instituição não alterado");
					}
				} else {
					TipoInstituicao tipoSalvo = tipoInstituicaoMediator.salvar(tipoInstituicao);
					for (String s : selects) {
						if (TipoArquivoEnum.valueOf(s) != null) {
							permitidos.setTipoInstituicao(tipoInstituicao);
							permitidos.setTipoArquivo(tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.valueOf(s)));
							permissaoEnvioMediator.salvar(permitidos);
						}
					}
					if (tipoSalvo != null)
						info("Tipo instituição criado com sucesso");
					else
						error("Tipo instituição não criado");
				}
			}
		};
		return button;

	}
}
