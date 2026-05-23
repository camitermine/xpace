import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDate
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class Entrega3Test : DescribeSpec({

    // Mocks compartidos para los servicios externos
    val mailSenderMock = mockk<MailSender>(relaxed = true)
    val sistemaContableMock = mockk<SistemaContable>(relaxed = true)
    val servicePlanetaMock = mockk<ServicePlaneta>()

    beforeEach {
        clearAllMocks() // Limpiamos los contadores antes de cada test
    }

    describe("Acciones Dinámicas de Lanzamiento (Observer)") {

        it("NotificarTripulantesPorMail envía un correo a cada tripulante") {
            val tripulante1 = mockk<Tripulante> { every { nombre } returns "Juan" }
            val tripulante2 = mockk<Tripulante> { every { nombre } returns "Ana" }

            val planetaMock = mockk<Planeta> { every { nombre } returns "Marte" }
            val misionMock = mockk<Mision>(relaxed = true) {
                every { nombre } returns "Mision Alpha"
                every { destino } returns planetaMock
                every { duracion() } returns 120.0
                every { tripulantes } returns listOf(tripulante1, tripulante2)
            }

            val accion = NotificarTripulantesPorMail(mailSenderMock)
            accion.ejecutar(misionMock)

            verify(exactly = 1) {
                mailSenderMock.enviarMail("lanzamientos@xpace.com", "Misión Mision Alpha lanzada", match { it.contains("Juan") })
                mailSenderMock.enviarMail("lanzamientos@xpace.com", "Misión Mision Alpha lanzada", match { it.contains("Ana") })
            }
        }

        it("NotificarMisionPionera envía mensaje ALTA prioridad al inbox si el planeta es nuevo y habitable") {
            val tripulanteReal = mockk<Tripulante>(relaxed = true)
            val planetaPionero = mockk<Planeta> {
                every { nombre } returns "Kepler-186f"
                every { esHabitable() } returns true
                every { fueAterrizado } returns false
            }
            val misionMock = mockk<Mision> {
                every { nombre } returns "Pionero I"
                every { destino } returns planetaPionero
                every { tripulantes } returns listOf(tripulanteReal)
            }

            val accion = NotificarMisionPionera()
            accion.ejecutar(misionMock)

            // Verificamos que se llamó a recibirMensaje con un mensaje de prioridad ALTA
            verify(exactly = 1) {
                tripulanteReal.recibirMensaje(match { it.prioridad == Prioridad.ALTA && it.asunto.contains("Pionera") })
            }
        }

        it("EvaluarMantenimientoNave marca la nave para reparar si no es moderna y la misión desgasta") {
            val naveViejaMock = mockk<Nave>(relaxed = true) {
                every { esModerna() } returns false
            }
            val misionDesgastante = mockk<Mision> {
                every { nave } returns naveViejaMock
                every { esDesgastante() } returns true
            }

            val accion = EvaluarMantenimientoNave()
            accion.ejecutar(misionDesgastante)

            verify(exactly = 1) { naveViejaMock.solicitarMantenimiento() }
        }
    }

    describe("Procesos Administrativos (Command + Template Method)") {

        it("BorrarMensajesAntiguos elimina del inbox solo los leídos de más de 30 días") {
            val tripulanteMock = mockk<Tripulante>(relaxed = true)

            // Usamos tu clase Mensaje (asegurate de tenerla importada)
            val msgViejoLeido = mockk<Mensaje> {
                every { leido } returns true
                every { fechaEmision } returns LocalDate.now().minusDays(40)
            }
            val msgViejoNoLeido = mockk<Mensaje> {
                every { leido } returns false
                every { fechaEmision } returns LocalDate.now().minusDays(40)
            }

            every { tripulanteMock.inbox() } returns listOf(msgViejoLeido, msgViejoNoLeido)

            val proceso = BorrarMensajesAntiguos(listOf(tripulanteMock), mailSenderMock)

            // Ejecutamos el Template Method
            proceso.ejecutar()

            // Verificamos que se borró exclusivamente el viejo y leído
            verify(exactly = 1) { tripulanteMock.eliminarMensajes(listOf(msgViejoLeido)) }

            // Verificamos la notificación al admin (propia del Template Method)
            verify(exactly = 1) {
                mailSenderMock.enviarMail(
                    "admin@xpace.com",
                    match { it.contains("Borrar Mensajes") },
                    any()
                )
            }
        }

        it("ActualizacionPlanetas parsea el JSON y guarda los planetas en el repositorio") {
            val repoPlanetaReal = RepositorioPlaneta()

            // Simulamos el string JSON que devuelve el servicio externo
            val jsonMock = """
                [
                  {
                    "nombre": "Planeta Nuevo",
                    "temperaturaMedia": 25.0,
                    "gravedad": 9.8,
                    "nivelRadiacion": 10,
                    "hayAguaLiquida": true,
                    "toxicidadAtmosferica": 5,
                    "actividadTectonica": 2,
                    "tamanio": 6000.0
                  }
                ]
            """.trimIndent()

            every { servicePlanetaMock.getPlanetas() } returns jsonMock

            val proceso = ActualizacionPlanetas(repoPlanetaReal, servicePlanetaMock, mailSenderMock)
            proceso.ejecutar()

            // Verificamos que el repositorio incorporó el planeta del JSON
            repoPlanetaReal.getAll().size shouldBe 1
            repoPlanetaReal.getAll().first().nombre shouldBe "Planeta Nuevo"

            // Verificamos la notificación al admin
            verify(exactly = 1) {
                mailSenderMock.enviarMail(
                    "admin@xpace.com",
                    match { it.contains("Actualización") },
                    any()
                )
            }
        }

        it("RepararNaves arregla solo las naves rotas que no están asignadas a una misión") {
            val baseMock = mockk<BaseLanzamiento>()

            // Nave 1: Rota y libre (DEBE repararse)
            val naveRotaLibre = mockk<Nave>(relaxed = true) {
                every { requiereMantenimiento } returns true
                every { misionAsignada } returns null
            }

            // Nave 2: Rota pero ocupada (NO debe repararse)
            val naveRotaOcupada = mockk<Nave>(relaxed = true) {
                every { requiereMantenimiento } returns true
                every { misionAsignada } returns mockk<Mision>()
            }

            // Nave 3: Sana y libre (NO debe repararse)
            val naveSana = mockk<Nave>(relaxed = true) {
                every { requiereMantenimiento } returns false
                every { misionAsignada } returns null
            }

            every { baseMock.navesEstacionadas } returns mutableListOf(naveRotaLibre, naveRotaOcupada, naveSana)

            val proceso = RepararNaves(baseMock, mailSenderMock)
            proceso.ejecutar()

            // Verificamos que solo se llamó al método reparar() de la nave correcta
            verify(exactly = 1) { naveRotaLibre.reparar() }
            verify(exactly = 0) { naveRotaOcupada.reparar() }
            verify(exactly = 0) { naveSana.reparar() }

            // Verificación del Template Method
            verify(exactly = 1) {
                mailSenderMock.enviarMail(
                    "admin@xpace.com",
                    match { it.contains("Reparar Naves") },
                    any()
                )
            }
        }
    }



})