# Origin

Origin is a large library (writen in _Scala_) which can be used as an API or as a foundation for a new mod.

It includes:
* an API
* a foundation to make other mods on
* an internal mod, for tweaking the minecraft experience
* addons

To use Origin as an API or library, see the [wiki]().

Versioning:

Origin uses the [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) system.

In a quick breakdown, Semantic versioning uses 3 integers to determine version. Although SemVer can handle more than 3 integers, the current system for maven, forge, and the modding community, is 3 integers.

SemVer is formatted as: MAJOR.MINOR.PATCH

When API functions or classes are changed, the MAJOR version is incremented.

When API internal code (not the declarations for functions or classes) is changed, the MINOR version is incremented.

When any other code, resources, or documentation is changed (that would require a build), the PATCH version is incremented.

When MAJOR version is incremented, MINOR and PATCH are set to 0.

When MINOR version is incremented, PATCH is set to 0.
