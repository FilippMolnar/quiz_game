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
package client.jokers;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import client.controllers.MainAppController;
import client.controllers.questions.AbstractQuestion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import client.utils.ServerUtils;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Joker {
    public String name;
    public String imagePath;
    private boolean used;
    protected ServerUtils serverUtils;
    @SuppressWarnings("unused")

    // For object mapper
    private Joker() {}

    /**
     * Constructor for a Joker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public Joker(String name, String imagePath, ServerUtils serverUtils) {
        this.name = name;
        this.imagePath = imagePath;
        this.used = false;
        this.serverUtils = serverUtils;
    }

    public boolean isUsed(){
        return this.used;
    }

    public void use(){
        this.used = true;
    }

    /**
     * Method that is called when the joker is clicked
     * This uses the joker
     * This one is overwritten by all the joker types
     * @param mainCtrl - the MainAppController
     */
    public void onClick(MainAppController mainCtrl){
        System.out.println("Joker");
    }

    /**
     * Equals method for a Joker
     * @param obj - the object we are comparing to
     * @return if this and obj are equal (true/false)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, imagePath, used, serverUtils);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    public static void playSound(String dirPath) {
        String path = "src/main/resources/client/sounds/" + dirPath;
        File[] dir = new File(path).listFiles();
        int idx = new Random().nextInt(dir.length);
        Media sound = new Media(dir[idx].toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void markUsed(MainAppController mainCtrl){
        if(!used) return;
        List<Joker> jokers = mainCtrl.getJokers().getJokers();
        int idx = 1;
        for(int i=0; i<jokers.size(); i++){
            if(jokers.get(i).getClass() == this.getClass()){
                idx = i+1;
                break;
            }
        }
        AbstractQuestion qCtrl = (AbstractQuestion) mainCtrl.getCurrentScene().getController();
        switch (idx) {
            case 1 -> {
                qCtrl.getCircle1().setOpacity(0.5);
                qCtrl.getImage1().setOpacity(0.5);
            }
            case 2 -> {
                qCtrl.getCircle2().setOpacity(0.5);
                qCtrl.getImage2().setOpacity(0.5);
            }
            case 3 -> {
                qCtrl.getCircle3().setOpacity(0.5);
                qCtrl.getImage3().setOpacity(0.5);
            }
        }
    }
}
