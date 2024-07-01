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
package commons;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    private String name;
    private String socketID;


    /**
     * Empty constructor for a Player
     */
    private Player() {
        // for object mapper
    }

    /**
     * Constructor for a Player
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Constructor for a Player
     */
    public Player(String name, String socketID) {
        this.name = name;
        this.socketID = socketID;
    }

    /**
     * Getter for the game ID
     * @return the game ID
     */
    public long getGameID() {
        return this.id;
    }

    /**
     * Setter for the game ID
     * @param id - the game ID
     */
    public void setGameID(int id) {
        this.id = id;
    }

    /**
     * Getter for the name
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the socket ID
     * @return the socket ID
     */
    public String getSocketID() {
        return this.socketID;
    }

    /**
     * Setter for the socket ID
     * @param socketID - the socket ID
     */
    public void setSocketID(String socketID) {this.socketID=socketID;}

    /**
     * Compares if obj is equal to this instance
     * @param obj - the object we are comparing
     * @return if this is equal to obj
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Player that) {
            return that.getName().equals(this.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return Human-readable format of this Class
     */
    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", socketID='" + socketID + '\'' +
                '}';
    }
}
