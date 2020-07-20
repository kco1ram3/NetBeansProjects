/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networkserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.crypto.SecretKey;
import networkchat.ChatConfig;
import networkchat.SecretHelper;

/**
 *
 * @author Administrator
 */
public class ChatServer extends javax.swing.JFrame {

    private final DateFormat dateFormat;
    private Thread listenThread;
    private final ArrayList<ClientInfo> clientList;
    private final ArrayList<SessionInfo> sessionList;

    /**
     * Creates new form ChatServer
     */
    public ChatServer() {
        initComponents();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        clientList = new ArrayList<>();
        sessionList = new ArrayList<>();

        StartListenThread();
    }

    private void StartListenThread() {
        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(ChatConfig.PORT_KDC_SERVER);

                    while (!Thread.currentThread().isInterrupted()) {
                        Socket socket = serverSocket.accept();
                        StartSocketThread(socket);
                    }

                } catch (IOException ex) {
                    WriteLog("Error: " + ex.getMessage());
                }
            }
        });

        listenThread.start();
        
        WriteLog("Server started.");
    }

    private void StartSocketThread(final Socket socket) {
        Thread socketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SecretKey key = GetSecretKey(socket);

                if (key == null) {
                    try {
                        key = SecretHelper.generateKey();
                        if (SendSecretKey(key, socket)) {
                            clientList.add(new ClientInfo(socket.getInetAddress().getHostAddress(), key));
                            WriteLog("Send key "
                                    + "(" + socket.getInetAddress().getHostAddress()
                                    + ":" + socket.getPort() + "): "
                                    + SecretHelper.BytesToStringBase64(key.getEncoded()));
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        WriteLog("Error: " + ex.getMessage());
                    }
                } else {
                    /*resend SecretKey*/
                    SendSecretKey(key, socket);
                }

                ReceiveClientRequest(key, socket);
                WriteLog("Socket closed (" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ")");
            }
        });

        socketThread.start();
        WriteLog("Socket accepted (" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ")");
    }

    private SecretKey GetSecretKey(Socket socket) {
        return GetSecretKey(socket.getInetAddress());
    }

    private SecretKey GetSecretKey(InetAddress address) {
        SecretKey key = null;

        String ip = address.getHostAddress();
        for (ClientInfo client : clientList) {
            if (ip.equals(client.getIP())) {
                key = client.getKey();
                break;
            }
        }

        return key;
    }

    private SecretKey GetSecretKeySession(InetAddress addrA, InetAddress addrB) {
        SecretKey key = null;

        String ipA = addrA.getHostAddress();
        String ipB = addrB.getHostAddress();

        for (SessionInfo session : sessionList) {
            if (ipA.equals(session.getIPA()) && ipB.equals(session.getIPB())) {
                key = session.getKey();
                break;
            } else if (ipA.equals(session.getIPB()) && ipB.equals(session.getIPA())) {
                key = session.getKey();
                break;
            }
        }

        return key;
    }

    private boolean SendSecretKey(SecretKey key, Socket socket) {
        boolean success = false;
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(key.getEncoded());
            outputStream.flush();
            success = true;
        } catch (IOException ex) {
            WriteLog("Error: " + ex.getMessage());
        }

        return success;
    }

    private boolean ReceiveClientRequest(SecretKey key, Socket socket) {
        boolean success = false;
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int byteRead = inputStream.read(buffer);

            if (byteRead >= 0) {
                String request = SecretHelper.DecryptString(buffer, byteRead, key);
                WriteLog("Request Info: " + request);
                String[] chatIPs = request.split(",");

                InetAddress ipSource = InetAddress.getByName(chatIPs[0]);
                InetAddress ipTarget = InetAddress.getByName(chatIPs[1]);

                if (RequestUserLogin(key, socket)) {
                    OutputStream outputStream = socket.getOutputStream();

                    SecretKey sessionKey = GetSecretKeySession(ipSource, ipTarget);
                    if (sessionKey == null) {
                        sessionKey = SecretHelper.generateKey();
                        sessionList.add(new SessionInfo(ipSource.getHostAddress(), ipTarget.getHostAddress(), sessionKey));
                    }

                    SecretKey targetKey = GetSecretKey(ipTarget);
                    if (targetKey != null) {
                        byte[] sessionKeyEncrypted = SecretHelper.EncryptBytes(sessionKey.getEncoded(), targetKey);
                        WriteLog("sessionKeyEncrypted: " + SecretHelper.BytesToStringBase64(sessionKeyEncrypted));

                        byte[] sessionKeyBytes = sessionKey.getEncoded();
                        WriteLog("sessionKeyBytes: " + SecretHelper.BytesToStringBase64(sessionKeyBytes));

                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                        byteArray.write(sessionKeyBytes);
                        byteArray.write(sessionKeyEncrypted);

                        byte[] resultKeyEncrypted = SecretHelper.EncryptBytes(byteArray.toByteArray(), key);
                        WriteLog("resultKeyEncrypted: " + SecretHelper.BytesToStringBase64(resultKeyEncrypted));

                        outputStream.write(resultKeyEncrypted);
                        outputStream.flush();
                    }

                    /*
                     if(ipTarget.isMulticastAddress()) {
                     //Multicast Handler
                     SecretKey groupKey = GetSecretKeyGroup(ipTarget);
                     if(groupKey == null) {
                     groupKey = SecretHelper.generateKey();
                     groupList.add(new GroupInfo(ipTarget.getHostAddress(), groupKey));
                     }
                        
                     byte[] groupKeyBytes = groupKey.getEncoded();
                     byte[] groupKeyEncrypted = SecretHelper.EncryptBytes(groupKeyBytes, key);
                     outputStream.write(groupKeyEncrypted);
                     outputStream.flush();

                     WriteLog("groupKeyBytes: " + SecretHelper.BytesToStringBase64(groupKeyBytes));
                     }
                     else {
                     SecretKey sessionKey = GetSecretKeySession(ipSource, ipTarget);
                     if(sessionKey == null) {
                     sessionKey = SecretHelper.generateKey();
                     sessionList.add(new SessionInfo(ipSource.getHostAddress(), ipTarget.getHostAddress(), sessionKey));
                     }

                     SecretKey targetKey = GetSecretKey(ipTarget);
                     if(targetKey != null) {
                     byte[] sessionKeyEncrypted = SecretHelper.EncryptBytes(sessionKey.getEncoded(), targetKey);
                     WriteLog("sessionKeyEncrypted: " + SecretHelper.BytesToStringBase64(sessionKeyEncrypted));

                     byte[] sessionKeyBytes = sessionKey.getEncoded();
                     WriteLog("sessionKeyBytes: " + SecretHelper.BytesToStringBase64(sessionKeyBytes));

                     ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                     byteArray.write(sessionKeyBytes);
                     byteArray.write(sessionKeyEncrypted);

                     byte[] resultKeyEncrypted = SecretHelper.EncryptBytes(byteArray.toByteArray(), key);
                     WriteLog("resultKeyEncrypted: " + SecretHelper.BytesToStringBase64(resultKeyEncrypted));

                     outputStream.write(resultKeyEncrypted);
                     outputStream.flush();
                     }
                     }
                     */
                }
            }
        } catch (IOException | NoSuchAlgorithmException ex) {
            WriteLog("Error: " + ex.getMessage());
        }

        return success;
    }

    private boolean RequestUserLogin(SecretKey key, Socket socket) {
        boolean success = false;
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int byteRead = inputStream.read(buffer);

            String usrpwd = SecretHelper.DecryptString(buffer, byteRead, key);
            WriteLog("User/Password: " + usrpwd);

            success = true;
        } catch (IOException ex) {
            WriteLog("Error: " + ex.getMessage());
        }
        return success;
    }

    public synchronized void WriteLog(String log) {
        this.logPanel.append(String.format("[%s]: %s\n", dateFormat.format(new Date()), log));
    }

    public class ClientInfo {

        private final String ip;
        private final SecretKey key;

        public ClientInfo(String ip, SecretKey key) {
            this.ip = ip;
            this.key = key;
        }

        public String getIP() {
            return ip;
        }

        public SecretKey getKey() {
            return key;
        }
    }

    public class SessionInfo {

        private final String ipA, ipB;
        private final SecretKey key;

        public SessionInfo(String ipA, String ipB, SecretKey key) {
            this.ipA = ipA;
            this.ipB = ipB;
            this.key = key;
        }

        public String getIPA() {
            return ipA;
        }

        public String getIPB() {
            return ipB;
        }

        public SecretKey getKey() {
            return key;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        logPanel = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Output : ");

        btnClear.setLabel("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        logPanel.setEditable(false);
        logPanel.setBackground(new java.awt.Color(204, 204, 204));
        logPanel.setColumns(20);
        logPanel.setRows(5);
        jScrollPane1.setViewportView(logPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClear)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        this.logPanel.setText("");
    }//GEN-LAST:event_btnClearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatServer().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logPanel;
    // End of variables declaration//GEN-END:variables
}
