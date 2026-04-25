interface Entidad {
    var id: Int?// NUEVO: Obligamos a toda entidad a saber cómo buscarse a sí misma
    fun coincideCon(criterio: String): Boolean
}

