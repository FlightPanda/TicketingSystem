package es.dsw.controllers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.dsw.models.Sala;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class MainController {

	@GetMapping(value = { "/", "/index" })
	public String index(Model model) {
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
		LocalDateTime now = LocalDateTime.now();
		DayOfWeek dayOfWeek = now.getDayOfWeek();

		int numeroSalas = (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.WEDNESDAY
				|| dayOfWeek == DayOfWeek.SUNDAY) ? 4 : 7;

		double precioEntrada = (dayOfWeek == DayOfWeek.WEDNESDAY) ? 3.5 : 6.0;

		List<String> peliculasDisponibles = new ArrayList<>();
		for (int i = 1; i <= 14; i++) {
			peliculasDisponibles.add("film" + i); // Nombre de las películas
		}

		Collections.shuffle(peliculasDisponibles);
		List<String> peliculasMostradas = peliculasDisponibles.subList(0, numeroSalas);

		List<Sala> salas = new ArrayList<>();
		for (int i = 1; i <= numeroSalas; i++) {
			Sala sala = new Sala(i, peliculasMostradas.get(i - 1), precioEntrada);
			salas.add(sala);
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
		if (apellidos == null || apellidos.isEmpty()) {
			model.addAttribute("errorApellidos", true);
			hasError = true;
		}
		if (email == null || email.isEmpty()) {
			model.addAttribute("errorEmail", true);
			hasError = true;
		}
		if (!email.equals(repEmail)) {
			model.addAttribute("errorRepEmail", true);
			hasError = true;
		}
		if (fecha == null || fecha.isEmpty()) {
			model.addAttribute("errorFecha", true);
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
			model.addAttribute("errorCampos", true);

			return "Views/step2";
		}

		session.setAttribute("nombre", nombre);
		session.setAttribute("apellidos", apellidos);
		session.setAttribute("email", email);
		session.setAttribute("fecha", fecha);
		session.setAttribute("hora", hora);
		session.setAttribute("numEntradasAdult", numEntradasAdult);
		session.setAttribute("numEntradasMen", numEntradasMen);

		model.addAttribute("filmSeleccionado", session.getAttribute("filmSeleccionado"));
		model.addAttribute("nombre", nombre);
		model.addAttribute("apellidos", apellidos);
		model.addAttribute("email", email);
		model.addAttribute("fecha", fecha);
		model.addAttribute("hora", hora);
		model.addAttribute("numEntradasAdult", numEntradasAdult);
		model.addAttribute("numEntradasMen", numEntradasMen);

		return "Views/step3";
	}

	@GetMapping("/step4")
	public String step4() {
		return "Views/step4";
	}

	@GetMapping("/end")
	public String end() {
		return "Views/end";
	}
}
