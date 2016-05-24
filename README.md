Origin
=================

A wiki on how to implement our library (using _Scala_ of course), can be found [to the right](https://github.com/TheTemportalist/Origin/wiki).

Versioning:
	The versioning for Origin is based off of Semantic Versioning ([SemVer](http://semver.org/)).
	Format;
		MCPAIR.MAJOR.MINOR.PATCH
			i.e. -> 2.1.4.6
		MCPAIR - A version number which matches up with specifically one Minecraft version.
			This will increment when mod is updated to next Minecraft version.
		MAJOR - This is incremented when API changes or changes are made which break backwards compatibility.
			The API consists of files which can be used for soft-dependency (dependency without relying on mod code).
		MINOR - This is incremented when changes are committed and are backwards compatible.
		PATCH - Incremented in order to release bug fixes.
