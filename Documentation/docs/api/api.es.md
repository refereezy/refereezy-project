# Documentación de Autenticación

## Autenticación de la API

Este documento describe cómo autenticarse con la API de Refereezy utilizando varios métodos de autenticación.

## Métodos de Autenticación Disponibles

La API admite los siguientes métodos de autenticación:

1. **Autenticación basada en Token JWT** - Método principal para aplicaciones
2. **Autenticación por Clave API** - Para comunicación servidor a servidor
3. **Autenticación basada en Sesión** - Para aplicaciones web

## Flujo de Autenticación

```mermaid
sequenceDiagram
    participant Cliente
    participant API
    participant BaseDatos
    Cliente->>API: POST /auth/login (credenciales)
    API->>BaseDatos: Verificar credenciales
    BaseDatos-->>API: Credenciales válidas
    API->>API: Generar token JWT
    API-->>Cliente: Devolver token y token de actualización
    Note over Cliente,API: Para solicitudes posteriores
    Cliente->>API: Solicitud con token en cabecera
    API->>API: Validar token
    API-->>Cliente: Respuesta
```

## Autenticación JWT

### Obtención de un Token

Para obtener un token JWT, envíe una solicitud POST al endpoint de inicio de sesión:

```
POST /auth/login
```

**Solicitud:**
```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseñasegura"
}
```

**Respuesta:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "expires_in": 3600
}
```

### Uso del Token

Incluya el token JWT en la cabecera de Autorización:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Actualización del Token

Cuando el token de acceso caduque, utilice el token de actualización para obtener uno nuevo:

```
POST /auth/refresh
```

**Solicitud:**
```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Respuesta:**
```json
{
  "access_token": "neweyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "neweyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "expires_in": 3600
}
```

## Autenticación por Clave API

### Obtención de una Clave API

Las claves API se generan a través de la interfaz administrativa. Contacte con el administrador del sistema para solicitar una clave API.

### Uso de la Clave API

Incluya la clave API en la cabecera de la solicitud:

```
X-API-Key: su-clave-api-aquí
```

## Manejo de Errores

### Errores Comunes de Autenticación

- **401 No Autorizado** - Credenciales o token inválidos
- **403 Prohibido** - Autenticación válida pero permisos insuficientes
- **422 Entidad No Procesable** - Solicitud mal formada

### Formato de Respuesta de Error

```json
{
  "status": "error",
  "code": 401,
  "message": "Token inválido o caducado",
  "details": "El token proporcionado ha caducado. Por favor, obtenga un nuevo token."
}
```

## Consideraciones de Seguridad

1. **Almacenamiento de Tokens**
   - Almacene los tokens de forma segura (p. ej., cookies HttpOnly)
   - Nunca almacene en localStorage o sessionStorage

2. **Caducidad de Tokens**
   - Tokens de acceso de corta duración (1 hora)
   - Tokens de actualización de mayor duración (2 semanas)

3. **Seguridad de Transporte**
   - Utilice siempre HTTPS
   - Imponga TLS 1.2 o superior

## Ejemplos de Código

### Autenticación en JavaScript

```javascript
// Iniciar sesión y obtener token
async function iniciarSesion(email, password) {
  const response = await fetch('https://api.refereezy.com/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password })
  });
  
  if (!response.ok) {
    throw new Error('Autenticación fallida');
  }
  
  const data = await response.json();
  return data.access_token;
}

// Usar token para solicitudes a la API
async function obtenerPartidos(token) {
  const response = await fetch('https://api.refereezy.com/matches', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return response.json();
}
```

### Autenticación en Python

```python
import requests

def iniciar_sesion(email, password):
    response = requests.post(
        'https://api.refereezy.com/auth/login',
        json={'email': email, 'password': password}
    )
    
    if response.status_code != 200:
        raise Exception('Autenticación fallida')
        
    data = response.json()
    return data['access_token']

def obtener_partidos(token):
    response = requests.get(
        'https://api.refereezy.com/matches',
        headers={'Authorization': f'Bearer {token}'}
    )
    
    return response.json()
```

### Autenticación en Aplicaciones Móviles

```swift
// Ejemplo en Swift para iOS
func iniciarSesion(email: String, password: String, completion: @escaping (Result<String, Error>) -> Void) {
    let url = URL(string: "https://api.refereezy.com/auth/login")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let body: [String: String] = ["email": email, "password": password]
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)
    
    URLSession.shared.dataTask(with: request) { data, response, error in
        // Manejar respuesta y extraer token
    }.resume()
}
```

## Control de Acceso Basado en Roles

La API implementa acceso basado en roles para restringir el acceso a ciertos endpoints:

| Rol | Descripción | Nivel de Acceso |
|-----|-------------|-----------------|
| admin | Administrador del sistema | Acceso completo a todos los endpoints |
| referee | Árbitro de partido | Acceso a gestión de partidos e informes |
| user | Usuario regular | Acceso limitado a sus propios datos e información pública |

---

*Nota para los colaboradores de la documentación: Añada información detallada sobre la estructura de carga útil del token, mejores prácticas de seguridad y ejemplos de integración para varias plataformas.*
