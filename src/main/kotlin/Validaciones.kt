import java.time.LocalDate

object Validaciones {

    private fun validar(mensaje: String, condicion: Boolean) {
        if (!condicion) throw AttributeException(mensaje)
    }

    fun <T> validarNoNull(valor: T?, mensaje: String) = validar(mensaje, valor != null)
    fun validarFechaPosteriorAHoy(fechaA: LocalDate, mensaje: String) = validar(mensaje, fechaA.isAfter(LocalDate.now()))
    fun validarNoVacio(str: String, mensaje: String) = validar(mensaje, str.isNotBlank())
    fun validarMayorACero(valor: Number, mensaje: String) = validar(mensaje, valor.toDouble() > 0)
    fun validarMayorOIgualACero(valor: Number, mensaje: String) = validar(mensaje, valor.toDouble() >= 0)
    fun validarFechaAnteriorAHoy(fechaA: LocalDate, mensaje: String) = validar(mensaje, fechaA.isBefore(LocalDate.now()))
    fun validarEntre0y100(valor: Number, mensaje: String) = validar(mensaje, valor.toDouble() in 0.0..100.0)
    fun validarReglaNegocio(condicion: Boolean, mensaje: String) {
        if (!condicion) throw BusinessException(mensaje)
    }
}
