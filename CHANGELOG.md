# Changelog
## [1.1.4]
### Fixed
- Fixed ThaumicAPI breaking Multiblocked's AspectList JEI display

---

## [1.1.3]
### Fixed
- Fixed registry crash when loaded with Multiblocked

---

## [1.1.2]
### Fixed
- Fixed broken `AbstractItemCaster` logic when picking blocks for the exchange focus

---

## [1.1.1]
### Changed
- Clicking on Aspects in JEI in cheat mode now give players an essentia jar filled with that aspect (courtesy of [roidrole](https://github.com/roidrole))
- Modified `AbstractTileEssentiaSmelter` to allow fuel insertion from bottom and item insertion from sides
### Removed
- Removed vis discount support for mainhand and offhand to fix exploit

---

## [1.1.0]
### Added
- Added NBTTagCompound support for Vis Discount items
- Added vis discount support for items held in mainhand or offhand
- Added helper method for adding Vis Discount to items `ThaumicAPI#addWarpingToStack(ItemStack, int)` or by adding the `TC.VISDISCOUNT` integer NBT tag to the ItemStack tag compound
- Added helper method for adding Warping to items `ThaumicAPI#addVisDiscountToStack(ItemStack, int)`

---

## [1.0.2]
### Fixed
- Fixed edge case Warp Event crash

---

## [1.0.1]
### Fixed
- Fixed JEI crash with Modular Machinery: Community Edition

---

## [1.0.0]
### Added
- Initial Release