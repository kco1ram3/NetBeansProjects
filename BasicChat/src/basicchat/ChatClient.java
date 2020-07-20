package basicchat;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.swing.DefaultListModel;
import javax.swing.JScrollBar;

public class ChatClient extends javax.swing.JFrame {
    
    private final DateFormat dateFormat;
    private final ArrayList<ChatDialog> chatList;
    private final ArrayList<GroupChatDialog> groupChatList;
    private final DefaultListModel listModel, listGroupModel;
    private Thread listenThread, filerevThread;
    private Socket socketKdc;
    private SecretKey localKey;

    private void StartListenThread() {
        final ChatClient parent = this;
        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(ChatConfig.PORT_CHAT_CLIENT);
                    while(!Thread.currentThread().isInterrupted()) {
                        Socket socket = serverSocket.accept();
                        
                        WriteLog("New request chat (" 
                                + socket.getInetAddress().getHostAddress() + ":" 
                                + socket.getPort() + ")");

                        ChatDialog dialog = new ChatDialog(parent, false);
                        dialog.SessionInit(socket, localKey);
                        chatList.add(dialog);
                    }
                } 
                catch (IOException ex) {
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
                    while(!Thread.currentThread().isInterrupted()) {
                        Socket socket = serverSocket.accept();
                        
                        WriteLog("File receive notice (" 
                                + socket.getInetAddress().getHostAddress() + ":" 
                                + socket.getPort() + ")");
                        
                        ChatDialog chatdlg = getChatDialog(socket.getInetAddress().getHostAddress());
                        if(chatdlg != null) {
                            chatdlg.FileReceiverThread(socket);
                        }
                    }
                } 
                catch (IOException ex) {
                    WriteLog("Error: " + ex.getMessage());
                }
            }
        });

        filerevThread.start();
    }
    
    private ChatDialog getChatDialog(String Ip) {
        ChatDialog chatdlg = null;
        for(ChatDialog cd : chatList) {
            if(cd.getServiceIP().equals(Ip)) {
                chatdlg = cd;
                break;
            }
        }

        return chatdlg;
    }
    
    private GroupChatDialog getGroupChatDialog(String Ip) {
        GroupChatDialog groupdlg = null;
        for(GroupChatDialog cd : groupChatList) {
            if(cd.getServiceIP().equals(Ip)) {
                groupdlg = cd;
                break;
            }
        }        
        
        return groupdlg;
    }

    private boolean ConnectKdcServer() {
        boolean success = false;
        try {
            WriteLog("Connect KDC server (" 
                    + ChatConfig.IP_KDC_SERVER + ":" 
                    + ChatConfig.PORT_KDC_SERVER + ")");

            socketKdc = new Socket(ChatConfig.IP_KDC_SERVER, ChatConfig.PORT_KDC_SERVER);
            success = true;
        }
        catch (IOException ex) {
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
            
            if(byteRead >= 0) {
                localKey = SecretHelper.regenerateKey(buffer, byteRead);
                WriteLog("Receive key: " + SecretHelper.BytesToStringBase64(localKey.getEncoded()));
                success = true;
            }
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }
    
    public ChatClient() {
        initComponents();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
        listModel = new DefaultListModel();
        listGroupModel = new DefaultListModel();
        userList.setModel(listModel);
        groupList.setModel(listGroupModel);
        chatList = new ArrayList<>();
        groupChatList = new ArrayList<>();

        if(ConnectKdcServer()) {
            if(ReceiveSecretKey()) {
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
                }
                catch (IOException ex) {
                    WriteLog("Error on ChatClient/ReceiveSecretKey: " + ex.getMessage());
                }
            }
            
            listGroupModel.addElement(new UserInfo("Group A", "228.5.6.7"));
            listGroupModel.addElement(new UserInfo("Group B", "228.5.6.8"));
            listGroupModel.addElement(new UserInfo("Group C", "228.5.6.9"));
        }
        
        StartListenThread();
        StartFileRevThread();
    }
    
    private void InsertUserList(String userName, String localIP, String remoteIP) {
        if(!localIP.equals(remoteIP)) {
            listModel.addElement(new UserInfo(userName, remoteIP));
        }
        else {
            usrTextField.setText(userName);
            pwdTextField.setText(userName);
        }
    }

    public String GetUserName() {
        return usrTextField.getText();
    }
    
    public String GetPassword() {
        return new String(pwdTextField.getPassword());
    }
    
    private void OpenChatDialog() {
        UserInfo user = (UserInfo)userList.getSelectedValue();
        if(user != null) {
            ChatDialog dialog = getChatDialog(user.getIP());
            if(dialog == null) {
                dialog = new ChatDialog(this, false);
                dialog.setVisible(true);
                dialog.SessionInit(user.getIP());
                chatList.add(dialog);
            }
            else {
                if(!dialog.isVisible()) {
                    dialog.setVisible(true);
                }
                if(!dialog.isActive()) {
                    dialog.requestFocus();
                }
            }
        }
    }
    
    public void OnChatDialogClose(ChatDialog dialog) {
        chatList.remove(dialog);
    }

    private void OpenGroupChatDialog() {
        UserInfo user = (UserInfo)groupList.getSelectedValue();
        if(user != null) {
            GroupChatDialog dialog = getGroupChatDialog(user.getIP());
            if(dialog == null) {
                dialog = new GroupChatDialog(this, false);
                dialog.setVisible(true);
                dialog.SessionInit(user.getIP());
                groupChatList.add(dialog);
            }
            else {
                if(!dialog.isVisible()) {
                    dialog.setVisible(true);
                }
                if(!dialog.isActive()) {
                    dialog.requestFocus();
                }                
            }
        }        
    }

    public void OnGroupChatDialogClose(GroupChatDialog dialog) {
        groupChatList.remove(dialog);
    }

    public final void WriteLog(String log) {
        logPanel.append(String.format("[%s]: %s\n"
                , dateFormat.format(new Date())
                , log));
        
        JScrollBar scrollbar = logScrollPanel.getVerticalScrollBar();
        scrollbar.setValue(scrollbar.getMaximum());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        usrTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pwdTextField = new javax.swing.JPasswordField();
        logScrollPanel = new javax.swing.JScrollPane();
        logPanel = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        Add = new javax.swing.JButton();
        Chat = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        Delete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        groupList = new javax.swing.JList();
        ChatMulticast = new javax.swing.JButton();
        AddMulticast = new javax.swing.JButton();
        DeleteMulticast = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat Client");
        setLocationByPlatform(true);

        jLabel1.setText("User Name:");

        jLabel2.setText("Password:");

        logPanel.setEditable(false);
        logPanel.setBackground(new java.awt.Color(204, 204, 204));
        logPanel.setColumns(20);
        logPanel.setRows(5);
        logScrollPanel.setViewportView(logPanel);

        jLabel3.setText("Output:");

        Add.setText("Add");
        Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddActionPerformed(evt);
            }
        });

        Chat.setText("Chat");
        Chat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatActionPerformed(evt);
            }
        });

        userList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(userList);

        Delete.setText("Delete");
        Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Chat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Add)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Delete)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Chat)
                    .addComponent(Add)
                    .addComponent(Delete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Users", jPanel1);

        groupList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        groupList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                groupListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(groupList);

        ChatMulticast.setText("Chat");
        ChatMulticast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatMulticastActionPerformed(evt);
            }
        });

        AddMulticast.setText("Add");
        AddMulticast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddMulticastActionPerformed(evt);
            }
        });

        DeleteMulticast.setText("Delete");
        DeleteMulticast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteMulticastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ChatMulticast)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddMulticast)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteMulticast)
                        .addGap(0, 328, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ChatMulticast)
                    .addComponent(AddMulticast)
                    .addComponent(DeleteMulticast))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Groups", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(logScrollPanel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(usrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pwdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(usrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(pwdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userListMouseClicked
        if(evt.getClickCount() == 2) {
            OpenChatDialog();
        }
    }//GEN-LAST:event_userListMouseClicked

    private void AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddActionPerformed
        AddDialog dialog = new AddDialog(this, true);
        dialog.setVisible(true);

        if(dialog.targetIP.length() > 0 && dialog.targetName.length() > 0) {
            try {
                if(!InetAddress.getByName(dialog.targetIP).isMulticastAddress()) {
                    UserInfo user = new UserInfo(dialog.targetName, dialog.targetIP);                
                    listModel.addElement(user);
                }
            } 
            catch (UnknownHostException ex) {
                WriteLog("AddActionPerformed: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_AddActionPerformed

    private void ChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatActionPerformed
        OpenChatDialog();
    }//GEN-LAST:event_ChatActionPerformed

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteActionPerformed
        UserInfo user = (UserInfo)userList.getSelectedValue();
        if(user != null) {
            listModel.removeElement(user);
        }
    }//GEN-LAST:event_DeleteActionPerformed

    private void ChatMulticastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatMulticastActionPerformed
        WriteLog("ChatMulticastActionPerformed");
        OpenGroupChatDialog();
    }//GEN-LAST:event_ChatMulticastActionPerformed

    private void AddMulticastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMulticastActionPerformed
        WriteLog("AddMulticastActionPerformed");

        AddDialog dialog = new AddDialog(this, true);
        dialog.setVisible(true);

        if(dialog.targetIP.length() > 0 && dialog.targetName.length() > 0) {
            try {
                if(InetAddress.getByName(dialog.targetIP).isMulticastAddress()) {
                    UserInfo user = new UserInfo(dialog.targetName, dialog.targetIP);                
                    listGroupModel.addElement(user);
                }
            } 
            catch (UnknownHostException ex) {
                WriteLog("AddMulticastActionPerformed: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_AddMulticastActionPerformed

    private void DeleteMulticastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteMulticastActionPerformed
        UserInfo user = (UserInfo)groupList.getSelectedValue();
        if(user != null) {
            listGroupModel.removeElement(user);
        }
    }//GEN-LAST:event_DeleteMulticastActionPerformed

    private void groupListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_groupListMouseClicked
        if(evt.getClickCount() == 2) {
            OpenGroupChatDialog();
        }
    }//GEN-LAST:event_groupListMouseClicked

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

    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatClient().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Add;
    private javax.swing.JButton AddMulticast;
    private javax.swing.JButton Chat;
    private javax.swing.JButton ChatMulticast;
    private javax.swing.JButton Delete;
    private javax.swing.JButton DeleteMulticast;
    private javax.swing.JList groupList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea logPanel;
    private javax.swing.JScrollPane logScrollPanel;
    private javax.swing.JPasswordField pwdTextField;
    private javax.swing.JList userList;
    private javax.swing.JTextField usrTextField;
    // End of variables declaration//GEN-END:variables
}
