# Apluz Backend

Backend para aplicación inmobiliaria desarrollado con Java 21, Spring Boot, PostgreSQL y arquitectura hexagonal.

## 🏗️ Arquitectura

Este proyecto sigue los principios de **Arquitectura Hexagonal** (Ports and Adapters), separando las capas de:

### Estructura del Proyecto

```
com.apluz.backend/
├── domain/                    # Capa de dominio (núcleo del negocio)
│   ├── model/                 # Entidades de dominio
│   │   ├── Property.java
│   │   ├── PropertyType.java
│   │   └── PropertyStatus.java
│   └── port/                  # Interfaces (puertos)
│       └── PropertyRepository.java
│
├── application/               # Capa de aplicación (casos de uso)
│   └── service/
│       └── PropertyService.java
│
└── infrastructure/            # Capa de infraestructura (adaptadores)
    └── adapter/
        ├── persistence/       # Adaptador de persistencia
        │   └── JdbcPropertyRepository.java
        └── web/              # Adaptador web (REST API)
            ├── PropertyController.java
            └── dto/          # Data Transfer Objects
                ├── PropertyRequest.java
                ├── PropertyResponse.java
                └── PropertyStatusUpdateRequest.java
```

## 🚀 Tecnologías

- **Java 21** - Última versión LTS de Java
- **Spring Boot 3.2.0** - Framework de desarrollo
- **Spring JDBC** - Acceso a base de datos con JDBC
- **PostgreSQL** - Base de datos relacional
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking para tests unitarios
- **Maven** - Gestión de dependencias

## 📋 Requisitos Previos

- Java 21 o superior
- Maven 3.8+
- PostgreSQL 12+ o Docker

## 🛠️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd backend
```

### 2. Configurar la base de datos

#### Opción A: Usar Docker Compose (Recomendado)

```bash
docker-compose up -d
```

Esto iniciará PostgreSQL en el puerto 5432 con la base de datos `apluz_db` ya configurada.

#### Opción B: PostgreSQL instalado localmente

Crear la base de datos manualmente:

```sql
CREATE DATABASE apluz_db;
```

Ejecutar el script de schema:

```bash
psql -U postgres -d apluz_db -f src/main/resources/schema.sql
```

### 3. Configurar credenciales (opcional)

Editar `src/main/resources/application.properties` si necesitas cambiar las credenciales:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/apluz_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Compilar el proyecto

```bash
mvn clean install
```

### 5. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 API Endpoints

### Propiedades

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/properties` | Obtener todas las propiedades |
| GET | `/api/properties?city={city}` | Filtrar por ciudad |
| GET | `/api/properties?type={type}` | Filtrar por tipo |
| GET | `/api/properties?status={status}` | Filtrar por estado |
| GET | `/api/properties/{id}` | Obtener propiedad por ID |
| POST | `/api/properties` | Crear nueva propiedad |
| PUT | `/api/properties/{id}` | Actualizar propiedad |
| PATCH | `/api/properties/{id}/status` | Actualizar solo el estado |
| DELETE | `/api/properties/{id}` | Eliminar propiedad |

### Ejemplos de Uso

#### Crear una propiedad

```bash
curl -X POST http://localhost:8080/api/properties \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Casa en el centro",
    "description": "Hermosa casa en zona céntrica",
    "type": "HOUSE",
    "status": "AVAILABLE",
    "price": 250000.00,
    "address": "Calle Principal 123",
    "city": "Madrid",
    "state": "Madrid",
    "zipCode": "28001",
    "area": 150.0,
    "bedrooms": 3,
    "bathrooms": 2,
    "parkingSpaces": 2
  }'
```

#### Obtener todas las propiedades

```bash
curl http://localhost:8080/api/properties
```

#### Filtrar por ciudad

```bash
curl http://localhost:8080/api/properties?city=Madrid
```

#### Actualizar estado de una propiedad

```bash
curl -X PATCH http://localhost:8080/api/properties/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SOLD"}'
```

## 🎯 Modelos de Dominio

### PropertyType (Tipos de Propiedad)
- `HOUSE` - Casa
- `APARTMENT` - Apartamento
- `CONDO` - Condominio
- `TOWNHOUSE` - Casa adosada
- `LAND` - Terreno
- `COMMERCIAL` - Local comercial
- `OFFICE` - Oficina

### PropertyStatus (Estados)
- `AVAILABLE` - Disponible
- `SOLD` - Vendida
- `RENTED` - Alquilada
- `RESERVED` - Reservada
- `INACTIVE` - Inactiva

## 🧪 Testing

### Ejecutar todos los tests

```bash
mvn test
```

### Ejecutar solo tests unitarios

```bash
mvn test -Dtest="*Test"
```

### Ejecutar solo tests de integración

```bash
mvn test -Dtest="*IntegrationTest"
```

### Cobertura de tests

El proyecto incluye:
- **Tests unitarios** para servicios con Mockito
- **Tests de integración** para repositorios JDBC
- **Tests de API** para controllers con MockMvc

## 📦 Estructura de Base de Datos

### Tabla: properties

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único (autoincremental) |
| title | VARCHAR(255) | Título de la propiedad |
| description | TEXT | Descripción detallada |
| type | VARCHAR(50) | Tipo de propiedad |
| status | VARCHAR(50) | Estado actual |
| price | DECIMAL(15,2) | Precio |
| address | VARCHAR(500) | Dirección completa |
| city | VARCHAR(100) | Ciudad |
| state | VARCHAR(100) | Estado/Provincia |
| zip_code | VARCHAR(20) | Código postal |
| area | DOUBLE PRECISION | Área en m² |
| bedrooms | INTEGER | Número de habitaciones |
| bathrooms | INTEGER | Número de baños |
| parking_spaces | INTEGER | Espacios de estacionamiento |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Fecha de última actualización |

## 🔧 Configuración Adicional

### Perfiles de Spring

- **default**: Configuración para desarrollo local
- **test**: Configuración para tests (usa H2 en memoria)

### Variables de Entorno

Puedes sobrescribir la configuración usando variables de entorno:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=apluz_db
export DB_USER=postgres
export DB_PASSWORD=postgres
```

## 📝 Convenciones de Código

- Seguimos las convenciones de código de Java
- Usamos Arquitectura Hexagonal para separación de capas
- Los servicios de aplicación contienen la lógica de negocio
- Los adaptadores son intercambiables (JDBC puede ser reemplazado por JPA, etc.)

## 🐛 Troubleshooting

### Error de conexión a PostgreSQL

Verificar que PostgreSQL esté corriendo:

```bash
docker ps  # Si usas Docker
# o
pg_isctl status  # Si está instalado localmente
```

### Tests fallan

Asegurarte de que el perfil de test esté activo:

```bash
mvn test -Dspring.profiles.active=test
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/amazing-feature`)
3. Commit tus cambios (`git commit -m 'Add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.

## 👥 Autor

Apluz Team - Aplicación Inmobiliaria

## 📞 Contacto

Para preguntas o soporte, contacta con el equipo de desarrollo.
