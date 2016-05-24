Dependencies
===

There are 4 types of dependencies.

Incompatible
---

This is not so much dependency as non-dependency. Code which is is incompatible
causes conflicts and errors in the game and is not compatible.


Compatibility
---

Two mods which are compatible do not cause errors with each other, but don't
interact with one another either. Most mods are compatible with one another thanks to
Minecraft Forge, but most cannot also provide compatibility with every mod out there.


Soft-Dependency
---

This term is used to describe a mod which provides compatibility for another
mod, without requiring the other mod to be present to operate. This is commonly done through APIs
(Application Program Interfaces). For Origin, you would use the package:
	com.temportalist.origin.api


Hard-Dependency
---

This describes a mod which requires another mod present to operate. The bulk
of what will be taught in this area will assume that you require Origin as a Hard-Dependency.
The package for specifically this area (in addition to the api package above) is:
	com.temportalist.origin.foundation
	
