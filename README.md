Iron Cache Plugin for Play 2.x
===

Requirements
---

* Tested with [Play 2.1.x][play]
* [Iron.io][iron] credentials

Usage
---

Add the following dependency to your Play project:

```scala
  val appDependencies = Seq(
    "com.github.tmwtmp100" %% "iron-cache-plugin" % "1.0"
  )
  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "TMWTMP100 Repository" at "https://raw.github.com/tmwtmp100/maven/master/releases"
  )
```

Build
---

To build from source, clone this repo and then build this project using SBT.

    git clone https://github.com/tmwtmp100/iron-cache-plugin.git iron-cache
    cd iron-cache
    # Assuming play is on your path
    play compile package
    cp plugin/target/scala-2.10/iron-cache-plugin_2.10-1.0-SNAPSHOT.jar <play project dir>/lib

Setup
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

How to Use
---

The standard cache interface built into Play is now enabled and can be used normally.

```scala
    # Get a value
    Cache.get("key")

    # Set a value, set a value with an expiration time in milliseconds
    Cache.set("key", "value")
    Cache.set("key", "value", 3600)

    # Remove an item from the cache
    Cache.remove("key")
```
In addition, Iron Cache has a few more capabilities built into its API. To use those:

```scala
    import com.github.tmwtmp100.cache.IronCachePlugin

    # Increment an integer value (Use negative values to decrement)
    play.api.Play.current.plugin[IronCachePlugin].get.increment("key", amount_to_increment)

    # Delete all items from the cache
    play.api.Play.current.plugin[IronCachePlugin].get.clearCache()
```

Version
---

1.0 First Stable Version. Added Maven repository for easy use.

0.2 Added LOTS of error checking. Implemented and tested the Iron specific functions.

0.1 Initial Version. Consider it very rough (no error checking).

Contact
---

If you like this plugin and want to contribute, feel free to submit a pull request!

[play]: http://www.playframework.com/ "Play Framework"
[iron]: http://www.iron.io            "Iron.io"