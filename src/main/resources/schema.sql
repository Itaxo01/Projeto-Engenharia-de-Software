-- src/main/resources/schema.sql
CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(150) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(200) NOT NULL,
    matricula VARCHAR(20) UNIQUE NOT NULL,
    curso VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS subjects (
    codigo VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS professors (
    id_lattes VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS professor_subjects (
    professor_id VARCHAR(50) REFERENCES professors(id_lattes) ON DELETE CASCADE,
    subject_codigo VARCHAR(20) REFERENCES subjects(codigo) ON DELETE CASCADE,
    PRIMARY KEY(professor_id, subject_codigo)
);

CREATE TABLE IF NOT EXISTS avaliacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    professor_id VARCHAR(50) REFERENCES professors(id_lattes) ON DELETE CASCADE,
    subject_codigo VARCHAR(20) REFERENCES subjects(codigo) ON DELETE CASCADE,
    user_email VARCHAR(150) REFERENCES users(email) ON DELETE CASCADE,
    nota INTEGER NOT NULL CHECK (nota BETWEEN 0 AND 10),
    comentario TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS comentarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(150) REFERENCES users(email),
    up_votes INTEGER DEFAULT 0,
    down_votes INTEGER DEFAULT 0,
    pai_id BIGINT REFERENCES comentarios(id) ON DELETE CASCADE, -- ← Autoreferencial
    texto VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);