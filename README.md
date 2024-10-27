# Mastering Mixology Helper

### Features
* Appends the potion recipe to the standard minigame interface
* Configurable potion order highlighting strategies
* Station highlighting
* Station quick-action highlighting
* Digweed highlighting & notifications

### Changelog

#### V1.6.0
* Adds support for potion order reordering in the interface. For now, the only available options are: Vanilla (default) & By Station type
* Customizable notifications for digweed spawns

#### V1.5.0
* Removal of the potion highlighting strategies, these were conceived when you could only deliver one potion at a time, now it is almost always better to fulfill all orders before delivering.
* Improved quick-action highlighting behavior.
* An option to display the amount of resin you have has been added, it is set to disabled by default.

#### V1.4.1
* Fix repeated potion orders not being marked as fulfilled
* Fix `ready!` indicator when using the `NONE` strategy
* Replaced the `ready!` indicator with `done!`

#### V1.4.0
* Separated quick-action from station highlighting
* Station highlighting is now based on potions in the inventory instead of what you're currently mixing, this should greatly help those who mix multiple potions at the same time
* Added a `ready!` indicator whenever you finish refining a potion that fulfills an order
* Added inventory potion identification similar to the Item Identification plugin, this can be disabled in the plugin settings 

#### V1.3.0
* Added lever highlighting
* Fixed potion experience values

#### V1.2.0
* Fixed a bug where using another station rather than the highlighted would not dismiss it even after delivering the potions
* Fixed a bug where disabling station highlights would still keep them highlighted until fulfilling an order
* Added a strategy to favor the retort station
* Added the option of having no strategy at all, thus not highlighting any potion order
* Changed when stations are highlighted, now the plugin tries to highlight them as soon as you mix a potion order correctly

#### V1.1.0
* Added different configurable potion order strategy highlighting including: Favor experience, Favor alembic station, Favor mox/aga/lye
* Added station highlighting when you pick up a potion
* Added station quick-action highlighting
* Added digweed notifications & highlights