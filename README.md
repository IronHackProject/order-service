# Oder Service

Este microservicio forma parte del sistema de eCommerce distribuido y se encarga de gestionar la creación de Tickets 
de los usuarios.

## 📦 Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- MySQL
- Feign Client
- Eureka Client
- Lombok

## 💾 Base de datos
Este servicio utiliza una base de datos MySQL. Las entidades principales son:
- `Order ` (id, userId, orderDate, totalAmount,OrderItems).
- `OrderItem` (id, orderId, productId, quantity, total_price,user_id).
- Imagen de la base de datos:  
<br/>
![Imagen](/src/main/resources/static/img/imgBbdd.png)  

## ☎️ Comunicación a otros  servicios
Este servicio es **cliente de Eureka** y se comunica con otros microservicios mediante **Feign Clients**.
- llamada al servicio de `User` para obtener información del usuario que realiza la compra, comprueba que el cliente 
  exista en la bbdd de `User`.
- llamada al servicio de `Product` para obtener información de los productos comprados, comprueba que el producto 
  exista, que haya suficiente stock, y  actualiza el Stock.

## ⚙️ Endpoints principales
-Orders Controller

| Método | Ruta                           | Descripción               |
|--------|--------------------------------|---------------------------|
| POST   | /api/orders                    | Crear un nueva Orden      |
| GET    | /api/orders/findAll            | Retorna todas las Ordenes |
| PUT    | /api/orders/updateorder/{id}   | Modifica una Orden        |
| DELETE | /api/orders/delete/{id}        | Eliminar una Orden        |

-OrderItem Controller

| Método | Ruta                            | Descripción                       |
|--------|---------------------------------|-----------------------------------|
| POST   | /api/order/item/{orderId}/items | Crear un nueva OrdenItem          |
| GET    | /api/order/findbyid/{id}        | Retorna todas la ordenItem por id |
| UPDATE  | /api/order/update/{id}         | Modifica una OrdenItem por id     |
| DELETE | /api/order/delete/{id}          | Eliminar una OrdenItem por id     |


## 🧪 Tests

Incluye:

- Pruebas unitarias con JUnit y Mockito.
- Validaciones de endpoints con MockMvc.
- Pruebas de integración con @SpringBootTest.
- Ejecuta los tests con Maven:
  ```bash
  mvn test
  ```

## 📫 Instrucciones para ejecutar el servicio
1. Clona el repositorio.
2. Configura el archivo `application.properties`  con los datos de conexión a la base de datos MySQL.
3. Asegúrate de que el servidor Eureka esté corriendo.
4. Asegúrate que el Gateway esté corriendo.
5. Asegúrate de que los demás micro servicios están corriendo.
5. Asegúrate de que la base de datos MySQL esté corriendo.
6. Ejecuta el servicio con tu IDE o usando Maven:
   ```bash
   mvn spring-boot:run
   ```



## 🧪 Postman

- Usa Postman o cualquier cliente HTTP para probar los endpoints, Puedes acceder a las colección en el repositorio
  Principal de la apilcacion `.github/static/e-commerce.postman_collection.json`.
- Tienes un Json para crear Odenes en Postman `src/main/resources/Json/newOrdes.json`.

## 🗂️ Repositorio Principal
- 🔗 [GitHub Organization](https://github.com/IronHackProject)

## 👨‍💻 Autor
-[DevJerryX](https://github.com/planetWeb252)