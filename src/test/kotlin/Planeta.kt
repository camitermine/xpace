import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class PlanetaTest : FunSpec({

    test("verificar temperatura ideal") {
        val planeta = Planeta(
            "Kepler",
            25.0,
            9.8,
            40,
            true,
            20,
            10,
            6000.0,
            LocalDate.of(2015,7,23),
            1400.0,
            false
        )
        planeta.tieneTemperaturaIdeal() shouldBe true
    }
    test("verificar gravedad soportable") {
        val planeta = Planeta(
            "Kepler",
            25.0,
            9.8,
            40,
            true,
            20,
            10,
            6000.0,
            LocalDate.of(2015,7,23),
            1400.0,
            false
        )
        planeta.gravedadSoportable() shouldBe true
    }
    test("verificar temperatura NO ideal (demasiado calor)") {
        val planeta = Planeta(
            "Kepler",
            45.0, // Temperatura superior a 40 grados
            9.8,
            40,
            true,
            20,
            10,
            6000.0,
            LocalDate.of(2015, 7, 23),
            1400.0,
            false
        )
        planeta.tieneTemperaturaIdeal() shouldBe false
    }
})