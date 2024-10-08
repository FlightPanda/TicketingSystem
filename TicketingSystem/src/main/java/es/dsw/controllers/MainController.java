package es.dsw.controllers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import es.dsw.models.Sala;

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
	public String step1(Model model) {
		
		LocalDateTime now = LocalDateTime.now();
	    DayOfWeek dayOfWeek = now.getDayOfWeek();


	    int numeroSalas = (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.SUNDAY) ? 4 : 7;

	    double precioEntrada = (dayOfWeek == DayOfWeek.WEDNESDAY) ? 3.5 : 6.0;

	    List<String> peliculasDisponibles = new ArrayList<>();
	    for (int i = 1; i <= 14; i++) {
	        peliculasDisponibles.add("film" + i);  
	    }

	    Collections.shuffle(peliculasDisponibles);
	    List<String> peliculasMostradas = peliculasDisponibles.subList(0, numeroSalas);

	    List<Sala> salas = new ArrayList<>();
	    for (int i = 1; i <= numeroSalas; i++) {
	        Sala sala = new Sala(i, peliculasMostradas.get(i - 1), precioEntrada);  
	        salas.add(sala);
	    }

	    model.addAttribute("salas", salas);

		return "Views/step1";
	}

	@GetMapping("/step2")
	public String step2() {

		return "Views/step2";
	}

	@GetMapping("/step3")
	public String step3() {

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
