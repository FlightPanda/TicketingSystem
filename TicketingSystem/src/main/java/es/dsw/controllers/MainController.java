package es.dsw.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.dsw.connections.MySqlConnection;
import es.dsw.models.Sala;
import es.dsw.models.UsuarioReserva;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class MainController {

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
		session.removeAttribute("filmSeleccionado");

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
			model.addAttribute("nombre", nombre);
			model.addAttribute("apellidos", apellidos);
			model.addAttribute("email", email);
			model.addAttribute("repEmail", repEmail);
			model.addAttribute("fecha", fecha);
			model.addAttribute("hora", hora);
			model.addAttribute("numEntradasAdult", numEntradasAdult);
			model.addAttribute("numEntradasMen", numEntradasMen);

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
	    Integer totalButacas = (Integer) session.getAttribute("totalButacas");
	    model.addAttribute("totalButacas", totalButacas != null ? totalButacas : 0);

	    String butacasSeleccionadasStr = (String) session.getAttribute("butacasSeleccionadas");
	    int butacasSeleccionadasCount = butacasSeleccionadasStr != null ? butacasSeleccionadasStr.split(",").length : 0;
	    model.addAttribute("butacasSeleccionadasCount", butacasSeleccionadasCount);

	    return "Views/step3";
	}


	@PostMapping("/step4")
	public String step4(@RequestParam("FButacasSelected") String butacasSeleccionadas, Model model, HttpSession session) {
        Integer totalButacas = (Integer) session.getAttribute("totalButacas");
        
        String[] butacasArray = butacasSeleccionadas.split(",");
        int numButacasSeleccionadas = butacasArray.length;


        
        if (totalButacas != null && numButacasSeleccionadas != totalButacas) {
            model.addAttribute("errorButacas", "Debes seleccionar exactamente " + totalButacas + " butacas.");
            model.addAttribute("butacasSeleccionadas", butacasSeleccionadas);
            return "Views/step3"; 
        }
        session.setAttribute("butacasSeleccionadas", butacasSeleccionadas); 
        model.addAttribute("butacasSeleccionadas", butacasSeleccionadas); 

		return "Views/step4";
	}

	@GetMapping("/end")
	public String end() {
		return "Views/end";
	}
}
