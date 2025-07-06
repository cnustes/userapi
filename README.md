# 🧪 UserAPI

API REST para el registro de usuarios con autenticación mediante JWT, almacenamiento en base de datos y envío de eventos a Kafka.

## 🚀 Tecnologías

- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA + H2 (o PostgreSQL/MySQL)
- Apache Kafka
- Docker / Docker Compose
- Swagger OpenAPI 3

---

## 🔧 Requisitos previos

- Java 17+
- Maven 3+
- Docker y Docker Compose
- Insomnia, Postman o similar

---

## ⚙️ Levantar el entorno

### 1. Clonar el repositorio

```bash
git clone https://github.com/cnustes/userapi.git
cd userapi
```

### 2. Levantar infraestructura con Docker

```bash
docker-compose up -d
```

Esto levanta:

- Kafka en `localhost:9092`
- Zookeeper
- PostgreSQL (si estás usando persistencia con DB real)

> **Nota**: asegúrate que los puertos no estén ocupados.

### 3. Compilar el proyecto

```bash
./mvnw clean package
```

### 4. Correr la aplicación

```bash
./mvnw spring-boot:run
```

La API quedará disponible en:  
📍 `http://localhost:8080`

---

## 🔐 Probar endpoints con JWT

### 1. Registrar un usuario

**POST** `http://localhost:8080/api/users/register`

```json
{
  "name": "Camilo",
  "email": "camilo@example.com",
  "password": "123456",
  "phones": [
    {
      "number": "123456789",
      "cityCode": "57",
      "countryCode": "1"
    }
  ]
}
```

🔁 Esto retornará un `token` en el JSON de respuesta:

```json
{
  "id": "abc123",
  "name": "Camilo",
  "email": "camilo@example.com",
  "token": "eyJhbGc..."
}
```

### 2. Acceder a endpoint protegido

**GET** `http://localhost:8080/api/secure/test`

📥 Enviar el token en la cabecera:

```
Authorization: Bearer eyJhbGc...
```

🔁 Respuesta:

```json
Hola camilo@example.com, tu token es válido.
```

---

## 🐙 Kafka

Cada vez que un usuario se registra, se envía un evento a Kafka en el topic `user-events`.

Puedes monitorear usando alguna UI para Kafka como [Kafka Tool](https://www.kafkatool.com/) o `kafkacat`.

---

## 📚 Documentación Swagger

Disponible en:

```text
http://localhost:8080/swagger-ui.html
```

o

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 Ejecutar pruebas

```bash
./mvnw test
```

---

## 📁 Estructura del proyecto

```bash
com.example.userapi
├── config                 # Configuraciones de seguridad
├── controllers            # Controladores REST
├── dto                    # Request/Response DTOs
├── models                 # Entidades JPA
├── repositories           # Interfaces de acceso a datos
├── security               # Filtro y utilidades JWT
├── services               # Lógica de negocio
└── kafka                  # Productor Kafka
```

---

## 🙋‍♂️ Autor

Camilo Ñustes – [@cnustes](https://github.com/cnustes)

---

## 📄 Licencia

MIT