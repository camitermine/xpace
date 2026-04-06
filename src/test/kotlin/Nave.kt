import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class NaveTest : DescribeSpec({

    fun crearSonda() = Sonda(
        "Voyager",
        1,
        LocalDate.of(2020, 1, 1),
        1000.0,
        300,
        5.0
    )

    fun crearTransbordador() = Transbordador(
        "Endeavour",
        2,
        LocalDate.of(2018, 1, 1),
        1000.0,
        400,
        5.0
    )

    fun crearCargueroReciente() = Carguero(
        "CargoX",
        3,
        LocalDate.of(2022, 1, 1),
        1000.0,
        400,
        5.0,
        10.0
    )

    fun crearCargueroAntiguo() = Carguero(
        "CargoOld",
        4,
        LocalDate.of(2000, 1, 1),
        1000.0,
        400,
        5.0,
        10.0
    )

    describe("Sonda") {

        it("tiene solo consumo base") {
            val sonda = crearSonda()
            sonda.consumoPorAnioLuz(0) shouldBe 1000.0
        }

        it("no permite transportar tripulantes") {
            val sonda = crearSonda()
            sonda.capacidadTripulantes shouldBe 0
            sonda.puedeTransportar(0) shouldBe true
            sonda.puedeTransportar(1) shouldBe false
        }
    }

    describe("Transbordador") {

        it("calcula consumo con tripulantes") {
            val nave = crearTransbordador()
            nave.consumoPorAnioLuz(4) shouldBe 1400.0
        }

        it("funciona en el límite de capacidad de tripulantes") {
            val nave = crearTransbordador()
            nave.consumoPorAnioLuz(20) shouldBe 3000.0
        }

        it("falla si supera capacidad de tripulantes") {
            val nave = crearTransbordador()
            val exception = shouldThrow<BusinessException> {
                nave.consumoPorAnioLuz(25)
            }
            exception.message shouldBe "Excede la capacidad de la nave"
        }
    }

    describe("Carguero") {

        it("calcula consumo por carga correctamente") {
            val nave = crearCargueroReciente()
            nave.consumoPorAnioLuz(1) shouldBe 1500.0
        }

        it("no aumenta consumo si no es antigua") {
            val nave = crearCargueroReciente()
            nave.consumoPorAnioLuz(1) shouldBe 1500.0
        }

        it("aumenta consumo si es antigua") {
            val nave = crearCargueroAntiguo()
            nave.consumoPorAnioLuz(1) shouldBe 1800.0
        }
    }

    describe("Alcance") {

        it("puede alcanzar destino si autonomía alcanza") {
            val nave = crearSonda()
            nave.puedeAlcanzar(2.0) shouldBe true
        }

        it("puede alcanzar justo en el límite") {
            val nave = Sonda("V", 1, LocalDate.now(), 10.0, 292, 5.0)
            nave.puedeAlcanzar(2.0) shouldBe true
        }

        it("no puede alcanzar destino si autonomía no alcanza") {
            val nave = Sonda("V", 1, LocalDate.now(), 10.0, 200, 3.0)
            nave.puedeAlcanzar(2.0) shouldBe false
        }

        it("no puede alcanzar una distancia muy grande") {
            val nave = crearSonda()
            nave.puedeAlcanzar(1000.0) shouldBe false
        }
    }

    describe("Modernidad") {
        it("una nave fabricada hace 2 años es moderna") {
            val nave = Sonda("V", 1, LocalDate.now().minusYears(2), 10.0, 100, 5.0)
            nave.esModerna() shouldBe true
        }

        it("una nave fabricada hace 6 años no es moderna") {
            val nave = Sonda("V", 1, LocalDate.now().minusYears(6), 10.0, 100, 5.0)
            nave.esModerna() shouldBe false
        }
    }

    describe("Validaciones de transporte en Nave") {

        it("no permite tripulación negativa") {
            val nave = crearTransbordador()
            shouldThrow<AttributeException> {
                nave.puedeTransportar(-1)
            }
        }
    }

    describe("Validaciones") {

        it("no permite nombre vacío") {
            shouldThrow<AttributeException> {
                Sonda("", 1, LocalDate.of(2020, 1, 1), 1000.0, 300, 5.0)
            }
        }

        it("mensaje correcto en nombre vacío") {
            val exception = shouldThrow<AttributeException> {
                Sonda("", 1, LocalDate.now(), 10.0, 100, 50.0)
            }
            exception.message shouldBe "El nombre no puede estar vacío"
        }

        it("no permite consumo negativo") {
            shouldThrow<AttributeException> {
                Sonda("Test", 1, LocalDate.now(), -100.0, 300, 5.0)
            }
        }

        it("no permite velocidad negativa") {
            shouldThrow<AttributeException> {
                Sonda("Test", 1, LocalDate.now(), 10.0, 100, -5.0)
            }
        }

        it("no permite código de identificación inválido") {
            shouldThrow<AttributeException> {
                Sonda("Test", 0, LocalDate.now(), 10.0, 100, 10.0)
            }
        }

        it("no permite autonomía negativa") {
            shouldThrow<AttributeException> {
                Sonda("Test", 1, LocalDate.now(), 10.0, -100, 10.0)
            }
        }

        it("no permite capacidad de carga negativa") {
            shouldThrow<AttributeException> {
                Carguero(
                    "Error",
                    7,
                    LocalDate.of(2022, 1, 1),
                    1000.0,
                    300,
                    5.0,
                    -10.0
                )
            }
        }
    }
})