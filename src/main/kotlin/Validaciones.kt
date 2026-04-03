import java.time.LocalDate

inline fun validarAtributo(condicion: Boolean, mensaje: String) {
    try {
        require(condicion)
    } catch (e: IllegalArgumentException) {
        throw AttributeException(mensaje)
    }
}

inline fun validarReglaDeNegocio(condicion: Boolean, mensaje: String) {
    try {
        require(condicion)
    } catch (e: IllegalArgumentException) {
        throw BusinessException(mensaje)
    }
}


object Validaciones {

    private fun validar(mensaje: String, condicion: Boolean) {
        if (!condicion) throw AttributeException(mensaje)
    }

    fun <T> validarNoNull(valor: T?, mensaje: String) = validar(mensaje, valor != null)
    fun validarFechaPosteriorAHoy(fechaA: LocalDate, mensaje: String) = validar(mensaje, fechaA.isAfter(LocalDate.now()))
    fun validarNoVacio(str: String, mensaje: String) = validar(mensaje, str.isNotBlank())
}