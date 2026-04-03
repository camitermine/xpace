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