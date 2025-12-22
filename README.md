# BBS mod

BBS mod is a Minecraft mod for Fabric 1.20.4 and 1.20.1 (works on Forge as well) for creating animations within Minecraft. It has more features than that, but overall its main task is to facilitate making animated content within Minecraft. For more information, see BBS mod's [Modrinth](https://modrinth.com/mod/bbs-mod/) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/bbs-mod) pages.

This repository is the source code of BBS mod. The `1.20.4` code is in the `master` branch, and `1.20.1` in `1.20.1` branch, which is usually just a merge of the `master` branch.

If you'd like to contribute to BBS mod code-wise, I'm not looking for contributions. **Please fork the repository, and make your own version**.

See `LICENSE.md` for information about the license.

---

## Custom Features (This Fork)

This fork includes the following additional features:

### 1. Random Skins for Replays

Apply random skins to multiple selected replays without repetition.

**How to use:**
1. Open the Scene/Film editor in BBS Dashboard
2. Select multiple replays in the replay list (using multi-selection)
3. Right-click on the selection
4. Select "Apply random skins..." from the context menu
5. Enter the folder path containing PNG skin files (relative to the assets folder)
6. The mod will randomly assign different skins to each selected replay

**Notes:**
- Skin files must be PNG images in the specified folder
- Each replay will receive a unique skin (no duplicates)
- Only works with skin files located in the assets folder

### 2. Batch Model Block to Replay Conversion

Convert multiple model blocks to replays simultaneously instead of one at a time.

**How to use:**
1. Open the Scene/Film editor in BBS Dashboard
2. In the replays panel, right-click
3. Select "From model block(s)..." from the context menu
4. Select multiple model blocks using multi-selection (hold Shift/Ctrl)
5. All selected model blocks will be converted to replays at once

**Notes:**
- Position, rotation, scale, and shadow settings are preserved
- The form/model assigned to the model block is copied to the replay
- Equipment items are also transferred (see Equipment System below)

### 3. Equipment/Inventory System for Model Blocks

Add items and armor to model blocks for hands and armor slots.

**How to use:**
1. Open the Model Blocks panel in BBS Dashboard
2. Select a model block
3. Scroll down in the editor panel to find the equipment slots:
   - Main hand
   - Off hand
   - Helmet
   - Chestplate
   - Leggings
   - Boots
4. Click on any slot to open the item selector
5. Choose an item from your hotbar or search for items
6. The equipped items will render on the model block
7. To remove an item, right-click the slot and select "Reset"

**Equipment transfer:**
- When converting a model block to a replay, all equipped items are automatically copied to the replay's equipment keyframes

**⚠️ Known Issues:**
- **Bug 1:** When selecting an item for the boots slot, text elements may overlap with the overlay menu
- **Bug 2:** When editing the model block's pose/transform, the equipped armor and interactive axes may not display correctly
  - The equipment and axes only render properly when not in edit mode
  - This issue occurs specifically when the model block has equipment assigned

**Workaround for Bug 2:**
- Save your changes and exit edit mode to see the equipment rendered correctly
- The equipment data is properly saved and will render when not actively editing

---