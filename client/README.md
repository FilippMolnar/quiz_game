# Frameworks used
<hr/>

### JavaFX

References/Tutorials
<ul>
    <li><a href="https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/doc-files/cssref.html">CSS docs</a></li>
    <li><a href="https://fxdocs.github.io/docs/html5/#_introduction">JavaFX docs made by someone</a></li>
    <li><a href="https://examples.javacodegeeks.com/desktop-java/javafx/fxml/javafx-fxml-tutorial/">FXML Guide</a></li>
    <li><a href="https://www.youtube.com/watch?v=Z1W4E2d4Yxo">Scene Builder Youtube </a></li>
</ul>

### Juice
<ul>
    <li><a href="https://www.youtube.com/watch?v=wNclLOTxQjk&t=2s">Getting Start Youtube</a></li>
    <li><a href="https://google.github.io/guice/api-docs/4.2/javadoc/index.html?com/google/inject/Binder.html">JavaDoc Juice</a> (not easy to grasp everything)</li>
</ul>


While the project can be run out of the box via Gradle, running it from within Eclipse/Intelij seems to require adding the following as *VM* commands:

    --module-path="path/to/javafx-sdk-17.0.2/lib" --add-modules=javafx.controls,javafx.fxml


### Connecting to a remote server

To connect to a remote server, open the file called _ServerUtils.java_ in the _client/src/main/java/client/utils_ folder. This file contains basic utilities to communicate with the server.
All utilities are contained in the class _ServerUtils_, which has a field _SERVER_. This field allows you to specify the private (or public) IP address of the server you want to connect to.

#### Example:
You want to run a server on the localhost of device **A** and you want to connect to this server using device **B**. To find the IP address of **A**, you can run one of the following commands on that device to find the private ip address:

##### Linux:
    `ip a | grep inet`

##### Windows:
    `ipconfig`

##### Mac OS:
    `ifconfig | grep inet`

The local ip address will likely look like 192.x.x.x or 172.x.x.x where x is any number smaller than or equal to 255.
Now change the SERVER variable on device **B** (the client) to the IP-address you just found on device **A**. Run `gradle bootrun` on A and run `gradle run` on **B**. **B** should now be connected to **A**.
