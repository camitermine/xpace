import java.time.LocalDate

enum class Prioridad {
    ALTA,
    MEDIA,
    BAJA
}

class Mensaje(
    val fechaEmision: LocalDate,
    val asunto: String,
    val cuerpo: String,
    val prioridad: Prioridad,
    var leido: Boolean = false
) {

    fun marcarComoLeido() {
        this.leido = true
    }

    fun marcarComoNoLeido() {
        this.leido = false
    }
}