/*
Homework 11 : Concentration GUI
File Name : Listener.java
 */
package controller;

import model.ConcentrationModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static common.ConcentrationProtocol.*;
import static common.ConcentrationProtocol.GAME_OVER;


/**
 * A Thread that listens to the server.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class Listener extends Thread{

    /** the bufferedReader to read data */
    private final BufferedReader in;
    /** board dimension */
    private final int boardDimension;


    /**
     * Constructor
     *
     * @param in    to read data
     * @param boardDimension    board dimension
     */
    public Listener(BufferedReader in, int boardDimension) {
        this.in = in;
        this.boardDimension = boardDimension;

    }


    /**
     * The run method.
     *
     */
    public void run() {
//        messages received from server.
        List<String> messagesFromServer;
//        flag to check if game is over.
        boolean gameNotOver = true;
//        to process second card.
        int flag_for_second_card = 0;
//        the model.
        ConcentrationModel board = new ConcentrationModel(boardDimension);
//          loop until game not over.
        do {
            messagesFromServer = new ArrayList<>();
            while (true) {
                try {
                    if (!in.ready()) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    messagesFromServer.add(in.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (String message : messagesFromServer) {
//                for card.
                if (message.startsWith(CARD)) {
                    System.out.println("from server : " + message);
                    flag_for_second_card++;
//                    Creates letter image association and updates view.
                    board.serverRevealsCardGUI(message);
                    if (flag_for_second_card >= 2) {
                        flag_for_second_card = 0;
                    }
                }
//                for match found.
                else if (message.startsWith(MATCH)) {
                    System.out.println("from server : " + message);
//                    display face up value of cards.
                    board.displayCardOnMatch(message);
                }
//                if not a match.
                else if (message.startsWith(MISMATCH)) {
                    System.out.println("from server : " + message);
//                    hide card if not a match.
                    board.hideCard(message);
                }
//                if error occurred.
                else if (message.startsWith(ERROR)) {
                    System.err.println(message);
                }
//                if game over.
                else if (message.startsWith(GAME_OVER)) {
                    board.setGameOver();
                    System.out.println(GAME_OVER);
                    gameNotOver = false;
                }
            }
        } while (gameNotOver);
    }
}
