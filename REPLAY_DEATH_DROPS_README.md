# Replay Death Item Drops - BBS Mod

## 📋 Descripción

Esta funcionalidad agrega un sistema realista de drop de items cuando un replay muere. Cuando un replay configurado como "actor" (entidad física) recibe daño fatal, automáticamente dropeará todos los items que tenía almacenados, recreando el comportamiento vanilla de Minecraft.

## ✨ Características

### 1. **Drop de Items Equipados**
Cuando un replay muere, dropea todos los items que tenía equipados en ese momento:
- **Mano principal** (Main Hand)
- **Mano secundaria** (Off Hand)
- **Casco** (Helmet)
- **Pechera** (Chestplate)
- **Pantalones** (Leggings)
- **Botas** (Boots)

### 2. **Drop de Inventario Completo**
Si el film tiene un inventario grabado (del jugador durante la grabación), todos esos items también se dropean:
- Los 36 slots del inventario
- Items especiales grabados durante la sesión

### 3. **Física Vanilla**
Los items dropeados tienen el mismo comportamiento que en Minecraft vanilla:
- **Velocidad aleatoria** en dirección horizontal
- **Salto vertical** al caer
- **Delay de pickup** estándar antes de poder recogerlos
- **Física de colisión** realista

## 🎮 Cómo Funciona

### Requisitos Previos

Para que un replay dropee items al morir debe:

1. **Estar configurado como Actor**
   - En las propiedades del replay, activar la opción "Actor"
   - Esto hace que el replay sea una entidad física que puede recibir daño

2. **Tener items grabados**
   - Items equipados en los keyframes de animación
   - O inventario completo del film (grabado durante la sesión)

### Proceso de Drop

```
1. Replay recibe daño fatal
        ↓
2. Se ejecuta onDeath()
        ↓
3. Se obtienen items del tick actual:
   - Items equipados de keyframes
   - Items del inventario del film
        ↓
4. Se crean ItemEntity para cada item
        ↓
5. Se aplica física vanilla
        ↓
6. Items aparecen en el mundo
```

### Interpolación de Items

Los items se obtienen interpolando los keyframes al **tick exacto** de la muerte:
```java
float currentTick = actor.getCurrentTick();
ItemStack mainHand = replay.keyframes.mainHand.interpolate(currentTick, ItemStack.EMPTY);
```

Esto asegura que se dropeen exactamente los items que el replay tenía en ese momento.

## 🔧 Implementación Técnica

### Archivos Modificados

#### 1. `ActorEntity.java`
```java
// Nuevos campos
private Film film;
private Replay replay;
private int currentTick;

// Nuevos métodos
public void setReplayData(Film film, Replay replay, int tick)
public void updateTick(int tick)

// Override
@Override
public void onDeath(DamageSource damageSource)

// Método privado
private void dropReplayItems()
private void dropStack(ItemStack stack)
```

#### 2. `ActionPlayer.java`
```java
// Al crear ActorEntity
actor.setReplayData(this.film, replay, this.tick);

// Al actualizar cada tick
if (actor instanceof ActorEntity actorEntity)
{
    actorEntity.updateTick(this.tick);
}
```

### Flujo de Datos

```
Film (Film.java)
  └── inventory: List<ItemStack>
  └── replays: List<Replay>
        └── Replay (Replay.java)
              └── keyframes: ReplayKeyframes
                    ├── mainHand: KeyframeChannel<ItemStack>
                    ├── offHand: KeyframeChannel<ItemStack>
                    ├── armorHead: KeyframeChannel<ItemStack>
                    ├── armorChest: KeyframeChannel<ItemStack>
                    ├── armorLegs: KeyframeChannel<ItemStack>
                    └── armorFeet: KeyframeChannel<ItemStack>
                          ↓
                    ActorEntity
                          ↓
                    onDeath() → dropReplayItems()
                          ↓
                    ItemEntity (mundo)
```

