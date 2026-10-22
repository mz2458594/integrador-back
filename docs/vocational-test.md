# Test vocacional

## Instalacion
1. Desplegar el backend con estos cambios. Hibernate (`ddl-auto=update`) incorpora `vocational_test_result.career_ranking` (JSONB).
2. Ejecutar `docs/sql/vocational-test-seed.sql` con un cliente PostgreSQL conectado a la base de la aplicacion. Ejemplo: `psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f docs/sql/vocational-test-seed.sql`.
3. Abrir `/test` con una cuenta sin resultado. Al responder las 30 preguntas se guarda un resultado unico por usuario.

El SQL es transaccional y reutilizable; no borra preguntas, alternativas ni resultados. Rechaza un banco diferente o duplicado para evitar mezclar preguntas sin equilibrar carreras. No ejecutar contra otra base accidentalmente. Los resultados anteriores se conservan y no se recalculan: muestran que no tienen un desglose guardado.

## Criterio de puntuacion
30 preguntas: cinco para cada una de seis carreras (Software, Medicina, Psicologia, Administracion, Diseno Grafico e Ingenieria Civil). Cada pregunta ofrece cinco grados de interes: Nada=0, Poco=1, Algo=2, Bastante=3, Mucho=4. El JSONB `career_score` de cada alternativa asigna esos puntos a la carrera de su pregunta.

El servidor valida entre 20 y 40 preguntas, una alternativa por pregunta, IDs vigentes, ausencia de duplicados y puntajes enteros no negativos. Suma los puntos elegidos por carrera. El maximo es la suma del mayor puntaje disponible de cada pregunta para esa carrera. Afinidad = 100 * puntos / maximo, redondeado a dos decimales. En este banco cada carrera tiene un maximo de 20 puntos. Orden: afinidad descendente, nombre ascendente (Java). En empate se conserva ese orden y la interfaz informa todas las carreras empatadas. Si todos son cero, la sugerencia es "Sin preferencia definida".

El resultado guarda el ranking JSONB como una instantanea: cambiar preguntas o puntajes despues no cambia el desglose de un resultado ya completado. No es una prueba psicometrica validada y no predice exito academico; es una herramienta de exploracion de intereses.

## API
- `GET /vocational-question?page=0&size=100&sort=id,asc`: preguntas y alternativas ordenadas.
- `GET /vocational-test-result/me`: resultado propio, o HTTP 204 si no existe.
- `POST /vocational-test-result`: `{ "vocacionalOptionIds": [1, 7, ...] }`. No acepta como fuente de calculo una carrera ni puntajes del navegador. Devuelve 201 con `suggestedCareer`, `ranking`, `completedAt` y `vocacionalOptions`.
- Cada elemento de `ranking`: `career`, `score`, `maxScore`, `percentage`.
- La lista historica y el detalle solo exponen resultados del usuario autenticado. Un segundo intento devuelve 409; la restriccion unica de `user_id` cubre envios simultaneos.
- Los resultados son inmutables (sin PUT/DELETE). Modificar preguntas o alternativas requiere administrador.
- Los accesos a roadmap, chats y agente siguen comprobando el resultado persistido mediante VocationalAccessService; en frontend usan `/vocational-test-result/me`.

## Verificacion
Frontend: `node --test src/pages/private/Test/vocationalApi.test.mjs src/lib/vocationalAccess.test.mjs`.
Backend: `./mvnw -o test` (Windows: `mvnw.cmd`).

Comprobacion manual: responder, avanzar/retroceder, guardar, recargar `/test`, comprobar ranking y desbloqueo de acciones. Una segunda cuenta no debe poder leer el resultado de la primera. El seed debe poder ejecutarse dos veces sin aumentar conteos.
