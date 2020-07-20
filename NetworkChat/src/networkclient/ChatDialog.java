/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networkclient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.swing.JFileChooser;
import networkchat.ChatConfig;
import networkchat.SecretHelper;

/**
 *
 * @author Administrator
 */
public class ChatDialog extends javax.swing.JDialog {

    private final DateFormat dateFormat;
    private InetAddress localAddr, remoteAddr;
    private SecretKey localKey, sessionKey;
    private Socket socketKdc, socketRemote;
    private Thread receiveThread, receiveFileThread, sendFileThread;

    /**
     * Creates new form ChatDialog
     */
    public ChatDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void SessionInit(String remoteIp) {
        try {
            setTitle("Chat with " + remoteIp);

            if (ConnectKdcServer()) {
                if (ReceiveSecretKey()) {
                    localAddr = socketKdc.getLocalAddress();
                    remoteAddr = InetAddress.getByName(remoteIp);

                    if (RequestSessionKey()) {
                        if (AuthenUserPwd()) {
                            if (ReceiveSessionKey()) {
                                WriteLog("Ready to start session thread.");
                                StartReceiveThread();
                            }
                        }
                    }
                }
            }
        } catch (UnknownHostException ex) {
            WriteLog(ex.getMessage());
        }
    }

    public void SessionInit(Socket socket, SecretKey key) {
        localAddr = socket.getLocalAddress();
        remoteAddr = socket.getInetAddress();

        setTitle("Chat with " + remoteAddr.getHostAddress());

        localKey = key;
        socketRemote = socket;
        if (UpdateSessionKey()) {
            StartReceiveThread();
        }
    }

    public String getServiceIP() {
        String serviceIP = null;

        if (remoteAddr != null) {
            serviceIP = remoteAddr.getHostAddress();
        }

        return serviceIP;
    }

