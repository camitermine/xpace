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
            // 1. Arrange: Configuramos los robots con nombre Y EMAIL
            val tripulante1 = mockk<Tripulante> {
                every { nombre } returns "Juan"
                every { email } returns "juan@xpace.com"
            }
            val tripulante2 = mockk<Tripulante> {
                every { nombre } returns "Ana"
                every { email } returns "ana@xpace.com"
            }

            // Ya no hace falta el planetaMock si la misión ya expone el string directamente
            val misionMock = mockk<Mision>(relaxed = true) {
                every { nombre } returns "Mision Alpha"
                // Actualizamos el mock para que responda al nuevo método que creamos
                every { nombreDestino() } returns "Marte"
                every { duracion() } returns 120.0
                every { tripulantes } returns listOf(tripulante1, tripulante2)
            }

            val accion = NotificarTripulantesPorMail(mailSenderMock)

            // 2. Act
            accion.ejecutar(misionMock)

            // 3. Assert: Verificamos que se mandó AL mail del tripulante con los textos exactos
            verify(exactly = 1) {
                mailSenderMock.enviarMail(
                    "juan@xpace.com",
                    "Misión Mision Alpha lanzada",
                    "Tripulante Juan, la misión Mision Alpha con destino al planeta Marte ha sido lanzada exitosamente. Duración estimada: 120 días."
                )
                mailSenderMock.enviarMail(
                    "ana@xpace.com",
                    "Misión Mision Alpha lanzada",
                    "Tripulante Ana, la misión Mision Alpha con destino al planeta Marte ha sido lanzada exitosamente. Duración estimada: 120 días."
                )
            }
        }

        it("NotificarMisionPionera envía mensaje ALTA prioridad al inbox si el planeta es nuevo y habitable") {
            val tripulanteReal = mockk<Tripulante>(relaxed = true)

            // Ya no hace falta crear ni configurar planetaPionero,
            // porque la acción nunca interactúa con él directamente.

            val misionMock = mockk<Mision> {
                every { nombre } returns "Pionero I"
                every { tripulantes } returns listOf(tripulanteReal)

                // Le enseñamos al mock cómo responder a los nuevos métodos
                every { esPionera() } returns true
                every { nombreDestino() } returns "Kepler-186f"
            }

            val accion = NotificarMisionPionera()
            accion.ejecutar(misionMock)

            // Verificamos que se llamó a recibirMensaje con un mensaje de prioridad ALTA
            verify(exactly = 1) {
                tripulanteReal.recibirMensaje(match { it.prioridad == Prioridad.ALTA && it.asunto.contains("Pionera") })
            }
        }
        it("EvaluarMantenimientoNave solicita mantenimiento a la nave si la misión lo provoca") {
            val naveMock = mockk<Nave>(relaxed = true)

            val misionMock = mockk<Mision> {
                // Configuramos la respuesta del nuevo método de validación
                every { provocaMantenimientoNave() } returns true

                // Configuramos que devuelva la naveMock mediante un método
                every { nave } returns naveMock
            }

            val accion = EvaluarMantenimientoNave()
            accion.ejecutar(misionMock)

            // Verificamos que se le mandó el mensaje de solicitar mantenimiento a la nave
            verify(exactly = 1) { naveMock.solicitarMantenimiento() }
        }



        describe("Procesos Administrativos (Command + Template Method)") {

            it("BorrarMensajesAntiguos ordena al tripulante limpiar los mensajes obsoletos con la fecha correcta") {
                val tripulanteMock = mockk<Tripulante>(relaxed = true)
                val proceso = BorrarMensajesAntiguos(listOf(tripulanteMock), mailSenderMock)

                // Ejecutamos el Template Method
                proceso.ejecutar()

                // Verificamos que se calculó la fecha de corte correcta (hace 30 días)
                val fechaCorteEsperada = LocalDate.now().minusDays(30)
                verify(exactly = 1) { tripulanteMock.limpiarMensajesObsoletos(fechaCorteEsperada) }

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

            it("RepararNaves delega la reparación a la base de lanzamiento y notifica") {
                val baseMock = mockk<BaseLanzamiento>(relaxed = true)
                val proceso = RepararNaves(baseMock, mailSenderMock)

                proceso.ejecutar()

                // Verificamos la coordinación: el proceso le da la orden a la base
                verify(exactly = 1) { baseMock.repararNavesDisponibles() }

                // Verificamos el Template Method
                verify(exactly = 1) {
                    mailSenderMock.enviarMail(
                        "admin@xpace.com",
                        match { it.contains("Reparar Naves") },
                        any()
                    )
                }
            }

            it("BorrarSubvencionesVencidas elimina solo las vencidas y no aplicadas") {
                val repoSubvencion = mockk<RepositorioSubvencion>(relaxed = true)

                val subVencidaNoAplicada = mockk<Subvencion> {
                    every { estaVigente() } returns false
                    every { estaAplicada } returns false
                }
                val subVencidaAplicada = mockk<Subvencion> {
                    every { estaVigente() } returns false
                    every { estaAplicada } returns true
                }
                val subVigente = mockk<Subvencion> {
                    every { estaVigente() } returns true
                    every { estaAplicada } returns false
                }

                every { repoSubvencion.getAll() } returns listOf(subVencidaNoAplicada, subVencidaAplicada, subVigente)

                val proceso = BorrarSubvencionesVencidas(repoSubvencion, mailSenderMock)
                proceso.ejecutar()

                verify(exactly = 1) { repoSubvencion.delete(subVencidaNoAplicada) }
                verify(exactly = 0) { repoSubvencion.delete(subVencidaAplicada) }
                verify(exactly = 0) { repoSubvencion.delete(subVigente) }
                verify(exactly = 1) {
                    mailSenderMock.enviarMail("admin@xpace.com", match { it.contains("Subvenciones") }, any())
                }
            }

            it("BorrarSubvencionesVencidas no borra nada si no hay vencidas sin aplicar") {
                val repoSubvencion = mockk<RepositorioSubvencion>(relaxed = true)

                val subVigente = mockk<Subvencion> {
                    every { estaVigente() } returns true
                    every { estaAplicada } returns false
                }

                every { repoSubvencion.getAll() } returns listOf(subVigente)

                BorrarSubvencionesVencidas(repoSubvencion, mailSenderMock).ejecutar()

                verify(exactly = 0) { repoSubvencion.delete(any()) }
            }

            it("RetirarTripulantesInactivos retira tripulantes cuya última misión fue hace más de 2 años") {
                val repoTripulante = mockk<RepositorioTripulante>(relaxed = true)
                val repoMision = mockk<RepositorioMision>(relaxed = true)

                val tripulanteInactivo = mockk<Tripulante>()
                val tripulanteActivo = mockk<Tripulante>()

                val misionVieja = mockk<Mision> {
                    every { tripulantes } returns listOf(tripulanteInactivo)
                    every { fechaLanzamiento } returns LocalDate.now().minusYears(3)
                }
                val misionReciente = mockk<Mision> {
                    every { tripulantes } returns listOf(tripulanteActivo)
                    every { fechaLanzamiento } returns LocalDate.now().minusMonths(6)
                }

                every { repoTripulante.getAll() } returns listOf(tripulanteInactivo, tripulanteActivo)
                every { repoMision.getAll() } returns listOf(misionVieja, misionReciente)

                RetirarTripulantesInactivos(repoTripulante, repoMision, mailSenderMock).ejecutar()

                verify(exactly = 1) { repoTripulante.delete(tripulanteInactivo) }
                verify(exactly = 0) { repoTripulante.delete(tripulanteActivo) }
                verify(exactly = 1) {
                    mailSenderMock.enviarMail("admin@xpace.com", match { it.contains("Tripulantes") }, any())
                }
            }

            it("RetirarTripulantesInactivos retira tripulantes sin ninguna misión") {
                val repoTripulante = mockk<RepositorioTripulante>(relaxed = true)
                val repoMision = mockk<RepositorioMision>(relaxed = true)

                val tripulanteSinMisiones = mockk<Tripulante>()

                every { repoTripulante.getAll() } returns listOf(tripulanteSinMisiones)
                every { repoMision.getAll() } returns emptyList()

                RetirarTripulantesInactivos(repoTripulante, repoMision, mailSenderMock).ejecutar()

                verify(exactly = 1) { repoTripulante.delete(tripulanteSinMisiones) }
            }

            it("RetirarTripulantesInactivos no retira tripulantes con misiones recientes") {
                val repoTripulante = mockk<RepositorioTripulante>(relaxed = true)
                val repoMision = mockk<RepositorioMision>(relaxed = true)

                val tripulanteActivo = mockk<Tripulante>()
                val misionReciente = mockk<Mision> {
                    every { tripulantes } returns listOf(tripulanteActivo)
                    every { fechaLanzamiento } returns LocalDate.now().minusDays(30)
                }

                every { repoTripulante.getAll() } returns listOf(tripulanteActivo)
                every { repoMision.getAll() } returns listOf(misionReciente)

                RetirarTripulantesInactivos(repoTripulante, repoMision, mailSenderMock).ejecutar()

                verify(exactly = 0) { repoTripulante.delete(any()) }
            }

            it("AgregarNavesMasivas agrega todas las naves de la lista al repositorio") {
                val repoNave = mockk<RepositorioNave>(relaxed = true)
                val nave1 = mockk<Nave>()
                val nave2 = mockk<Nave>()
                val nave3 = mockk<Nave>()

                AgregarNavesMasivas(listOf(nave1, nave2, nave3), repoNave, mailSenderMock).ejecutar()

                verify(exactly = 1) { repoNave.create(nave1) }
                verify(exactly = 1) { repoNave.create(nave2) }
                verify(exactly = 1) { repoNave.create(nave3) }
                verify(exactly = 1) {
                    mailSenderMock.enviarMail("admin@xpace.com", match { it.contains("Naves") }, any())
                }
            }

            it("AgregarNavesMasivas no agrega nada si la lista está vacía") {
                val repoNave = mockk<RepositorioNave>(relaxed = true)

                AgregarNavesMasivas(emptyList(), repoNave, mailSenderMock).ejecutar()

                verify(exactly = 0) { repoNave.create(any()) }
            }
        }
    }
})
