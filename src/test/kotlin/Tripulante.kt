import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class TripulanteTest : FunSpec({
    test("contar correctamente las misiones exitosas, parciales y fracasadas") {
        val tripulante = Tripulante(
            "Juan",
            "Perez",
            LocalDate.of(1990, 5, 10),
            LocalDate.of(2015, 3, 1)
        )
        tripulante.agregarMision(EstadoMision.COMPLETADA)
        tripulante.agregarMision(EstadoMision.COMPLETADA)
        tripulante.agregarMision(EstadoMision.FALLIDA)
        tripulante.agregarMision(EstadoMision.PARCIALMENTE_EXITOSA)

        tripulante.cantidadMisiones(EstadoMision.COMPLETADA) shouldBe 2
        tripulante.cantidadMisiones(EstadoMision.FALLIDA) shouldBe 1
        tripulante.cantidadMisiones(EstadoMision.PARCIALMENTE_EXITOSA) shouldBe 1
    }
    test("calcular correctamente la experiencia del tripulante") {
        val tripulante = Tripulante(
            "Ana",
            "Gomez",
            LocalDate.of(1992, 4, 15),
            LocalDate.of(2020, 1, 1)
        )
        tripulante.agregarMision(EstadoMision.COMPLETADA)
        tripulante.agregarMision(EstadoMision.FALLIDA)
        tripulante.agregarMision(EstadoMision.COMPLETADA)
        tripulante.agregarMision(EstadoMision.PARCIALMENTE_EXITOSA)

        val experiencia = tripulante.calcularExperiencia()
        experiencia shouldBe 7.75
    }

    test("verificar que la mision fue agregada correctamente") {
        val tripulante = Tripulante("Nicolas", "Diez", LocalDate.of(2002, 8, 11), LocalDate.of(2026, 3, 16));
        val misionesActuales = tripulante.misiones.size
        tripulante.agregarMision(EstadoMision.COMPLETADA)

        tripulante.misiones.last() shouldBe EstadoMision.COMPLETADA

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