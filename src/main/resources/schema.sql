CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    rol             VARCHAR(20) NOT NULL DEFAULT 'USER',
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    creado          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS companies (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    sitio_web       VARCHAR(255),
    industria       VARCHAR(100),
    ubicacion       VARCHAR(150),
    logo_url        VARCHAR(500),
    usuario_id      BIGINT NOT NULL REFERENCES users(id),
    creado          TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS candidaturas (
    id              BIGSERIAL PRIMARY KEY,
    empresa_id      BIGINT NOT NULL REFERENCES companies(id),
    usuario_id      BIGINT NOT NULL REFERENCES users(id),
    puesto          VARCHAR(200) NOT NULL,
    estado          VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    url_oferta      VARCHAR(500),
    salario_min     NUMERIC(12,2),
    salario_max     NUMERIC(12,2),
    moneda          VARCHAR(3) DEFAULT 'EUR',
    ubicacion       VARCHAR(150),
    notas           TEXT,
    aplicado_en     TIMESTAMP,
    respondido_en   TIMESTAMP,
    creado          TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS entrevistas (
    id                  BIGSERIAL PRIMARY KEY,
    candidatura_id      BIGINT NOT NULL REFERENCES candidaturas(id) ON DELETE CASCADE,
    tipo                VARCHAR(30) NOT NULL,
    fecha_hora          TIMESTAMP NOT NULL,
    duracion_min        INTEGER,
    entrevistador       VARCHAR(150),
    feedback            TEXT,
    notas               TEXT,
    creado              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS historial_estado (
    id                  BIGSERIAL PRIMARY KEY,
    candidatura_id      BIGINT NOT NULL REFERENCES candidaturas(id) ON DELETE CASCADE,
    estado_anterior     VARCHAR(30),
    estado_nuevo        VARCHAR(30) NOT NULL,
    cambiado_en         TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_candidaturas_usuario ON candidaturas(usuario_id);
CREATE INDEX idx_candidaturas_empresa ON candidaturas(empresa_id);
CREATE INDEX idx_candidaturas_estado ON candidaturas(estado);
CREATE INDEX idx_entrevistas_candidatura ON entrevistas(candidatura_id);
CREATE INDEX idx_historial_candidatura ON historial_estado(candidatura_id);
CREATE INDEX idx_companies_usuario ON companies(usuario_id);
