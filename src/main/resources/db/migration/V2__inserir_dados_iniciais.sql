-- Setores
INSERT INTO setor (id, nome, sigla) VALUES
    (1, 'Tecnologia da Informação', 'TI'),
    (2, 'Administração', 'ADM');

-- Categorias
INSERT INTO categoria (id, nome, descricao, setor_id) VALUES
    (1, 'Acesso a sistemas', 'Solicitações de acesso ou permissão em sistemas institucionais.', 1),
    (2, 'Infraestrutura de rede', 'Problemas de internet, rede cabeada ou rede sem fio.', 1),
    (3, 'Documentação', 'Solicitações relacionadas a documentos administrativos.', 2),
    (4, 'Patrimônio', 'Solicitações relacionadas a bens e equipamentos patrimoniais.', 2);

-- Técnicos: possuem setor_id.
-- Solicitantes: não são vinculados diretamente a um setor.
INSERT INTO usuario (id, nome, email, matricula, tipo, setor_id) VALUES
    (1, 'Ana Tecnologia', 'ana.tecnologia@ifrn.edu.br', 100001, 'TECNICO', 1),
    (2, 'Bruno Administração', 'bruno.administracao@ifrn.edu.br', 100002, 'TECNICO', 2),
    (3, 'Carla Solicitante', 'carla.solicitante@ifrn.edu.br', 200001, 'SOLICITANTE', NULL),
    (4, 'Daniel Solicitante', 'daniel.solicitante@ifrn.edu.br', 200002, 'SOLICITANTE', NULL);

-- Os IDs foram informados manualmente. Avançar as sequências evita que os
-- próximos INSERTs automáticos tentem reutilizar esses identificadores.
ALTER TABLE setor ALTER COLUMN id RESTART WITH 3;
ALTER TABLE categoria ALTER COLUMN id RESTART WITH 5;
ALTER TABLE usuario ALTER COLUMN id RESTART WITH 5;
