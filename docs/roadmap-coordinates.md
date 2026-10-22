# Coordenadas de los pasos del roadmap

Se agregan `x` e `y` opcionales (Double, columnas PostgreSQL DOUBLE PRECISION) a
`roadmap_steps`. No se eliminan tablas, relaciones ni campos anteriores.

## Despliegue

Antes de publicar el backend, ejecutar `docs/sql/add-roadmap-step-coordinates.sql`
en la base correspondiente mediante el proceso habitual de despliegue. El script
solo agrega columnas y puede repetirse. No se ejecuta automaticamente. No se cambia
la configuracion existente de Hibernate ni se conecta con produccion desde las pruebas.
Las filas anteriores conservan NULL: no se inventan posiciones ni se reescriben datos.

## API

POST /roadmap-step acepta los campos existentes y adicionalmente `x` e `y`.
PUT /roadmap-step/{id} permite enviar solo las coordenadas:

```json
{"x": 450.5, "y": -120.25}
```

Se admiten cero, negativos y decimales; se rechazan valores no finitos.
En PUT, omitir un eje o enviarlo como null conserva su valor actual.
Los clientes anteriores pueden seguir enviando el mismo JSON.
GET /roadmap-step, GET /roadmap-step/roadmap/{id}, GET /roadmap-step/{id}
y los pasos anidados en GET /roadmap/{id} incluyen `x` e `y`.

## Frontend

React Flow utiliza `position: { x: step.x, y: step.y }` con el origen central
`nodeOrigin={[0.5, 0.5]}` configurado en Roadmap.jsx. Para los pasos antiguos
sin coordenadas, calcular una posicion inicial a partir de orderIndex (usar
`??` y no `||` para conservar ceros). Guardar las coordenadas del nodo al
finalizar el arrastre mediante PUT /roadmap-step/{id}.

La pantalla actual usa datos de ejemplo: conectar sus cargas y eventos con
estos endpoints sigue siendo un trabajo de integracion del frontend. Estos campos
no modelan conexiones arbitrarias ni agregan nuevas entidades para fases.

## Verificacion local sin base de datos

`mvn -Dtest=RoadmapCoordinatesControllerTest test`

Las pruebas usan MockMvc standalone y repositorios simulados; no arrancan el
contexto de Spring Boot, la conexion PostgreSQL ni los servicios externos.
