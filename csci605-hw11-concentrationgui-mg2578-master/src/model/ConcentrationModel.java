/*
Homework 11 : Concentration GUI
File Name : ConcentrationModel.java
 */
package model;

import java.io.File;
import java.util.*;


/**
 * The model is responsible for managing the data and the rules of the application.
 * It receives input from the user (and network this case) from the controller.
 * When the state of the model changes, it notifies its view observers to update.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class ConcentrationModel {

    /** the image to be updated on card when revealed */
    private String cardImage;
    /** the board dimension */
    private int DIM;
    /** array to store all image file. */
    private String [] imageArray;
    /** map to store all letter - image pairs */
    private static Map<Character, String> map;
    /** number of card mattches */
    private int matches;
    /** number of moves made */
    private int numMoves;
    /** flag to check if game over */
    private boolean gameOver = false;
    /** the observers of this model */
    private static List<Observer<ConcentrationModel>> observers;
    /** if card match */
    public static final String MATCHED = "matched";
    /** if cards do not match */
    public static final String NOTMATCHED = "not_matched";
    /** create initial board */
    public static final String CREATE = "create_board";
    /** face down card value */
    private static final String DEFAULT_IMAGE = "pokeball.png";


    /**
     * Constructor.
     *
     */
    public ConcentrationModel() {}


    /**
     * Constructor.
     *
     * @param boardDimension    board dimension
     */
    public ConcentrationModel(int boardDimension) {
        this.DIM = boardDimension;
    }


    /**
     * Get number of matches.
     *
     * @return matches
     */
    public int getMatches() {
        return matches;
    }

    /**
     * Get number of moves made.
     *
     * @return number of moves
     */
    public int getNumMoves() {
        return numMoves;
    }


    /**
     * Get image to be updated on card.
     *
     * @return image to be updated
     */
    public String getCardImage() {
        return cardImage;
    }


    /**
     * Get board dimension.
     *
     * @return board dimension
     */
    public int getDIM(){
        return DIM;
    }


    /**
     * Sets gameOver value.
     *
     */
    public void setGameOver() {
        gameOver = true;
    }


    /**
     * Gets gameover value.
     *
     * @return true, if game over. Else, false.
     *
     */
    public boolean getGameOver(){
        return gameOver;
    }


    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     *
     */
    public void addObserver(Observer<ConcentrationModel> observer) {
        observers = new LinkedList<>();
        observers.add(observer);

    }


    /**
     * When the model changes, the observers are notified via their update()
     * method.
     *
     * @param row       row
     * @param col       column
     * @param action    action to be performed
     *
     */
    private void notifyObservers(int row, int col, String action) {
        for (Observer<ConcentrationModel> obs: observers ) {
            obs.update(this, row, col, action);
        }
    }


    /**
     * Creates initial board.
     *
     * @param boardDimension    board dimension
     */
    public void createBoard(int boardDimension) {
        notifyObservers(boardDimension, 0, CREATE);
    }


    /**
     * Stores all image names in a list.
     * Removes face down value of card from final list of images.
     *
     * @param boardDimension    board dimension
     *
     */
    public void loadImagesInGrid(int boardDimension){
        imageArray = new String[(boardDimension * boardDimension)/2];
        File f = new File(System.getProperty("user.dir"));
        List<String> allImages = new ArrayList<>();
//        store all images from image directory into list.
        List<String> faceUpCardImages = List.of(f.list());
//        remove face-down card value from list.
        for(String name : faceUpCardImages){
            if(!name.equals(DEFAULT_IMAGE)){
                allImages.add(name);
            }
        }
//        populate array with required images based on board dimension.
        for(int j = 0; j< (boardDimension * boardDimension)/2; j++){
            imageArray[j] = allImages.get(j);
        }
        letterImagePair(boardDimension);
    }


    /**
     * Creates letter image association.
     *
     * @param boardDimension    board dimension
     *
     */
    private void letterImagePair(int boardDimension) {
        map = new HashMap<>();
//        ASCII value for character 'A'.
        int value = 65;
        for (int i = 0; i < (boardDimension * boardDimension)/2; i++) {

            map.put((char) value, imageArray[i]);
            value++;
        }
    }


    /**
     * Creates letter image association.
     *
     * @param message    input received from server
     *
     */
    public void serverRevealsCardGUI(String message){
        String[] messageArray = message.split(" ");
        int row = Integer.parseInt(messageArray[1]);
        int col = Integer.parseInt(messageArray[2]);
        char cardCharacterString = messageArray[3].charAt(0);
//        get image associated with the card letter.
        if(map.containsKey(cardCharacterString)){
            cardImage = map.get(cardCharacterString);
        }
        numMoves ++;
//        notify observers to update card value.
        notifyObservers(row, col, NOTMATCHED);
    }


    /**
     * Hide cards if not a match.
     *
     * @param message    input received from server
     *
     */
    public void hideCard(String message){
        String[] messageArray = message.split(" ");
        int card1_row = Integer.parseInt(messageArray[1]);
        int card1_col = Integer.parseInt(messageArray[2]);
        int card2_row = Integer.parseInt(messageArray[3]);
        int card2_col = Integer.parseInt(messageArray[4]);
        cardImage = DEFAULT_IMAGE;
//        notify observers to update card with face down card value.
        notifyObservers(card1_row, card1_col, NOTMATCHED);
        notifyObservers(card2_row, card2_col, NOTMATCHED);
    }


    /**
     * Hide cards if not a match.
     *
     * @param message    input received from server
     *
     */
    public void displayCardOnMatch(String message){
        String[] messageArray = message.split(" ");
        int card1_row = Integer.parseInt(messageArray[1]);
        int card1_col = Integer.parseInt(messageArray[2]);
        int card2_row = Integer.parseInt(messageArray[3]);
        int card2_col = Integer.parseInt(messageArray[4]);
        matches ++;
//        notify observers to update card with face up card value.
        notifyObservers(card1_row, card1_col, MATCHED);
        notifyObservers(card2_row, card2_col, MATCHED);
    }
}




