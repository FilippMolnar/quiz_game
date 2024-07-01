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
package client.utils;

import commons.*;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import javafx.application.Platform;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    // Use this variable to define the server address and port to connect to
    private String SERVER;

    private final List<List<Object>> subscribeParameters = new ArrayList<>();
    private StompSession session;
    private String WEBSOCKET_SERVER;
    private Set<List<Object>> connections = new HashSet<>();

    /**
     * Initialize the server
     * @param server - the server address
     */
    public void initializeServer(String server) {
        SERVER = "http://" + server + ":8080";
        WEBSOCKET_SERVER = "ws://" + server + ":8080/websocket";
        System.out.println("Session is : " + session);
        if(session != null){
            System.out.println("Session is not null so disconnecting!");
            session.disconnect(); // Close all socket subscriptions with this session
        }
        session = connect(WEBSOCKET_SERVER);
        for (List<Object> l : subscribeParameters) {
            subscribeSocketFromList((String) l.get(0), (Class<Object>) l.get(1), (Consumer<Object>) l.get(2));
        }
        connections.clear();
    }

    /**
     * Connects the websockets to a URL specified in <code>WebSocketConfig</code> class on the server side
     * @param url URL to connect to
     * @return a new StompSession
     */
    private StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        System.out.println("calling connect");
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException();
        }
        throw new IllegalArgumentException("Something went bad here!");
    }

    /**
     * Utility function to be used from the client to register events when there is a message on a channel.
     * The client <code>StompSession</code> (A subprotocol of websockets) listens to messages coming and calls
     * the consumer function
     *
     * @param dest      the channel name where the client wants to listen to
     * @param classType the class type of the expected object response. eg: <code>Player</code> maybe in the future
     *                  <code> Emoji</code>
     * @param consumer  the callback to execute when a message is received
     */
    public <T> void subscribeForSocketMessages(String dest, Class<T> classType, Consumer<T> consumer) {
        List<Object> objects = List.of(dest, classType, consumer);
        if (session == null) {
            subscribeParameters.add(objects);
        }else {
            System.out.println("Trying to subscribe to messages twice or from outside initialize() called once per controller");
        }
    }

    public <T> void subscribeSocketFromList(String dest, Class<T> classType, Consumer<T> consumer) {
        System.out.println("Registered to listen on the track " + dest);
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            @Nonnull
            public Type getPayloadType(@Nonnull StompHeaders headers) {
                return classType;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                Platform.runLater(() -> {
                    System.out.println("Consumer called! for track" + dest);
                    consumer.accept((T) payload);
                });
            }
        });
    }

    /**
     * Method to be used to send data from the client to the server through websockets
     *
     * @param destination url provided in a socket controller with <code>@MessageMapping</code>
     * @param obj         object to send over the socket protocol
     */
    public void sendThroughSocket(String destination, Object obj) {
        System.out.println("Sending object " + obj + "to " + destination);
        session.send(destination, obj);
    }

    /**
     * Does a get request on the mapping <code>api/wait</code> receiving a list of players that are
     * already in the waiting room.
     * It is used by the <code>WaitingRoomCtrl</code> class to initialize the
     * UI based on the list it receives.
     *
     * @return List of players that are currently in the waiting room
     */
    public List<Player> getAllNamesInWaitingRoom() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/wait")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public void postStartGame() {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/wait/start")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(null);
    }


    /**
     * Gets 20 question objects from the server
     * This method should be used to store the questions on the client side
     *
     * @param gameID id of the game
     * @return a list of 20 question retrieved from the server
     */
    public List<Question> getAllGameQuestions(int gameID) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/game/getQuestions/" + gameID) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    public Map<Integer, List<String>> getLeaderboard(int gameID) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/leaderboard/" + gameID)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Map<Integer, List<String>>>() {
                });
    }

    /**
     * This method is used by single players, who do not have a game ID
     * and just need to get 20 questions at the start of the game.
     *
     * @return 20 random questions
     */
    public ArrayList<Question> getRandomQuestions() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/wait/getRandomQuestions") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    public List<Score> getSingleLeaderboard() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/score") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Score>>() {
                });
    }

    public Score addScore(Score score) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/score") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(score, APPLICATION_JSON), Score.class);
    }

    public List<Activity> getAllActivities() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/data/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {});
    }

    public Activity deleteActivity(Activity activity) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/activities/delete")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    /**
     * Method that adds activity
     * It is called from AdminEditCtrl and works with addActivity in ActivityController
     * @param activity - the activity to be added 
     * @return
     */
    public Activity addAct(Activity activity) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/activities")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }
}
