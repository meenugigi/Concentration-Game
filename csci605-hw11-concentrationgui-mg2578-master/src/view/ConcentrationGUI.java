/*
Homework 11 : Concentration GUI
File Name : ConcentrationGUI.java
 */
package view;

import common.ConcentrationException;
import controller.ConcentrationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ConcentrationModel;
import model.Observer;
import java.io.*;
import java.util.*;
import static model.ConcentrationModel.*;

/**
 * The view is the visual presentation of the model that the user sees.
 * It registers itself as an observer of the model.
 * It can receive and display the current state of the model.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class ConcentrationGUI extends Application implements Observer<ConcentrationModel> {

    /** the model object */
    private ConcentrationModel model;
    /** the constructor object */
    private ConcentrationController controller;
    /** the gridpane to create buttons */
    private GridPane gridPane;
    /** the borderpane */
    private BorderPane borderPane;
    /** to display game stats */
    private HBox hBox;
    /** number of moves made */
    private Label numMovesMade;
    /** number of matches */
    private Label numMatches;
    /** game status */
    private static final String GAME_ON = "OK";
    /** game over */
    private static final String GAME_OVER = "GAME_OVER";
    /** game status */
    private String gameStatus;
    /** label to display game status */
    private Label gameStatusLabel;


    /**
     * Creates the model and add ourselves as an observer.
     * Takes and process command line arguments.
     * Creates the controller.
     *
     * @throws ConcentrationException   if invalid coordinates
     * @throws IOException              handles any IOExceptions
     * @throws InterruptedException     if thread interrupted
     *
     */
    @Override
    public void init() throws ConcentrationException, IOException, InterruptedException {
        this.model = new ConcentrationModel();
        model.addObserver(this);
//        get command line arguments.
        List<String> args = getParameters().getRaw();
        if (args.size() != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        String hostName = args.get(0);
        int portNumber = Integer.parseInt(args.get(1));
//        creates controller.
        controller = new ConcentrationController(hostName, portNumber);
        controller.begin();
    }


    /**
     * Creates the BorderPane.
     * Calls function to create a grid of discs and display game statistics.
     * Sets scene and stage title.
     *
     * @param stage     the stage
     *
     */
    @Override
    public void start(Stage stage){
        borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
//        display board statistics.
        displayInfo();
        Scene scene = new Scene(borderPane);
        stage.setTitle("Concentration GUI");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    /**
     * Creates board with face-down value of cards.
     * Sets button action on click cards.
     *
     * @param boardDimension    board dimension
     *
     */
    private void makeGrid(int boardDimension){
        gridPane = new GridPane();
        Image pokeball = new Image(getClass().getResourceAsStream("images" +
                "/pokeball.png"));
//        sets face down card image.
        for(int row = 0; row < boardDimension; row++) {
            for (int col = 0; col < boardDimension; col++) {
                Button button = new Button();
//                set image on buttons.
                button.setGraphic(new ImageView(pokeball));
                gridPane.add(button, col, row);
                int finalRow = row;
                int finalCol = col;
                button.setOnAction((event) -> {
//                    send coordinates to server.
                    try {
                        controller.sendToServer(finalRow, finalCol);
                    } catch (IOException | ConcentrationException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }


    /**
     * Display board statistics.
     *
     */
    private void displayInfo() {
        hBox = new HBox();
//        align hBox to center.
        hBox.setAlignment(Pos.CENTER);

//        to display number of moves.
        Label numMovesLabel = new Label("Moves: ");
        this.numMovesMade = new Label( String.valueOf( model.getNumMoves() ) );
//        to get appropriate amount of spacing.
        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);

//        to display number of matches.
        Label numMatchesLabel = new Label("Matches: ");
        this.numMatches = new Label( String.valueOf( model.getMatches() ) );
//        to get appropriate amount of spacing.
        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);

//        to display game status.
        this.gameStatusLabel = new Label(gameStatus);

//        add all labels to hBox.
        hBox = new HBox(numMovesLabel, numMovesMade, region1, numMatchesLabel,
                numMatches, region2, gameStatusLabel);
        borderPane.setBottom(hBox);
    }


    /**
     * Update model if current thread is JavaFX Application thread.
     * Else wait until current thread is JavaFX Application thread.
     *
     * @param model     the model
     * @param row       row
     * @param col       column
     * @param action    action to be performed
     *
     */
    public void update(ConcentrationModel model, int row, int col,
                       String action) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh(model, row, col, action);
        }
        else {
            Platform.runLater( () -> this.refresh( model, row, col, action) );
        }
    }


    /**
     * Creates initial grid on receiving board dimensions from model.
     * Reveal cards on a match.
     * Hides card if not a match.
     * Disables board at end of game.
     * Updates board statistics.
     *
     * @param model     the model
     * @param row       row
     * @param col       column
     * @param action    action to be performed
     *
     */
    private void refresh(ConcentrationModel model, int row, int col,
                         String action) {
//        create initial board.
        switch (action) {
            case CREATE -> makeGrid(row);
            case NOTMATCHED -> {
                Button button = updateCardImage(model, row, col);
                int finalRow = row;
                int finalCol = col;
//            allow reveal card if not matched.
                button.setOnAction((event) -> {
                    try {
                        controller.sendToServer(finalRow, finalCol);
                    } catch (IOException | ConcentrationException e) {
                        e.printStackTrace();
                    }
                });
                updateGameStats(model);
                break;
            }
            case MATCHED -> {
                Button button = updateCardImage(model, row, col);
//            disable button if card matched.
                button.setDisable(false);
                updateGameStats(model);
                break;
            }
        }
    }


    /**
     * Updates card depending on match or mismatch.
     *
     * @param model     the model
     * @param row       row
     * @param col       column
     * @return the updated button
     *
     */
    private Button updateCardImage(ConcentrationModel model, int row, int col) {
        Button button = new Button();
        Image displayImage =
                new Image(getClass().getResourceAsStream("images" + "/" + model.getCardImage()));
        button.setGraphic(new ImageView(displayImage));
        gridPane.add(button, col, row);
        return button;
    }


    /**
     * Updates game statistics.
     *
     * @param model     the model
     *
     */
    private void updateGameStats(ConcentrationModel model) {
        numMovesMade.setText(String.valueOf(model.getNumMoves()));
        numMatches.setText(String.valueOf(model.getMatches()));
        if (model.getGameOver()) {
            gameStatus = GAME_OVER;
            gridPane.setDisable(true);
        } else {
            gameStatus = GAME_ON;
        }
        gameStatusLabel.setText(gameStatus);
    }
}
