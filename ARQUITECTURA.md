# Arquitectura Hexagonal - Apluz Backend

## 📐 Introducción

Este documento describe la implementación de la arquitectura hexagonal (también conocida como Ports and Adapters) en el proyecto Apluz Backend.

## 🎯 Principios de Arquitectura Hexagonal

La arquitectura hexagonal fue propuesta por Alistair Cockburn y se basa en los siguientes principios:

1. **Separación de concerns**: El dominio de negocio está aislado de la infraestructura
2. **Independencia de frameworks**: La lógica de negocio no depende de frameworks externos
3. **Testabilidad**: Facilita el testing al permitir cambiar adaptadores fácilmente
4. **Flexibilidad**: Permite cambiar implementaciones sin afectar el dominio

## 🏛️ Capas de la Arquitectura

### 1. Dominio (Core/Hexágono)

**Ubicación**: `com.apluz.backend.domain`

Es el corazón de la aplicación. Contiene:

#### Entidades (`domain/model`)
- `Property.java`: Entidad principal que representa una propiedad inmobiliaria
- `PropertyType.java`: Enum con tipos de propiedades
- `PropertyStatus.java`: Enum con estados de propiedades

#### Puertos (`domain/port`)
- `PropertyRepository.java`: Interfaz que define el contrato de persistencia

**Características**:
- ✅ NO depende de ninguna otra capa
- ✅ NO contiene anotaciones de Spring
- ✅ Contiene la lógica de negocio pura
- ✅ Es completamente independiente de la infraestructura

### 2. Aplicación (Application)

**Ubicación**: `com.apluz.backend.application`

Contiene los casos de uso y orquesta el dominio:

#### Servicios (`application/service`)
- `PropertyService.java`: Orquesta las operaciones sobre propiedades
  - Coordina llamadas al repositorio
  - Aplica reglas de negocio
  - Gestiona transacciones

**Características**:
- ✅ Depende solo del dominio
- ✅ Contiene lógica de aplicación (casos de uso)
- ✅ Usa anotaciones de Spring (@Service)
- ✅ Define qué hace la aplicación, no cómo

### 3. Infraestructura (Infrastructure)

**Ubicación**: `com.apluz.backend.infrastructure`

Contiene los adaptadores que conectan el dominio con el mundo exterior:

#### Adaptador de Persistencia (`infrastructure/adapter/persistence`)
- `JdbcPropertyRepository.java`: Implementación JDBC del puerto PropertyRepository
  - Implementa la interfaz del dominio
  - Maneja la conexión con PostgreSQL
  - Traduce entre objetos de dominio y tablas SQL

#### Adaptador Web (`infrastructure/adapter/web`)
- `PropertyController.java`: API REST para propiedades
  - Expone endpoints HTTP
  - Maneja peticiones y respuestas
  - Delega lógica al servicio de aplicación

**DTOs** (`infrastructure/adapter/web/dto`):
- `PropertyRequest.java`: DTO de entrada
- `PropertyResponse.java`: DTO de salida
- `PropertyStatusUpdateRequest.java`: DTO para actualización de estado

**Características**:
- ✅ Depende de aplicación y dominio
- ✅ Contiene implementaciones concretas
- ✅ Usa frameworks (Spring, JDBC, etc.)
- ✅ Es intercambiable sin afectar el dominio

## 🔄 Flujo de Datos

```
Cliente HTTP
    ↓
PropertyController (Infraestructura/Web)
    ↓
PropertyService (Aplicación)
    ↓
PropertyRepository (Puerto/Dominio)
    ↓
JdbcPropertyRepository (Infraestructura/Persistencia)
    ↓
PostgreSQL
```

### Ejemplo de Flujo: Crear una Propiedad

1. **Cliente** envía POST `/api/properties` con JSON
2. **PropertyController** recibe `PropertyRequest`
3. **PropertyController** convierte DTO a entidad `Property`
4. **PropertyController** llama a `PropertyService.createProperty()`
5. **PropertyService** aplica lógica de negocio (timestamps, estado default)
6. **PropertyService** llama a `PropertyRepository.save()`
7. **JdbcPropertyRepository** ejecuta INSERT SQL
8. **PostgreSQL** persiste los datos
9. **JdbcPropertyRepository** retorna `Property` con ID
10. **PropertyService** retorna `Property`
11. **PropertyController** convierte a `PropertyResponse`
12. **Cliente** recibe JSON response

