# 006 · Búsqueda Adzuna
**Estado:** pendiente

## Qué hace

Busca ofertas de empleo reales en la API de Adzuna y las muestra al usuario. El usuario puede ver los resultados y decidir importar una oferta como candidatura.

## Por qué

El usuario necesita encontrar ofertas reales sin salir de la aplicación. Integrar con Adzuna permite buscar por palabra clave y país.

## Criterios de aceptación

- [ ] `GET /api/v1/ofertas/buscar?q=developer&pais=gb` busca ofertas en Adzuna
- [ ] Devuelve lista de ofertas con: título, empresa, ubicación, URL, descripción, salario
- [ ] Si no hay resultados, devuelve lista vacía
- [ ] Si la API de Adzuna falla, devuelve error 502
- [ ] Los parámetros obligatorios: `q` (query de búsqueda)
- [ ] El parámetro `pais` es opcional (default: `gb`)

## Fuera de alcance

- Importar oferta como candidatura (será una feature separada)
- Guardar búsquedas favoritas
- Paginación de resultados de Adzuna
- Filtrar por salario mínimo/máximo
