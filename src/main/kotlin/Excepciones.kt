class ErrorDeMision(mensaje: String) :
    Exception("ERROR EN LA GESTION DE LA MISION: ${mensaje.uppercase()}")

class AttributeException(mensaje: String) : RuntimeException(mensaje)

class BusinessException(mensaje: String) : RuntimeException(mensaje)
