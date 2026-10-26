# Panel de administrador

Acceso: rol ADMINISTRADOR. Las rutas /admin/** exigen el rol en Spring Security y en AdminController. El frontend protege /admin/:section y muestra el grupo del sidebar solo al administrador.

## Recursos

- /admin/users: GET paginado y POST; /{id}: PUT y DELETE (desactivacion). PUT permite reactivar. Contrasena opcional en PUT; null conserva el hash. No se puede desactivar ni degradar la propia cuenta.
- /admin/roadmaps: GET paginado y POST; /{id}: PUT y DELETE, incluyendo roadmaps de otros propietarios. Se editan titulo, descripcion, categoria y visibilidad; se conserva el propietario.
- /admin/posts: GET paginado y POST; /{id}: PUT y DELETE. El DTO incluye contenido, autor, categorias, me gusta y vistas; no devuelve respuestas. Se conserva el autor al editar.
- /admin/questions: GET paginado y POST; /{id}: PUT y DELETE.
- /admin/questions/{id}/options: POST. /admin/options/{id}: PUT y DELETE.

Los formularios usan validacion del servidor. Los puntajes son enteros entre 0 y 100. Al eliminar alternativas se desvinculan las respuestas historicas, conservando los resultados y rankings ya calculados.

El frontend obtiene todas las paginas de los listados para combinar filtros globales y ordenacion por metricas antes de paginar de diez en diez. Para grandes volumenes conviene trasladar estas agregaciones y filtros al servidor.

## Seguridad de cuentas

El registro publico siempre crea ESTUDIANTE, aunque se envie otro rol. Un estudiante solo puede modificar su propio perfil y no puede enviar cambios de rol. Una cuenta inactiva no puede iniciar sesion ni reutilizar su cookie existente.

No se crea un administrador predeterminado ni se cambian roles existentes. Se necesita una cuenta ADMINISTRADOR ya provisionada para acceder.

## Verificacion

Frontend: ESLint, build y adminModel.test.mjs. Backend: AdminPersistenceTest y AdminSecurityBoundaryTest, junto con las pruebas de regresion de roadmaps.
