# 009 · Exportar candidaturas a CSV/PDF — Plan

## Enfoque

Un solo endpoint `GET /api/candidaturas/exportar` con parámetro `formato` (`csv` default, `pdf`). La consulta reutiliza la misma `Specification` que el listado para aplicar los filtros y devolver TODAS las coincidencias (sin paginación). El CSV se genera a mano (RFC 4180) y el PDF con OpenPDF.

## Implementación

1. Agregar dependencia `com.github.librepdf:openpdf` (LGPL/MPL)
2. Extraer en `CandidaturaService` el método `construirEspecificacion(...)` con los filtros actuales para reutilizarlo en listar y exportar
3. Crear `ExportacionCandidaturaService`:
   - `exportarCsv(List<CandidaturaResponse>)` → `byte[]` con encabezados y escapado RFC 4180
   - `exportarPdf(List<CandidaturaResponse>)` → `byte[]` con `PdfPTable`
4. Endpoint en `CandidaturaController`:
   - `GET /exportar` con `formato` + filtros; valida `formato` (400 si no es csv/pdf)
   - Response `byte[]` con `Content-Disposition: attachment` y `Content-Type` según formato
5. Tests unitarios (generación CSV/PDF) y de integración (descarga respetando filtros)

## Decisiones

- **CSV manual sin dependencia**: el formato es simple; evita una librería más. Escapado RFC 4180 (comillas duplicadas, campos con comas/comillas/saltos).
- **OpenPDF en vez de iText**: licencia permisiva (LGPL/MPL) vs AGPL de iText; API estable y suficiente para tablas.
- **Sin paginación en el export**: la exportación incluye todas las coincidencias de los filtros; la paginación es solo para la UI.
- **Un solo endpoint con `formato`**: reduce duplicación; el default `csv` mantiene compatibilidad.

## Riesgos

- **Valores con comas en CSV**: mitigado con escapado RFC 4180.
- **Caracteres no ASCII en PDF (acentos)**: OpenPDF por defecto usa Helvetica estándar sin acentos; se usa un mapeo/encoding UTF-8 en las celdas o se confía en el set de codificación del documento. Se verifica con un dato con acentos en el test de integración.
- **`Content-Disposition` con caracteres especiales**: el nombre de archivo es fijo (`candidaturas.csv`/`.pdf`), sin riesgo de inyección.
