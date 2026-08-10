# 007 · Filtro por rango salarial
**Estado:** propuesta

## Qué hace

Permite filtrar el listado de candidaturas por un rango salarial. El usuario indica un salario mínimo y/o máximo y solo ve las candidaturas cuyo rango salarial se solapa con el buscado.

## Por qué

El usuario necesita encontrar sus candidaturas por el rango salarial para priorizar procesos con mejor compensación, sin revisar cada registro manualmente.

## Criterios de aceptación

- [ ] `GET /api/candidaturas?salarioDesde=40000&salarioHasta=60000` devuelve las candidaturas cuyo rango `[salarioMin, salarioMax]` se solapa con `[40000, 60000]`
- [ ] `salarioDesde` y `salarioHasta` son opcionales e independientes: puede usarse solo uno
- [ ] Si ambos se envían y `salarioDesde > salarioHasta`, devuelve error 422
- [ ] Con un filtro de salario activo, las candidaturas sin salario (salarioMin/salarioMax nulos) quedan excluidas
- [ ] El filtro ignora la moneda: compara montos directamente
- [ ] El filtro se combina con los existentes: `estado`, `empresaId`, `fechaDesde`, `fechaHasta` y paginación

## Fuera de alcance

- Filtrar por moneda
- Filtrar por rango salarial en la búsqueda de Adzuna (006)
- Ordenar por salario
