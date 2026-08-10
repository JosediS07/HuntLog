# 007 · Filtro por rango salarial — Plan

## Enfoque

Extender la `Specification` ya usada en `CandidaturaService.listar` con dos predicados opcionales. El filtrado sigue la semántica de solapamiento de rangos: la candidatura coincide si su banda salarial `[salarioMin, salarioMax]` intersecta el rango buscado `[salarioDesde, salarioHasta]`.

## Implementación

1. Agregar `salarioDesde` y `salarioHasta` como `@RequestParam` opcionales en `CandidaturaController.listar`
2. Agregar los parámetros a la firma de `CandidaturaService.listar` y validar que `salarioDesde <= salarioHasta`
3. Agregar predicados a la `Specification`:
   - `salarioDesde != null` → `salarioMax IS NOT NULL AND salarioMax >= salarioDesde`
   - `salarioHasta != null` → `salarioMin IS NOT NULL AND salarioMin <= salarioHasta`
4. Actualizar la documentación de filtros en `README.md`
5. Tests unitarios (validación de rango) y de integración (semántica de solapamiento con H2)

## Decisiones

- **Solapamiento en vez de inclusión**: es la semántica habitual de búsqueda por rango y la más útil: una candidatura 30k-50k aparece al buscar 40k-60k porque cae dentro del rango.
- **Excluir candidaturas sin salario**: si el usuario filtra por salario, solo interesan registros con datos salariales; incluir los nulos sería impredecible.
- **Ignorar moneda**: el usuario típicamente maneja una sola moneda; comparar montos directos simplifica sin perder utilidad.

## Riesgos

- **Predicados con valores nulos**: se protege con `IS NOT NULL` para no filtrar por coincidencia accidental con NULL.
- **Candidaturas con banda incompleta** (solo min o solo max): el solapamiento parcial se resuelve con las reglas de `IS NOT NULL` por lado, excluyendo lo no comparable.
