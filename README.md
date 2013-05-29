Iron Cache Plugin for Play 2.x (Experimental)
===

Requirements
---

* Tested with [Play 2.1.0][play]
* [Iron.io][iron] credentials

Build
---

To build this currently, clone this repo and then build this project using SBT.

    git clone https://github.com/tmwtmp100/iron-cache-plugin.git iron-cache
    cd iron-cache
    # Assuming play is on your path
    play compile package
    cp plugin/target/scala-2.10/iron-cache-plugin_2.10-1.0-SNAPSHOT.jar <play project dir>/lib

How to Use
---

To use, first the default cache has to be disabled. Then your specific iron.io credentials must also be added.
To do that, open the `application.conf` file and add a property:

    # Disable Default Cache
    ehcacheplugin=disabled

    #Iron Cache Properties

    # Required. Get these settings from the iron.io dashboard
    iron.token      = "<Your iron.io token>"
    iron.project.id = "<Your iron.io project's ID>"

    # Optional. If not specified, these values will be used instead.
    iron.cache.host = "cache-aws-us-east-1"
    iron.cache.name = "cache"

The plugin must then be activated by adding a line to the `play.plugins` file. If one has not be created yet, create one
in the conf folder of your Play application. Add this line:

    1501:com.github.tmwtmp100.cache.IronCachePlugin

Version
---

0.1 Initial Version. Consider it very rough (no error checking).

Contact
---

If you like this plugin and want to contribute, feel free to submit a pull request!

[play]: http://www.playframework.com/ "Play Framework"
[iron]: http://www.iron.io            "Iron.io"