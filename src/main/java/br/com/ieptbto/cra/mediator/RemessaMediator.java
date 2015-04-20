package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
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
	
	/***
	 * 
	 * */
	public List<Remessa> buscarRemessasComFiltros(Instituicao instituicao,ArrayList<String> tipos,ArrayList<String> status,String date){
			
			try{ 
				remessasFiltradas = new ArrayList<Remessa>();
				remessas = listarRemessa(instituicao);
				
				for (Remessa remessa: remessas){
					filtroTipoArquivo(tipos,remessa);
					filtroStatusArquivo(status, remessa, instituicao);
					if (!date.equals(""))
						filtroData(date, remessa);
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				throw new InfraException("Não foi possível realizar a busca, contate a CRA.");
			}
		return remessasFiltradas;
	}
	
	/***
	 * 
	 * */
	public List<Remessa> buscarArquivos(Arquivo arquivo,Municipio municipio,Instituicao portador, LocalDate dataInicio,LocalDate dataFim,ArrayList<String> tipos,Usuario usuario){
		
		try{ 
			remessasFiltradas = new ArrayList<Remessa>();
			remessas = remessaDao.buscarRemessas(arquivo, municipio, portador, dataInicio, dataFim, usuario);
			for (Remessa remessa: remessas){
				if (!tipos.isEmpty()) 
					filtroTipoArquivo(tipos,remessa);
				else
					return remessas;	
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			new InfraException("Não foi possível realizar a busca, contate a CRA.");
		}
		return remessasFiltradas;
	}
	
	private void filtroTipoArquivo(ArrayList<String> tipos,Remessa remessa){
		TipoArquivo tipoArquivo = new TipoArquivo();
		
		for (String constante: tipos) {
			if (remessa.getArquivo().getTipoArquivo().getTipoArquivo().getConstante().equals(constante)){
				if(!remessasFiltradas.contains(remessa))
				    remessasFiltradas.add(remessa);      
			}
		}
	}
	
	private void filtroStatusArquivo(ArrayList<String> status, Remessa remessa, Instituicao instituicaoUsuario){
		if (!status.isEmpty() || !status.equals(null)) {
			for (String situacao: status) {
				switch (situacao) {
					case "Aguardando":
						String statusRemessa = remessa.getArquivo().getStatusArquivo().getStatus();
						if (situacao.equals(statusRemessa)){
							if(!remessasFiltradas.contains(remessa))
								remessasFiltradas.add(remessa);      
						}
						break;
					case "Enviado":
						String instituicaoEnvio = remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia();
						if (instituicaoUsuario.getNomeFantasia().equals(instituicaoEnvio)){
							if(!remessasFiltradas.contains(remessa))
								remessasFiltradas.add(remessa);      
						}
						break;
					case "Recebido":
						String destino = remessa.getInstituicaoDestino().getNomeFantasia();
						if (instituicaoUsuario.getNomeFantasia().equals(destino)){
							if(!remessasFiltradas.contains(remessa))
								remessasFiltradas.add(remessa);      
						}
						break;
				}
				
			}
		}
	}
	
	private void filtroData(String dataFiltro, Remessa remessa){
		String dataEnvio = DataUtil.localDateToString(remessa.getArquivo().getDataEnvio());
		if (dataEnvio.contains(dataFiltro)){
			if(!remessasFiltradas.contains(remessa))
			    remessasFiltradas.add(remessa);      
		}
	}
}
