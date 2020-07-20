/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networkclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.swing.DefaultListModel;
import networkchat.ChatConfig;
import networkchat.SecretHelper;

/**
 *
 * @author Administrator
 */
public class ChatClient extends javax.swing.JFrame {

    private final DateFormat dateFormat;
    private final ArrayList<ChatDialog> chatList;
    private final DefaultListModel listModel;
    private Thread listenThread, filerevThread;
    private Socket socketKdc;
    private SecretKey localKey;

    /**
     * Creates new form ChatClient
     */
    public ChatClient() {
        initComponents();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        listModel = new DefaultListModel();
        userList.setModel(listModel);
        chatList = new ArrayList<>();

        if (ConnectKdcServer()) {
            if (ReceiveSecretKey()) {
                try {
                    String localIP = socketKdc.getLocalAddress().getHostAddress();
                    InsertUserList("userA", localIP, "192.168.1.1");
                    InsertUserList("userB", localIP, "192.168.1.101");
                    InsertUserList("userC", localIP, "192.168.1.102");
                    InsertUserList("userD", localIP, "192.168.1.103");

                    socketKdc.close();
                    WriteLog("Disconnect KDC server ("
                            + socketKdc.getInetAddress().getHostAddress() + ":"
                            + socketKdc.getPort() + ")");
                } catch (IOException ex) {
                    WriteLog("Error on ChatClient/ReceiveSecretKey: " + ex.getMessage());
                }
            }
        }

        StartListenThread();
        StartFileRevThread();
    }

    private void StartListenThread() {
        final ChatClient parent = this;
        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(ChatConfig.PORT_CHAT_CLIENT);
                    while (!Thread.currentThread().isInterrupted()) {
                        Socket socket = serverSocket.accept();

                        WriteLog("New request chat ("
                                + socket.getInetAddress().getHostAddress() + ":"
                                + socket.getPort() + ")");

                        ChatDialog dialog = new ChatDialog(parent, false);
                        dialog.SessionInit(socket, localKey);
                        chatList.add(dialog);
                    }
                } catch (IOException ex) {
                    WriteLog("Error: " + ex.getMessage());
                }
            }
        });

        listenThread.start();
    }

    private void StartFileRevThread() {
        //final ChatClient parent = this;
        filerevThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(ChatConfig.PORT_FILE_LISTENER);
                    while (!Thread.currentThread().isInterrupted()) {
                        Socket socket = serverSocket.accept();

                        WriteLog("File receive notice ("
                                + socket.getInetAddress().getHostAddress() + ":"
                                + socket.getPort() + ")");

                        ChatDialog dialog = getChatDialog(socket.getInetAddress().getHostAddress());
                        if (dialog != null) {
                            dialog.FileReceiverThread(socket);
                        }
                    }
                } catch (IOException ex) {
                    WriteLog("Error: " + ex.getMessage());
                }
            }
        });

        filerevThread.start();
    }
    
    public String GetUserName() {
        return this.txtUsername.getText();
    }
    
    public String GetPassword() {
        return new String(this.txtPassword.getPassword());
    }

    private ChatDialog getChatDialog(String Ip) {
        ChatDialog chatdlg = null;
        for (ChatDialog cd : chatList) {
            if (cd.getServiceIP().equals(Ip)) {
                chatdlg = cd;
                break;
            }
        }

        return chatdlg;
    }

    private boolean ConnectKdcServer() {
        boolean success = false;
        try {
            WriteLog("Connect KDC server ("
                    + ChatConfig.IP_KDC_SERVER + ":"
                    + ChatConfig.PORT_KDC_SERVER + ")");

            socketKdc = new Socket(ChatConfig.IP_KDC_SERVER, ChatConfig.PORT_KDC_SERVER);
            success = true;
        } catch (IOException ex) {
            WriteLog(ex.getMessage());
        }
        return success;
    }

    private boolean ReceiveSecretKey() {
        boolean success = false;
        InputStream inputStream;
        try {
            inputStream = socketKdc.getInputStream();
            byte[] buffer = new byte[1024];
            int byteRead = inputStream.read(buffer);

            if (byteRead >= 0) {
                localKey = SecretHelper.regenerateKey(buffer, byteRead);
                WriteLog("Receive key: " + SecretHelper.BytesToStringBase64(localKey.getEncoded()));
                success = true;
            }
        } catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }

    private void InsertUserList(String userName, String localIP, String remoteIP) {
        if (!localIP.equals(remoteIP)) {
            listModel.addElement(new UserInfo(userName, remoteIP));
        } else {
            this.txtUsername.setText(userName);
            this.txtPassword.setText(userName);
        }
    }

    private void OpenChatDialog() {
        UserInfo user = (UserInfo) userList.getSelectedValue();
        if (user != null) {
            ChatDialog dialog = new ChatDialog(this, false);
            dialog.setVisible(true);
            dialog.SessionInit(user.getIP());
            chatList.add(dialog);
        }
    }

    public void OnChatDialogClose(ChatDialog chatdlg) {
        chatList.remove(chatdlg);
    }

    public final void WriteLog(String log) {
        this.logPanel.append(String.format("[%s]: %s\n", dateFormat.format(new Date()), log));
    }

    public class UserInfo {

        private final String userName;
        private final String userIP;

        public UserInfo(String userName, String userIP) {
            this.userName = userName;
            this.userIP = userIP;
        }

        public String getName() {
            return userName;
        }

        public String getIP() {
            return userIP;
        }

        @Override
        public String toString() {
            return userName + " (" + userIP + ")";
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
        txtUsername = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        logPanel = new javax.swing.JTextArea();
        btnChatP2P = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Username : ");

        jLabel2.setText("Password : ");

        userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(userList);

        jLabel3.setText("Output : ");

        logPanel.setEditable(false);
        logPanel.setBackground(new java.awt.Color(204, 204, 204));
        logPanel.setColumns(20);
        logPanel.setRows(5);
        jScrollPane2.setViewportView(logPanel);

        btnChatP2P.setText("Chat");
        btnChatP2P.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChatP2PActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnChatP2P))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChatP2P)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userListMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            OpenChatDialog();
        }
    }//GEN-LAST:event_userListMouseClicked

    private void btnChatP2PActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChatP2PActionPerformed
        // TODO add your handling code here:
        OpenChatDialog();
    }//GEN-LAST:event_btnChatP2PActionPerformed

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
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatClient().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChatP2P;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea logPanel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables
}
