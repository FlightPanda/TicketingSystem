$(function () {
    $(".butaca").click(function (e) {
        UpdateButaca(e.target.id);
    });

    $("#SubmitButacas").click(function (e) {
        // Se obtiene la cadena almacenada en el input oculto.
        let txtButacas = $("#txtButacas").val().trim().toUpperCase();

        // Si el número de butacas seleccionado aún no es el que previamente indicó en la reserva,
        // se informa de ello al usuario y se bloquea el envío de datos al servidor.
        if (NumButacasSelected != MaxNumButacasToSelect) {
            $("div.Pantalla > p").html("Aún te faltan por seleccionar <span>" + (MaxNumButacasToSelect - NumButacasSelected) + " butacas</span>");
            e.preventDefault();
        }
    });
});

function UpdateButaca(IdButaca) {
    // Se obtiene la cadena almacenada en el input oculto.
    let txtButacas = $("#txtButacas").val().trim().toUpperCase();
    // Se formatea el IdButaca seleccionado para añadirlo o quitarlo de la selección.
    let IdButacaSelected = IdButaca.trim().toUpperCase() + ";";
    // Se localiza la posición de la butaca elegida en la cadena de butacas seleccionadas.
    let posIdButaca = txtButacas.indexOf(IdButacaSelected);
    
    // Resetea mensaje de error
    $("div.Pantalla > p").html("");

    // Solo si la butaca está libre (verde) o ya seleccionada (amarillo) se procede a evaluar.
    if (($("#" + IdButaca).css("background-color").trim() == "rgb(112, 175, 87)") ||
        ($("#" + IdButaca).css("background-color").trim() == "rgb(194, 194, 79)")) {
        
        if ($("#" + IdButaca).css("background-color").trim() == "rgb(112, 175, 87)") {
            // Si la butaca está en verde (libre)
            if (NumButacasSelected < MaxNumButacasToSelect) {
                // Añadir butaca si no está ya seleccionada.
                if (posIdButaca < 0) {
                    txtButacas += IdButacaSelected;
                    NumButacasSelected++;
                }
                $("#" + IdButaca).css("background-color", "rgb(194, 194, 79)"); // Marcar como seleccionada (amarillo)
            } else {
                // Mostrar mensaje de error si se excede el número permitido de butacas.
                $("div.Pantalla > p").html("No puedes seleccionar más de " + MaxNumButacasToSelect + " butacas.");
            }
        } else {
            // Si la butaca ya está seleccionada (amarillo) y se quiere deseleccionar.
            if (posIdButaca >= 0) {
                txtButacas = txtButacas.replace(IdButacaSelected, "");
                NumButacasSelected--;
            }
            $("#" + IdButaca).css("background-color", "rgb(112, 175, 87)"); // Marcar como NO seleccionada (verde)
        }

        $("#txtButacas").val(txtButacas);
    }
}
