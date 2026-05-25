import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.uqbar.geodds.Point

class SubvencionTest :
        DescribeSpec({
            fun crearPlanetaHabitable() =
                    Planeta(
                            nombre = "Kepler",
                            temperaturaMedia = 25.0,
                            nivelRadiacion = 39,
                            hayAguaLiquida = true,
                            toxicidadAtmosferica = 20,
                            actividadTectonica = 10,
                            radioKm = 6000.0,
                            distanciaATierra = 1400.0,
                            fueAterrizado = false
                    )

            fun crearPlanetaNoHabitable() =
                    Planeta(
                            nombre = "Marte",
                            temperaturaMedia = 25.0,
                            nivelRadiacion = 40,
                            hayAguaLiquida = false,
                            toxicidadAtmosferica = 20,
                            actividadTectonica = 10,
                            radioKm = 6000.0,
                            distanciaATierra = 1400.0,
                            fueAterrizado = false
                    )

            fun crearPilotoVeterano() =
                    Tripulante(
                                    nombre = "John",
                                    apellido = "Cena",
                                    fechaNacimiento = LocalDate.now().minusYears(40),
                                    fechaInicioActividad = LocalDate.now().minusYears(15),
                                    baseLanzamiento =
                                            BaseLanzamiento(
                                                    "Base",
                                                    Direccion(
                                                            "Argentina",
                                                            "BA",
                                                            "Falsa",
                                                            "123",
                                                            Point(0.0, 0.0)
                                                    ),
                                                    100
                                            ),
                                    direccion =
                                            Direccion(
                                                    "Argentina",
                                                    "BA",
                                                    "Falsa",
                                                    "123",
                                                    Point(0.0, 0.0)
                                            )
                            )
                            .apply {
                                salarioBase = 1000.0
                                (1..10).forEach { _ ->
                                    val base = this.baseLanzamiento
                                    val trans =
                                            Transbordador(
                                                    "T",
                                                    1,
                                                    LocalDate.now(),
                                                    100.0,
                                                    10000000,
                                                    100000.0
                                            )
                                    base.estacionar(trans)
                                    val m2 =
                                            Mision(
                                                    "M",
                                                    "D",
                                                    LocalDate.now().plusDays(1),
                                                    trans,
                                                    crearPlanetaHabitable()
                                            )
                                    m2.sumarTripulacion(listOf(this))
                                    m2.lanzar()
                                    m2.completar()
                                }
                            }

            fun crearSonda() = Sonda("Sonda T", 1, LocalDate.now(), 100.0, 10000, 10.0)

            describe("Subvencion Gubernamental") {
                it("condicion particular: planeta no debe haber sido aterrizado") {
                    val subvencion = SubvencionGubernamental("Gub", 100000.0, LocalDate.now(), 30)
                    val planeta = crearPlanetaHabitable()
                    val planetaAterrizado = crearPlanetaHabitable().apply { fueAterrizado = true }

                    val mision = Mision("M", "", LocalDate.now().plusDays(1), crearSonda(), planeta)
                    val misionA =
                            Mision(
                                    "M2",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    crearSonda(),
                                    planetaAterrizado
                            )

                    subvencion.puedeAplicarseA(mision) shouldBe true
                    subvencion.puedeAplicarseA(misionA) shouldBe false
                }

                it("monto adicional: 30% para habitable, 15% para no habitable") {
                    val subvencion = SubvencionGubernamental("Gub", 100000.0, LocalDate.now(), 30)
                    val misionHabitable =
                            Mision(
                                    "M",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    crearSonda(),
                                    crearPlanetaHabitable()
                            )
                    val misionNoHabitable =
                            Mision(
                                    "M2",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    crearSonda(),
                                    crearPlanetaNoHabitable()
                            )

                    subvencion.calcularMontoTotal(misionHabitable) shouldBe (100000.0 + 30000.0)
                    subvencion.calcularMontoTotal(misionNoHabitable) shouldBe (100000.0 + 15000.0)
                }
            }

            describe("Subvencion Privada") {
                it("condicion particular: exp promedio mayor a 5") {
                    val subvencion = SubvencionPrivada("Priv", 80000.0, LocalDate.now(), 30)
                    val mision =
                            Mision(
                                    "M",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    Transbordador("T", 5, LocalDate.now(), 100.0, 1000, 10.0),
                                    crearPlanetaHabitable()
                            )
                    mision.sumarTripulacion(listOf(crearPilotoVeterano()))

                    subvencion.puedeAplicarseA(mision) shouldBe true
                }

                it("monto adicional: 10000 por veterano > 10 años, tope 50000") {
                    val subvencion = SubvencionPrivada("Priv", 80000.0, LocalDate.now(), 30)
                    val mision =
                            Mision(
                                    "M",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    Transbordador("T", 10, LocalDate.now(), 100.0, 1000, 10.0),
                                    crearPlanetaHabitable()
                            )
                    val veteranos = (1..6).map { crearPilotoVeterano() }
                    mision.sumarTripulacion(veteranos)

                    subvencion.calcularMontoTotal(mision) shouldBe (80000.0 + 50000.0)
                }

                it("condicion particular: falla si exp promedio es menor o igual a 5") {
                    val subvencion = SubvencionPrivada("Priv", 1.0, LocalDate.now(), 30)
                    val novatoBase = BaseLanzamiento(
                            "Base",
                            Direccion("Argentina", "BA", "Falsa", "123", Point(0.0, 0.0)),
                            10
                    )
                    val novato = Tripulante(
                            nombre = "Novato",
                            apellido = "Test",
                            fechaNacimiento = LocalDate.now().minusYears(25),
                            fechaInicioActividad = LocalDate.now().minusYears(1),
                            baseLanzamiento = novatoBase,
                            direccion = Direccion("Argentina", "BA", "Falsa", "123", Point(0.0, 0.0))
                    )
                    val mision = Mision(
                            "M", "", LocalDate.now().plusDays(1),
                            Transbordador("T", 7, LocalDate.now(), 100.0, 1000, 10.0),
                            crearPlanetaHabitable()
                    )
                    mision.sumarTripulacion(listOf(novato))

                    subvencion.puedeAplicarseA(mision) shouldBe false
                }
            }

            describe("Subvencion Emergencia") {
                it("monto adicional es 50% del presupuesto fijo") {
                    val subvencion = SubvencionEmergencia("Emerg", 100.0, LocalDate.now(), 30)
                    val mision =
                            Mision(
                                    "M",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    crearSonda(),
                                    crearPlanetaHabitable()
                            )
                    val pFijo = mision.calcularPresupuestoFijo()

                    subvencion.calcularMontoTotal(mision) shouldBe
                            (100.0 + (pFijo * 0.50)).plusOrMinus(0.01)
                }
            }

            describe("Mision: Presupuesto Total") {
                it("si no hay subvencion, presupuesto es igual al presupuesto fijo") {
                    val mision =
                            Mision(
                                    "M",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    crearSonda(),
                                    crearPlanetaHabitable()
                            )
                    mision.presupuesto.calcularPresupuesto() shouldBe mision.calcularPresupuestoFijo()
                }

                it("si hay subvencion aplicada, la resta del presupuesto fijo") {
                    val mision =
                            Mision(
                                    "M",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    crearSonda(),
                                    crearPlanetaHabitable()
                            )
                    val subvencion = SubvencionEmergencia("Emerg", 10.0, LocalDate.now(), 30)

                    subvencion.aplicar(mision)

                    val pFijo = mision.calcularPresupuestoFijo()
                    val montoSub = subvencion.calcularMontoTotal(mision)
                    mision.presupuesto.calcularPresupuesto() shouldBe (pFijo - montoSub)
                }

                it("falla al aplicar subvencion invalida o vencida") {
                    val mision =
                            Mision(
                                    "M",
                                    "",
                                    LocalDate.now().plusDays(1),
                                    crearSonda(),
                                    crearPlanetaHabitable()
                            )
                    val subvencionVencida =
                            SubvencionEmergencia("Emerg", 10.0, LocalDate.now().minusDays(50), 30)

                    shouldThrow<Exception> { subvencionVencida.aplicar(mision) }
                }

                it("no se puede aplicar una subvencion ya aplicada") {
                    val subvencion = SubvencionEmergencia("Emerg", 10.0, LocalDate.now(), 30)
                    val mision = Mision("M", "", LocalDate.now().plusDays(1), crearSonda(), crearPlanetaHabitable())
                    subvencion.aplicar(mision)

                    shouldThrow<Exception> { subvencion.aplicar(mision) }
                }

                it("presupuesto fijo aplica ambos recargos cuando planeta no habitable y nave no moderna") {
                    val naveAntigua = Sonda("Vieja", 2, LocalDate.now().minusYears(6), 100.0, 10000000, 10.0)
                    val planeta = crearPlanetaNoHabitable()
                    val mision = Mision("M", "", LocalDate.now().plusDays(1), naveAntigua, planeta)

                    val costoCombustible = naveAntigua.consumoDeCombustible(planeta.distanciaATierra, 0)
                    val esperado = costoCombustible * 1.20 * 1.10

                    mision.calcularPresupuestoFijo() shouldBe esperado.plusOrMinus(0.01)
                }
            }
        })
