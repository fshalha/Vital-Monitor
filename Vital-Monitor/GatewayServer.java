//E/17/327
//SHALHA.A.M.F
//Mini Project-code

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//Gateway server to get data from UDP broad cast and to initiate TCP connections 

public class GatewayServer 
{
    static Vector<VitalMonitorHandler> ArrayOfVitalMonitors = new Vector<>();//vector to hold Monitor objects
    static List <String> listOfMonitorIDs=new ArrayList<String>(); //Array list to hold Monitor IDs which are connected to Gatewayserver via TCP
    
    static int MonitorCount = 0;//variable to hold monitor count
    public static void main(String[] args) throws IOException
    {
        //variables to hold Ip address,Monitor port and monitorID
        InetAddress sourceAddress; 
        int port;
        String monitorID;
        int BROADCAST_PORT = 6000;//UDP broadcast port
        
        //create new datagram socket to Listen on port 6000
        DatagramSocket datagramSocket = new DatagramSocket(BROADCAST_PORT) ;
        //byte array to hold receiving data from UDP broadcast
        byte[] receivedData = new byte[65535];
        //datagram packet to hold from UDP broadcast data
        DatagramPacket DpReceive = null;
        try {
            
            while (true)
            {
                //calling receiveDatagramPcaket function to to receive data
                DpReceive =receiveDatagramPcaket(datagramSocket, receivedData,DpReceive) ;
                // calling byteArraytoMonitor function to convert received byte array into monitor object
                Monitor monitor = byteArraytoMonitor(DpReceive.getData());
                //getting port,IP address and monitor ID of vital monitor
                port =monitor.getPort();
                sourceAddress = monitor.getIp();
                monitorID=monitor.getMonitorID();
                
                //if monitor is  not connected already
                if (!listOfMonitorIDs.contains(monitorID)){
                    
                    //calling addThread function to create a new thread for particular monitor, to initiate TCP connection
                    addThread(sourceAddress, port,monitorID);
                    //adding that monitor id to a list
                    listOfMonitorIDs.add(monitor.getMonitorID());
                    //clearing receiveData buffer
                    receivedData = new byte[65535];
                    //incrementing monitor count
                    MonitorCount++;
                }
            }
        } 
        finally {
            //close the udp datagram socket connection
            datagramSocket.close();
            System.out.println("Connection closed.");
        }
    }
    
    //method to get datagram packet from the socket and to return it
    public static  DatagramPacket receiveDatagramPcaket(DatagramSocket datagramSocket,byte[] receivedData,DatagramPacket DpReceive) throws SocketException {
        
        System.out.println("Vital Monitors connecting...");
        //receiving data
        DpReceive = new DatagramPacket(receivedData, receivedData.length);
        
        try {
            datagramSocket.receive(DpReceive);
        } catch (IOException e) {//error handling
            e.printStackTrace();
        }
        return DpReceive;//returning the packet
    }
    
    
    
    //method to convert received byte array to monitor object and return it
    private static Monitor byteArraytoMonitor(byte[] data) {
        Monitor monitor = null;
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            ObjectInputStream Objectinput = null;
            Objectinput = new ObjectInputStream(input);
            
            monitor = (Monitor) Objectinput.readObject();
        } catch (IOException e) {//error handling
            e.printStackTrace();
        }catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        }
        return monitor;
    }
    //method to create new thread when a new vital monitor is connected
    public static void addThread(InetAddress sourceAddress,int port,String monitorID)
    { 
        //creating new vital monitor handler class object
        VitalMonitorHandler newMonitor = new VitalMonitorHandler(sourceAddress,port,monitorID);
        
        // Create a new Thread with this object.
        Thread newThread = new Thread(newMonitor);
        System.out.println("Adding monitor to the monitor list");
        try{
            newThread.start();//start the thread
        } catch (Exception e) {
            System.out.println(
            "Another thread is not supported");//error handling
        }
        
        
        ArrayOfVitalMonitors.add(newMonitor);//adding monitor object to the array of vital monitors
        
    }
    
    
    
    
    
}

