-- Journet: cuestionario orientativo v1, 30 preguntas y 150 alternativas.
-- Ejecutar sobre PostgreSQL despues de crear las tablas JPA, antes de habilitar el test.
-- No borra datos. Si hay otro banco, aborta para que se revise manualmente.
-- Los porcentajes son interes declarado; no es un instrumento psicometrico validado.
BEGIN;
LOCK TABLE vocational_questions, vocacional_option IN SHARE ROW EXCLUSIVE MODE;
ALTER TABLE vocational_test_result ADD COLUMN IF NOT EXISTS career_ranking jsonb;
CREATE TEMP TABLE seed_vocational_questions (
    sequence integer PRIMARY KEY, text text UNIQUE NOT NULL, career text NOT NULL
) ON COMMIT DROP;
INSERT INTO seed_vocational_questions (sequence, text, career) VALUES
(1, '¿Cuánto te interesa crear una aplicación para resolver un problema cotidiano?', 'Ingeniería de Software'),
(2, '¿Cuánto te interesa estudiar el funcionamiento del cuerpo humano?', 'Medicina'),
(3, '¿Cuánto te interesa comprender cómo las personas piensan y sienten?', 'Psicología'),
(4, '¿Cuánto te interesa organizar recursos y personas para alcanzar una meta?', 'Administración de Empresas'),
(5, '¿Cuánto disfrutas comunicar una idea mediante imágenes, formas y colores?', 'Diseño Gráfico'),
(6, '¿Cuánto te interesa comprender cómo se construyen edificios, puentes y carreteras?', 'Ingeniería Civil'),
(7, '¿Cuánto disfrutas encontrar errores y probar soluciones paso a paso en un programa?', 'Ingeniería de Software'),
(8, '¿Cuánto te gustaría aprender a identificar las causas de una enfermedad?', 'Medicina'),
(9, '¿Cuánto disfrutas escuchar a una persona e intentar comprender su experiencia?', 'Psicología'),
(10, '¿Cuánto te gustaría planificar el presupuesto de un proyecto o negocio?', 'Administración de Empresas'),
(11, '¿Cuánto te interesa crear la identidad visual de una marca o proyecto?', 'Diseño Gráfico'),
(12, '¿Cuánto te gustaría aplicar matemáticas y física para analizar estructuras?', 'Ingeniería Civil'),
(13, '¿Cuánto te interesa aprender cómo funcionan las bases de datos y los sistemas digitales?', 'Ingeniería de Software'),
(14, '¿Cuánto te interesa participar en actividades de prevención y cuidado de la salud?', 'Medicina'),
(15, '¿Cuánto te interesa estudiar la conducta y las relaciones entre las personas?', 'Psicología'),
(16, '¿Cuánto te interesa analizar las necesidades de clientes y proponer un servicio?', 'Administración de Empresas'),
(17, '¿Cuánto te gustaría diseñar afiches, publicaciones o piezas digitales?', 'Diseño Gráfico'),
(18, '¿Cuánto te interesa estudiar la resistencia y el comportamiento de materiales?', 'Ingeniería Civil'),
(19, '¿Cuánto te gustaría automatizar una tarea repetitiva mediante código?', 'Ingeniería de Software'),
(20, '¿Cuánto te motiva acompañar a pacientes durante su recuperación?', 'Medicina'),
(21, '¿Cuánto te gustaría investigar cómo aprendemos y desarrollamos habilidades?', 'Psicología'),
(22, '¿Cuánto disfrutas coordinar tareas y acuerdos dentro de un equipo?', 'Administración de Empresas'),
(23, '¿Cuánto disfrutas explorar distintas composiciones visuales para un mismo mensaje?', 'Diseño Gráfico'),
(24, '¿Cuánto te motiva planificar obras que mejoren los servicios de una comunidad?', 'Ingeniería Civil'),
(25, '¿Cuánto te interesa colaborar en la construcción y mejora de productos de software?', 'Ingeniería de Software'),
(26, '¿Cuánto te interesa revisar evidencia científica para comprender tratamientos médicos?', 'Medicina'),
(27, '¿Cuánto te motiva diseñar actividades que promuevan el bienestar emocional?', 'Psicología'),
(28, '¿Cuánto te motiva evaluar los resultados de un negocio y proponer mejoras?', 'Administración de Empresas'),
(29, '¿Cuánto te interesa mejorar un diseño a partir de las opiniones de su público?', 'Diseño Gráfico'),
(30, '¿Cuánto te gustaría supervisar la seguridad y la calidad de un proyecto de construcción?', 'Ingeniería Civil');
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM vocational_questions q
               WHERE NOT EXISTS (SELECT 1 FROM seed_vocational_questions s WHERE s.text = q.text)) THEN
        RAISE EXCEPTION 'Hay preguntas ajenas a este banco. No se modifica ni elimina el banco existente. Revise antes de continuar.';
    END IF;
    IF EXISTS (SELECT text FROM vocational_questions GROUP BY text HAVING count(*) > 1) THEN
        RAISE EXCEPTION 'Hay preguntas duplicadas. Revise el banco existente.';
    END IF;
