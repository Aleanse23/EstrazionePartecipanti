package it.betacom.estrazionepartecipanti.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbHandler {
	
	private static final String path = "./src/main/resources/db.properties";
	private Connection connection;
	private static final DbHandler instance = new DbHandler();
	private static final Logger logger = LogManager.getLogger(DbHandler.class);
	
	private DbHandler() {
		try (InputStream input = new FileInputStream(path)) {
			Properties properties = new Properties();
			properties.load(input);
			Class.forName("com.mysql.cj.jdbc.Driver");
			String jdbcUrl = properties.getProperty("db.url");
			String schema = properties.getProperty("db.schema");
			String user = properties.getProperty("db.user");
			String password = properties.getProperty("db.password");
			
			connection = DriverManager.getConnection(jdbcUrl + schema, user, password);
			
		} catch (FileNotFoundException e) {
			logger.error("Il file di properties non e' stato trovato: " + path, e);
		} catch (IOException e) {
			logger.error("Errore nella lettura del file: " + path, e);
		} catch (ClassNotFoundException e) {
			logger.error("JBDC driver non trovato", e);
		} catch (SQLException e) {
			logger.error("Connessione al database non riucita", e);
		}
	}
	
	public static DbHandler getInstance() {
		return instance;
	}
	
	public Connection getConnection() {
		return connection;
	}

}
