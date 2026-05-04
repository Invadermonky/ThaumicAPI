## ThaumicAPI
 ThaumicAPI is a library mod used to standardize and expand a large number of previously hardcoded features in Thaumcraft 6.

### New Features
- Adds [`ISmelterAuxiliary`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/block/ISmelterAuxiliary.java) and [`ISmelterVent`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/block/ISmelterVent.java), allowing smelter addon creation.
- Adds [`@WarpEvent`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/warpevent/WarpEvent.java) functional registry interface and [`IWarpEvent`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/warpevent/IWarpEvent.java) interface allowing the creation of new Thaumcraft Warp Events.
- Adds `/tapi warpevent` command, allowing users to manually trigger specific warp events.
- Adds JEI [`IIngredientType`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/ThaumicAPIJEIPlugin.java) for AspectLists, allowing developers to easily draw aspects into JEI handlers. Includes built-in integration for ThaumicJEI.

### Helpers
- Adds [`AbstractItemCaster`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/item/AbstractItemCaster.java) casting gauntlet class to allow easy complex gauntlet creation.
- Adds [`AbstractTileEssentiaSmelter`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/tile/AbstractTileEssentiaSmelter.java) for easy Essentia Smelter creation that includes integration for the new `ISmelterAuxiliary` and `ISmelterVent` interfaces.
- Adds [`PlayerMovementAbilityHandler`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/handlers/PlayerMovementAbilityHandler.java) to allow easier creation of movement speed increasing items such as the Boots of the Traveler.
- Adds [`ThaumicAPI#registerInfusionEnchantment()`](https://github.com/Invadermonky/ThaumicAPI/blob/master/src/main/java/com/invadermonky/thaumicapi/api/ThaumicAPI.java) helper method for easily registering new Infusion Enchantments.

### Thaumcraft Changes:
- Overhauls Thaumcraft warp events to use an event handler so they can be cancelled or modified.
- Overhauls Thaumcraft Essentia Smelter to use `ISmelterAuxiliary` and `ISmelterVent` interfaces instead of hardcoded block checks.
