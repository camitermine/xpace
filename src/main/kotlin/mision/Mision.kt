package mision

import Planeta
import Tripulante
import java.util.Optional

const val IDA_Y_VUELTA = 2
const val DURACION_DE_ALTO_RIESGO = 500


class Mision {
    private val nombre: String
    private val descripción: String
    private val fecha_lanzamiento: String
    private val nave: String
    private val destino: Planeta
    private var estado: EstadoMision = EstadoMision.BORRADOR
    private var tripulantes: List<Tripulante>

    constructor(
        nombre: String,
        descripción: String,
        fecha_lanzamiento: String,
        nave: String,
        destino: Planeta,
        tripulantes: List<Tripulante>?
    ) {
        this.nombre = nombre
        this.descripción = descripción
        this.fecha_lanzamiento = fecha_lanzamiento
        this.nave = nave
        this.destino = destino
        this.tripulantes = tripulantes ?: emptyList()
    }

    fun duracion(): Double = destino.radioKm * 365 /*/ this.nave.velocidadPromedio */ * IDA_Y_VUELTA
    fun altoRiesgo(): Boolean =/*!destino.habitable() && */ duracion() > DURACION_DE_ALTO_RIESGO

    fun cancelar() {
        if (altoRiesgo()) {
            tripulantes.forEach { tripulante ->
                tripulante.agregarMision(EstadoMision.PARCIALMENTE_EXITOSA)
            }
        }
        misionFinalizada(EstadoMision.CANCELADA)
    }

    fun completar() {
        destino.fueAterrizado = true
        misionFinalizada(EstadoMision.COMPLETADA)
    }

    fun fallo() {
        misionFinalizada(EstadoMision.FALLIDA)
    }

    fun lanzar() {
        try {

            if (this.estado !== EstadoMision.BORRADOR) {
                throw Exception("La mision ya fue lanzada")
            }

            prepararNave()
            prepararTripulantes()

            this.estado = EstadoMision.EN_CURSO

        } catch (e: Exception) {

        }
    }

    private fun misionFinalizada(estado: EstadoMision) {
        this.estado = estado
        //nave.liberar()
    }

    private fun prepararNave() {
    }

    private fun prepararTripulantes() {
    }


}