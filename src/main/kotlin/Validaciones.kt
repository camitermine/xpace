inline fun requireAttribute(condicion: Boolean, mensaje: String) {
    try {
        require(condicion)
    } catch (e: IllegalArgumentException) {
        throw AttributeException(mensaje)
    }
}

inline fun requireBusiness(condicion: Boolean, mensaje: String) {
    try {
        require(condicion)
    } catch (e: IllegalArgumentException) {
        throw BusinessException(mensaje)
    }
}