## 📝 Ejemplo de Uso

### Escenario 1: Actor con Espada

```
1. Grabar un replay con una espada en mano
2. Configurar el replay como "Actor"
3. Reproducir el film
4. Dañar al replay hasta matarlo
5. El replay dropea la espada al morir
```

### Escenario 2: Actor con Armadura Completa

```
1. Grabar un replay con armadura completa
2. Los keyframes registran cada pieza de armadura
3. Configurar como "Actor"
4. Al morir, dropea todas las piezas
```

### Escenario 3: Actor con Inventario Completo

```
1. Grabar un film con inventario lleno
2. El Film.inventory guarda todos los items
3. Al morir el replay, dropea:
   - Items equipados
   - Todo el inventario grabado
```

## ⚙️ Configuración

### En el Editor de Replays

1. Seleccionar un replay en el panel de Replays
2. Activar la opción **"Actor"** en las propiedades
3. El replay ahora puede:
   - Recibir daño
   - Morir
   - Dropear items

### Items que se Dropean

#### Items Equipados (Keyframes)
- Se obtienen del tick exacto de muerte
- Se interpolan automáticamente
- Incluyen todos los slots de equipamiento

#### Items del Inventario (Film)
- Se dropean TODOS los items del inventario grabado
- No se interpolan (son estáticos del momento de grabación)
- Incluyen los 36 slots del inventario del jugador

## 🐛 Notas Técnicas

### Interpolación de Items

Los `KeyframeChannel<ItemStack>` NO interpolan entre items diferentes. Cuando hay un cambio de item en los keyframes, simplemente retorna el item del keyframe más cercano:

```java
// En ItemStackKeyframeFactory
@Override
public ItemStack interpolate(ItemStack a, ItemStack b, ...)
{
    return a; // No hay interpolación real entre items
}
```

### Prevención de Duplicados

Si un replay tiene items tanto en keyframes como en el inventario del film, puede dropear items duplicados. Esto es intencional y refleja el estado real grabado.

### Performance

- Los drops son procesados server-side únicamente
- Se crean `ItemEntity` normales de Minecraft
- No hay impacto significativo en el rendimiento

## 🎯 Casos de Uso Avanzados

### PvP en Escenas

```
Crear batallas donde los actores dropean su equipo al morir,
permitiendo que otros jugadores lo recojan
```

### Puzzles y Retos

```
Diseñar escenas donde derrotar enemigos es necesario
para obtener items específicos
```

### Storytelling

```
Crear momentos cinematográficos donde un personaje
muere y dropea un item importante para la trama
```

## 🔍 Debugging

### Verificar que el Replay tiene Items

```java
// En el editor, verificar keyframes
replay.keyframes.mainHand.getKeyframes().size() > 0

// Verificar inventario del film
film.inventory.getStacks().size() > 0
```

### Verificar que el Actor está Configurado

```java
replay.actor.get() == true
```

### Ver Items Dropeados en Logs

Los items se dropean silenciosamente, pero puedes agregar logs:

```java
System.out.println("Dropping item: " + stack.getName().getString());
```

## 📄 Compatibilidad

- ✅ Compatible con todos los tipos de Forms
- ✅ Compatible con el sistema de equipamiento existente
- ✅ Compatible con Model Blocks convertidos a Replays
- ✅ Compatible con el sistema de inventario del Film
- ✅ No afecta replays que NO son actores

## 🚀 Mejoras Futuras Posibles

- [ ] Opción para desactivar drops por replay
- [ ] Drop selectivo (solo algunos items)
- [ ] Efectos visuales al dropear
- [ ] Sonidos personalizados
- [ ] Control de probabilidad de drop
- [ ] Integración con sistema de loot tables

---

**Autor**: Sistema de Replay Death Drops para BBS Mod  
**Fecha**: Enero 2026  
**Versión**: 1.0.0
