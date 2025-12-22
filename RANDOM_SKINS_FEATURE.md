# Característica: Aplicar Skins Aleatorias a Replays

## Descripción
Esta nueva característica permite aplicar skins aleatorias desde una carpeta a múltiples replays seleccionados sin repetición (a menos que haya más replays que skins disponibles).

## Cómo Usar

1. **Preparar las Skins:**
   - Crea una carpeta en tu computadora con archivos de skins en formato PNG
   - Ejemplo: `C:\Users\TuUsuario\Documents\skins\`
   - Asegúrate de que todos los archivos sean `.png`

2. **Seleccionar Replays:**
   - En el editor de films, ve a la lista de replays
   - Selecciona uno o más replays (puedes mantener presionado Ctrl/Shift para seleccionar múltiples)

3. **Aplicar Skins Aleatorias:**
   - Haz clic derecho en los replays seleccionados
   - Selecciona la opción **"Apply random skins..."** (o "Aplicar skins aleatorios...")
   - Se abrirá un diálogo pidiendo la ruta de la carpeta

4. **Ingresar Ruta:**
   - Pega la ruta completa de la carpeta que contiene las skins
   - Ejemplo: `C:\Users\TuUsuario\Documents\skins`
   - Presiona Enter o haz clic en Confirmar

5. **Resultado:**
   - Las skins se aplicarán aleatoriamente a los replays seleccionados
   - Si hay más replays que skins, las skins se reutilizarán
   - Si hay más skins que replays, solo se usarán las necesarias
   - Recibirás una notificación con el resultado de la operación

## Notas Importantes

- Solo funciona con archivos PNG
- La función busca archivos PNG directamente en la carpeta especificada (no en subcarpetas)
- Los replays deben tener un form de tipo MobForm o ModelForm
- Las skins se asignan aleatoriamente sin repetición hasta que se agoten
- La ruta debe ser una ruta absoluta completa del sistema de archivos

## Ejemplos de Rutas

### Windows:
```
C:\Users\Matias\Desktop\skins
D:\Minecraft\Recursos\skins_personalizadas
```

### Advertencias

- Asegúrate de que la carpeta exista antes de ingresar la ruta
- Verifica que la carpeta contenga archivos PNG
- Si la carpeta no existe o no contiene PNGs, recibirás un mensaje de error

## Localizaciones Agregadas

- **Clave de traducción:** `bbs.ui.scene.replays.context.random_skins`
- **Texto en inglés:** "Apply random skins..."

## Archivos Modificados

1. `UIKeys.java` - Agregada constante `SCENE_REPLAYS_CONTEXT_RANDOM_SKINS`
2. `en_us.json` - Agregada traducción para el menú contextual
3. `UIReplayList.java` - Agregados métodos:
   - `applyRandomSkins()` - Muestra el diálogo para ingresar la ruta
   - `processRandomSkins(String folderPath)` - Procesa y aplica las skins
