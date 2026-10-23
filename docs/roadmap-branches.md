# Roadmaps con bifurcaciones

El cambio agrega `roadmap_graphs` y `roadmap_connections`. No agrega columnas a las tablas existentes ni reescribe sus filas. Cada conexion une dos pasos del mismo roadmap; se permiten ramas y convergencias, pero no ciclos, duplicados ni autorreferencias.

Los roadmaps anteriores se leen con las conexiones lineales calculadas por `orderIndex` y luego por `id`, sin escribir en la BD. Al editar sus conexiones por primera vez, se persiste el grafo completo. Un grafo guardado sin conexiones sigue vacio: no se regenera una cadena. Los pasos nuevos quedan independientes hasta conectarlos. Al borrar un paso se eliminan solo sus conexiones y no se unen automaticamente sus vecinos.

## Despliegue en produccion

1. Respaldar y ensayar en staging con una copia reciente. Revisar las consultas de `sql/diagnose-roadmap-save.sql` y la restriccion exacta del log del 409. El codigo 409 anterior agrupaba errores diferentes; no prueba que un nombre este duplicado.
2. Ejecutar `sql/add-roadmap-branches.sql` antes del backend. Solo crea tablas e indices nuevos. El timeout de bloqueo aborta si no puede adquirir los bloqueos pronto; ejecutar en un periodo de baja carga. No se ha ejecutado este script desde el asistente.
3. Comprobar que `roadmap_steps.description` sea TEXT. Si aun es VARCHAR(255), revisar y programar `sql/roadmap-step-description-text.sql` por separado: ALTER TABLE requiere un bloqueo sobre la tabla existente. No quitar restricciones ni cambiar secuencias sin identificar el error real.
4. Desplegar backend y luego frontend. En produccion es preferible aplicar el DDL revisado y usar `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` en vez de delegar cambios de esquema a `update`.
5. Verificar generacion, guardado con varios pasos, bifurcaciones, eliminacion de conexiones, permisos y rechazo de ciclos. El reemplazo de conexiones usa la revision y bloquea el roadmap para detectar ediciones simultaneas.

Para revertir la aplicacion, conservar las tablas nuevas y sus datos; una version anterior solo visualizara el flujo lineal y no respetara bifurcaciones. Evitar editar esos mapas con una version antigua. No es necesario borrar las tablas para revertir el despliegue.

## Diagnostico del guardado

El handler registra SQLSTATE y el detalle completo de la restriccion, sin exponer SQL o datos de usuario al cliente. `22001` indica contenido demasiado largo; `23514`, una restriccion CHECK; `23505`, unicidad; y `23503`, una referencia inexistente. Revisar el log y el esquema real de produccion antes de corregir el DDL. El frontend conserva la vista previa si falla el guardado.
