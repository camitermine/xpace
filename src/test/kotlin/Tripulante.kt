import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import mision.EstadoMisionTripulante
import java.time.LocalDate

class TripulanteTest : FunSpec({
    test("contar correctamente las misiones exitosas, parciales y fracasadas") {
        val tripulante = Tripulante(
            "Juan",
            "Perez",
            LocalDate.of(1990, 5, 10),
            LocalDate.of(2015, 3, 1)
        )
        tripulante.agregarMision(EstadoMisionTripulante.EXITOSA)
        tripulante.agregarMision(EstadoMisionTripulante.EXITOSA)
        tripulante.agregarMision(EstadoMisionTripulante.FRACASADA)
        tripulante.agregarMision(EstadoMisionTripulante.PARCIALMENTE_EXITOSA)

        tripulante.cantidadMisiones(EstadoMisionTripulante.EXITOSA) shouldBe 2
        tripulante.cantidadMisiones(EstadoMisionTripulante.FRACASADA) shouldBe 1
        tripulante.cantidadMisiones(EstadoMisionTripulante.PARCIALMENTE_EXITOSA) shouldBe 1
    }
    test("calcular correctamente la experiencia del tripulante") {
        val tripulante = Tripulante(
            "Ana",
            "Gomez",
            LocalDate.of(1992, 4, 15),
            LocalDate.of(2020, 1, 1)
        )
        tripulante.agregarMision(EstadoMisionTripulante.EXITOSA)
        tripulante.agregarMision(EstadoMisionTripulante.FRACASADA)
        tripulante.agregarMision(EstadoMisionTripulante.EXITOSA)
        tripulante.agregarMision(EstadoMisionTripulante.PARCIALMENTE_EXITOSA)

        val experiencia = tripulante.calcularExperiencia()
        experiencia shouldBe 7.75
    }

    test("verificar que la mision fue agregada correctamente") {
        val tripulante = Tripulante("Nicolas", "Diez", LocalDate.of(2002, 8, 11), LocalDate.of(2026, 3, 16));
        val misionesActuales = tripulante.misiones.size
        tripulante.agregarMision(EstadoMisionTripulante.EXITOSA)

        tripulante.misiones.last() shouldBe EstadoMisionTripulante.EXITOSA

    }

    test("verificar que fecha de nacimiento sea correcta") {
        shouldThrow<IllegalArgumentException> {
            Tripulante(
                "Nicolas", "Diez",
                LocalDate.now().plusDays(1),
                LocalDate.of(2026, 3, 16)
            );
        //cuando intento crear al tripulante le asigno una fecha de nac posterior al dia de la fecha
        }
    }


})