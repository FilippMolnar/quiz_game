package client.jokers;

import client.controllers.MainAppController;
import client.controllers.questions.QuestionInsertNumberCtrl;
import client.controllers.questions.QuestionMultiOptionsCtrl;
import client.controllers.questions.QuestionSameAsCtrl;
import client.utils.ServerUtils;
import commons.Activity;
import commons.Question;

import java.util.ArrayList;

public class ElimWrongJoker extends Joker {

    /**
     * Constructor for an ElimWrongJoker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public ElimWrongJoker(String name, String imagePath, ServerUtils serverUtils) {
        super(name, imagePath, serverUtils);
    }

    /**
     * Method that is called when the joker is clicked
     * This uses the joker
     * @param mainCtrl - the MainAppController
     */
    public void onClick(MainAppController mainCtrl) {
        if (isUsed()) {
            return;
        }
        if (mainCtrl.getCurrentScene().getController() instanceof QuestionInsertNumberCtrl qCtrl) {
            return;
        }

        playSound("remove-answer");
        Question question = mainCtrl.getCurrentQuestion();

        ArrayList<Integer> wrong_options = new ArrayList<>();
        if (mainCtrl.getCurrentScene().getController() instanceof QuestionMultiOptionsCtrl qCtrl) {
            int i = 0;
            for (Activity a : question.getChoices()) {
                if (a.id != question.getCorrect().id) {
                    wrong_options.add(i);
                }
                i++;
            }
        }

        if (mainCtrl.getCurrentScene().getController() instanceof QuestionSameAsCtrl qCtrl) {
            int i = 0;
            for (Activity a : question.getChoices()) {
                if(i==3){
                    break;
                }
                if (a.id != question.getCorrect().id) {
                    wrong_options.add(i);
                }
                i++;
            }
        }


        int index = (int) (Math.random() * wrong_options.size());
        if (mainCtrl.getCurrentScene().getController() instanceof QuestionMultiOptionsCtrl qCtrl) {
            switch (wrong_options.get(index)) {
                case 0 -> qCtrl.getOptionA().setDisable(true);
                case 1 -> qCtrl.getOptionB().setDisable(true);
                case 2 -> qCtrl.getOptionC().setDisable(true);
            }
            use();
            markUsed(mainCtrl);
        }
        if (mainCtrl.getCurrentScene().getController() instanceof QuestionSameAsCtrl qCtrl2) {
            switch (wrong_options.get(index)) {
                case 0 -> qCtrl2.getOptionA().setDisable(true);
                case 1 -> qCtrl2.getOptionB().setDisable(true);
                case 2 -> qCtrl2.getOptionC().setDisable(true);
            }
            use();
            markUsed(mainCtrl);
        }
    }
}
