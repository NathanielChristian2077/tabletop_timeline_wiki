-- Criação da enumeração de tipo de usuário
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipousuario') THEN
        CREATE TYPE tipousuario AS ENUM ('MESTRE', 'JOGADOR');
    END IF;
END $$;

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id UUID PRIMARY KEY,
    nome TEXT NOT NULL,
    senha TEXT NOT NULL,
    tipo TEXT NOT NULL
);

-- Tabela de campanhas
CREATE TABLE IF NOT EXISTS campanha (
    id UUID PRIMARY KEY,
    nome TEXT NOT NULL,
    descricao TEXT,
    imagem_path TEXT
);

-- Tabela de eventos
CREATE TABLE IF NOT EXISTS evento (
    id UUID PRIMARY KEY,
    titulo TEXT NOT NULL,
    descricao TEXT,
    campanha_id UUID REFERENCES campanha(id) ON DELETE CASCADE,
    anterior_id UUID REFERENCES evento(id),
    posterior_id UUID REFERENCES evento(id)
);

-- Tabela de personagens
CREATE TABLE IF NOT EXISTS personagem (
    id UUID PRIMARY KEY,
    nome TEXT NOT NULL,
    descricao TEXT,
    campanha_id UUID REFERENCES campanha(id) ON DELETE CASCADE
);

-- Tabela de locais
CREATE TABLE IF NOT EXISTS local (
    id UUID PRIMARY KEY,
    nome TEXT NOT NULL,
    descricao TEXT,
    campanha_id UUID REFERENCES campanha(id) ON DELETE CASCADE
);

-- Tabela de objetos
CREATE TABLE IF NOT EXISTS objeto (
    id UUID PRIMARY KEY,
    nome TEXT NOT NULL,
    descricao TEXT,
    campanha_id UUID REFERENCES campanha(id) ON DELETE CASCADE
);

-- Tabela evento <-> personagem
CREATE TABLE IF NOT EXISTS evento_personagem (
    evento_id UUID REFERENCES evento(id) ON DELETE CASCADE,
    personagem_id UUID REFERENCES personagem(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id, personagem_id)
);

-- Tabela evento <-> local
CREATE TABLE IF NOT EXISTS evento_local (
    evento_id UUID REFERENCES evento(id) ON DELETE CASCADE,
    local_id UUID REFERENCES local(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id, local_id)
);

-- Tabela evento <-> objeto
CREATE TABLE IF NOT EXISTS evento_objeto (
    evento_id UUID REFERENCES evento(id) ON DELETE CASCADE,
    objeto_id UUID REFERENCES objeto(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id, objeto_id)
);

-- Tabela evento <-> evento (relacionados, além de anterior/posterior)
CREATE TABLE IF NOT EXISTS evento_evento (
    evento_id1 UUID REFERENCES evento(id) ON DELETE CASCADE,
    evento_id2 UUID REFERENCES evento(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id1, evento_id2)
);

-- Tabela personagem <-> personagem
CREATE TABLE IF NOT EXISTS personagem_personagem (
    personagem1_id UUID REFERENCES personagem(id) ON DELETE CASCADE,
    personagem2_id UUID REFERENCES personagem(id) ON DELETE CASCADE,
    PRIMARY KEY (personagem1_id, personagem2_id)
);

-- Tabela personagem <-> local
CREATE TABLE IF NOT EXISTS personagem_local (
    personagem_id UUID REFERENCES personagem(id) ON DELETE CASCADE,
    local_id UUID REFERENCES local(id) ON DELETE CASCADE,
    PRIMARY KEY (personagem_id, local_id)
);

-- Tabela personagem <-> objeto
CREATE TABLE IF NOT EXISTS personagem_objeto (
    personagem_id UUID REFERENCES personagem(id) ON DELETE CASCADE,
    objeto_id UUID REFERENCES objeto(id) ON DELETE CASCADE,
    PRIMARY KEY (personagem_id, objeto_id)
);

-- Tabela objeto <-> objeto
CREATE TABLE IF NOT EXISTS objeto_objeto (
    objeto1_id UUID REFERENCES objeto(id) ON DELETE CASCADE,
    objeto2_id UUID REFERENCES objeto(id) ON DELETE CASCADE,
    PRIMARY KEY (objeto1_id, objeto2_id)
);

-- Tabela objeto <-> local
CREATE TABLE IF NOT EXISTS objeto_local (
    objeto_id UUID REFERENCES objeto(id) ON DELETE CASCADE,
    local_id UUID REFERENCES local(id) ON DELETE CASCADE,
    PRIMARY KEY (objeto_id, local_id)
);

-- Tabela local <-> local
CREATE TABLE IF NOT EXISTS local_local (
    local1_id UUID REFERENCES local(id) ON DELETE CASCADE,
    local2_id UUID REFERENCES local(id) ON DELETE CASCADE,
    PRIMARY KEY (local1_id, local2_id)
);