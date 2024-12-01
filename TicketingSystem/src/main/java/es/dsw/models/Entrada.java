package es.dsw.models;

public class Entrada {

	private String codigo;
	private String pelicula;
	private int sala;
	private String fecha;
	private String hora;
	private int fila;
	private int butaca;

	public Entrada(String codigo, String pelicula, int sala, String fecha, String hora, int fila, int butaca) {
		this.codigo = codigo;
		this.pelicula = pelicula;
		this.sala = sala;
		this.fecha = fecha;
		this.hora = hora;
		this.fila = fila;
		this.butaca = butaca;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getPelicula() {
		return pelicula;
	}

	public void setPelicula(String pelicula) {
		this.pelicula = pelicula;
	}

	public int getSala() {
		return sala;
	}

	public void setSala(int sala) {
		this.sala = sala;
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

	public int getFila() {
		return fila;
	}

	public void setFila(int fila) {
		this.fila = fila;
	}

	public int getButaca() {
		return butaca;
	}

	public void setButaca(int butaca) {
		this.butaca = butaca;
	}

}
