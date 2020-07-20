package basicchat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.SecretKey;

public class GroupChatDialog extends javax.swing.JDialog {

    private final DateFormat dateFormat;
    private MulticastSocket socketGroup;
    private InetAddress localAddr, groupAddr;
    private SecretKey localKey, groupKey;
    private Socket socketKdc;
    private Thread receiveThread;

    public GroupChatDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    public void SessionInit(String remoteIp) {
        try {
            setTitle("Chat group " + remoteIp);

            if(ConnectKdcServer()) {
                if(ReceiveSecretKey()) {
                    localAddr = socketKdc.getLocalAddress();
                    groupAddr = InetAddress.getByName(remoteIp);

                    if(RequestGroupKey()) {
                        if(AuthenUserPwd()) {
                            if(ReceiveGroupKey()) {
                                WriteLog("Ready to start session thread.");
                                StartReceiveThread();
                            }
                        }
                    }
                }
            }
        }
        catch (UnknownHostException ex) {
            WriteLog(ex.getMessage());
        }
    }
    
    private boolean RequestGroupKey() {
        boolean success = true;
        try {
            String sessionInfo = localAddr.getHostAddress() + "," + groupAddr.getHostAddress();
            byte[] data = SecretHelper.EncryptString(sessionInfo, localKey);

            OutputStream outputStream = socketKdc.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            
            success = true;
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }
        return success;
    }
    
    private boolean AuthenUserPwd() {
        boolean success = true;
        String usr = ((ChatClient)this.getParent()).GetUserName();
        String pwd = ((ChatClient)this.getParent()).GetPassword();

        try {
            String autheninfo = usr + "~" + pwd;
            byte[] data = SecretHelper.EncryptString(autheninfo, localKey);

            OutputStream outputStream = socketKdc.getOutputStream();
            outputStream.write(data);
            outputStream.flush();

            success = true;
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }
    
    private boolean ReceiveGroupKey() {
        boolean success = true;
        groupKey = null;
        socketGroup = null;

        try {
            InputStream inputStream = socketKdc.getInputStream();

            byte[] buffer = new byte[1024];
            int byteRead = inputStream.read(buffer);
            if(byteRead >= 0) {
                byte[] groupKeyBytes = SecretHelper.DecryptBytes(buffer, byteRead, localKey);
                WriteLog("groupKeyBytes: " + SecretHelper.BytesToStringBase64(groupKeyBytes));

                groupKey = SecretHelper.regenerateKey(groupKeyBytes, 16);
                
                /**** Send connect and join multi-cast group ****/
                socketGroup = new MulticastSocket(new InetSocketAddress(localAddr,ChatConfig.PORT_MULTICAST));
                socketGroup.joinGroup(groupAddr);
                
                success = true;
            }
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }
    
    private void StartReceiveThread() {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        ReceiveMessage();
                    } 
                    catch (IOException ex) {
                        WriteLog("ERROR: " + ex.getMessage());
                        break;
                    }
                }
            }
        });

        receiveThread.start();
        WriteLog("StartReceiveThread started.");
    }
    
    private boolean SendMessage(String message) {
        boolean success = true;
        try {
            byte[] md5code = SecretHelper.MD5HashToBytes(message);
            byte[] keycode = SecretHelper.EncryptBytes(md5code, groupKey);
            
            WriteLog("keycode:" + SecretHelper.BytesToStringBase64(keycode));

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byteArray.write(keycode);
            byteArray.write(message.getBytes());

            byte[] data = byteArray.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, groupAddr, ChatConfig.PORT_MULTICAST);
            socketGroup.send(packet);

            success = true;
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }
    
    private boolean ReceiveMessage() throws IOException {
        boolean success = true;        

        int byteRead;
        byte[] buffer = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socketGroup.receive(packet);

        buffer = packet.getData();
        byteRead = packet.getLength();
        
        if(byteRead >= 32) {
            byte[] keycode1 = Arrays.copyOfRange(buffer, 0, 32);
            byte[] data = Arrays.copyOfRange(buffer, 32, byteRead);

            byte[] md5code = SecretHelper.MD5Hash(data);
            byte[] keycode2 = SecretHelper.EncryptBytes(md5code, groupKey);

            WriteLog("keycode1:" + SecretHelper.BytesToStringBase64(keycode1));
            WriteLog("keycode2:" + SecretHelper.BytesToStringBase64(keycode2));

            if(Arrays.equals(keycode1, keycode2)) {
                AppendMessage(new String(data));
                success = true;
            }
            else {
                WriteLog("Error on receive message.");
            }
        }

        return success;
    }
    
    private boolean ConnectKdcServer() {
        boolean success = false;
        try {
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
                success = true;
            }
        } 
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }
    
    private void AppendMessage(String message) {   
        if(!isVisible()) {
            setVisible(true);
        }
        
        if(!isActive()) {
            requestFocus();
        }

        messagePanel.append(String.format("[%s]: %s\n", dateFormat.format(new Date()), message));
    }

    private void WriteLog(String log) {
        ((ChatClient)this.getParent()).WriteLog(log);
    }
    
    public String getServiceIP() {
        String Ip = null;
    
        if(socketGroup != null) {
            Ip = socketGroup.getInetAddress().getHostAddress();
        }
       
        return Ip; 
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chatTextField = new javax.swing.JTextField();
        Send = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        messagePanel = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Group Chat");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        Send.setText("Send");
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        messagePanel.setEditable(false);
        messagePanel.setColumns(20);
        messagePanel.setRows(5);
        jScrollPane1.setViewportView(messagePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Send)
                    .addComponent(chatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
        if(socketGroup != null) {
            String message = chatTextField.getText();
            if(message.length() > 0) {
                SendMessage(message);
                chatTextField.setText("");
            }
        }
    }//GEN-LAST:event_SendActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        try {
            socketGroup.leaveGroup(groupAddr);
            ((ChatClient)getParent()).OnGroupChatDialogClose(this);
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }
    }//GEN-LAST:event_formWindowClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Send;
    private javax.swing.JTextField chatTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea messagePanel;
    // End of variables declaration//GEN-END:variables
}
