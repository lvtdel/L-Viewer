package NET;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import BLL.BLL_LANForm;
import DTO.DTO_ArrayLANImageInforObject;
import GUI.LANForm;
import GUI.ServerChatForm;
import OS.MouseKeyExcuter;
import OS.ScreenCapturer;

public class LANServerThread extends Thread {

    private int port;
    private String pass;
    private LANSocketInfor clientInfor = null;
    private static ServerSocket serverSocket;
    private static Socket socket;//client socket
    private static InputStream is;
    private static ObjectInputStream ois;
    private static OutputStream os;
    private static ObjectOutputStream oos;
    //private static BufferedOutputStream bout;// = new BufferedOutputStream(os);
    private static MouseKeyExcuter mouseKeyExcuter;
    private LANServerMessageReceiver messageReceiver;
    private boolean isRunningMessageReceive = false;
    private LANForm lanFormInstance = null;

    public LANServerThread(int port, String pass) {
        // TODO Auto-generated constructor stub
        this.port = port;
        this.pass = pass;
        messageReceiver = new LANServerMessageReceiver();
        mouseKeyExcuter = new MouseKeyExcuter();
        lanFormInstance = LANForm.GetInstance();
    }

    @Override
    public void run() {
        super.run();
        StartServer();
    }

    public void ResetServer() {
        try {
            isRunningMessageReceive = false;
            //messageReceiver=null; //Khong bo cai nay vi duoc khai bao o tren ham khoi tao
            messageReceiver = new LANServerMessageReceiver();
            if (oos != null)
                try {
                    oos.reset();
                    oos.close();
                } catch (Exception e) {
                }
//			if(bout!=null)
//				try {
//					
//					bout.close();
//				}catch (Exception e) {}			
            if (os != null)
                try {

                    os.close();
                } catch (Exception e) {
                }
            if (ois != null)
                try {
                    ois.reset();
                    ois.close();
                } catch (Exception e) {
                }
            if (is != null)
                try {
                    is.reset();
                    is.close();
                } catch (Exception e) {
                }
            if (socket != null)
                try {

                    socket.close();
                } catch (Exception e) {
                }

            oos = null;
            //bout=null;
            os = null;
            ois = null;
            is = null;
            socket = null;

            serverSocket.close();
            serverSocket = null;
            clientInfor = null;
            arrLANIIO.Clear();
            Runtime.getRuntime().gc();
            StartServer();
        } catch (Exception e) {
            System.out.println("Reset server faild");
            lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttResetServerFailed"));
            e.printStackTrace();
        }
    }

    private void StartServer() {
        try {
            //Khoi tao server
            try {
                serverSocket = new ServerSocket(port);
            } catch (Exception e) {
                System.out.println(e);
            }
            while (clientInfor == null) {
                //TAT KHUNG CHAT neu co
                try {
                    if (ServerChatForm.GetInstance() != null)
                        BLL_LANForm.GetInstance().CloseChatWindow();
                } catch (Exception e) {
                    // TODO: handle exception
                }
                //Bat client socket doi ket noi
                System.out.println("Doi client yeu cau ket noi");
                lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttWaitingClientConnect"));
                socket = serverSocket.accept();
                System.out.println("Co client ket noi");
                lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttAClientTryingConnect"));
                is = socket.getInputStream();
                ois = new ObjectInputStream(is);

                //Lay may thong diep client gui toi
                String message = (String) ois.readObject();
                System.out.println("Reading message");
                System.out.println("Message: " + message);

                //Neu check pass dung va chua co client nao ket noi thi gan clientinfor
                if (message.equals("RequireConnect:" + pass) && clientInfor == null) {
                    clientInfor = new LANSocketInfor(socket.getInetAddress(), socket.getPort());
                    System.out.println("Pass chinh xac, start sending image to" + clientInfor.getIp().toString() + ":" + clientInfor.getPort());
                    lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttCheckedSuccess"));

                    //Khoi tao cac output stream
                    os = socket.getOutputStream();
                    oos = new ObjectOutputStream(os);
                    oos.writeObject("XacThucThanhCong");
                    oos.reset();
                    //Goi ham gui image va break
                    try {
                        //OK KET NOI Mo Khung chat
                        BLL_LANForm.GetInstance().OpenChatWindow(port + 1);

                        lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttStartSendImg") + clientInfor.getIp().toString()
                                + ":" + clientInfor.getPort());
                        System.out.println("Chuan bi gui hinh");
                        LoopSendImage();
                    } catch (Exception e) {
                        // TODO: handle exception
                        BLL_LANForm.GetInstance().AnnounceConnectError("LANServerThread Khong co ket noi!");
                        lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttNoConnection"));
                        //TAT KHUNG CHAT
                        BLL_LANForm.GetInstance().CloseChatWindow();
                    }
                } else {
                    ois.close();
                    is.close();
                    socket.close();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Loi roi: " + e.toString());
            lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttStartServerFailed"));

        }
    }

    private DTO_ArrayLANImageInforObject arrLANIIO = null;

    private void LoopSendImage() {
        ScreenCapturer screenCapturer = new ScreenCapturer();
        isRunningMessageReceive = true;
        messageReceiver.start();
        int countFaild = 0;
        while (true) {
            try {
                Thread.sleep(40);
                //Convert to byte
                arrLANIIO = screenCapturer.GetLANScreenCaptureImageArray();
                if (arrLANIIO.arr.size() == 0) {
                    continue;
                }
                try {
                    oos.writeObject(arrLANIIO);
                    arrLANIIO.Clear();
                    oos.flush();
                    os.flush();
                    oos.reset();
                    Runtime.getRuntime().gc();
                    //System.out.println("Send img OK");
                } catch (Exception e) {
                    countFaild++;
                    if (countFaild >= 10) {
                        System.out.println("Send obj to client failed!-Reset Server");
                        lanFormInstance.ShowStatus(lanFormInstance.GetLanguageString("sttResetServer"));
                        break;
                    }
                }

            } catch (Exception ex) {
                System.out.println(ex.toString());

            }
        }
        ResetServer();
    }

    public class LANServerMessageReceiver extends Thread {
        @Override
        public void run() {
            super.run();
            int countFaild = 0;
            while (isRunningMessageReceive) {
                String message = "";
                try {
                    message = (String) ois.readObject();
                    //System.out.println(message);
                    mouseKeyExcuter.ExecuteByMessage(message);
                } catch (EOFException e) { //Bat Exception khi mat ket noi client
                    countFaild++;
                    if (countFaild >= 10)
                        return;
                } catch (ClassNotFoundException | IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }

            }
        }
    }

    public static void main(String[] args) {
        LANServerThread lanServerThread = new LANServerThread(1999, "ahihi");
        lanServerThread.run();
    }

    public void CloseServer() {
        try {
            isRunningMessageReceive = false;
            if (oos != null)
                try {
                    oos.reset();
                    oos.close();
                } catch (Exception e) {
                }
            if (os != null)
                try {
                    os.close();
                } catch (Exception e) {
                }
            if (ois != null)
                try {
                    ois.reset();
                    ois.close();
                } catch (Exception e) {
                }
            if (is != null)
                try {
                    is.reset();
                    is.close();
                } catch (Exception e) {
                }
            if (socket != null)
                try {

                    socket.close();
                } catch (Exception e) {
                }

            oos = null;
            //bout=null;
            os = null;
            ois = null;
            is = null;
            socket = null;

            serverSocket.close();
            serverSocket = null;
            clientInfor = null;
            arrLANIIO.Clear();
            messageReceiver.interrupt();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
