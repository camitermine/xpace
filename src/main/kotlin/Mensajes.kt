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
    private var leido: Boolean = false
) {
    fun estaLeido(): Boolean {
        return leido
    }
    fun fechaEmision(): LocalDate {
        return fechaEmision
    }

    fun esObsoleto(fechaLimite: LocalDate): Boolean {
        return estaLeido() && fechaEmision().isBefore(fechaLimite)
    }

    fun marcarComoLeido() {
        this.leido = true
    }

    fun marcarComoNoLeido() {
        this.leido = false
    }
}

