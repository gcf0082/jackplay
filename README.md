# Jackplay

## What is Jackplay?
  Jackplay is a **JVM tracing tool** that helps you troubleshoot application behaviour. It works at JVM level and supports languages like Java, Groovy, Clojure, etc.

  It allows you to **trace** method execution in a JVM. It also allows you to **redefine** a method in a JVM **live**! All these comes without any need to change your application code.

## Latest Version
  ```com.github.alfredxiao/jackplay 0.9.2```

## Features
 - Trace or redefine a method in a JVM live even after a class has been loaded
 - Trace method execution no matter there is exception thrown or not
 - Can turn on or off a method being traced or redefined
 - Can blacklist or whitelist specified packages
 - No need to change application code
 - No need to restart a JVM

## Q and A

### Why do I need Jackplay?
1. Developers are not sure where to put log statements and what to log - before running into a problem in an deployed environment.
2. Sometimes we want to apply a quite trivial change to the code to verify some thing.
3. We are lazy people and do not want to go through the dev/test/deploy cycle again and again, especially in a testing setting.
4. Sometimes we need to trace into a library but it is a challenge since you cannot change its source code.

If you have ever run into any situation as mentioned above, you would benefit from Jackplay's code change free tracing and redefinition capabilities.

### What's the difference between 'Tracing' and 'Debugging'?

1. A debugger is heavyweight, it requires full source code, an IDE and deep knowledge of how the code works. Whereas a tracing tool is lightweight, it does not require full source code or deep knowledge of the code.
2. A debugger stops or pauses execution, whereas a tracing tool like Jackplay does not change application behaviour. A tracing tool's role is like a silent interceptor.
3. A debugger give more insight into how a function body executes, but a tracing tool touches only the entry and exit points of your functions.
4. A debugger normally works at TCP level, and does not provide a UI; Jackplay gives a web UI which is easy to use for developers, testers, and DevOps.

## Usage

### Start you application with jackplay

  Add the follow two arguments to your JVM startup command line:

  ```
  -Xbootclasspath/a:jackplay-bootstrap-<version>.jar -javaagent:jackplay-agent-<version>.jar
  ```

  Example:

  ```
  java -Xbootclasspath/a:jackplay-bootstrap-<version>.jar -javaagent:jackplay-agent-<version>.jar -cp myapplication.jar myapp.Main
  ```

  To add custom options:
  e.g. to set port number for the embedded web server to listen on, and specify blacklist, you append these parameters to -javaagent argument, as follows:

  ```
  -javaagent:jackplay-agent-<version>.jar=port=8282,blacklist=java.net:java.nio
  ```

  Notice that package names in 'blacklist' are separated by ':', you can set whitelist as well, but you can't set both blacklist and whitelist at the same time.

  Options supported:

| Option           | Description                                                               | Default | Example      |
|------------------|---------------------------------------------------------------------------|---------|--------------|
|*port*            | port number the web server listens on                                     | 8181    |              |
|*https*           | whether to use https                                                      | true    |              |
|*keystoreFilepath*| when using https, this is used to set the file path to your keystore file |         |              |
|*keystorePassword*| when using https, this is used to set the password for your keystore file |         |              |
|*logLevel*        | can be either info, debug, or error, default is info                      | info    | error, debug |
|*traceLogLimit*   | how many entries of trace log the server holds, old log entries are removed when new entries come while we have run out of capacity | 200 | |
|*autoSuggestLimit*| specifies the limit of items auto suggestion displays | 100 | |
|*blacklist*       | colon separated packages to not allow tracing or redefining, java.lang is always blacklisted | | ```java.net:myapp.utils``` |
|*whitelist*       | colon separated packages to allow tracing or redefining, once you provide a whitelist, other packages are prevented from being able to be traced or redefined | | |
|*defaultTrace*    | colon separated full method names that we want Jackplay to trace by default | | ```myapp.Math.add(int)``` |
|*logFile*         | file path to write Jackplay logs to. Note if file size grows over 100M, it will be truncated | | ```./jackplay.log``` |

  **Note**:
  1. When *https* is set to true, but no password or keystore path provided, a built-in demo self-signed keystore would be used instead.
  2. When you define *defaultTrace*, the way you specifiy JVM argument might need to be quoted, e.g.

  ```
  java -Xbootclasspath/a:../jackplay/dist/jackplay-bootstrap-latest.jar -javaagent:../jackplay/dist/jackplay-agent-latest.jar="logLevel=debug,blacklist=java.net:java.nio,defaultTrace=myapp.greeter.NiceGreeter.greet(java.lang.String):myapp.Demo.main(java.lang.String[])" -cp . myapp.Demo
  ```

### Open Jackplay control panel

   Open your [http://yourserver:8181] (http://yourserver:8181)

### To trace a method

   Type a class or method name, Jackplay should automatically suggest matching classes/methods. Select one method, then click 'Trace'.

### To redefine a method

   Click 'Redefine...', similarly, choose a method, then key in new method body. The method body can only be written in Java, and must be enclosed with {}.

   Example:

   ```
   {
     java.util.List myList = new java.util.ArrayList();
     myList.add("A");
     myList.add("B");

     return myList;
   }
   ```

## How to Run the provided Demo app

1. Clone project, and build jackplay

  ```
  git clone https://github.com/alfredxiao/jackplay.git
  cd jackplay/jackplay
  ant clean dist
  ```

2. Run Demo (assuming you current work directory is jackplay/jackplay)

  ```
  cd ../demo-java
  ./run.sh
  ```

3. Open browser and point to Jackplay page

  Open [http://localhost:8181](http://localhost:8181), and try to play with it.

## Limitations

 - Cannot trace or redefine native, abstract method, or a method in an Interface, because they don't have body.
 - Does not support constructor method yet
 - When redefining a method, you can only use Java. And you better avoid using advanced Java features like Lambda, Generics, etc. For details, see [javassist page](https://jboss-javassist.github.io/javassist/tutorial/tutorial2.html#limit).

## License

  [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
