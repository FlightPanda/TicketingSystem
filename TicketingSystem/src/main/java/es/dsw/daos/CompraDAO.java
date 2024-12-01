package es.dsw.daos;

import es.dsw.connections.MySqlConnection;
import es.dsw.models.UsuarioReserva;
import es.dsw.models.Entrada;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class CompraDAO {

	private MySqlConnection objMySqlConnection;

	public CompraDAO() {
		objMySqlConnection = new MySqlConnection();
	}

	public boolean procesarCompra(UsuarioReserva usuarioReserva, List<Entrada> entradas, String titularTarjeta,
			String numeroTarjeta, String mesCaduca, String anioCaduca, String codigoSeguridad, double totalCompra) {

		objMySqlConnection.open();
		if (objMySqlConnection.isError()) {
			System.err.println("Error al abrir la conexión a la base de datos.");
			return false;
		}

		try {

			// 1. Inserta la compra y obtiene el ID generado
			int idCompra = insertarCompra(usuarioReserva, titularTarjeta, numeroTarjeta, mesCaduca, anioCaduca,
					codigoSeguridad, totalCompra);

			if (idCompra == -1) {
				throw new RuntimeException("Error al insertar la compra.");
			}

			return false;
		} finally {
			objMySqlConnection.close();
		}

	}

	/**
	 * Inserta una compra en la tabla BUYTICKETS_FILM.
	 */
	private int insertarCompra(UsuarioReserva usuarioReserva, String titularTarjeta, String numeroTarjeta,
			String mesCaduca, String anioCaduca, String codigoSeguridad, double totalCompra) {

		String insertCompraSql = """
				    INSERT INTO DB_FILMCINEMA.BUYTICKETS_FILM
					(NAME_BTF, SURNAMES_BTF, EMAIL_BTF,
					CARDHOLDER_BTF, CARDNUMBER_BTF, MONTHCARD_BTF, YEARCARD_BTF,
					CCS_CARD_CODE_BTF, TOTALPRICE_BTF, S_ACTIVEROW_BTF, S_INSERTDATE_BTF,
					S_UPDATEDATE_BTF, S_IDUSER_BTF)

				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, b'1', NOW(), NOW(), '1')
				""";

		objMySqlConnection.open();

		try {
			// Crear un PreparedStatement manualmente
			PreparedStatement stmt = objMySqlConnection.prepareInsertStatement(insertCompraSql);

			// Asignar los valores de los parámetros
			stmt.setString(1, usuarioReserva.getNombre());
			stmt.setString(2, usuarioReserva.getApellidos());
			stmt.setString(3, usuarioReserva.getEmail());
			stmt.setString(4, titularTarjeta);
			stmt.setString(5, numeroTarjeta);
			stmt.setString(6, mesCaduca);
			stmt.setString(7, anioCaduca);
			stmt.setString(8, codigoSeguridad);
			stmt.setDouble(9, totalCompra);

			// Ejecutar la consulta
			stmt.executeUpdate();

			// Obtener las claves generadas
			ResultSet result = stmt.getGeneratedKeys();
			if (result != null && result.next()) {
				return result.getInt(1); // Retorna el ID generado
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error al insertar la compra: " + e.getMessage());
		} finally {
			objMySqlConnection.close(); // Cierra la conexión
		}

		return -1; // Retorna -1 si hay un error
	}

}