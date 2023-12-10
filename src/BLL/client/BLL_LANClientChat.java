package BLL.client;

import java.net.InetAddress;
import java.nio.file.Path;

import BLL.BLL_LANChat;
import NET.client.LANClientChat;
import NET.LANSocketInfor;

public class BLL_LANClientChat implements BLL_LANChat {
    private LANClientChat lanClientChat = null;
    private LANSocketInfor serverInfor = null;

    public BLL_LANClientChat(InetAddress serverIP, int serverPort) {
        serverInfor = new LANSocketInfor(serverIP, serverPort);
    }

    public BLL_LANClientChat(LANSocketInfor serverInfor) {
        this.serverInfor = serverInfor;
    }

    @Override
    public void Start() {
        lanClientChat = new LANClientChat(serverInfor);
        lanClientChat.start();
    }

    @Override
    public void Stop() {
        if (lanClientChat != null) {
            lanClientChat.stopChat();
            lanClientChat = null;
        }
    }

    @Override
    public void sendMessage(String message) {
        if (lanClientChat != null)
            lanClientChat.sendMessage(message);
    }
    @Override
    public void sendFile(String path) {
        String fileName = Path.of(path).getFileName().toString();

        lanClientChat.sendFile(path, fileName);
    }

    public int GetClientChatSocketPort() {
        return lanClientChat.GetClientChatSocketPort();
    }
}
