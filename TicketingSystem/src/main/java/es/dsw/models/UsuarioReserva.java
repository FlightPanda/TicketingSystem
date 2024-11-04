package es.dsw.models;

public class UsuarioReserva {
	private String nombre;
    private String apellidos;
    private String email;
    private String fecha;
    private String hora;
    private int numEntradasAdult;
    private int numEntradasMen;
    
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public int getNumEntradasAdult() {
		return numEntradasAdult;
	}
	public void setNumEntradasAdult(int numEntradasAdult) {
		this.numEntradasAdult = numEntradasAdult;
	}
	public int getNumEntradasMen() {
		return numEntradasMen;
	}
	public void setNumEntradasMen(int numEntradasMen) {
		this.numEntradasMen = numEntradasMen;
	}

}
