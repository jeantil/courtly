### Prérequis
* 1.7 JVM
* [sbt](http://www.scala-sbt.org/) 0.13.0+ (or brew install sbt)
* [rvm](https://rvm.io/) and ruby 1.9.3
* node.js 0.10+ ( you may want to look at [nvm](https://github.com/creationix/nvm)
* A clone of this repo

### Lancer la console play
Ca ne sert a rien mais ça affiche le logo :) Depuis la racine du projet lancez `sbt play` :

    $ sbt play
    [info] Loading project definition from /Users/jean/dev/sdev/src/work/metacfp/project
    [info] Set current project to XBackend (in build file:/Users/jean/dev/sdev/src/work/metacfp/)
           _            _
     _ __ | | __ _ _  _| |
    | '_ \| |/ _' | || |_|
    |  __/|_|\____|\__ (_)
    |_|            |__/

    play! 2.3 (using Scala 2.10.0), http://www.playframework.org

    > Type "help play" or "license" for more information.
    > Type "exit" or use Ctrl+D to leave this console.

    [courtly] $

### Configurer un IDE
Play supporte en natif la configuration des deux plux gros IDE java : [Eclipse (ScalaIDE)](http://scala-ide.org/download/current.html), [IntelliJ (Community suffit)](http://www.jetbrains.com/idea/free_java_ide.html). Le détail des instructions pour eclipse est là http://www.playframework.org/documentation/2.1.0/IDE , en court il s'agit de lancer la console play et d'utiliser `eclipsify with-source=true` 
    
    [XBackend] $ eclipse with-source=true
    [info] About to create Eclipse project files for your project(s).
    [info] Successfully created Eclipse project files for project(s):                      
    [info] XBackend
    [XBackend] $    

Pour IntelliJ, c'est tout aussi simple avec `idea with-sources` : 

    [XBackend] $ idea with-sources=yes
    [info] Trying to create an Idea module XBackend
    [info] downloading half the universe ...
    ...
    [info] Excluding folder target
    [info] Created /Users/jean/dev/sdev/src/work/metacfp/.idea/IdeaProject.iml
    [info] Created /Users/jean/dev/sdev/src/work/metacfp/.idea
    [info] Excluding folder /Users/jean/dev/sdev/src/work/metacfp/target/scala-2.10/cache
    [info] Excluding folder /Users/jean/dev/sdev/src/work/metacfp/target/scala-2.10/classes
    [info] Excluding folder /Users/jean/dev/sdev/src/work/metacfp/target/scala-2.10/classes_managed
    [info] Excluding folder /Users/jean/dev/sdev/src/work/metacfp/target/native_libraries
    [info] Excluding folder /Users/jean/dev/sdev/src/work/metacfp/target/resolution-cache
    [info] Excluding folder /Users/jean/dev/sdev/src/work/metacfp/target/scala-2.10/resource_managed
    [info] Excluding folder /Users/jean/dev/sdev/src/work/metacfp/target/streams
    [info] Created /Users/jean/dev/sdev/src/work/metacfp/.idea_modules/metacfp.iml
    [info] Created /Users/jean/dev/sdev/src/work/metacfp/.idea_modules/metacfp-build.iml
    [XBackend] $    

### Les commandes sbt utiles : 

- `update` met à jour les dépendances
- `compile` compile l'application
- `test` joue les tests unitaires
- `~ [command]` joue [command] en continu (compile et test sont de bonnes idées)
- `run` lance l'application