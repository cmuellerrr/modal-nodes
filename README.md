# Modal Nodes
===========

An gesture-controlled music installation.

## Installation
Note: This project is built using Processing 1.5.1 as a library only.  All code is plain Java.

Setup an eclipse project with the checked out code.
Include the Processing library.  See `http://processing.org/learning/eclipse/` for more details.


### Addon Libraries
Include the addon libraries to your project.  
You can see `http://wiki.processing.org/w/How_to_Install_a_Contributed_Library` for more details but it will be more similar to when you added the processing .jar.

```
SimpleOpenNI: https://code.google.com/p/simple-openni/
Minim 2.0.2: http://code.compartmental.net/tools/minim/
controlIP5: http://www.sojamo.de/libraries/controlP5/
```

##Common Errors
You may encounter errors when setting up SimpleOpenNI.
Make sure to include a reference to the native library location on the SimpleOpenNI.jar when adding it in Eclipse.

![setting the native library](resources\images\openni_native_lib.png)
