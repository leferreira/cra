package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RemessaMediator {

	protected static final Logger logger = Logger.getLogger(RemessaMediator.class);
	
	@Autowired
	private RemessaDAO remessaDao;
	@Autowired
	private TipoArquivoDAO tipoArquivoDao;
	private List<Remessa> remessasFiltradas;
	private List<Remessa> remessas = new ArrayList<Remessa>();
	
	public List<Remessa> listarRemessa(Instituicao instituicao){
		return remessaDao.listarRemessasPorInstituicao(instituicao);
	}
	
	public List<Remessa> buscarRemessasComFiltros(Instituicao instituicao,ArrayList<String> tipos,ArrayList<String> status,String date){
		
		try {
			remessasFiltradas = new ArrayList<Remessa>();
			remessas = listarRemessa(instituicao);
			filtroTipoArquivo(tipos);
			filtroStatusArquivo(status);
			if (date != null || date!="")
				filtroData(date);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new InfraException("Não foi possível realizar a busca, contate a CRA.");
		}
		return remessasFiltradas;
	}
	
	private void filtroTipoArquivo(ArrayList<String> tipos){
		//[B,C,D]
		TipoArquivo tipoArquivo = new TipoArquivo();
		if (!tipos.isEmpty() || !tipos.equals(null)) {
			for (String constante: tipos) {
				tipoArquivo = tipoArquivoDao.buscarPorTipoArquivo(TipoArquivoEnum.getTipoArquivoEnum(constante));
				for (Remessa r: remessas){
					if (r.getArquivo().getTipoArquivo().equals(tipoArquivo)){
						if(!remessasFiltradas.contains(r))
						    remessasFiltradas.add(r);      
					}
				}
			}
		}
	}
	
	private void filtroStatusArquivo(ArrayList<String> status){
		if (!status.isEmpty() || !status.equals(null)) {
			for (String situacao: status) {
				for (Remessa r: remessas){
					if (r.getArquivo().getStatusArquivo().getStatus().equals(situacao)){
						if(!remessasFiltradas.contains(r))
						    remessasFiltradas.add(r);      
					}
				}
			}
		}
	}
	
	private void filtroData(String date){
		DateTime dt = DataUtil.stringToDateTime(date);
		for (Remessa r: remessas){
			if (!r.getArquivo().getDataEnvio().equals(dt)){
				if(!remessasFiltradas.contains(r))
				    remessasFiltradas.add(r);      
			}
		}
	}
}
