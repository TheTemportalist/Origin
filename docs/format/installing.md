Libraries
===

This section will describe how to add a "dev" or "decomp" jar as a library for your mod. This is
commonly done so that the said jar can be used as a Hard-Dependency.

1. Navigate to your mod directory - This directory holds the "src" and "build" folders, as well as
the "build.gradle" file.

2. Create a "libs" folder - This is the folder which ForgeGradle auto loads libraries from

3. Run the "setupDevWorkspace" or "setupDecompWorkspace" commands through gradle - This will load
all the jars inside the "libs" folder into the workspace. The 'dev' version will just load the
classes, while the 'decomp' version will attempt to decompile the sources for you.
