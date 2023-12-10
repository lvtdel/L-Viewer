package NET.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import GUI.ClientChatForm;
import GUI.ServerChatForm;
import NET.LANChat;
import NET.LANSocketInfor;
import NET.constants.LANChatConstants;
import NET.util.FileSupport;

public class LANServerChat extends Thread implements LANChat {
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream iStream = null;
    private DataOutputStream oStream = null;
    private final int serverPort;
    private boolean isChatting = false;
    private ServerChatForm myServerChatForm = null;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        try {
            myServerChatForm = ServerChatForm.GetInstance();
            serverSocket = new ServerSocket(serverPort);
            socket = serverSocket.accept();
            myServerChatForm.SetLblPartnerIP(socket.getInetAddress().toString());
            open();
            isChatting = true;
            while (isChatting) {
                try {
                    String message = iStream.readUTF();
                    if (message.equals(LANChatConstants.SEND_FILE_COMMAND)) {
                        receiveFile();
//                        message = "Đã gửi 1 file";
                        continue;
                    }

                    myServerChatForm.AddMessage("Partner", message);
                } catch (IOException ioe) {
                    isChatting = false;
                    System.out.println("Khong co ket noi!");
                }
            }
            close();
        } catch (IOException e) {
            System.out.println("Loi khoi tao server chat" + e.toString());
        }
    }

    public void sendMessage(String message) {
        try {
            oStream.writeUTF(message);
            oStream.flush();
        } catch (Exception e) {
            System.out.println("Khong gui tin nhan duoc!");
        }
    }

    @Override
    public void sendFile(String path, String fileName) {
        try {
            int bytes = 0;
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            oStream.writeUTF(LANChatConstants.SEND_FILE_COMMAND);
            oStream.writeUTF(fileName);
            oStream.writeLong(file.length());

            byte[] buffer = new byte[1024 * 1024];// max 4GB
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                oStream.write(buffer, 0, bytes);
                oStream.flush();
            }

            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LANServerChat(int serverPort) {
        // TODO Auto-generated constructor stub
        this.serverPort = serverPort;
    }

    private void receiveFile() {
        String pathSave = FileSupport.saveFile(iStream);

        String message = pathSave != null ? "Đã nhận 1 tệp tin: " + pathSave : "Nhận tệp tin thất bại";
        ServerChatForm.GetInstance().AddMessage("Partner", message);
    }

    public void open() throws IOException {
        iStream = new DataInputStream(socket.getInputStream());
        oStream = new DataOutputStream(socket.getOutputStream());
    }

    public void close() throws IOException {
        try {
            isChatting = false;
            if (socket != null)
                socket.close();
            if (iStream != null)
                iStream.close();
            if (oStream != null)
                oStream.close();
            if (serverSocket != null)
                serverSocket.close();
            System.out.println("Chat dong ket noi");
        } catch (Exception e) {
            System.out.println("Loi dong ket noi");
        }
    }

    public void stopChat() {
        isChatting = false;
        try {
            close();
        } catch (IOException e) {
        }
    }

    public LANSocketInfor GetClientChatInfor() {
        // TODO Auto-generated method stub
        return new LANSocketInfor(socket.getInetAddress(), socket.getPort());
    }

}
