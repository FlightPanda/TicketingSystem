
$(function () {
    $(".butaca").click(function (e) {
        UpdateButaca(e.target.id);
    });

    $("#SubmitButacas").click(function (e) {
        //Se obtiene la cadena almacenada en el input oculto.
        let txtButacas = $("#txtButacas").val().trim().toUpperCase();



        //Si el número de butacas seleccionado aún no es el que previamente indico en la reserva,
        //se informa de ello al usuario y se bloquea el envío de datos al servidor.

        if (NumButacasSelected != MaxNumButacasToSelect) {
                $("div.Pantalla > p").html("Aún te faltan por seleccionar <span>" + (MaxNumButacasToSelect - NumButacasSelected) + " butacas</span>");
                e.preventDefault();
        }
    });
});

function UpdateButaca (IdButaca) {


    //Se obtiene la cadena almacenada en el input oculto.
    let txtButacas = $("#txtButacas").val().trim().toUpperCase();
    //Se formatea el IdButaca seleccionado para añadirlo o quitarlo de la selección.
    let IdButacaSelected = IdButaca.trim().toUpperCase()+";";
    //Se localiza la posicion de la butaca elegida en la cadena de butacas seleccionadas.
    let posIdButaca = txtButacas.indexOf(IdButacaSelected);
    
    //Se resetea mensaje de error
    $("div.Pantalla > p").html("");

    //Solo si la butaca está verde (libre) o en amarillo (ya seleccionada) se procede 
    //a evaluar la selección.Si tiene otro color no se hace nada. Ej: Rojo (Ocupada/Reservada por otro usuario)
    if (($("#"+IdButaca).css("background-color").trim() == "rgb(112, 175, 87)") ||
        ($("#"+IdButaca).css("background-color").trim() == "rgb(194, 194, 79)")
       ) {
            //Si la butaca está en verde (libre)
            if ($("#"+IdButaca).css("background-color").trim() == "rgb(112, 175, 87)") {
                //Si aún no ha seleccionado el máximo de butacas permitido
                if (NumButacasSelected < MaxNumButacasToSelect) {
                        //Si no está en la cadena de butacas seleccionadas. (comprobación redundante por seguridad)
                        if (posIdButaca < 0) {
                            txtButacas = txtButacas + IdButacaSelected;
                            //Se incrementa el contador de butacas seleccionadas
                            NumButacasSelected++;
                        }
                        //Se marca la butaca como seleccionada (amarillo)
                        $("#"+IdButaca).css("background-color","rgb(194, 194, 79)");
                }


            } else {
                    //Si no está en la cadena de butacas seleccionadas. (comprobación redundante por seguridad)
                    if (posIdButaca >= 0) {
                       txtButacas = txtButacas.replace(IdButacaSelected,"");
                       //Se decrementa el contador de butacas seleccionadas
                       NumButacasSelected--;
                    }
                    //Se marca la butaca como NO seleccionada (verde)
                    $("#"+IdButaca).css("background-color","rgb(112, 175, 87)");
            }

            $("#txtButacas").val(txtButacas);
            
         }

}