## 🔌 Puertos y Adaptadores

### Puerto de Salida (Output Port)

**Puerto**: `PropertyRepository` (interface en dominio)

**Adaptador**: `JdbcPropertyRepository` (implementación en infraestructura)

```java
// Puerto (dominio)
public interface PropertyRepository {
    Property save(Property property);
    Optional<Property> findById(Long id);
    // ...
}

// Adaptador (infraestructura)
@Repository
public class JdbcPropertyRepository implements PropertyRepository {
    // Implementación con JDBC
}
```

### Puerto de Entrada (Input Port)

**Puerto**: `PropertyService` (servicio de aplicación)

**Adaptador**: `PropertyController` (REST controller en infraestructura)

```java
// Servicio (aplicación)
@Service
public class PropertyService {
    public Property createProperty(Property property) {
        // Lógica de negocio
    }
}

// Adaptador (infraestructura)
@RestController
public class PropertyController {
    private final PropertyService propertyService;
    
    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@RequestBody PropertyRequest request) {
        Property property = mapToEntity(request);
        Property created = propertyService.createProperty(property);
        return ResponseEntity.ok(mapToResponse(created));
    }
}
```

## 🎨 Beneficios de esta Arquitectura

### 1. Testabilidad

```java
// Test del servicio sin dependencias de infraestructura
@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {
    @Mock
    private PropertyRepository propertyRepository; // Mock del puerto
    
    @InjectMocks
    private PropertyService propertyService;
    
    @Test
    void testCreateProperty() {
        // Test puro sin Spring, sin BD
    }
}
```

### 2. Flexibilidad

Podemos cambiar JDBC por JPA sin tocar dominio ni aplicación:

```java
// Nueva implementación
@Repository
public class JpaPropertyRepository implements PropertyRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Property save(Property property) {
        // Implementación con JPA
    }
}
```

### 3. Independencia

- El dominio **NO** conoce Spring
- El dominio **NO** conoce PostgreSQL
- El dominio **NO** conoce HTTP/REST
- El dominio solo contiene lógica de negocio

### 4. Mantenibilidad

Cada capa tiene una responsabilidad clara:

- **Dominio**: ¿Qué es una propiedad? ¿Qué reglas de negocio tiene?
- **Aplicación**: ¿Qué operaciones puedo hacer con propiedades?
- **Infraestructura**: ¿Cómo persisto? ¿Cómo expongo la API?

## 🚀 Evolución Futura

### Posibles Adaptadores Adicionales

1. **Adaptador de Eventos**
   ```java
   @Component
   public class KafkaPropertyEventPublisher implements PropertyEventPublisher {
       public void publishPropertyCreated(Property property) {
           // Publicar evento a Kafka
       }
   }
   ```

2. **Adaptador de Caché**
   ```java
   @Repository
   public class CachedPropertyRepository implements PropertyRepository {
       private final PropertyRepository delegate;
       private final Cache cache;
       
       @Override
       public Optional<Property> findById(Long id) {
           return cache.get(id, () -> delegate.findById(id));
       }
   }
   ```

3. **Adaptador GraphQL**
   ```java
   @Controller
   public class PropertyGraphQLController {
       private final PropertyService propertyService;
       
       @QueryMapping
       public Property propertyById(@Argument Long id) {
           return propertyService.getPropertyById(id).orElse(null);
       }
   }
   ```

## 📚 Referencias

- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [DDD (Domain-Driven Design)](https://martinfowler.com/bliki/DomainDrivenDesign.html)

## 🎓 Conclusión

La arquitectura hexagonal nos permite:

✅ Mantener el dominio limpio y enfocado en el negocio
✅ Testear fácilmente sin infraestructura
✅ Cambiar implementaciones sin afectar el core
✅ Agregar nuevos adaptadores sin modificar código existente
✅ Escalar y evolucionar la aplicación de forma sostenible

Esta arquitectura es especialmente útil para:
- Aplicaciones empresariales complejas
- Proyectos de larga duración
- Equipos grandes
- Sistemas que necesitan evolucionar constantemente
