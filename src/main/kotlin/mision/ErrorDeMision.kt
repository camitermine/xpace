package mision

class ErrorDeMision(mensaje: String) :
    Exception("ERROR EN LA GESTION DE LA MISION: ${mensaje.uppercase()}")