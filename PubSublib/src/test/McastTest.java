package test;

import java.net.*;
import java.io.*;

public class McastTest {

    public static void main(String[] args) {

        int mcastPort = 6000;

        InetAddress mcastAddr = null;

        try {

            mcastAddr = InetAddress.getByName("230.0.0.1");

        } catch (UnknownHostException uhe) {

            System.out.println("Problems getting the symbolic multicast address");

            uhe.printStackTrace();
            System.exit(1);

        }

        // start new thread to receive multicasts

        new Thread(new McastReceiver(mcastPort, mcastAddr),
                "McastReceiver").start();

        // start new thread to send multicasts

        new Thread(new McastRepeater(mcastPort, mcastAddr),
                "McastRepeater").start();

    }
}

class McastReceiver implements Runnable {

    int mcastPort = 0;
    InetAddress mcastAddr = null;
    InetAddress localHost = null;

    public McastReceiver(int port, InetAddress addr) {

        mcastPort = port;

        mcastAddr = addr;

        try {

            localHost = InetAddress.getLocalHost();

        } catch (UnknownHostException uhe) {

            System.out.println("Problems identifying local host");

            uhe.printStackTrace();
            System.exit(1);

        }

    }

    public void run() {

        MulticastSocket mSocket = null;

        try {

            System.out.println("Setting up multicast receiver");

            mSocket = new MulticastSocket(mcastPort);

            mSocket.joinGroup(mcastAddr);

        } catch (IOException ioe) {

            System.out.println("Trouble opening multicast port");

            ioe.printStackTrace();
            System.exit(1);

        }



        DatagramPacket packet;

        System.out.println("Multicast receiver set up ");

        while (true) {

            try {

                byte[] buf = new byte[1000];

                packet = new DatagramPacket(buf, buf.length);

                //System.out.println("McastReceiver: waiting for packet");

                mSocket.receive(packet);



                ByteArrayInputStream bistream =
                        new ByteArrayInputStream(packet.getData());

                ObjectInputStream ois = new ObjectInputStream(bistream);

                Integer value = (Integer) ois.readObject();



                // ignore packets from myself, print the rest

                if (!(packet.getAddress().equals(localHost))) {

                    System.out.println("Received multicast packet: "
                            + value.intValue() + " from: " + packet.getAddress());

                }

                ois.close();

                bistream.close();

            } catch (IOException ioe) {

                System.out.println("Trouble reading multicast message");

                ioe.printStackTrace();
                System.exit(1);

            } catch (ClassNotFoundException cnfe) {

                System.out.println("Class missing while reading mcast packet");

                cnfe.printStackTrace();
                System.exit(1);

            }

        }

    }
}

class McastRepeater implements Runnable {

    private DatagramSocket dgramSocket = null;
    int mcastPort = 0;
    InetAddress mcastAddr = null;
    InetAddress localHost = null;

    McastRepeater(int port, InetAddress addr) {

        mcastPort = port;

        mcastAddr = addr;

        try {

            dgramSocket = new DatagramSocket();

        } catch (IOException ioe) {

            System.out.println("problems creating the datagram socket.");

            ioe.printStackTrace();
            System.exit(1);

        }

        try {

            localHost = InetAddress.getLocalHost();

        } catch (UnknownHostException uhe) {

            System.out.println("Problems identifying local host");

            uhe.printStackTrace();
            System.exit(1);

        }



    }

    public void run() {

        DatagramPacket packet = null;

        int count = 0;



        // send multicast msg once per second 

        while (true) {

            // careate the packet to sned.

            try {

                // serialize the multicast message

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                ObjectOutputStream out = new ObjectOutputStream(bos);

                out.writeObject(new Integer(count++));

                out.flush();

                out.close();



                // Create a datagram packet and send it

                packet = new DatagramPacket(bos.toByteArray(),
                        bos.size(),
                        mcastAddr,
                        mcastPort);



                // send the packet

                dgramSocket.send(packet);

                System.out.println("sending multicast message");

                Thread.sleep(1000);

            } catch (InterruptedException ie) {

                ie.printStackTrace();

            } catch (IOException ioe) {

                System.out.println("error sending multicast");

                ioe.printStackTrace();
                System.exit(1);

            }

        }
    }
}