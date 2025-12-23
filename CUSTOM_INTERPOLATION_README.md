# Custom Interpolation Curves - BBS Mod

## 📋 Descripción

Esta funcionalidad agrega **interpolaciones personalizables** al mod BBS, similar al sistema de curvas de After Effects. Permite a los usuarios crear, editar y guardar sus propias curvas de interpolación personalizadas usando puntos de control editables visualmente.

## ✨ Características

### 1. **Editor Visual de Curvas**
- Interfaz gráfica intuitiva de 300x300px
- Sistema de puntos de control arrastrables
- Vista previa en tiempo real de la curva
- Grid de referencia para mayor precisión

### 2. **Gestión de Puntos**
- **Agregar puntos**: Click en el botón "Add Point"
- **Eliminar puntos**: Selecciona un punto y click en "Remove Point"
- **Mover puntos**: Arrastra los puntos con el mouse
- **Mínimo**: 2 puntos (inicio y fin)
- Los puntos se ordenan automáticamente por posición X

### 3. **Guardado Persistente**
- Las curvas se guardan automáticamente en: `config/bbs/custom_curves/`
- Formato JSON para fácil edición manual
- Se cargan automáticamente al iniciar Minecraft
- Se pueden compartir copiando los archivos JSON

### 4. **Integración con el Sistema Existente**
- Aparecen junto a las interpolaciones predefinidas (Linear, Hermite, etc.)
- Compatible con keyframes de cámara, actores, y todos los sistemas que usan interpolaciones
- Se pueden copiar/pegar usando el sistema de clipboard de BBS

## 🎮 Cómo Usar

### Crear una Nueva Curva Personalizada

1. **Abrir el editor de keyframes** (cámara, actor, etc.)
2. **Click derecho en un keyframe** para abrir el menú de interpolaciones
3. **Click en el botón verde "+"** en la parte inferior del menú
4. Se abrirá el **Custom Curve Editor**

### En el Editor de Curvas

```
┌─────────────────────────────────────┐
│  [Nombre de la curva]               │
├─────────────────────────────────────┤
│                                     │
│         GRÁFICO INTERACTIVO         │
│                                     │
│   ● ────────── ●                    │
│                                     │
├─────────────────────────────────────┤
│ [Add Point] [Remove Point] [Reset]  │
│ [Save]           [Close]            │
└─────────────────────────────────────┘
```

**Controles:**
- **Click en un punto rojo**: Seleccionarlo
- **Arrastrar punto**: Moverlo en el gráfico
- **Add Point**: Agrega un nuevo punto en el centro
- **Remove Point**: Elimina el punto seleccionado
- **Reset**: Vuelve a curva lineal básica (2 puntos)
- **Save**: Guarda la curva y la agrega al sistema
- **Close**: Cierra sin guardar cambios

### Editar una Curva Existente

1. Selecciona la interpolación custom en el menú
2. Click en el botón "+" nuevamente
3. El editor se abrirá con la curva actual cargada
4. Modifica y guarda

## 🔧 Estructura Técnica

### Archivos Creados

```
src/main/java/mchorse/bbs_mod/utils/interps/
├── types/
│   └── CustomInterp.java          # Clase de interpolación custom
└── CustomCurveManager.java        # Gestor de curvas

src/client/java/mchorse/bbs_mod/ui/framework/elements/overlay/
└── UICustomCurveEditor.java       # Editor visual

config/bbs/custom_curves/
└── [nombre_curva].json            # Archivos de curvas guardadas
```

### Formato JSON de Curva

```json
{
  "key": "my_custom_curve",
  "name": "My Custom Curve",
  "points": [
    {"x": 0.0, "y": 0.0},
    {"x": 0.3, "y": 0.7},
    {"x": 0.7, "y": 0.3},
    {"x": 1.0, "y": 1.0}
  ]
}
```

### Algoritmo de Interpolación

- Entre puntos adyacentes: **Cubic Hermite** (suave)
- Con puntos vecinos: usa 4 puntos para mayor suavidad
- Sin vecinos: **Linear interpolation**
- Los valores X e Y están normalizados entre 0.0 y 1.0

## 📝 Notas Importantes

### Restricciones
- El **primer punto** (x=0) no se puede mover en X
- El **último punto** (x=1) no se puede mover en X
- Ambos pueden moverse libremente en Y
- Mínimo 2 puntos, sin límite máximo
- Valores restringidos a [0.0, 1.0]

### Compatibilidad
- ✅ Compatible con todos los sistemas de keyframes existentes
- ✅ Se serializa/deserializa automáticamente
- ✅ Funciona con el sistema de copy/paste de interpolaciones
- ✅ Compatible con presets y exportación de films

### Localización
Se agregaron traducciones en:
- Inglés (`en_us.json`)
- Español (`es_es.json`)

Keys agregadas:
```
bbs.ui.custom_curve.editor_title
bbs.ui.custom_curve.add_point
bbs.ui.custom_curve.remove_point
bbs.ui.custom_curve.reset
bbs.ui.custom_curve.create_new
bbs.ui.custom_curve.edit
bbs.ui.custom_curve.delete
bbs.ui.custom_curve.manage
```

## 🎨 Casos de Uso

### Bounce Personalizado
```
Crea una curva con múltiples picos para simular
rebotes con control total sobre cada rebote
```

### Ease Custom
```
Define tu propio ease-in/ease-out que se ajuste
perfectamente a tu animación específica
```

### Curvas Asimétricas
```
Crea aceleraciones/desaceleraciones que no son
posibles con las interpolaciones predefinidas
```

## 🐛 Debugging

Si una curva no carga:
1. Verifica el JSON en `config/bbs/custom_curves/`
2. Asegúrate que tenga al menos 2 puntos
3. Verifica que los valores X e Y estén entre 0.0 y 1.0
4. Los puntos deben estar ordenados por X

Para recargar curvas sin reiniciar:
```java
CustomCurveManager.getInstance().reload();
```

## 🚀 Futuras Mejoras Posibles

- [ ] Sistema de presets (ease-in, ease-out, bounce, etc.)
- [ ] Importar/exportar curvas individualmente
- [ ] Bezier handles para control más preciso
- [ ] Copiar curvas entre diferentes keyframes
- [ ] Librería compartida de curvas de la comunidad
- [ ] Preview animado de la interpolación

## 📄 Licencia

Esta funcionalidad se integra con BBS mod y sigue la misma licencia del proyecto principal.

---

**Autor**: Sistema de Interpolación Custom para BBS
**Fecha**: Diciembre 2025
**Versión**: 1.0.0
