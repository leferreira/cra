package br.com.ieptbto.cra.report;

import br.com.ieptbto.cra.exception.InfraException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RelatorioPropertiesImpl implements IRelatorioProperties {

    private static final String RESOURCE_FILE = "application.properties";
    private static final String DRIVER_KEY = "jdbc.driver";
    private static final String HOST_KEY = "jdbc.url";
    private static final String USER_KEY = "jdbc.username";
    private static final String PASSWORD_KEY = "jdbc.password";
    protected Properties props;

	public RelatorioPropertiesImpl() {
        getInstance();
	}
	
	@Override
	public String getHost() {
        return (String)props.getProperty(HOST_KEY);
	}

	@Override
	public String getUser() {
        return (String)props.getProperty(USER_KEY);
	}

	@Override
	public String getPassword() {
        return (String)props.getProperty(PASSWORD_KEY);
	}

    @Override
    public String getDriver() {
        return (String)props.getProperty(DRIVER_KEY);
    }

    private Properties getInstance() {
        if (props == null) {
            synchronized(Properties.class) {
                if (props == null) {

                    try {
                        InputStream in = this.getClass().getClassLoader().getResourceAsStream(RESOURCE_FILE);
                        this.props = new Properties();
                        this.props.load(in);

                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new InfraException("Não foi possível carregar o arquivo de configurações de acesso a base de dados. Favor entrar em contato com a CRA...");
                    }
                }
            }
        }
        return props;
    }
}