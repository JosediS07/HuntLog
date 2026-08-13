# 009 · Exportar candidaturas a CSV/PDF
**Estado:** propuesta

## Qué hace

Permite descargar las candidaturas del usuario en CSV o PDF. La exportación respeta los mismos filtros del listado (`estado`, `empresaId`, `fechaDesde`, `fechaHasta`, `salarioDesde`, `salarioHasta`).

## Por qué

El usuario necesita llevar sus candidaturas fuera de la aplicación (reportes, hojas de cálculo, impresión) sin copiar datos a mano.

## Criterios de aceptación

- [ ] `GET /api/candidaturas/exportar?formato=csv` devuelve un CSV con todas las candidaturas del usuario
- [ ] `GET /api/candidaturas/exportar?formato=pdf` devuelve un PDF con las mismas candidaturas
- [ ] El parámetro `formato` es opcional (default: `csv`) y solo acepta `csv` o `pdf`; cualquier otro valor devuelve 400
- [ ] Los filtros del listado aplican a la exportación
- [ ] El CSV tiene encabezados y valores escapados correctamente (RFC 4180)
- [ ] Columnas exportadas: puesto, empresa, estado, salario (min-max moneda), ubicación, urlOferta, aplicadoEn, respondidoEn, creado
- [ ] El response incluye `Content-Disposition: attachment` con nombre de archivo adecuado
- [ ] Sin datos, la exportación genera el archivo con solo los encabezados

## Fuera de alcance

- Exportar todas las candidaturas de todos los usuarios (solo admin)
- Programar exportaciones / envío por email
- Exportar historial de estados o entrevistas
- Elegir columnas dinámicamente
