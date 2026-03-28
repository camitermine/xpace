import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.LocalDate

class NaveSpec : DescribeSpec({

    describe("una Sonda") {

        it("tiene consumo igual al consumo base") {
            val sonda = Sonda(
                "Voyager",
                1,
                LocalDate.of(2020, 1, 1),
                100.0,
                500,
                50.0
            )

            sonda.consumoPorAnioLuz() shouldBe 100.0
        }

        it("calcula correctamente el consumo total") {
            val sonda = Sonda(
                "Voyager",
                1,
                LocalDate.of(2020, 1, 1),
                100.0,
                500,
                50.0
            )

            sonda.consumoTotal(2.0) shouldBe 200.0
        }
    }

    describe("un Transbordador") {

        it("incrementa el consumo según tripulantes") {
            val transbordador = Transbordador(
                "Apollo",
                2,
                LocalDate.of(2020, 1, 1),
                100.0,
                500,
                50.0,
                2
            )

            transbordador.consumoPorAnioLuz() shouldBe 120.0
        }

        it("no permite tripulantes negativos") {
            shouldThrow<IllegalArgumentException> {
                Transbordador(
                    "Apollo",
                    2,
                    LocalDate.of(2020, 1, 1),
                    100.0,
                    500,
                    50.0,
                    -1
                )
            }
        }
    }

    describe("un Carguero") {

        it("calcula consumo por carga correctamente") {
            val carguero = Carguero(
                "CargoX",
                3,
                LocalDate.of(2020, 1, 1),
                100.0,
                500,
                50.0,
                2.0
            )

            carguero.consumoPorCarga() shouldBe 110.0
        }

        it("aplica factor de antiguedad si es antiguo") {
            val carguero = Carguero(
                "CargoX",
                3,
                LocalDate.of(2010, 1, 1), // vieja
                100.0,
                500,
                50.0,
                2.0
            )

            carguero.consumoPorAnioLuz() shouldBe 132.0
        }

        it("no aplica factor si no es antiguo") {
            val carguero = Carguero(
                "CargoX",
                3,
                LocalDate.now(),
                100.0,
                500,
                50.0,
                2.0
            )

            carguero.consumoPorAnioLuz() shouldBe 110.0
        }

        it("detecta correctamente si es antigua") {
            val carguero = Carguero(
                "CargoX",
                3,
                LocalDate.of(2000, 1, 1),
                100.0,
                500,
                50.0,
                2.0
            )

            carguero.esAntigua() shouldBe true
        }
    }

    describe("validaciones de Nave") {

        it("no permite nombre vacío") {
            shouldThrow<IllegalArgumentException> {
                Sonda(
                    "",
                    1,
                    LocalDate.now(),
                    100.0,
                    500,
                    50.0
                )
            }
        }

        it("no permite código inválido") {
            shouldThrow<IllegalArgumentException> {
                Sonda(
                    "Test",
                    0,
                    LocalDate.now(),
                    100.0,
                    500,
                    50.0
                )
            }
        }

        it("no permite consumo base negativo") {
            shouldThrow<IllegalArgumentException> {
                Sonda(
                    "Test",
                    1,
                    LocalDate.now(),
                    -10.0,
                    500,
                    50.0
                )
            }
        }
    }

    describe("puedeAlcanzar") {

        it("retorna true si la nave puede hacer el viaje") {
            val sonda = Sonda(
                "Voyager",
                1,
                LocalDate.now(),
                100.0,
                10000,
                100.0
            )

            sonda.puedeAlcanzar(1.0) shouldBe true
        }

        it("retorna false si no tiene autonomía suficiente") {
            val sonda = Sonda(
                "Voyager",
                1,
                LocalDate.now(),
                100.0,
                10,
                10.0
            )

            sonda.puedeAlcanzar(5.0) shouldBe false
        }
    }
})