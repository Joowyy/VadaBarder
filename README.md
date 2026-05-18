<div align="center">

# ✂️ VadaBarber

**Aplicación Android de reserva de citas para barbería**

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth_%26_Firestore-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Min SDK](https://img.shields.io/badge/Min_SDK-24_(Android_7)-blue?style=flat-square)
![Target SDK](https://img.shields.io/badge/Target_SDK-36-blue?style=flat-square)
![License](https://img.shields.io/badge/Licencia-Académico-lightgrey?style=flat-square)

*Proyecto Intermodular · Ciclo DAM · 2025 – 2026*

</div>

---

## Tabla de contenidos

- [Descripción](#descripción)
- [Funcionalidades](#funcionalidades)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Modelo de datos Firestore](#modelo-de-datos-firestore)
- [Reglas de seguridad](#reglas-de-seguridad-firestore)
- [Internacionalización](#internacionalización)
- [Notificaciones](#notificaciones)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Autores](#autores)

---

## Descripción

**VadaBarber** es una aplicación Android nativa desarrollada en Kotlin que permite a los clientes de una barbería gestionar sus citas de forma cómoda desde el móvil. El usuario puede registrarse, consultar el horario y los servicios disponibles, reservar una franja horaria y recibir recordatorios automáticos antes de su cita.

El proyecto está diseñado como una solución **white-label**: el logo, nombre, servicios, precios y horarios son fácilmente intercambiables para adaptar la app a cualquier barbería cliente.

---

## Funcionalidades

### Autenticación
- Registro con nombre de usuario, correo y contraseña (Firebase Auth)
- Inicio de sesión con opción *Recordar usuario* (persistida en `SharedPreferences`)
- Cierre de sesión con confirmación

### Pantalla de inicio
- Saludo personalizado con el nombre del usuario
- Horario semanal expandible (lunes–viernes, sábado, domingo cerrado)
- Tarjeta de **Citas Pendientes** con cuenta atrás en tiempo real (*Mañana*, *En ~3h*, *En menos de 1h*…)
- Sección de novedades
- Galería horizontal de cortes de ejemplo
- Tabla de servicios populares con precios
- Acceso directo a llamar y a abrir la ubicación en Google Maps

### Nueva cita
- Selector de fecha con `CalendarView` (días pasados bloqueados)
- Chips de horario dinamicos según el día de la semana; domingos ocultos automáticamente
- Horas ya reservadas bloqueadas en tiempo real mediante Firestore Snapshot Listener
- Selección de hasta **2 servicios** simultáneos con resumen de precio expandible
- Transacción atómica en Firestore: si dos usuarios eligen el mismo hueco a la vez, el segundo recibe un aviso y el primero confirma

### Perfil
- Datos del usuario (nombre y correo)
- Tarjeta de citas pendientes (igual que el inicio)
- Historial completo de citas en `RecyclerView`
- Selector de idioma (ver [Internacionalización](#internacionalización))
- Cerrar sesión

### Notificaciones
- Recordatorio automático **24 h antes** de la cita
- Recordatorio automático **1 h antes** de la cita
- Gestionadas con WorkManager (sobreviven al reinicio del dispositivo)

---

## Arquitectura

El proyecto sigue el patrón **MVVM + Repository** recomendado por Google para aplicaciones Android modernas:

```
Fragment (UI)
    │  observa LiveData
    ▼
ViewModel  (activityViewModels — scope de Activity)
    │  llama métodos del repositorio
    ▼
Repository (única puerta a Firebase)
    │  registra SnapshotListeners
    ▼
Firebase Auth / Firestore
```

### Detalles clave de implementación

| Elemento | Solución |
|---|---|
| Navegación entre pantallas | Navigation Component (grafo `nav_graph.xml`) |
| Estado de autenticación | `AuthState<T>` sealed class (`Loading`, `Success`, `Error`) |
| Reactivo a cambios de usuario | `switchMap` en `CitaViewModel` — el listener de Firestore se recrea solo si cambia el UID |
| Bloqueo de huecos ocupados | `SnapshotListener` en `observarHorasOcupadas(fecha)` → actualiza chips en tiempo real |
| Concurrencia en reservas | `runTransaction` de Firestore — aborta si el documento ya existe |
| Idioma de la app | `LocaleHelper.wrap(context)` en `attachBaseContext` de `MainActivity` |

---

## Tecnologías

| Tecnología | Uso |
|---|---|
| **Kotlin** | Lenguaje principal |
| **Firebase Authentication** | Registro e inicio de sesión con email/contraseña |
| **Cloud Firestore** | Almacenamiento y sincronización en tiempo real de citas |
| **Navigation Component** | Navegación entre fragments y back stack |
| **ViewModel + LiveData** | Gestión de estado reactivo y ciclo de vida |
| **ViewBinding** | Acceso seguro a vistas del layout |
| **WorkManager** | Recordatorios programados que sobreviven al proceso death |
| **Material Design 3** | Componentes visuales (`MaterialCardView`, `Chip`, `MaterialButton`…) |
| **ConstraintLayout** | Layouts principales de cada fragment |

---

## Estructura del proyecto

```
app/src/main/
├── java/com/example/vadabarder/
│   ├── data/
│   │   ├── model/
│   │   │   ├── Cita.kt              — data class mapeada a/desde Firestore
│   │   │   └── AuthState.kt         — sealed class: Loading | Success | Error
│   │   ├── repository/
│   │   │   ├── CitaFirestoreRepository.kt   — CRUD + SnapshotListeners de reservas
│   │   │   └── AuthRepository.kt            — registro, login y logout con Firebase Auth
│   │   ├── prefs/
│   │   │   └── SessionPreferences.kt        — checkbox "Recordar usuario"
│   │   └── BarberiaData.kt          — servicios, precios, horario y resolver i18n
│   │
│   ├── viewmodel/
│   │   ├── CitaViewModel.kt         — citas (switchMap), horasOcupadas, insertar, WorkManager
│   │   └── AuthViewModel.kt         — currentUser LiveData, login/registro/logout
│   │
│   ├── ui/
│   │   ├── main/
│   │   │   └── MainActivity.kt      — BottomNavigationView + NavHostFragment + LocaleHelper
│   │   ├── register/
│   │   │   ├── LoginFragment.kt
│   │   │   └── RegistroFragment.kt
│   │   ├── home/
│   │   │   └── HomeFragment.kt      — horario, servicios, citas pendientes, galería
│   │   ├── add/
│   │   │   └── AddFragment.kt       — CalendarView + chips de hora/servicio + reserva
│   │   └── profile/
│   │       ├── ProfileFragment.kt   — datos usuario, citas pendientes, historial, idioma
│   │       └── HistorialAdapter.kt  — RecyclerView de todas las citas
│   │
│   ├── notifications/
│   │   ├── CitaReminderWorker.kt    — Worker de WorkManager para los recordatorios
│   │   └── NotificationHelper.kt   — construcción y envío de notificaciones
│   │
│   └── utils/
│       └── LocaleHelper.kt          — guarda/carga locale en SharedPrefs y lo aplica al Context
│
└── res/
    ├── layout/                      — fragment_*.xml, item_citas.xml
    ├── menu/menu_bottom.xml         — BottomNavigationView
    ├── navigation/nav_graph.xml     — grafo de navegación
    ├── values/strings.xml           — ES (por defecto)
    ├── values-en/strings.xml        — EN
    ├── values-fr/strings.xml        — FR
    ├── font/grotesk.ttf
    └── drawable/                    — ic_logo_vadabarder.xml, corte1–3, iconos
```

---

## Modelo de datos Firestore

Las citas viven en una **colección global** `reservas/` en lugar de subcolecciones por usuario. Esto permite que todos los usuarios autenticados puedan consultar qué huecos están ocupados sin escanear la colección entera.

### Documento `reservas/{id}`

```
id        : "18-05-2026_12:30"   ← determinista: fecha-con-guiones_hora
userId    : "uid_firebase_auth"
fecha     : "18/05/2026"         ← formato display dd/MM/yyyy
hora      : "12:30"
servicio  : "servicio_corte_clasico||servicio_barba"  ← entry-names separados por ||
precio    : "20€"
```

El `id` es determinista (`dd-MM-yyyy_HH:mm`): si dos usuarios intentan crear el mismo documento simultáneamente, la `runTransaction` de Firestore aborta la segunda escritura.

### Ordenación

La ordenación cronológica se realiza **en el cliente** (no con `orderBy` en Firestore) para evitar la necesidad de un índice compuesto:

```kotlin
.sortedBy { sdf.parse("${it.fecha} ${it.hora}")?.time ?: 0L }
```

---

## Reglas de seguridad Firestore

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /reservas/{id} {
      allow read:   if request.auth != null;
      allow create: if request.auth != null
                    && request.auth.uid == request.resource.data.userId;
      allow delete: if request.auth != null
                    && request.auth.uid == resource.data.userId;
    }
  }
}
```

- Cualquier usuario autenticado puede **leer** todas las reservas (necesario para bloquear huecos ocupados).
- Solo el propietario puede **crear** o **eliminar** su propia reserva.

---

## Internacionalización

La app soporta tres idiomas seleccionables desde el perfil del usuario:

| Código | Idioma |
|---|---|
| `es` | Español (por defecto) |
| `en` | English |
| `fr` | Français |

El locale se persiste en `SharedPreferences` mediante `LocaleHelper` y se aplica en `attachBaseContext` de `MainActivity`. Al cambiar de idioma, la Activity se reinicia con `FLAG_ACTIVITY_CLEAR_TASK` para aplicar los nuevos recursos.

Los nombres de los servicios guardados en Firestore se almacenan como **entry-names canónicos** (`servicio_corte_clasico`, `servicio_fade`…) para ser independientes del idioma. `BarberiaData.resolverServicio()` los traduce al idioma activo en tiempo de presentación.

---

## Notificaciones

Al crear una cita se programan dos `OneTimeWorkRequest` con WorkManager:

| Worker tag | Cuándo se lanza |
|---|---|
| `vada_{id}_24h` | 24 horas antes de la cita |
| `vada_{id}_1h` | 1 hora antes de la cita |

El `CitaReminderWorker` obtiene el locale guardado del usuario mediante `LocaleHelper.wrap(applicationContext)` para que la notificación aparezca en el idioma correcto aunque el sistema haya cambiado.

Al eliminar una cita, ambos workers se cancelan con `cancelAllWorkByTag`.

---

## Configuración y ejecución

### Prerrequisitos

- Android Studio Hedgehog o superior
- JDK 11
- Cuenta en [Firebase Console](https://console.firebase.google.com)

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/Joowyy/VadaBarder.git
   cd VadaBarder
   ```

2. **Conecta Firebase**
   - Crea un proyecto en Firebase Console
   - Activa **Authentication** → método *Correo/Contraseña*
   - Activa **Cloud Firestore** en modo producción
   - Descarga `google-services.json` y colócalo en `app/`

3. **Despliega las reglas de Firestore**
   - Copia las reglas de la sección [Reglas de seguridad](#reglas-de-seguridad-firestore) en la consola

4. **Compila y ejecuta**
   ```bash
   ./gradlew assembleDebug
   ```
   O abre el proyecto en Android Studio y pulsa **Run**.

> **Nota:** La primera vez que se carguen citas en un dispositivo real, Logcat puede mostrar un enlace para crear un índice en Firestore si se añade `orderBy` a alguna consulta futura. Con la configuración actual (ordenación en cliente) no es necesario.

---

## Autores

| Nombre | GitHub |
|---|---|
| Joel Sánchez Fernández | [@Joowyy](https://github.com/Joowyy) |

---

<div align="center">

*Proyecto académico desarrollado para el Proyecto Intermodular del Ciclo Superior de Desarrollo de Aplicaciones Multiplataforma (DAM) · 2025–2026*

</div>
