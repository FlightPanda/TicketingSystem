package es.dsw.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.dsw.connections.MySqlConnection;
import es.dsw.daos.CompraDAO;
import es.dsw.models.Entrada;
import es.dsw.models.Sala;
import es.dsw.models.UsuarioReserva;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class MainController {
	
    private final CompraDAO compraDAO;
    

	@Autowired
    public MainController(CompraDAO compraDAO) {
        this.compraDAO = compraDAO;
    }

	@GetMapping(value = { "/", "/index" })
	public String index(Model model, HttpSession session) {
		session.invalidate(); // Limpia la sesión al acceder a index

		LocalDateTime now = LocalDateTime.now();

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH'h' mm'm'");
		String hora = now.format(timeFormatter);

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, 'día' dd 'de' MMMM");
		String fecha = now.format(dateFormatter);

		DayOfWeek dayOfWeek = now.getDayOfWeek();
		String mensajeDelDia = "";
		double precioEntrada = 6.0;

		switch (dayOfWeek) {
		case MONDAY:
			mensajeDelDia = "Comienza la semana a lo grande.";
			break;
		case TUESDAY:
			mensajeDelDia = "Hoy doble de palomitas.";
			break;
		case WEDNESDAY:
			mensajeDelDia = "Día del espectador.";
			precioEntrada = 3.5;
			break;
		case THURSDAY:
			mensajeDelDia = "La noche de las aventuras.";
			break;
		case FRIDAY:
			mensajeDelDia = "No te quedes en tu casa.";
			break;
		case SATURDAY:
			mensajeDelDia = "¿Ya has hecho planes para esta noche?";
			break;
		case SUNDAY:
			mensajeDelDia = "Vente y carga las pilas.";
			break;
		}

		model.addAttribute("hora", hora);
		model.addAttribute("fecha", fecha);
		model.addAttribute("mensajeDelDia", mensajeDelDia);
		model.addAttribute("precioEntrada", precioEntrada);

		return "index";
	}

	@GetMapping("/step1")
	public String step1(Model model, HttpSession session) {
		MySqlConnection dbConnection = new MySqlConnection();
		dbConnection.open();

		LocalDateTime now = LocalDateTime.now();
		DayOfWeek dayOfWeek = now.getDayOfWeek();

		List<Sala> salas = new ArrayList<>();

		String query = "SELECT NUMBERROOM_RCF AS NUMSALA, IDFILM_SSF AS IDPELICULA, IDSESSION_SSF AS IDSESION "
				+ "FROM DB_FILMCINEMA.SESSION_FILM, DB_FILMCINEMA.ROOMCINEMA_FILM "
				+ "WHERE S_ACTIVEROW_SSF = 1 AND IDROOMCINEMA_RCF = IDROOMCINEMA_SSF "
				+ "AND S_ACTIVEROW_RCF = 1 ORDER BY NUMBERROOM_RCF ASC";

		ResultSet resultSet = dbConnection.executeSelect(query);
		try {
			while (resultSet.next()) {
				int numeroSala = resultSet.getInt("NUMSALA");
				int pelicula = resultSet.getInt("IDPELICULA");
				double precioEntrada = (LocalDateTime.now().getDayOfWeek() == DayOfWeek.WEDNESDAY) ? 3.5 : 6.0;
				Sala sala = new Sala(numeroSala, pelicula, precioEntrada);
				salas.add(sala);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbConnection.close();
		}

		model.addAttribute("salas", salas);
		session.setAttribute("salas", salas);
		session.removeAttribute("filmSeleccionado");
		session.removeAttribute("salaSeleccionada");

		return "Views/step1";
	}

	@GetMapping("/step2")
	public String getStep2(@RequestParam(value = "film", required = false) String film, HttpSession session,
			Model model) {

		if (film == null && session.getAttribute("filmSeleccionado") == null) {
			return "redirect:/step1";
		}

		if (film != null) {
			session.setAttribute("filmSeleccionado", film);
		}

		@SuppressWarnings("unchecked")
		List<Sala> salas = (List<Sala>) session.getAttribute("salas");
		if (salas != null) {
			// Busca la sala correspondiente a la película seleccionada
			Sala salaSeleccionada = salas.stream().filter(s -> Integer.toString(s.getPelicula()).equals(film))
					.findFirst().orElse(null);
			session.setAttribute("salaSeleccionada", salaSeleccionada); // Guarda la sala en la sesión
			model.addAttribute("salaSeleccionada", salaSeleccionada); // Añade al modelo
		}

		model.addAttribute("filmSeleccionado", session.getAttribute("filmSeleccionado"));
		model.addAttribute("nombre", session.getAttribute("nombre"));
		model.addAttribute("apellidos", session.getAttribute("apellidos"));
		model.addAttribute("email", session.getAttribute("email"));
		model.addAttribute("repEmail", session.getAttribute("repEmail"));
		model.addAttribute("fecha", session.getAttribute("fecha"));
		model.addAttribute("hora", session.getAttribute("hora"));
		model.addAttribute("numEntradasAdult", session.getAttribute("numEntradasAdult"));
		model.addAttribute("numEntradasMen", session.getAttribute("numEntradasMen"));
		return "Views/step2";
	}

	@PostMapping("/step3")
	public String step3(@RequestParam("fnom") String nombre, @RequestParam("fapell") String apellidos,
			@RequestParam("fmail") String email, @RequestParam("frepmail") String repEmail,
			@RequestParam("fdate") String fecha, @RequestParam("fhour") String hora,
			@RequestParam("fnumentradasadult") int numEntradasAdult,
			@RequestParam("fnumentradasmen") int numEntradasMen, Model model, HttpSession session) {

		boolean hasError = false;

		if (nombre == null || nombre.isEmpty()) {
			model.addAttribute("errorNombre", true);
			hasError = true;
		}
		if (email == null || email.isEmpty()) {
			model.addAttribute("errorEmail", true);
			hasError = true;
		}
		if (!email.equalsIgnoreCase(repEmail)) {
			model.addAttribute("errorRepEmail", true);
			hasError = true;
		}
		if (fecha == null || fecha.isEmpty()) {
			model.addAttribute("errorFecha", true);
			hasError = true;
		}

		if (hora == null || hora.isEmpty()) {
			model.addAttribute("errorHora", true);
			hasError = true;
		}

		if (numEntradasAdult < 1) {
			model.addAttribute("errorNumEntradasAdult", true);
			hasError = true;
		}

		if (hasError) {
			model.addAttribute("filmSeleccionado", session.getAttribute("filmSeleccionado"));
			return "Views/step2";
		}
		int totalButacas = numEntradasAdult + numEntradasMen;
		session.setAttribute("totalButacas", totalButacas);
		model.addAttribute("totalButacas", totalButacas);

		UsuarioReserva usuarioReserva = new UsuarioReserva();
		usuarioReserva.setNombre(nombre);
		usuarioReserva.setApellidos(apellidos);
		usuarioReserva.setEmail(email);
		usuarioReserva.setFecha(fecha);
		usuarioReserva.setHora(hora);
		usuarioReserva.setNumEntradasAdult(numEntradasAdult);
		usuarioReserva.setNumEntradasMen(numEntradasMen);
		session.setAttribute("usuarioReserva", usuarioReserva);

		model.addAttribute("filmSeleccionado", session.getAttribute("filmSeleccionado"));
		model.addAttribute("usuarioReserva", usuarioReserva);

		return "Views/step3";
	}

	@GetMapping("/step3")
	public String step3(Model model, HttpSession session) {
		UsuarioReserva usuarioReserva = (UsuarioReserva) session.getAttribute("usuarioReserva");

		if (usuarioReserva != null) {
			model.addAttribute("nombre", usuarioReserva.getNombre());
			model.addAttribute("apellidos", usuarioReserva.getApellidos());
			model.addAttribute("email", usuarioReserva.getEmail());
			model.addAttribute("fecha", usuarioReserva.getFecha());
			model.addAttribute("hora", usuarioReserva.getHora());
			model.addAttribute("numEntradasAdult", usuarioReserva.getNumEntradasAdult());
			model.addAttribute("numEntradasMen", usuarioReserva.getNumEntradasMen());
		}
		Integer totalButacas = (Integer) session.getAttribute("totalButacas");
		model.addAttribute("totalButacas", totalButacas != null ? totalButacas : 0);

		String butacasSeleccionadasStr = (String) session.getAttribute("butacasSeleccionadas");
		model.addAttribute("butacasSeleccionadas", butacasSeleccionadasStr != null ? butacasSeleccionadasStr : "");

		int butacasSeleccionadasCount = butacasSeleccionadasStr != null ? butacasSeleccionadasStr.split(",").length : 0;
		model.addAttribute("butacasSeleccionadasCount", butacasSeleccionadasCount);

		return "Views/step3";
	}

	@GetMapping("/step4")
	public String step4(Model model, HttpSession session) {
		UsuarioReserva usuarioReserva = (UsuarioReserva) session.getAttribute("usuarioReserva");
		Sala salaSeleccionada = (Sala) session.getAttribute("salaSeleccionada");
		Entrada entrada = (Entrada) session.getAttribute("entradas");

		if (usuarioReserva == null) {
			return "redirect:/step3";
		}

		double precioEntradaMenor = 3.5;
		double totalCompra = (usuarioReserva.getNumEntradasAdult() * salaSeleccionada.getPrecioEntrada())
				+ (usuarioReserva.getNumEntradasMen() * precioEntradaMenor);

		session.setAttribute("totalCompra", totalCompra);
		model.addAttribute("usuarioReserva", usuarioReserva);
		model.addAttribute("salaSeleccionada", salaSeleccionada);
		model.addAttribute("precioEntradaMenor", precioEntradaMenor);
		model.addAttribute("totalCompra", totalCompra);
		model.addAttribute("entradas", entrada);
		return "Views/step4";

	}

	@PostMapping("/step4")
	public String step4(@RequestParam("FButacasSelected") String butacasSeleccionadas, Model model,
			HttpSession session) {
		UsuarioReserva usuarioReserva = (UsuarioReserva) session.getAttribute("usuarioReserva");
		Sala salaSeleccionada = (Sala) session.getAttribute("salaSeleccionada");

		if (usuarioReserva == null) {
			return "redirect:/step3";
		}

		Integer totalButacas = (Integer) session.getAttribute("totalButacas");

		String[] butacasArray = butacasSeleccionadas.split(",");
		int numButacasSeleccionadas = butacasArray.length;

		double precioEntradaMenor = 3.5;
		double totalCompra = (usuarioReserva.getNumEntradasAdult() * salaSeleccionada.getPrecioEntrada())
				+ (usuarioReserva.getNumEntradasMen() * precioEntradaMenor);

		session.setAttribute("butacasSeleccionadas", butacasSeleccionadas);
		model.addAttribute("butacasSeleccionadas", butacasSeleccionadas);
		model.addAttribute("filmSeleccionado", session.getAttribute("filmSeleccionado"));
		model.addAttribute("usuarioReserva", usuarioReserva);
		model.addAttribute("salaSeleccionada", salaSeleccionada);
		model.addAttribute("totalCompra", totalCompra);

		return "Views/step4";
	}

	@GetMapping("/end")
	public String getEnd(Model model, HttpSession session) {

		UsuarioReserva usuarioReserva = (UsuarioReserva) session.getAttribute("usuarioReserva");
		Sala salaSeleccionada = (Sala) session.getAttribute("salaSeleccionada");
		String butacasSeleccionadasStr = (String) session.getAttribute("butacasSeleccionadas");

		if (usuarioReserva == null || salaSeleccionada == null) {
			System.err.println("Error: Datos del usuario o sala no encontrados en la sesión.");
			return "redirect:/step4";
		}

		List<Entrada> entradas = new ArrayList<>();
		if (butacasSeleccionadasStr != null && !butacasSeleccionadasStr.isEmpty()) {
			String[] butacas = butacasSeleccionadasStr.split(";");

			for (String butaca : butacas) {
				try {
					String filaStr = butaca.replaceAll("[^0-9]", " ").trim().split(" ")[0];
					String butacaStr = butaca.replaceAll("[^0-9]", " ").trim().split(" ")[1];

					int fila = Integer.parseInt(filaStr);
					int butacaNum = Integer.parseInt(butacaStr);

					String codigo = generarCodigoEntrada(entradas.size() + 1);

					Entrada entrada = new Entrada(codigo, salaSeleccionada.getPelicula() + "",
							salaSeleccionada.getNumeroSala(), usuarioReserva.getFecha(), usuarioReserva.getHora(), fila,
							butacaNum);
					entradas.add(entrada);

				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					System.err.println("Error al procesar butaca: " + butaca);
				}
			}
		}
		session.setAttribute("entradas", entradas);

		model.addAttribute("usuarioReserva", usuarioReserva);
		model.addAttribute("entradas", entradas);

		return "Views/end";
	}

	@PostMapping("/end")
	public String postEnd(@RequestParam("ftitulartarjeta") String titularTarjeta,
			@RequestParam("fnumtarjeta") String numeroTarjeta, @RequestParam("fMesCaduca") String mesCaduca,
			@RequestParam("fAnioCaduca") String anioCaduca, @RequestParam("fccstarjeta") String codigoSeguridad,
			Model model, HttpSession session) {

		UsuarioReserva usuarioReserva = (UsuarioReserva) session.getAttribute("usuarioReserva");

		@SuppressWarnings("unchecked")
		List<Entrada> entradas = (List<Entrada>) session.getAttribute("entradas");

		if (usuarioReserva == null) {
			System.err.println("Error: Datos del usuario no encontrados en la sesión.");
			return "redirect:/step4";
		}

		Double totalCompra = (Double) session.getAttribute("totalCompra");
	    if (totalCompra == null) {
	        Sala salaSeleccionada = (Sala) session.getAttribute("salaSeleccionada");
	        if (salaSeleccionada != null) {
	            double precioEntradaMenor = 3.5;
	            totalCompra = (usuarioReserva.getNumEntradasAdult() * salaSeleccionada.getPrecioEntrada())
	                    + (usuarioReserva.getNumEntradasMen() * precioEntradaMenor);
	            session.setAttribute("totalCompra", totalCompra);
	        } else {
	            System.err.println("Error: No se pudo calcular totalCompra.");
	            return "redirect:/step4";
	        }
	    }
		entradas = generarEntradas(session);
		boolean compraExitosa = compraDAO.procesarCompra(usuarioReserva, entradas, titularTarjeta, 
                numeroTarjeta, mesCaduca, anioCaduca, 
                codigoSeguridad, totalCompra);

		session.setAttribute("entradas", entradas);
		session.setAttribute("totalCompra", totalCompra); 
		model.addAttribute("totalCompra", totalCompra); 
		model.addAttribute("usuarioReserva", usuarioReserva);
		model.addAttribute("entradas", entradas);

		return "Views/end";
	}

	private List<Entrada> generarEntradas(HttpSession session) {
		List<Entrada> entradas = new ArrayList<>();

		UsuarioReserva usuarioReserva = (UsuarioReserva) session.getAttribute("usuarioReserva");
		Sala salaSeleccionada = (Sala) session.getAttribute("salaSeleccionada");
		String butacasSeleccionadasStr = (String) session.getAttribute("butacasSeleccionadas");

		if (usuarioReserva == null || salaSeleccionada == null || butacasSeleccionadasStr == null) {
			System.err.println("Error: Datos insuficientes para generar entradas.");
			return entradas;
		}

		String[] butacas = butacasSeleccionadasStr.split(";");
		for (String butaca : butacas) {
			try {
				if (butaca.startsWith("F") && butaca.contains("B")) {
					int fila = Integer.parseInt(butaca.substring(1, butaca.indexOf("B")));

					int numero = Integer.parseInt(butaca.substring(butaca.indexOf("B") + 1));

					String codigo = generarCodigoEntrada(entradas.size() + 1);

					Entrada entrada = new Entrada(codigo, salaSeleccionada.getPelicula() + "",
							salaSeleccionada.getNumeroSala(), usuarioReserva.getFecha(), usuarioReserva.getHora(), fila,
							numero);
					entradas.add(entrada);
				}
			} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
				System.err.println("Error al procesar butaca: " + butaca + " - " + e.getMessage());
			}
		}

		return entradas;
	}

	private String generarCodigoEntrada(int numeroEntrada) {
		return "100000" + System.currentTimeMillis() + numeroEntrada;
	}

	@GetMapping("/imprimirTicket")
	public String imprimirEntradas(Model model, HttpSession session) {
		// Recuperar las entradas de la sesión
		@SuppressWarnings("unchecked")
		List<Entrada> entradas = (List<Entrada>) session.getAttribute("entradas");
		if (entradas == null || entradas.isEmpty()) {
			return "redirect:/end"; // Redirige si no hay entradas
		}

		model.addAttribute("entradas", entradas);

		return "Views/imprimirTicket";
	}

}
