package es.dsw.daos;

import es.dsw.connections.MySqlConnection;
import es.dsw.models.Entrada;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TicketDAO {

    private final MySqlConnection objMySqlConnection;

    public TicketDAO(MySqlConnection objMySqlConnection) {
        this.objMySqlConnection = objMySqlConnection;
    	// TODO: Finalizar inserción de esta parte en la base de datos 

    }
}
