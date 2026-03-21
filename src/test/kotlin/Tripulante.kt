import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import mision.EstadoMision
import java.time.LocalDate

class TripulanteTest : FunSpec({
    test("contar correctamente las misiones exitosas, parciales y fracasadas") {
        val tripulante = Tripulante(
            "Juan",
            "Perez",
            LocalDate.of(1990, 5, 10),
            LocalDate.of(2015, 3, 1)
        )
        tripulante.agregarMision(EstadoMision.EXITOSA)
        tripulante.agregarMision(EstadoMision.EXITOSA)
        tripulante.agregarMision(EstadoMision.FRACASADA)
        tripulante.agregarMision(EstadoMision.PARCIALMENTE_EXITOSA)

        tripulante.cantidadMisiones(EstadoMision.EXITOSA) shouldBe 2
        tripulante.cantidadMisiones(EstadoMision.FRACASADA) shouldBe 1
        tripulante.cantidadMisiones(EstadoMision.PARCIALMENTE_EXITOSA) shouldBe 1
    }
    test("calcular correctamente la experiencia del tripulante") {
        val tripulante = Tripulante(
            "Ana",
            "Gomez",
            LocalDate.of(1992, 4, 15),
            LocalDate.of(2020, 1, 1)
        )
        tripulante.agregarMision(EstadoMision.EXITOSA)
        tripulante.agregarMision(EstadoMision.FRACASADA)
        tripulante.agregarMision(EstadoMision.EXITOSA)
        tripulante.agregarMision(EstadoMision.PARCIALMENTE_EXITOSA)

        val experiencia = tripulante.calcularExperiencia()
        experiencia shouldBe 7.75
    }

    test("Agregar mision") {
        val tripulante = Tripulante("Nicolas", "Diez", LocalDate.of(2002, 8, 11), LocalDate.of(2026, 3, 16));
        val misionesActuales = tripulante.misiones.size
        tripulante.agregarMision(EstadoMision.EXITOSA)

        misionesActuales shouldBeLessThan tripulante.misiones.size
        tripulante.misiones.last() shouldBe EstadoMision.EXITOSA

    }
})