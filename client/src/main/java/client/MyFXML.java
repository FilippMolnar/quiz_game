/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class MyFXML {

    private final Injector injector;

    public MyFXML(Injector injector) {
        this.injector = injector;
    }

    /**
     * Load method
     * @param c     class type to load, the controller's class, eg: <code>QuoteOverviewCtrl.class</code>
     * @param parts array that gives the path to the <code>.fxml</code> file
     * @param <T>   controller's type
     * @return a pair of the controller and the FXML root object
     */
    public <T> Pair<T, Scene> load(Class<T> c, String... parts) {
        try {
            var loader = new FXMLLoader(getLocation(parts), null, null, new MyFactory(), StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add("client/scenes/waiting_room.css");
            return new Pair<>(ctrl, scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the location of the file
     * @param parts array that gives the path to the <code>.fxml</code> file
     * @return URI of the file location
     */
    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return Main.class.getClassLoader().getResource(path);
    }

    /**
     * Class that handles the injection of the controllers
     * <code>AddQuoteCtrl</code> and <code>QuoteOverview</code> that have <code>@Inject</code> in their constructor, which basically
     * uses dependency injection for the constructor. <br/>
     * This comes from the module <b>Google Juice</b> which is a <b><i>dependency injection framework.</i></b>
     * <code>JavaFx</code> knows nothing about <code>Juice</code> and will try to instantiate the controllers using the constructor
     * but it does not know how to fill in the arguments. It will throw in error if <code>Juice</code> does not do something about this.
     * <pre>{@code
     * // code taken from AddQuoteCtrl
     * // JavaFx does not know about who is MainCtrl or the ServerUtils
     * @Inject
     * public AddQuoteCtrl(ServerUtils server, MainCtrl mainCtrl) {
     *      this.mainCtrl = mainCtrl;
     *      this.server = server;
     * }
     * }</pre>
     *
     * <b><code>Juice</code></b> is the framework responsible for <b>injecting</b> the arguments in the constructor.
     * Thus it has to know how to inject the controller's dependencies and communicate with the <b><code>JavaFX</code></b> framework. <br/>
     * To do so, at line <code>42</code> we tell <b><code>JavaFX</code></b> to call this <code>call</code> method to instantiate the controller<br/>
     * This class is nothing more than a callback, a function that <b><code>JavaFX</code></b> calls before instantiating the controller. <br/>
     * Here is where <b><code>Juice</code></b> shines and instantiates the controllers with their dependencies. <br/>
     * Checkout this tutorial for more information about <code>Juice</code> <a href="https://www.youtube.com/watch?v=wNclLOTxQjk">Youtube link</a>
     */
    private class MyFactory implements Callback<Class<?>, Object> {
        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}