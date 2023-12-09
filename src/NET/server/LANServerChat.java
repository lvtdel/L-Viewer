package NET.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import GUI.ServerChatForm;
import NET.LANChat;
import NET.LANSocketInfor;
import NET.constants.LANChatConstants;

import static NET.util.FileSupport.saveToFile;

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
                        message = "Đã gửi 1 file";
//                        continue;
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

    private void receiveFile() {
        try {
            System.out.println("Server receive file");
            String fileName = iStream.readUTF();
            byte[] fileByte = iStream.readAllBytes();
            saveToFile(LANChatConstants.SAVE_FILE_LOCATION + fileName, fileByte);
        } catch (Exception e) {
        }
    }

    public void sendMessage(String message) {
        try {
            Path path = Path.of("D:\\Downloads\\about.txt");
            byte[] fileByte = Files.readAllBytes(path);
            String fileName = path.getFileName().toString();

            sendFile(path.toString(), fileName);

            oStream.writeUTF(message);
            oStream.flush();
        } catch (Exception e) {
            System.out.println("Khong gui tin nhan duoc!");
        }
    }

    @Override
    public void sendFile(String path, String fileName) {
//        try {
//            oStream.writeUTF(LANChatConstants.SEND_FILE_COMMAND);
//            oStream.writeUTF(fileName);
//            oStream.flush();
//
//            oStream.write(fileByte);
////            System.out.println("Nội dung file được gửi là: " + new String(fileByte));
//            oStream.flush();
//        } catch (Exception e) {
//            System.out.println("Khong gui file duoc");
//        }
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
            throw new RuntimeException(e);
        }


    }

    public LANServerChat(int serverPort) {
        // TODO Auto-generated constructor stub
        this.serverPort = serverPort;
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