    private void StartReceiveThread() {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        ReceiveMessage();
                    } catch (IOException ex) {
                        WriteLog("ERROR: " + ex.getMessage());
                        break;
                    }
                }
            }
        });

        receiveThread.start();
    }

    private boolean ConnectKdcServer() {
        boolean success = false;
        try {
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
                success = true;
            }
        } catch (IOException ex) {
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
        } catch (IOException ex) {
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

            if (byteRead >= 0) {
                buffer = SecretHelper.DecryptBytes(buffer, byteRead, localKey);
                byte[] sessionKeyBytes = Arrays.copyOfRange(buffer, 0, 16);
                byte[] sessionKeyEncrypted = Arrays.copyOfRange(buffer, 16, byteRead);

                sessionKey = SecretHelper.regenerateKey(sessionKeyBytes, 16);

                /**
                 * ** Send sessionKeyEncrypted to another chat client ***
                 */
                socketRemote = new Socket(remoteAddr.getHostAddress(), ChatConfig.PORT_CHAT_CLIENT);
                OutputStream outputStream = socketRemote.getOutputStream();
                outputStream.write(sessionKeyEncrypted);
                outputStream.flush();

                success = true;
            }
        } catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }

    private boolean AuthenUserPwd() {
        boolean success = false;

        String usr = ((ChatClient) this.getParent()).GetUserName();
        String pwd = ((ChatClient) this.getParent()).GetPassword();

        try {
            String autheninfo = usr + "~" + pwd;
            byte[] data = SecretHelper.EncryptString(autheninfo, localKey);

            OutputStream outputStream = socketKdc.getOutputStream();
            outputStream.write(data);
            outputStream.flush();

            success = true;
        } catch (IOException ex) {
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

            if (byteRead >= 0) {
                buffer = SecretHelper.DecryptBytes(buffer, byteRead, localKey);
                sessionKey = SecretHelper.regenerateKey(buffer, 16);
                success = true;
            }
        } catch (IOException ex) {
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
            while (byteRead >= 0) {
                outputStream.write(buffer, 0, byteRead);
                byteRead = inputStream.read(buffer);
            }

            outputStream.flush();
            outputStream.close();
            socket.close();
            success = true;
        } catch (IOException ex) {
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
        } catch (IOException ex) {
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

        for (int i = 0; i < totalByte; i++) {
            encryptBytes[i] = (byte) (iv ^ messageBytes[i]);
            iv = encryptBytes[i];
        }

        WriteLog("messageBytes:" + SecretHelper.BytesToStringBase64(messageBytes));
        WriteLog("encryptBytes:" + SecretHelper.BytesToStringBase64(messageBytes));

        OutputStream outputStream;
        try {
            outputStream = socketRemote.getOutputStream();
            outputStream.write(encryptBytes);
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(ChatDialog.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (IOException ex) {
            WriteLog(ex.getMessage());
        }

        return success;
    }

    private boolean ReceiveMessage() throws IOException {
        boolean success = false;
        InputStream inputStream = socketRemote.getInputStream();

        byte[] buffer = new byte[1024];
        int byteRead = inputStream.read(buffer);

        buffer = Arrays.copyOfRange(buffer, 0, byteRead);

        if (byteRead >= 32) {
            byte[] keycode1 = Arrays.copyOfRange(buffer, 0, 32);
            byte[] data = Arrays.copyOfRange(buffer, 32, byteRead);

            byte[] md5code = SecretHelper.MD5Hash(data);
            byte[] keycode2 = SecretHelper.EncryptBytes(md5code, sessionKey);

            WriteLog("keycode1:" + SecretHelper.BytesToStringBase64(keycode1));
            WriteLog("keycode2:" + SecretHelper.BytesToStringBase64(keycode2));

            if (Arrays.equals(keycode1, keycode2)) {
                success = true;
                AppendMessage(new String(data));
            } else {
                WriteLog("Error on receive message.");
            }
        }

        return success;
    }

    private boolean ReceiveMessage2() throws IOException {
        boolean success = false;
        InputStream inputStream = socketRemote.getInputStream();

        byte iv = 1;
        byte[] buffer = new byte[1024];
        int byteRead = inputStream.read(buffer);

        //buffer = Arrays.copyOfRange(buffer, 0, byteRead);

        if (byteRead >= 0) {
            //byte[] messageBytes = message.getBytes();
        }

        return success;
    }

    private String ReceiveMessage(Socket socket) throws IOException {

        String message = null;
        InputStream inputStream;
        inputStream = socket.getInputStream();

        byte[] buffer = new byte[1024];
        int byteRead = inputStream.read(buffer);

        buffer = Arrays.copyOfRange(buffer, 0, byteRead);

        if (byteRead >= 32) {
            byte[] keycode1 = Arrays.copyOfRange(buffer, 0, 32);
            byte[] data = Arrays.copyOfRange(buffer, 32, byteRead);

            byte[] md5code = SecretHelper.MD5Hash(data);
            byte[] keycode2 = SecretHelper.EncryptBytes(md5code, sessionKey);

            if (Arrays.equals(keycode1, keycode2)) {
                message = new String(data);
            } else {
                WriteLog("Error on receive message.");
            }
        }

        return message;
    }

    private void AppendMessage(String message) {

        if (!isVisible()) {
            setVisible(true);
        }

        if (!isActive()) {
            requestFocus();
        }

        this.messagePanel.append(String.format("[%s]: %s\n", dateFormat.format(new Date()), message));
    }

    private void FileSenderThread(final File file) {
        try {
            final Socket socket = new Socket(socketRemote.getInetAddress(), ChatConfig.PORT_FILE_LISTENER);

            sendFileThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String fileName = file.getName();
                    long fileSize = file.length();

                    WriteLog("FileSenderThread(1)");

                    if (fileName.length() > 0) {
                        WriteLog("FileSenderThread(2) - " + fileName);
                        if (SendMessage(fileName + "~" + fileSize, socket)) {
                            WriteLog("FileSenderThread(3)");
                            try {
                                String message = ReceiveMessage(socket);
                                if (message.equals("ready")) {
                                    WriteLog("FileSenderThread(4)");
                                    if (SendFile(file, socket)) {
                                        WriteLog("FileSenderThread(5)");
                                        WriteLog("Send file completed - " + file.getName());
                                    } else {
                                        WriteLog("FileSenderThread(6)");
                                        WriteLog("Send file ERROR - " + file.getName());
                                    }
                                }
                            } catch (IOException ex) {
                                WriteLog("FileSenderThread(7)");
                                WriteLog(ex.getMessage());
                            }
                        } else {
                            WriteLog("ERROR: SendMessage");
                        }
                    }
                }
            });

            sendFileThread.start();
        } catch (IOException ex) {
            WriteLog("ERROR: " + ex.getMessage());
        }
    }

    public void FileReceiverThread(final Socket socket) {
        final ChatDialog parent = this;

        if (!isVisible()) {
            setVisible(true);
        }
        if (!isActive()) {
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
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileSaver.getSelectedFile();

                        if (SendMessage("ready", socket)) {
                            ReceiveFile(file, Long.parseLong(fileSize), socket);
                        }
                    } else {
                        socket.close();
                    }
                } catch (IOException ex) {
                    WriteLog(ex.getMessage());
                }
            }
        });

        receiveFileThread.start();
    }

    private boolean ReceiveFile(File file, long size, Socket socket) throws IOException {
        boolean success = false;
        FileOutputStream fileOutput = null;
        InputStream inputStream;
        byte[] keycode1 = null;
        long sizeReceive = -1;

        WriteLog("ReceiveFile(1)");

        try {
            fileOutput = new FileOutputStream(file);
            inputStream = socket.getInputStream();
            byte[] buffer = new byte[4096];
            int byteRead = inputStream.read(buffer);

            sizeReceive = 0;

            if (byteRead >= 32) {
                WriteLog("ReceiveFile(2)");
                keycode1 = Arrays.copyOfRange(buffer, 0, 32);

                byteRead = inputStream.read(buffer);
                while (byteRead >= 0) {
                    sizeReceive += byteRead;
                    fileOutput.write(buffer, 0, byteRead);
                    byteRead = inputStream.read(buffer);
                }

                fileOutput.flush();
            }
        } catch (IOException ex) {
            WriteLog(ex.getMessage());
        } finally {
            WriteLog("ReceiveFile(3)");
            if (sizeReceive >= 0 && fileOutput != null) {
                WriteLog("ReceiveFile(4)");
                fileOutput.flush();
                fileOutput.close();

                if (sizeReceive == size) {
                    byte[] md5code = SecretHelper.MD5HashToBytes(new FileInputStream(file));
                    byte[] keycode2 = SecretHelper.EncryptBytes(md5code, sessionKey);

                    WriteLog("keycode1:" + SecretHelper.BytesToStringBase64(keycode1));
                    WriteLog("keycode2:" + SecretHelper.BytesToStringBase64(keycode2));
                    success = Arrays.equals(keycode1, keycode2);

                    if (success) {
                        WriteLog("ReceiveFile(5)");
                        WriteLog("File receive completed - " + file.getName());
                    }
                }
            }
        }

        return success;
    }

    private void WriteLog(String log) {
        ((ChatClient) this.getParent()).WriteLog(log);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        fileSaver = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        messagePanel = new javax.swing.JTextArea();
        txtMessage = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();
        btnFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        messagePanel.setColumns(20);
        messagePanel.setRows(5);
        jScrollPane1.setViewportView(messagePanel);

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        btnFile.setText("File");
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMessage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFile, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSend)
                    .addComponent(btnFile)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
        if (socketRemote != null) {
            String message = this.txtMessage.getText();
            if (message.length() > 0) {
                SendMessage(message);
                this.txtMessage.setText("");
            }
        }
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        // TODO add your handling code here:
        int result = this.fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileSenderThread(file);
        } else {
            WriteLog("File access cancelled by user.");
        }
    }//GEN-LAST:event_btnFileActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        if (receiveThread != null) {
            receiveThread.interrupt();
        }

        if (socketRemote != null) {
            try {
                socketRemote.close();
                ((ChatClient) this.getParent()).OnChatDialogClose(this);
            } catch (IOException ex) {
                WriteLog(ex.getMessage());
            }
        }
    }//GEN-LAST:event_formWindowClosed

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
            java.util.logging.Logger.getLogger(ChatDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ChatDialog dialog = new ChatDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFile;
    private javax.swing.JButton btnSend;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JFileChooser fileSaver;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea messagePanel;
    private javax.swing.JTextField txtMessage;
    // End of variables declaration//GEN-END:variables
}
