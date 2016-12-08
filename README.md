# sbt-docker-helper
sbt-native-packager Docker plugin helper

Depends on [sbt-native-packager](https://github.com/sbt/sbt-native-packager) version 1.2.0+ of the DockerPlugin.

```
addSbtPlugin("com.terradatum" % "sbt-docker-helper" % "0.4.0-SNAPSHOT")
```

1. Alters the behavior of `publishLocal` to leave off the `dockerRepository`.

   The default behavior of `sbt-native-packager` `docker:publishLocal` task is to prepend the local machine name as the
   `dockerRepository` value (via the `DockerAlias`) so that you end up with a tagged image like so:
   ```
   192.168.1.129:5000/some-server:1.0.0
   ```
   
   Where the IP address is the "real" IP of the local machine.
   
   This is less than ideal if you need to create integration tests that will work on multiple developer workstations.
   
   With this plugin, the new default behavior produces the following tagged image:
   ```
   some-server:1.0.0
   ```
   
   This is necessary in the event you need to create integration scripts which target a local image:
   ```
   # easy
   docker run some-server:1.0.0
   # works for mac, not for windows or linux - also interface-dependent
   docker run $(ipconfig getifaddr en0)/some-server:1.0.0
   ```

2. As a result of #1, this plugin will now tag the image properly before the `docker:publish` task.

3. Adds a `dockerUseSudo` setting to enable `sudo` docker commands. Necessary for most CI builds on any RedHat derivative.
   See http://www.projectatomic.io/blog/2015/08/why-we-dont-let-non-root-users-run-docker-in-centos-fedora-or-rhel/

4. Adds a `docker:clean` task which recursively removes the local images based on the project name.