END $$;
INSERT INTO vocational_questions (text)
SELECT s.text FROM seed_vocational_questions s
WHERE NOT EXISTS (SELECT 1 FROM vocational_questions q WHERE q.text = s.text)
ORDER BY s.sequence;
CREATE TEMP TABLE seed_vocational_options (
    score integer PRIMARY KEY, text text NOT NULL
) ON COMMIT DROP;
INSERT INTO seed_vocational_options VALUES
(0, 'Nada: no me interesa'), (1, 'Poco: me interesa ligeramente'),
(2, 'Algo: me interesa moderadamente'), (3, 'Bastante: me interesa mucho'),
(4, 'Mucho: me entusiasma realizarlo');
-- Reject incompatible pre-existing options, preserving historical answers and their IDs.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM vocacional_option o
        JOIN vocational_questions q ON q.id = o.question_id
        JOIN seed_vocational_questions s ON s.text = q.text
        WHERE NOT EXISTS (SELECT 1 FROM seed_vocational_options a
            WHERE a.text = o.text AND o.career_score = jsonb_build_object(s.career, a.score))
    ) THEN RAISE EXCEPTION 'Hay alternativas incompatibles. No se reemplazan alternativas usadas por resultados.'; END IF;
    IF EXISTS (SELECT question_id, text FROM vocacional_option GROUP BY question_id, text HAVING count(*) > 1) THEN
        RAISE EXCEPTION 'Hay alternativas duplicadas. Revise el banco existente.';
    END IF;
END $$;
INSERT INTO vocacional_option (question_id, text, career_score)
SELECT q.id, a.text, jsonb_build_object(s.career, a.score)
FROM seed_vocational_questions s
JOIN vocational_questions q ON q.text = s.text
CROSS JOIN seed_vocational_options a
WHERE NOT EXISTS (SELECT 1 FROM vocacional_option o WHERE o.question_id = q.id AND o.text = a.text)
ORDER BY q.id, a.score;
DO $$
BEGIN
    IF (SELECT count(*) FROM vocational_questions) <> 30 OR
       (SELECT count(*) FROM vocacional_option) <> 150 THEN
        RAISE EXCEPTION 'El banco debe contener exactamente 30 preguntas y 150 alternativas.';
    END IF;
END $$;
-- Each career must show five questions and max_score = 20.
SELECT s.career, count(DISTINCT q.id) AS questions, sum((o.career_score ->> s.career)::integer) FILTER (WHERE a.score = 4) AS max_score
FROM seed_vocational_questions s JOIN vocational_questions q ON q.text = s.text
JOIN vocacional_option o ON o.question_id = q.id
JOIN seed_vocational_options a ON a.text = o.text
GROUP BY s.career ORDER BY s.career;
COMMIT;
