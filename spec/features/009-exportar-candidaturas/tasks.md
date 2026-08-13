# 009 · Exportar candidaturas a CSV/PDF — Tareas

- [x] Documentar spec, plan y tareas en `spec/features/009-exportar-candidaturas/`
- [ ] Agregar dependencia OpenPDF en `pom.xml`
- [ ] Extraer `construirEspecificacion` en `CandidaturaService`
- [ ] Crear `ExportacionCandidaturaService` (CSV + PDF)
- [ ] Endpoint `GET /api/candidaturas/exportar` con validación de formato
- [ ] Tests unitarios de generación CSV y PDF
- [ ] Test de integración de descarga respetando filtros
- [ ] Actualizar README (endpoint de exportación)
- [ ] Mover feature a "Done" en roadmap.md
- [ ] Verificar que `./mvnw test` pasa completo
