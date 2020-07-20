package basicchat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.crypto.SecretKey;
import javax.swing.JFileChooser;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatDialog extends javax.swing.JDialog {

    private final DateFormat dateFormat;
    private InetAddress localAddr, remoteAddr;
    private SecretKey localKey, sessionKey;
    private Socket socketKdc, socketRemote;
    private Thread receiveThread, receiveFileThread, sendFileThread;
    
    public ChatDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    public String getServiceIP() {
        String serviceIP = null;
        
        if(remoteAddr != null) {
            serviceIP = remoteAddr.getHostAddress();
        }

        return serviceIP;
    }
    
    public void SessionInit(String remoteIp) {
        try {
            setTitle("Chat with " + remoteIp);

            if(ConnectKdcServer()) {
                if(ReceiveSecretKey()) {
                    localAddr = socketKdc.getLocalAddress();
                    remoteAddr = InetAddress.getByName(remoteIp);

                    if(RequestSessionKey()) {
                        if(AuthenUserPwd()) {
                            if(ReceiveSessionKey()) {
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

    public void SessionInit(Socket socket, SecretKey key) {
        localAddr = socket.getLocalAddress();
        remoteAddr = socket.getInetAddress();
        
        setTitle("Chat with " + remoteAddr.getHostAddress());

        localKey = key;
        socketRemote = socket;
        if(UpdateSessionKey()) {
            StartReceiveThread();
        }
    }
    
    private void StartReceiveThread() {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        //ReceiveMessage2();
                        ReceiveMessage();
                    }
                    catch (IOException ex) {
                        WriteLog("ERROR(" + ex.toString() + "): " + ex.getMessage());
                        break;
                    }
                }
            }
        });

        receiveThread.start();
    }
    
    private void FileSenderThread(final File file) {
        try {
            final Socket socket = new Socket(socketRemote.getInetAddress(), ChatConfig.PORT_FILE_LISTENER);
            
            sendFileThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String fileName = file.getName();
                    long fileSize = file.length();
                    
                    if(fileName.length()>0) {
                        if( SendMessage(fileName+"~"+fileSize, socket)) {
                            try {
                                String message = ReceiveMessage(socket);
                                if(message.equals("ready")) {
                                    if(SendFile(file, socket)) {
                                        WriteLog("Send file completed - " + file.getName());
                                    }
                                    else {
                                        WriteLog("Send file ERROR - " + file.getName());
                                    }
                                }
                            }
                            catch (IOException ex) {
                                WriteLog(ex.getMessage());
                            }
                        }
                        else {
                            WriteLog("ERROR: SendMessage");
                        }
                    }
                }
            });
            
            sendFileThread.start();
        } 
        catch (IOException ex) {
            WriteLog("ERROR: " + ex.getMessage());
        }
    }

    public void FileReceiverThread(final Socket socket) {
        final ChatDialog parent = this;

        if(!isVisible()) {
            setVisible(true);
        }
        if(!isActive()) {
            requestFocus();
        }
        
        receiveFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = ReceiveMessage(socket);
                    String[] fileInfo = message.split("~");
                    String fileName = fileInfo[0];
                    String fileSize = fileInfo[1];
                    
                    WriteLog("Receive file: " + fileName + " (" + fileSize + ")");

                    fileSaver.setSelectedFile(new File(fileName));
                    int result = fileSaver.showSaveDialog(parent);
                    if(result == JFileChooser.APPROVE_OPTION) {
                        File file = fileSaver.getSelectedFile();

                        if(SendMessage("ready", socket)) {
                            if(!ReceiveFile(file, Long.parseLong(fileSize), socket)) {
                                if(file.isFile()) {
                                    file.delete();
                                }
                            }
                        }
                    }
                    else {
                        socket.close();
                    }
                } 
                catch (IOException ex) {
                    WriteLog(ex.getMessage());
                }
            }
        });
        
        receiveFileThread.start();
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

    private boolean RequestSessionKey() {
        boolean success = false;
        try {
            String sessionInfo = localAddr.getHostAddress() + "," + remoteAddr.getHostAddress();
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

    private boolean ReceiveSessionKey() {
        boolean success = false;
        sessionKey = null;
        socketRemote = null;

        try {
            InputStream inputStream = socketKdc.getInputStream();

            byte[] buffer = new byte[1024];
            int byteRead = inputStream.read(buffer);
            
            if(byteRead >= 0) {
                buffer = SecretHelper.DecryptBytes(buffer, byteRead, localKey);
                byte[] sessionKeyBytes = Arrays.copyOfRange(buffer, 0, 16);
                byte[] sessionKeyEncrypted = Arrays.copyOfRange(buffer, 16, byteRead);
                
                WriteLog("sessionKeyBytes: " + SecretHelper.BytesToStringBase64(sessionKeyBytes));
                sessionKey = SecretHelper.regenerateKey(sessionKeyBytes, 16);
                
                /**** Send sessionKeyEncrypted to another chat client ****/
                socketRemote = new Socket(remoteAddr.getHostAddress(), ChatConfig.PORT_CHAT_CLIENT);
                OutputStream outputStream = socketRemote.getOutputStream();
                outputStream.write(sessionKeyEncrypted);
                outputStream.flush();

                success = true;
            }
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;        
    }
    
    private boolean UpdateSessionKey() {
        boolean success = false;
        try {
            InputStream inputStream = socketRemote.getInputStream();

            byte[] buffer = new byte[1024];
            int byteRead = inputStream.read(buffer);
            
            if(byteRead >= 0) {
                buffer = SecretHelper.DecryptBytes(buffer, byteRead, localKey);
                sessionKey = SecretHelper.regenerateKey(buffer, 16);
                WriteLog("sessionKeyBytes: " + SecretHelper.BytesToStringBase64(sessionKey.getEncoded()));
                success = true;
            }
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }
        
        return success;
    }

    private boolean AuthenUserPwd() {
        boolean success = false;
        
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

    private boolean SendFile(File file, Socket socket) {
        boolean success = false;
        try {
            byte[] md5code = SecretHelper.MD5HashToBytes(new FileInputStream(file));
            byte[] keycode = SecretHelper.EncryptBytes(md5code, sessionKey);

            WriteLog("keycode:" + SecretHelper.BytesToStringBase64(keycode));

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(keycode);
            outputStream.flush();

            byte buffer[] = new byte[2048];
            FileInputStream inputStream = new FileInputStream(file);
            int byteRead = inputStream.read(buffer);
            while(byteRead >= 0) {
                outputStream.write(buffer, 0, byteRead);
                byteRead = inputStream.read(buffer);
            }

            outputStream.flush();
            outputStream.close();
            socket.close();
            success = true;
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }
    
    private boolean SendMessage(String message) {
        boolean success = false;

        try {
            byte[] md5code = SecretHelper.MD5HashToBytes(message);
            byte[] keycode = SecretHelper.EncryptBytes(md5code, sessionKey);
            
            WriteLog("keycode:" + SecretHelper.BytesToStringBase64(keycode));

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byteArray.write(keycode);
            byteArray.write(message.getBytes());

            byte[] data = byteArray.toByteArray();

            OutputStream outputStream = socketRemote.getOutputStream();
            outputStream.write(data);
            outputStream.flush();

            AppendMessage(message);
            success = true;
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }

    private boolean SendMessage2(String message) {
        boolean success = false;
        byte iv = 1;
        byte[] messageBytes = message.getBytes();
        
        int totalByte = messageBytes.length;
        byte[] encryptBytes = new byte[totalByte];
        
        for(int i=0; i<totalByte; i++) {
            encryptBytes[i] = (byte)(iv^messageBytes[i]);
            iv = encryptBytes[i];
        }

        WriteLog("messageBytes:" + SecretHelper.BytesToStringBase64(messageBytes));
        WriteLog("encryptBytes:" + SecretHelper.BytesToStringBase64(encryptBytes));

        OutputStream outputStream;
        try {
            outputStream = socketRemote.getOutputStream();
            outputStream.write(encryptBytes);
            outputStream.flush();
        } 
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        AppendMessage(message);

        return success;
    }
    
    private boolean SendMessage(String message, Socket socket) {
        boolean success = false;

        try {
            byte[] md5code = SecretHelper.MD5HashToBytes(message);
            byte[] keycode = SecretHelper.EncryptBytes(md5code, sessionKey);
            
            WriteLog("keycode:" + SecretHelper.BytesToStringBase64(keycode));

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byteArray.write(keycode);
            byteArray.write(message.getBytes());

            byte[] data = byteArray.toByteArray();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            
            success = true;
        }
        catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }
    
    private boolean ReceiveFile(File file, long size, Socket socket) throws IOException {
        boolean success = false;
        FileOutputStream fileOutput = null;
        InputStream inputStream;
        byte[] keycode1 = null;
        long sizeReceive = -1;
        
        try {
            fileOutput = new FileOutputStream(file);
            inputStream = socket.getInputStream();
            byte[] buffer = new byte[4096];
            int byteRead = inputStream.read(buffer);

            sizeReceive = 0;

            if(byteRead >= 32) {
                keycode1 = Arrays.copyOfRange(buffer, 0, 32);

                byteRead = inputStream.read(buffer);
                while(byteRead >= 0) {
                    sizeReceive += byteRead;
                    fileOutput.write(buffer, 0, byteRead);
                    byteRead = inputStream.read(buffer);
                }

                fileOutput.flush();
            }
        }
        catch(IOException ex) {
            WriteLog(ex.getMessage());
        }
        finally {
            if(sizeReceive >= 0 && fileOutput != null) {
                fileOutput.flush();
                fileOutput.close();
                
                if(sizeReceive == size) {
                    byte[] md5code = SecretHelper.MD5HashToBytes(new FileInputStream(file));
                    byte[] keycode2 = SecretHelper.EncryptBytes(md5code, sessionKey);

                    WriteLog("keycode1:" + SecretHelper.BytesToStringBase64(keycode1));
                    WriteLog("keycode2:" + SecretHelper.BytesToStringBase64(keycode2));
                    success = Arrays.equals(keycode1, keycode2);

                    if(success) {
                        WriteLog("File receive completed - " + file.getName());
                    }                    
                }
            }
        }

        return success;
    }
    
    private boolean ReceiveMessage() throws IOException {
        boolean success = false;
        InputStream inputStream = socketRemote.getInputStream();

        byte[] buffer = new byte[1024];
        int byteRead = inputStream.read(buffer);

        if(byteRead >= 32) {
            buffer = Arrays.copyOfRange(buffer, 0, byteRead);

            byte[] keycode1 = Arrays.copyOfRange(buffer, 0, 32);
            byte[] data = Arrays.copyOfRange(buffer, 32, byteRead);

            byte[] md5code = SecretHelper.MD5Hash(data);
            byte[] keycode2 = SecretHelper.EncryptBytes(md5code, sessionKey);

            WriteLog("keycode1:" + SecretHelper.BytesToStringBase64(keycode1));
            WriteLog("keycode2:" + SecretHelper.BytesToStringBase64(keycode2));

            if(Arrays.equals(keycode1, keycode2)) {
                success = true;
                AppendMessage(new String(data));
            }
            else {
                WriteLog("Error on receive message.");
            }
        }
        else if(byteRead < 0) {
            WriteLog("Socket closed.");
            AppendMessage("Socket closed.");

            Send.setEnabled(false);
            File.setEnabled(false);

            chatTextField.setEnabled(false);
            receiveThread.interrupt();
        }
        
        return success;
    }

    private boolean ReceiveMessage2() throws IOException {
        boolean success = false;
        InputStream inputStream = socketRemote.getInputStream();

        byte iv = 1;
        byte[] encryptBytes = new byte[1024];
        int byteRead = inputStream.read(encryptBytes);

        if(byteRead >= 0) {
            byte[] messageBytes = new byte[byteRead];
            
            for(int i=0; i<byteRead; i++) {
                messageBytes[i] = (byte)(iv^encryptBytes[i]);
                iv = encryptBytes[i];
            }

            WriteLog("messageBytes:" + SecretHelper.BytesToStringBase64(messageBytes));
            WriteLog("encryptBytes:" + SecretHelper.BytesToStringBase64(Arrays.copyOfRange(encryptBytes, 0, byteRead)));

            AppendMessage(new String(messageBytes));
        }
        else {
            WriteLog("Socket closed.");
            AppendMessage("Socket closed.");

            Send.setEnabled(false);
            File.setEnabled(false);

            chatTextField.setEnabled(false);
            receiveThread.interrupt();
        }
        
        return success;
    }
 
    
    
    private String ReceiveMessage(Socket socket) throws IOException {
        
        String message = null;
        InputStream inputStream;
        inputStream = socket.getInputStream();

        byte[] buffer = new byte[1024];
        int byteRead = inputStream.read(buffer);

        if(byteRead >= 32) {
            buffer = Arrays.copyOfRange(buffer, 0, byteRead);

            byte[] keycode1 = Arrays.copyOfRange(buffer, 0, 32);
            byte[] data = Arrays.copyOfRange(buffer, 32, byteRead);

            byte[] md5code = SecretHelper.MD5Hash(data);
            byte[] keycode2 = SecretHelper.EncryptBytes(md5code, sessionKey);

            if(Arrays.equals(keycode1, keycode2)) {
                message = new String(data);
            }
            else {
                WriteLog("Error on receive message.");
            }
        }
        
        return message;
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

    /*private boolean RequestMulticastKey(String address) {
        boolean success = false;
        
        return success;        
    }

    private boolean ReceiveMulticastKey() {
        boolean success = false;
        
        return success;        
    }*/
   
    private void WriteLog(String log) {
        ((ChatClient)this.getParent()).WriteLog(log);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        fileSaver = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        messagePanel = new javax.swing.JTextArea();
        Send = new javax.swing.JButton();
        chatTextField = new javax.swing.JTextField();
        File = new javax.swing.JButton();

        fileSaver.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Chat");
        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        messagePanel.setEditable(false);
        messagePanel.setColumns(20);
        messagePanel.setRows(5);
        jScrollPane1.setViewportView(messagePanel);

        Send.setText("Send");
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        File.setText("File");
        File.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(File, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(File)
                    .addComponent(Send)
                    .addComponent(chatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
        if(socketRemote != null) {
            String message = chatTextField.getText();
            if(message.length() > 0) {
                //SendMessage2(message);
                SendMessage(message);
                chatTextField.setText("");
            }
        }
    }//GEN-LAST:event_SendActionPerformed

    private void FileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileActionPerformed
        int result = fileChooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileSenderThread(file);
        }
        else {
            WriteLog("File access cancelled by user.");
        }
    }//GEN-LAST:event_FileActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        if(receiveThread != null) {
            receiveThread.interrupt();
        }
        
        if(socketRemote != null) {
            try {
                socketRemote.close();
                ((ChatClient)getParent()).OnChatDialogClose(this);
            }
            catch (IOException ex) {
                WriteLog(ex.getMessage());
            }
        }
    }//GEN-LAST:event_formWindowClosed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton File;
    private javax.swing.JButton Send;
    private javax.swing.JTextField chatTextField;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JFileChooser fileSaver;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea messagePanel;
    // End of variables declaration//GEN-END:variables
}
