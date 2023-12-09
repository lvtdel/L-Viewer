package NET.client;

import java.net.*;


import BLL.client.BLL_RemoteScreenForm;
import DTO.DTO_ArrayLANImageInforObject;
import GUI.RemoteScreenForm;

import java.io.*;

public class LANClientThread extends Thread {
    public static void main(String[] args) {
        LANClientThread lanClientThread = new LANClientThread("192.168.56.1", 1999, "ahihi");
        lanClientThread.StartClient();
    }

    private final String serverIP;
    private final int serverPort;
    private final String pass;

    private Socket socket;
    private OutputStream os;
    private ObjectOutputStream oos;
    private InputStream is;
    private ObjectInputStream ois;
    //private BufferedInputStream bin;

    private boolean isReceivingImage = false;
    private int countFaild = 0;
    private final int maxCountFail = 30;//=6;
    public ThreadGuiNhanYeuCauXacThuc threadGuiNhanYeuCauXacThuc = null;

    public LANClientThread(String serverIP, int serverPort, String pass) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.pass = pass;
    }

    public void run() {
        StartClient();
        int countXacThucFaild = 0;
        while (!isXacThucThanhCong) {
            countXacThucFaild++;
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
            if (countXacThucFaild > 200) {
                System.out.println("Hủy kết nối và kết nối lại");
                DestroyClient();
                StartClient();
                countXacThucFaild = 0;
            }
        }
        if (isXacThucThanhCong) {
            System.out.println("Chay client thanh cong!");
            //OK KET NOI Mo Khung chat
            BLL_RemoteScreenForm.GetInstance().OpenChatWindow(serverIP, serverPort + 1);
            try {
                LoopReceiveImage();
            } catch (Exception e) {
                BLL_RemoteScreenForm.GetInstance().AnnounceConnectError("Mat ket noi!");
                //TAT KHUNG CHAT
                BLL_RemoteScreenForm.GetInstance().CloseChatWindow();
            }
        }

    }

    //Start and destroy client

    public void StartClient() {
        //Gui goi tin yeu cau ket noi
        try {
            if (socket == null) {
                socket = new Socket(serverIP, serverPort);
                System.out.println("Client: Tạo lại kết nối");
                if (os == null && oos == null) {
                    System.out.println("Client: Tạo lại stream data");
                    os = socket.getOutputStream();
                    oos = new ObjectOutputStream(os);
                }
            }

            //is vs ois da chuyen cho thread o duoi khoi tao
            if (threadGuiNhanYeuCauXacThuc == null) {
                threadGuiNhanYeuCauXacThuc = new ThreadGuiNhanYeuCauXacThuc();
                threadGuiNhanYeuCauXacThuc.start();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        isReceivingImage = true;

        remoteScreenForm.ShowStatus(remoteScreenForm.GetLanguageString("sttRequestsent") + serverIP + ":" + serverPort + " pass: " + pass);

        System.out.println("Da gui yeu cau ket noi den: " + serverIP + ":" + serverPort + " pass: " + pass);
    }

    public void DestroyClient() {
        remoteScreenForm.ShowStatus(remoteScreenForm.GetLanguageString("sttDisconnect"));

        System.out.println("Destroy client!");
        isReceivingImage = false;
        isXacThucThanhCong = false;
        try {
            oos = null;
            os = null;

            ois = null;
            is = null;
            socket.close();
            socket = null;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Đóng kết nối thất bại");
            e.printStackTrace();
        }
    }

    public void SetStateReceiveImage(boolean state) {
        isReceivingImage = state;
    }

    //Receive image
    public void LoopReceiveImage() {
        countFaild = 0;
        while (isReceivingImage) {
            try {
                ReceiveImage();
                //System.out.println("Nhan hinh roi nhe!");
            } catch (InterruptedException e) {
                System.out.println("Loi nhan hinh in LoopReceiveImage");
                remoteScreenForm.ShowStatus(remoteScreenForm.GetLanguageString("sttReceiveImgFailed"));
            }

        }
    }

    public DTO_ArrayLANImageInforObject arrLANIIO = null;
    private final RemoteScreenForm remoteScreenForm = RemoteScreenForm.GetInstance();

    public void ReceiveImage() throws InterruptedException {
        Thread.sleep(30);
        if (arrLANIIO != null)
            arrLANIIO.Clear();
        arrLANIIO = null;
        try {
            arrLANIIO = (DTO_ArrayLANImageInforObject) ois.readObject();
        } catch (Exception e) { //Co gang tao client neu ket noi fail
            System.out.println("Receive Image Failed!");
            System.out.println(e.toString());
            remoteScreenForm.ShowStatus(remoteScreenForm.GetLanguageString("sttReceiveImgFailed"));
            try {
                //StartClient();
            } catch (Exception e2) {
            }
        }
        if (arrLANIIO != null) {
            remoteScreenForm.ShowImageToPanel(arrLANIIO);
            //System.out.println("AHIHI");
            try {
                Runtime.getRuntime().gc();
            } catch (Exception e) {
            }
            //System.out.println("OK nhan duoc");
            if (countFaild != 0)
                countFaild = 0;
        } else {
            System.out.println("NULLLLLLL");
            countFaild++;
            Thread.sleep(30);
            if (countFaild > maxCountFail) {
                isReceivingImage = false;
                DestroyClient();
            }
        }
    }

    public void SendMessage(String message) {
        if (socket != null && oos != null) {
            try {
                oos.writeObject(message);
                oos.flush();
                oos.reset();
            } catch (Exception e) {
            }
        }
    }

    private boolean isXacThucThanhCong = false;

    public class ThreadGuiNhanYeuCauXacThuc extends Thread {
        public ThreadGuiNhanYeuCauXacThuc() {
            // TODO Auto-generated constructor stub
        }

        public boolean isXacThuc = false;
        public boolean dangGet = false;

        @Override
        public void run() {
            super.run();
            System.out.println("Dang gui yeu cau xac thuc len server");
            GetMessageXacThuc getMessageXacThuc = new GetMessageXacThuc();
            getMessageXacThuc.start();
            while (!isXacThuc) {
                try {
                    if (!dangGet) {
                        Thread.sleep(1000);
                        oos.writeObject("RequireConnect:" + pass);
                        oos.reset();
                        dangGet = true;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println("Loi xac thuc connect");
                    remoteScreenForm.ShowStatus(remoteScreenForm.GetLanguageString("sttValidationErr"));
                }
            }
            Runtime.getRuntime().gc();
            System.out.println("Thread stop");
        }

        public class GetMessageXacThuc extends Thread {
            public void run() {
                while (true) {
                    if (true) {
                        try {
                            is = socket.getInputStream();
                            //bin=new BufferedInputStream(is);
                            //ois = new ObjectInputStream(bin);
                            ois = new ObjectInputStream(is);
                            String message = (String) ois.readObject();
                            if (message.equals("XacThucThanhCong")) {
                                isXacThuc = true;
                                isXacThucThanhCong = true;
                                remoteScreenForm.ShowStatus(remoteScreenForm.GetLanguageString("sttConnectSuccess"));
                                return;
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                            dangGet = false;
                        }
                    }
                }
            }
        }
    }
}
