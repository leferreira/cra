package br.com.ieptbto.cra.robo;

import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.mediator.CraMediator;

/**
 * @author Thasso Araújo
 *
 */
public class RoboDeArquivos {

	public static final Logger logger = Logger.getLogger(RoboDeArquivos.class);
	public static final LocalDateTime HORA_INICIO_EXPEDIENTE = DataUtil.stringToLocalDateTime("HH:mm", "07:59");
	public static final LocalDateTime HORA_FINAL_EXPEDIENTE = DataUtil.stringToLocalDateTime("HH:mm", "17:01");

	private ClassPathXmlApplicationContext context;
	private CraMediator craMediator;

	public RoboDeArquivos() {
		initContext();
		iniciarProcesso();
	}

	private void initContext() {
		if (context == null) {
			this.context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}
		this.craMediator = (CraMediator) context.getBean("craMediator");
	}

	private void mensagemInício() {
		logger.info("=====================================================");
		logger.info("Iniciando thread : Robô de Arquivos");
		logger.info("=====================================================");
	}

	private void iniciarProcesso() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				mensagemInício();
			}
		};
		thread.start();
	}

}