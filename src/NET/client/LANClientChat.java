package NET.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import GUI.ClientChatForm;
import NET.LANChat;
import NET.LANSocketInfor;
import NET.constants.LANChatConstants;

import static NET.util.FileSupport.saveToFile;

public class LANClientChat extends Thread implements LANChat {
    private Socket socket = null;
    private DataInputStream iStream = null;
    private DataOutputStream oStream = null;
    private LANSocketInfor serverInfor = null;
    private boolean isChatting = false;
    private ClientChatForm myClientChatForm = null;

    public LANClientChat(LANSocketInfor serverInfor) {
        this.serverInfor = serverInfor;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        try {
            myClientChatForm = ClientChatForm.GetInstance();
            socket = new Socket(serverInfor.getIp(), serverInfor.getPort());
            System.out.println("Connected: " + socket);
            open();
            isChatting = true;
            System.out.println("Khoi tao client thanh cong!");
            while (isChatting) {
                try {
                    String message = iStream.readUTF();
                    if (message.equals(LANChatConstants.SEND_FILE_COMMAND)) {
                        receiveFile();
//                        message = "Đã gửi 1 file";
                        continue;
                    }
                    System.out.println(message);
                    myClientChatForm.AddMessage("Partner", message);
                } catch (IOException ioe) {
                    System.out.println("Khong co ket noi!");
                    isChatting = false;
                }
            }
            close();

        } catch (Exception e) {
            System.out.println("Loi khoi tao client");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            oStream.writeUTF(message);
            oStream.flush();

        } catch (IOException e) {
            System.out.println("Khong gui tin nhan duoc!");
        }

    }

    @Override
    public void sendFile(String path, String fileName) {

    }

    private void receiveFile() {
        try {
            int bytes = 0;

            String fileName = iStream.readUTF();
            long size = iStream.readLong(); // read file size

            String pathSave = LANChatConstants.SAVE_FILE_LOCATION + fileName;
            FileOutputStream fileOutputStream = new FileOutputStream(pathSave);
            byte[] buffer = new byte[1024 * 1024]; // max 4GB
            while (size > 0
                    && (bytes = iStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                // Here we write the file using write method
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes; // read upto file size
            }
            // Here we received file
            System.out.println("File is Received");
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//        try {
//            System.out.print("Client receive file: ");
//            String fileName = iStream.readUTF();
//            System.out.println(fileName);
//
//            byte[] fileByte = iStream.readAllBytes();
//            System.out.println("Nội dung file được nhận là: " + new String(fileByte));
//
////            saveToFile(LANChatConstants.SAVE_FILE_LOCATION + fileName, fileByte);
////
////            String content = "hello";
////
////            // Chuyển đổi chuỗi thành mảng byte
////            byte[] fileByte = content.getBytes();
//            saveToFile("E:\\Workspace\\Test transfer file dacs4\\OUT\\" + fileName, fileByte);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//}

//    public void sendFile(byte[] fileByte, String fileName) {
//
//    }

    @Override
    public void open() throws IOException {
        // TODO Auto-generated method stub
        iStream = new DataInputStream(socket.getInputStream());
        oStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void close() throws IOException {
        try {
            isChatting = false;
            if (socket != null)
                socket.close();
            if (iStream != null)
                iStream.close();
            if (oStream != null)
                oStream.close();
            System.out.println("Chat dong ket noi");
        } catch (Exception e) {
            System.out.println("Loi dong ket noi");
        }
    }

    @Override
    public void stopChat() {
        isChatting = false;
        try {
            close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public int GetClientChatSocketPort() {
        // TODO Auto-generated method stub
        return socket.getLocalPort();
    }
}
