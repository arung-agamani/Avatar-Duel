package com.avatarduel.controller;

import com.avatarduel.event.*;
import com.avatarduel.model.Phase;
import com.avatarduel.model.Player;
import com.avatarduel.model.card.*;
import com.avatarduel.model.card.Character;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class BoardController implements Initializable, Subscriber {
    @FXML
    Pane hover_card_pane;

    @FXML
    VBox hover_card_view;

    @FXML
    VBox summoned_box;

    @FXML
    Label summoned_name, summoned_description;

    @FXML
    Pane player1_pane;

    @FXML
    Pane player2_pane;

    @FXML
    Label dp_label, mp1_label, bp_label, mp2_label, ep_label;

    Label[] phase_bar;

    AnchorPane player1_field, player2_field;


    /**
     * The model
     * It should be a class Game which has the deck, 2 players, and other game rules for each phase
     */
    Player player1, player2;
    PlayerFieldController player1_controller, player2_controller;

    StackPane hover_card_box;
    private CardController hover_card_controller;
    private EventChannel channel;

    public BoardController(EventChannel channel) {
        this.channel = channel;
        this.player1 = new Player(); // should be a singleton object
        this.player2 = new Player();
    }

    /**
     * For testing purposes
     */
    Phase[] phases = new Phase[]{Phase.DRAW, Phase.MAIN1, Phase.BATTLE, Phase.MAIN2, Phase.END};
    int phase_id = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            /**
             * Initialize the hover card pane
             */
            FXMLLoader hover_card_loader = new FXMLLoader(getClass().getResource("../view/Card.fxml"));
            hover_card_loader.setControllerFactory(c -> new CardController(this.channel));
            hover_card_box = hover_card_loader.load();

            hover_card_controller = hover_card_loader.getController();

            hover_card_box.prefHeightProperty().bind(hover_card_pane.prefHeightProperty());
            hover_card_box.prefWidthProperty().bind(hover_card_pane.prefWidthProperty());
            hover_card_pane.getChildren().add(hover_card_box);

            /**
             * Initialize player 1 field
             */
            FXMLLoader player1_loader = new FXMLLoader(getClass().getResource("../view/Player1Field.fxml"));
            player1_loader.setControllerFactory(c -> new PlayerFieldController(this.channel));
            player1_field = player1_loader.load();
            this.player1_controller = player1_loader.getController();
            this.player1_pane.getChildren().add(player1_field);

            /**
             * Initialize player 2 field
             */
            FXMLLoader player2_loader = new FXMLLoader(getClass().getResource("../view/Player2Field.fxml"));
            player2_loader.setControllerFactory(c -> new PlayerFieldController(this.channel));
            player2_field = player2_loader.load();
            this.player2_controller = player2_loader.getController();
            this.player2_pane.getChildren().add(player2_field);

            this.channel.addSubscriber(player1_controller, this);
            this.channel.addSubscriber(player2_controller, this);

            summoned_name.setText("");
            summoned_description.setText("");

            /**
             * Initialize the phase change mid bar
             */
            phase_bar = new Label[]{dp_label, mp1_label, bp_label, mp2_label, ep_label};
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public void setPlayer1(Player player) {
        try {
            this.player1 = player;
            player1_controller.setPlayer(this.player1);
        } catch (Exception e) {
            System.out.println("In Player Controller: " + e);
        }
    }

    public void setPlayer2(Player player) {
        this.player2 = player;
        player2_controller.setPlayer(this.player2);
        player2_controller.flipRow();
    }

    /**
     * For testing purposes.
     */
    public void drawBoth() {
        for (int i = 0; i < 7; i++) {
            player1_controller.draw();
            player2_controller.draw();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof NewCardDrawnEvent || event instanceof NewSummonedCardEvent) {
            CardController controller = (CardController) event.getInfo();
            this.channel.addSubscriber(controller, this.hover_card_controller);
        } else if (event instanceof HoverSummonedCardEvent) {
            Summoned summoned_card = (Summoned)event.getInfo();
            if (summoned_card == null) {
                this.summoned_name.setText("");
                this.summoned_description.setText("");
                return;
            }

            this.summoned_name.setText(summoned_card.getBaseCard().getName());
            String description = "";
            if (summoned_card instanceof SummonedCharacter) {
                description += "Net ATK: " + ((SummonedCharacter)summoned_card).getNetAtk() + "\n";
                description += "Net DEF: " + ((SummonedCharacter)summoned_card).getNetDef() + "\n";
                description += "Skills atttached:\n";
                if (((SummonedCharacter) summoned_card).getAttachedSkills().isEmpty()) {
                    description += "None\n";
                } else {
                    for (Skill skill: ((SummonedCharacter) summoned_card).getAttachedSkills()) {
                        description += skill.getName() + "\n";
                    }
                }
            }
            this.summoned_description.setText(description);
        }
    }

    public void proceedPhase(ActionEvent actionEvent) {
        phase_bar[phase_id].setStyle("-fx-background-color: darkgray;" +
                "-fx-color: dimgray");
        phase_id++;
        phase_id %= 5;
        phase_bar[phase_id].setStyle("-fx-background-color: aquamarine;" +
                "-fx-color: black");
        System.out.println(phases[phase_id]);
    }

//    public void addCardField(CardController card_controller) {
//        StackPane card_box = card_controller.getContent();
//
////        card_box.prefHeightProperty().bind(card_field.prefWidthProperty().divide(8));
////        card_field.add(card_box,col,row);
////        col++;
////        if (col==8){col=0;row++;}
//    }
//
//    public void updateDeck(IntegerProperty neff, int size){
//        // deck.getChildren().add(card_box);
//        neff_deck.textProperty().bind(Bindings.convert(neff));
//        size_deck.textProperty().setValue(Integer.toString(size));
//    }
}
