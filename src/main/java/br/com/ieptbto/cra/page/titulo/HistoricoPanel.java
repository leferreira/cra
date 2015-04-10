package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.mediator.TituloMediator;


public class HistoricoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	@SpringBean
	TituloMediator tituloMediator;
//	private List<Historico> historico;

	public HistoricoPanel(String id, IModel<Titulo> model, Titulo titulo){
        super(id, model);
//        this.historico = tituloMediator.getHistoricoTitulo(titulo);
//        add(historico());
        add(numeroProtocoloCartorio());
        add(dataProtocolo());
        add(codigoCartorio());
        add(pracaProtesto());
        add(nomeSacadorVendedor());
        add(documentoSacador());
        add(ufSacadorVendedor());
        add(cepSacadorVendedor());
        add(cidadeSacadorVendedor());
        add(enderecoSacadorVendedor());
        add(nomeDevedor());
        add(documentoDevedor());
        add(ufDevedor());
        add(cepDevedor());
        add(cidadeDevedor());
        add(enderecoDevedor());
        add(numeroTitulo());
//        add(dataRemessa());
//        add(portador());
//        add(agencia());
        add(nossoNumero());
        add(especieTitulo());
        add(dataEmissaoTitulo());
        add(dataVencimentoTitulo());
        add(valorTitulo());
        add(saldoTitulo());
        add(valorCustaCartorio());
        add(valorGravacaoEletronica());
        add(valorCustasCartorioDistribuidor());
        add(valorDemaisDespesas());
        add(nomeCedenteFavorecido());
        add(agenciaCodigoCedente());
	}
	
//	public Component historico(){
//		return new Label("historico");
//	}
	
	public TextField<String> numeroProtocoloCartorio(){
		return new TextField<String>("numeroProtocoloCartorio");
	}
	
	public TextField<String> dataProtocolo(){
		return new TextField<String>("dataProtocolo");
	}
	
	public TextField<String> codigoCartorio(){
		return new TextField<String>("codigoCartorio");
	}
	
//	public Component cartorio(){
//		return new Label("");
//	}
	
	public TextField<String> pracaProtesto(){
		return new TextField<String>("pracaProtesto");
	}
	
//	public Component status(){
//		return new Label("");
//	}
	
//	public Component dataStatus(){
//		return new Label("");
//	}
	
	public TextField<String> nomeSacadorVendedor(){
		return new TextField<String>("nomeSacadorVendedor");
	}
	
	public TextField<String> documentoSacador(){
		return new TextField<String>("documentoSacador");
	}
	
	public TextField<String> ufSacadorVendedor(){
		return new TextField<String>("ufSacadorVendedor");
	}
	
	public TextField<String> cepSacadorVendedor(){
		return new TextField<String>("cepSacadorVendedor");
	}
	
	public TextField<String> cidadeSacadorVendedor(){
		return new TextField<String>("cidadeSacadorVendedor");
	}
	
	public TextField<String> enderecoSacadorVendedor(){
		return new TextField<String>("enderecoSacadorVendedor");
	}
	
	public TextField<String> nomeDevedor(){
		return new TextField<String>("nomeDevedor");
	}
	
	public TextField<String> documentoDevedor(){
		return new TextField<String>("documentoDevedor");
	}
	
	public TextField<String> ufDevedor(){
		return new TextField<String>("ufDevedor");
	}
	
	public TextField<String> cepDevedor(){
		return new TextField<String>("cepDevedor");
	}
	
	public TextField<String> cidadeDevedor(){
		return new TextField<String>("cidadeDevedor");
	}
	
	public TextField<String> enderecoDevedor(){
		return new TextField<String>("enderecoDevedor");
	}
	
	public TextField<String> numeroTitulo(){
		return new TextField<String>("numeroTitulo");
	}
	
//	public Component portador(){
//		return new Label("");
//	}
	
//	public Component agencia(){
//		return new Label("");
//	}
	
	public TextField<String> nossoNumero(){
		return new TextField<String>("nossoNumero");
	}
	
	public TextField<String> especieTitulo(){
		return new TextField<String>("especieTitulo");
	}
	
	public TextField<String> dataEmissaoTitulo(){
		return new TextField<String>("dataEmissaoTitulo");
	}
	
	public TextField<String> dataVencimentoTitulo(){
		return new TextField<String>("dataVencimentoTitulo");
	}
	
	public TextField<String> valorTitulo(){
		return new TextField<String>("valorTitulo");
	}
	
	public TextField<String> saldoTitulo(){
		return new TextField<String>("saldoTitulo");
	}
	
	public TextField<String> valorCustaCartorio(){
		return new TextField<String>("valorCustaCartorio");
	}
	
	public TextField<String> valorGravacaoEletronica(){
		return new TextField<String>("valorGravacaoEletronica");
	}
	
	public TextField<String> valorCustasCartorioDistribuidor(){
		return new TextField<String>("valorCustasCartorioDistribuidor");
	}
	
	public TextField<String> valorDemaisDespesas(){
		return new TextField<String>("valorDemaisDespesas");
	}
	
	public TextField<String> nomeCedenteFavorecido(){
		return new TextField<String>("nomeCedenteFavorecido");
	}
	
	public TextField<String> agenciaCodigoCedente(){
		return new TextField<String>("agenciaCodigoCedente");
	}
}
