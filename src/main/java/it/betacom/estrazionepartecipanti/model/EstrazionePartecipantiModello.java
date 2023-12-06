package it.betacom.estrazionepartecipanti.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import it.betacom.estrazionepartecipanti.connection.DbHandler;

public class EstrazionePartecipantiModello {

	private static final Logger logger = LogManager.getLogger(EstrazionePartecipantiModello.class);
	private static String path = "./src/main/resources/estrazionePartecipanti.csv";

	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		EstrazionePartecipantiModello.path = path;
	}

	// cambiato la tabella estrazione togliendo nome, citta e mettendo id_estr. e
	// id_pers con foreign key ecc..
	public static void creaTabelle() {
		DbHandler dbHandler = DbHandler.getInstance();
		Connection connection = dbHandler.getConnection();
		Statement statement = null;

		try {
			statement = connection.createStatement();
			statement
					.executeUpdate("CREATE TABLE partecipanti (" + "ID_PERSONA INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
							+ "Nome VARCHAR(255)," + "Citta VARCHAR(255));");
			statement.executeUpdate("CREATE TABLE estrazione ("
					+ "ID_ESTRAZIONE INT PRIMARY KEY NOT NULL AUTO_INCREMENT," + "DataEstrazione DATE,"
					+ "ID_PERSONA INT," + "FOREIGN KEY (ID_PERSONA) REFERENCES partecipanti(ID_PERSONA));");
			logger.info("Tabelle create con successo");
		} catch (SQLException e) {
			logger.error("Tabelle non create, errore: " + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				logger.error("Errore nella chiusura dello statement in crea tabelle", e);
			}

			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("Errore nella chiusura della connessione in crea tabelle", e);
			}
		}
	}

	public static List<String[]> letturaCsv() {
		List<String[]> risultato = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path));
			String linea = reader.readLine();
			while (linea != null) {
				String[] riga = linea.split(";");
				risultato.add(riga);
				for (String cella : riga) {
					logger.info(cella + " ");
				}
				linea = reader.readLine();
			}
			logger.info("File CSV letto correttamente");
		} catch (IOException e) {
			logger.error("Errore durante la lettura del file CSV: ", e);
		}
		return risultato;
	}

	public static void inserimentoDatiSuTabellaPartecipanti(List<String[]> lista) {
		DbHandler dbHandler = DbHandler.getInstance();
		Connection connection = dbHandler.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			for (String[] array : lista) {
				String nome = array[0];
				String citta = array[1];
				statement.executeUpdate(
						"INSERT INTO partecipanti (Nome, Citta) VALUES ('" + nome + "', '" + citta + "')");
			}
			logger.info("Dati inseriti correttamente nella tabella partecipanti");
		} catch (SQLException e) {
			logger.error("Errore eseguimento query in inserimentoDatiSuTabellaPartecipanti", e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(
						"Errore nella chiusura dello statement o della connessione in inserimentoDatiSuTabellaPartecipanti",
						e);
			}
		}

	}

	// cambiato il return da String[] a int
	public static int estrazionePartecipanti() {
		DbHandler dbHandler = DbHandler.getInstance();
		Connection connection = dbHandler.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		int idPersona = 0;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT ID_PERSONA FROM partecipanti ORDER BY RAND() LIMIT 1;");
			if (resultSet.next()) {
				idPersona = resultSet.getInt("ID_PERSONA");
			}

		} catch (SQLException e) {
			logger.error("Errore eseguimento query in estrazionePartecipanti", e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				logger.error("Errore nella chiusura del resultSet in estrazionePartecipanti", e);
			}
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				logger.error("Errore nella chiusura dello statement in estrazionePartecipanti", e);
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("Errore nella chiusura della connessione in estrazionePartecipanti", e);
			}
		}
		return idPersona;
	}

	public static void inserimentoDatiSuTabellaEstrazione(int persona) {
		DbHandler dbHandler = DbHandler.getInstance();
		Connection connection = dbHandler.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(
					"INSERT INTO estrazione (ID_PERSONA, DataEstrazione) VALUES (" + persona + ", CURRENT_DATE())");
			logger.info("Dati inseriti correttamente nella tabella estrazione");
		} catch (SQLException e) {
			logger.error("Errore eseguimento query in inserimentoDatiSuTabellaEstrazione", e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(
						"Errore nella chiusura dello statement o della connessione in inserimentoDatiSuTabellaEstrazione",
						e);
			}
		}

	}

	//aggiunto la colonna numeroOccorrenze
	public static void stampaPDFConEstrazioni() {
		DbHandler dbHandler = DbHandler.getInstance();
		Connection connection = dbHandler.getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		List<String[]> listaPersoneEstratte = new ArrayList<>();
		String DEST = "./EstrazionePartecipanti.pdf";
		Document document = new Document();
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(
					"SELECT Nome, Citta, COUNT(estrazione.ID_ESTRAZIONE) as NumeroOccorrenze " + "FROM estrazione "
							+ "JOIN partecipanti ON partecipanti.ID_PERSONA = estrazione.ID_PERSONA "
							+ "GROUP BY Nome, Citta " + "ORDER BY NumeroOccorrenze DESC;");
			while (resultSet.next()) {
				String nome = resultSet.getString("Nome");
				String citta = resultSet.getString("Citta");
				String numeroOccorrenze = resultSet.getString("NumeroOccorrenze");
				listaPersoneEstratte.add(new String[] { nome, citta, numeroOccorrenze });
			}
			PdfWriter.getInstance(document, new FileOutputStream(DEST));
			document.open();
			PdfPTable tabella = new PdfPTable(3);
			tabella.addCell("Nome");
			tabella.addCell("Citta");
			tabella.addCell("Numero Occorrenze");

			for (String[] lista : listaPersoneEstratte) {
				for (String dato : lista) {
					tabella.addCell(dato);
				}
			}
			document.add(tabella);
		} catch (SQLException e) {
			logger.error("Errore connessione non riuscita in stampaPDFConEstrazioni", e);
		} catch (FileNotFoundException e) {
			logger.error("Errore file non trovato in stampaPDFConEstrazioni");

		} catch (DocumentException e) {
			logger.error("Errore scrittura documento in stampaPDFConEstrazioni");
		} finally {
			document.close();
		}
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			logger.error("Errore nella chiusura del resultset o dello statement o della connessione in stampaPDFConEstrazioni", e);
		}
	}

	public static void inizializzare() {
		EstrazionePartecipantiModello.creaTabelle();
		EstrazionePartecipantiModello.inserimentoDatiSuTabellaPartecipanti(EstrazionePartecipantiModello.letturaCsv());
	}

	public static void estrazione(int nEstrazioni) {
		while (nEstrazioni > 0) {
			EstrazionePartecipantiModello
					.inserimentoDatiSuTabellaEstrazione(EstrazionePartecipantiModello.estrazionePartecipanti());
			nEstrazioni -= 1;
		}
	}

	public static void riinizializza() {
		DbHandler dbHandler = DbHandler.getInstance();
		Connection connection = dbHandler.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate("DROP TABLE partecipanti, estrazione;");
			logger.info("Processo riinizializzato");
		} catch (SQLException e) {
			logger.error("Errore creazione statement in riinizializza", e);
		} finally {

			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("Errore nella chiusura dello statement o della connessione in riinizializza", e);
			}
		}

	}
}
