# Changelog
## [1.1.1]
### Changed
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