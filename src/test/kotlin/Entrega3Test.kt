import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDate


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
        it("InformarMisionCara avisa al sistema contable si el presupuesto supera el millón") {
            // Simulamos una misión que gasta 1.5 millones
            val misionCara = mockk<Mision> {
                every { nombre } returns "Apolo Lujo"
                every { calcularPresupuestoFijo() } returns 1500000.0
            }

            val accion = InformarMisionCara(sistemaContableMock)
            accion.ejecutar(misionCara)

            // Verificamos que se llamó al servicio contable con los datos exactos
            verify(exactly = 1) {
                sistemaContableMock.informarMisionCara("Apolo Lujo", 1500000.0, any())
            }
        }

        it("InformarMisionCara NO avisa al sistema contable si el presupuesto es de un millón o menos") {
            // Simulamos una misión austera
            val misionBarata = mockk<Mision> {
                every { nombre } returns "Apolo Económico"
                every { calcularPresupuestoFijo() } returns 900000.0
            }

            val accion = InformarMisionCara(sistemaContableMock)
            accion.ejecutar(misionBarata)

            // Verificamos que NUNCA se contactó al sistema contable
            verify(exactly = 0) {
                sistemaContableMock.informarMisionCara(any(), any(), any())
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

        // --- TESTS FALTANTES PARA PromoverAVeterano ---
        it("PromoverAVeterano actualiza el estado de los tripulantes si la misión es de alto riesgo") {
            // 1. Arrange
            val tripulante1 = mockk<Tripulante>(relaxed = true)
            val tripulante2 = mockk<Tripulante>(relaxed = true)

            val misionRiesgosa = mockk<Mision> {
                every { altoRiesgo() } returns true // Condición positiva
                every { tripulantes } returns listOf(tripulante1, tripulante2)
            }

            val accion = PromoverAVeterano(300)

            // 2. Act
            accion.ejecutar(misionRiesgosa)

            // 3. Assert
            verify(exactly = 1) { tripulante1.hacerVeterano(300) }
            verify(exactly = 1) { tripulante2.hacerVeterano(300) }
        }

        it("PromoverAVeterano NO hace nada si la misión NO es de alto riesgo") {
            val tripulante = mockk<Tripulante>(relaxed = true)

            val misionSegura = mockk<Mision> {
                every { altoRiesgo() } returns false // Condición negativa
                every { tripulantes } returns listOf(tripulante)
            }

            val accion = PromoverAVeterano(300)
            accion.ejecutar(misionSegura)

            // Verificamos que NUNCA se llamó al método hacerVeterano
            verify(exactly = 0) { tripulante.hacerVeterano(any()) }
        }

        it("NotificarFinanciamiento envía un mail a financiamiento si la misión tiene subvención aplicada") {
            // Creamos un mock de la subvención
            val subvencionMock = mockk<Subvencion> {
                every { nombre } returns "Fondo Galáctico"
            }

            val misionSubvencionada = mockk<Mision> {
                every { nombre } returns "Apolo X"
                every { subvencionAplicada } returns subvencionMock // Tiene subvención
            }

            val accion = NotificarFinanciamiento(mailSenderMock)
            accion.ejecutar(misionSubvencionada)

            // Verificamos el envío del mail con los datos correctos
            verify(exactly = 1) {
                mailSenderMock.enviarMail(
                    "financiamiento@xpace.com",
                    "Misión Apolo X con subvención",
                    "Se informa que la misión Apolo X fue lanzada utilizando la subvención: Fondo Galáctico."
                )
            }
        }

        it("NotificarFinanciamiento NO envía mail si la misión no tiene subvención aplicada") {
            val misionSinSubvencion = mockk<Mision> {
                every { subvencionAplicada } returns null // No tiene subvención
            }

            val accion = NotificarFinanciamiento(mailSenderMock)
            accion.ejecutar(misionSinSubvencion)

            // Verificamos que no se intentó mandar ningún mail
            verify(exactly = 0) {
                mailSenderMock.enviarMail(any(), any(), any())
            }
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
