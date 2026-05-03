# ThaumicAPI
 ThaumicAPI is a library mod used to standardize and expand a large number of previously hardcoded features in Thaumcraft 6.

## New Features
- Adds `ISmelterAuxiliary` and `ISmelterVent`, allowing smelter addon creation.
- Adds `@WarpEvent` functional registry interface and `IWarpEvent` interface allowing the creation of new Thaumcraft Warp Events.
- Adds `/tapi warpevent` command, allowing users to manually trigger specific warp events.
- Adds JEI `IIngredientType` for AspectLists, allowing developers to easily draw aspects into JEI handlers. Includes built-in integration for ThaumicJEI.

## Helpers
- Adds `AbstractItemCaster` casting gauntlet class to allow easy complex gauntlet creation.
- Adds `AbstrcatTileEssentiaSmelter` for easy Essentia Smelter creation that includes integration for the new `ISmelterAuxiliary` and `ISmelterVent` interfaces.
- Adds `PlayerMovementAbilityHandler` to allow easier creation of movement speed increasing items such as the Boots of the Traveler.
- Adds `ThaumicAPI#registerInfusionEnchantment()` helper method for easily registering new Infusion Enchantments.

## Thaumcraft Changes:
- Overhauls Thaumcraft warp events to use an event handler so they can be cancelled or modified.
- Overhauls Thaumcraft Essentia Smelter to use `ISmelterAuxiliary` and `ISmelterVent` interfaces instead of hardcoded block checks.
