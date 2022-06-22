/*
Homework 11 : Concentration GUI
File Name : ConcentrationController.java
 */
package controller;

import common.ConcentrationException;
import model.ConcentrationModel;
import java.io.*;
import java.net.Socket;
import static common.ConcentrationProtocol.*;

/**
 * The controller is set up to respond to input from the user (and the network in this case).
 * It responds by interacting with the model to cause changes to its state.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class ConcentrationController {

    /** board dimension */
    private int boardDimension = 0;
    /** socket object */
    private Socket kkSocket;
    /** the hostname */
    private final String hostName;
    /** the port number */
    private final int portNumber;


    /**
     * Constructor
     *
     * @param hostName      the hostname
     * @param portNumber    the port number
     *
     */
    public ConcentrationController(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }


    /**
     * Creates a socket connection
     * Starts the listener thread.
     *
     * @throws IOException to handle any IOExceptions
     *
     */
    public void begin() throws IOException{
//        creates socket connection
            kkSocket = new Socket(hostName, portNumber);
//            to read data from stream
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(kkSocket.getInputStream()));

            String fromServer;
            fromServer = in.readLine();
//            process board dimension data received from server.
            if(fromServer.startsWith(BOARD_DIM)){
                System.out.println("From server: " +fromServer);
                System.out.println();
                String[] messageArray = fromServer.split(" ");

                this.boardDimension = Integer.parseInt(messageArray[1]);
//                the model
                ConcentrationModel board = new ConcentrationModel(boardDimension);
//                load images
                board.loadImagesInGrid(boardDimension);
//                create initial board.
                board.createBoard(boardDimension);
//                create listener thread.
//                start listener.
                Listener listener = new Listener(in, boardDimension);
                listener.start();
            }
    }


    /**
     * Sends card coordinates to server
     *
     * @param row       the row
     * @param col       the column
     * @throws IOException to handle any IOExceptions.
     * @throws ConcentrationException for board related errors.
     *
     */
    public void sendToServer(int row, int col) throws IOException,
            ConcentrationException {
//        to write to stream
        PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
//        for invalid input on Plain-text game version.
        try {
            if(row >= boardDimension || col >= boardDimension){
                throw new ConcentrationException("out of bounds.");
            }
//            send message to server.
            else {
                String sendToServer = messageToServer(row, col);
                System.out.println("To server: " + sendToServer);
                out.println(sendToServer);
            }
        }
        catch (ConcentrationException e){
            System.err.println(e.getMessage());
        }
    }


    /**
     * Format string to be sent to server.
     *
     * @param row       the row
     * @param col       the column
     * @return String to be sent to server.
     *
     */
    private String messageToServer(int row, int col){
        return String.format(REVEAL_MSG, row, col);
    }
}

