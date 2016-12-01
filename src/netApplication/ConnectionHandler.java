/* The Connection Handler Class - Written by Derek Molloy for the EE402 Module
 * See: ee402.eeng.dcu.ie
 */

package netApplication;

import java.net.*;
import java.io.*;

public class ConnectionHandler
{
    private Socket clientSocket = null;				// Client socket object
    private ObjectInputStream is = null;			// Input stream
    private ObjectOutputStream os = null;			// Output stream
    private DateTimeService theDateService;
    
	// The constructor for the connection handler
    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket; //sets the clientSocket field to the one the function just received
        //Set up a service object to get the current date and time
        theDateService = new DateTimeService();
    }

    // Will eventually be the thread execution method - can't pass the exception back
    public void init() {
         try {
            this.is = new ObjectInputStream(clientSocket.getInputStream());//input stream = client input stream
            this.os = new ObjectOutputStream(clientSocket.getOutputStream());//output stream = client output stream
            while (this.readCommand()) {}
         } 
         catch (IOException e) 
         {
        	System.out.println("XX. There was a problem with the Input/Output Communication:");
            e.printStackTrace();
         }
    }

    // Receive and process incoming string commands from client socket 
    private boolean readCommand() {
        String s = null;
        try {
            s = (String) is.readObject(); //what is the object received ???
        } 
        catch (Exception e){    // catch a general exception
        	this.closeSocket();
            return false;
        }
        System.out.println("01. <- Received a String object from the client (" + s + ").");
        
        // At this point there is a valid String object
        // invoke the appropriate function based on the command 
        if (s.equalsIgnoreCase("GetDate")){ 
            this.getDate(); 
        }       
        else { 
            this.sendError("Invalid command: " + s); 
        }
        return true;
    }

    // Use our custom DateTimeService Class to get the date and time
    private void getDate() {	// use the date service to get the date
        String currentDateTimeText = theDateService.getDateAndTime();
        this.send(currentDateTimeText);
    }

    // Send a generic object back to the client 
    private void send(Object o) {
        try {
            System.out.println("02. -> Sending (" + o +") to the client.");
            this.os.writeObject(o);
            this.os.flush();
        } 
        catch (Exception e) {
            System.out.println("XX." + e.getStackTrace());
        }
    }
    
    // Send a pre-formatted error message to the client 
    public void sendError(String message) { 
        this.send("Error:" + message);	//remember a String IS-A Object!
    }
    
    // Close the client socket 
    public void closeSocket() { //gracefully close the socket connection
        try {
            this.os.close();
            this.is.close();
            this.clientSocket.close();
        } 
        catch (Exception e) {
            System.out.println("XX. " + e.getStackTrace());
        }
    }
}