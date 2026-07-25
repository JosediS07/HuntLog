# 002 · Empresas
**Estado:** pendiente

## Qué hace

CRUD completo de empresas. Cada empresa tiene nombre, sitio web, industria, ubicación y logo. Las empresas pertenecen al usuario que las creó.

## Por qué

Las candidaturas laborales están asociadas a empresas. Antes de crear una candidatura, el usuario debe poder registrar la empresa donde aplicó.

## Criterios de aceptación

- [ ] `POST /api/v1/empresas` crea una empresa con los datos ingresados
- [ ] `GET /api/v1/empresas` lista las empresas del usuario autenticado (paginado)
- [ ] `GET /api/v1/empresas/{id}` obtiene una empresa por ID (solo si pertenece al usuario)
- [ ] `PUT /api/v1/empresas/{id}` actualiza una empresa
- [ ] `DELETE /api/v1/empresas/{id}` elimina una empresa
- [ ] Los campos obligatorios: nombre
- [ ] Una empresa no puede ser accedida por otro usuario (404 si no existe o no pertenece)
- [ ] Los errores tienen formato consistente

## Fuera de alcance

- Empresas compartidas entre usuarios
- Búsqueda de empresas por nombre
- Importar empresa desde Adzuna
