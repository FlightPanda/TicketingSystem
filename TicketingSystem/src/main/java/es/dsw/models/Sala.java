package es.dsw.models;

public class Sala {
	private int numeroSala;
	private String pelicula; 
	private double precioEntrada;
	
	public Sala(int numeroSala, String pelicula, double precioEntrada) {
		this.numeroSala = numeroSala; 
		this.pelicula = pelicula;
		this.precioEntrada = precioEntrada;
	}

	public int getNumeroSala() {
		return numeroSala;
	}

	public void setNumeroSala(int numeroSala) {
		this.numeroSala = numeroSala;
	}

	public String getPelicula() {
		return pelicula;
	}

	public void setPelicula(String pelicula) {
		this.pelicula = pelicula;
	}

	public double getPrecioEntrada() {
		return precioEntrada;
	}

	public void setPrecioEntrada(double precioEntrada) {
		this.precioEntrada = precioEntrada;
	}

}
