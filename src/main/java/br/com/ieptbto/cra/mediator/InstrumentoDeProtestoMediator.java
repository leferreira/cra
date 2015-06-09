package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstrumentoProtestoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class InstrumentoDeProtestoMediator {
	
	@Autowired
	TituloDAO tituloDao; 
	@Autowired
	InstrumentoProtestoDAO instrumentoDao;

	public Retorno buscarTituloProtestado(String numeroProtocolo, Integer codigoIBGE) {
		return tituloDao.buscarTituloProtestado(numeroProtocolo, codigoIBGE);
	}

	public List<InstrumentoProtesto> processarInstrumentos(List<Retorno> listaRetorno) {
		InstrumentoProtesto instrumentoSalvo = new InstrumentoProtesto();
		List<InstrumentoProtesto> listaSlip = new ArrayList<InstrumentoProtesto>();
		
		for (Retorno retorno: listaRetorno){
			InstrumentoProtesto instrumento = new InstrumentoProtesto();
			
			instrumento.setDataDeEntrada(new LocalDate());
			instrumento.setDataSlip(new LocalDate());
			instrumento.setSituacao(false);
			instrumento.setTitulo(tituloDao.buscarTituloPorChave(retorno.getTitulo()));
			
			instrumentoSalvo = instrumentoDao.salvarInstrumento(instrumento);
			listaSlip.add(instrumentoSalvo);
		}
		return listaSlip;
	}
	
	public List<InstrumentoProtesto> buscarInstrumentosParaSlip() {
		return instrumentoDao.buscarInstrumentosParaSlip();
	}
}
