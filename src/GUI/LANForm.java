package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import BLL.BLL_LANForm;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Font;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.SystemColor;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class LANForm extends JFrame {

    private final JPanel contentPane;

    public static boolean isOpened = false;
    private final BLL_LANForm bll_LANForm;
    private final JTextField txtYourIP;
    private final JTextField txtYourPort;
    private final JTextField txtYourPassword;
    private final JTextField txtPartnerIP;
    private final JTextField txtPartnerPort;
    private final JTextField txtPartnerPassword;
    private final JLabel lblStatus;
    private final JLabel lblAllowConnection;
    private final JLabel lblRemoteAnother;
    private final JLabel lblInfoAllowConnection;
    private final JLabel lblInfoRemoteAnother;
    private final JLabel lblYourIP;
    private final JLabel lblYourPort;
    private final JLabel lblYourPassword;
    private final JButton btnOpenConnect;
    private final JLabel lblPartnerIP;
    private final JLabel lblPartnerPort;
    private final JLabel lblPartnerPassword;
    private final JButton btnConnect;

    private final JButton btnOpenchat;

    private String msgPortInvalid = "";
    private String msgOpenConnectFailed = "";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                //LANForm frame = new LANForm();
                //frame.setVisible(true);
                LANForm.OpenForm();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static void OpenForm() {
        if (isOpened)
            return;
        else {
            isOpened = true;
            instance = new LANForm();
            instance.setVisible(true);
            //main(args);
        }
    }

    private static LANForm instance = null;

    public static LANForm GetInstance() {
        return instance;
    }

    public static void RemoveInstance() {
        instance = null;
    }

    /**
     * Create the frame.
     */
    public LANForm() {
        setResizable(false);
        setTitle("L-Viewer");
        setBackground(Color.LIGHT_GRAY);
        setForeground(new Color(191, 205, 219));
        bll_LANForm = new BLL_LANForm();
        //Event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isOpened = false;
                //bll_LANForm.CloseChatWindow();
                bll_LANForm.CloseConnect();
                try {
                    bll_LANForm.CloseChatWindow();
                } catch (Exception e2) {
                    // TODO: handle exception
                }
                instance = null;
            }

            @Override
            public void windowOpened(WindowEvent e) {
                txtYourIP.setText(BLL_LANForm.GetMyIPv4());
                txtYourPort.setText(BLL_LANForm.GetMyPort());
                txtYourPassword.setText(BLL_LANForm.RandomPassword());

                SetLanguage(1);
            }
        });
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setBounds(100, 100, 860, 414);

        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.inactiveCaption);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(SystemColor.inactiveCaption);
        statusPanel.setBounds(5, 335, 789, 20);
        statusPanel.setFocusable(false);
        statusPanel.setAlignmentY(0.0f);
        statusPanel.setAlignmentX(0.0f);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 248, 255));
        panel.setBounds(5, 5, 837, 329);
        panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

        JPanel pnlOpenCnn = new JPanel();
        pnlOpenCnn.setBackground(new Color(248, 248, 255));
        pnlOpenCnn.setBorder(new MatteBorder(0, 0, 0, 0, (Color) new Color(0, 0, 0)));

        lblYourIP = new JLabel("Your IP");
        lblYourIP.setAlignmentY(Component.TOP_ALIGNMENT);
        lblYourIP.setFont(new Font("Tahoma", Font.PLAIN, 16));

        lblYourPort = new JLabel("Your Port");
        lblYourPort.setFont(new Font("Tahoma", Font.PLAIN, 16));

        txtYourIP = new JTextField();

        txtYourIP.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtYourIP.setEditable(false);
        txtYourIP.setColumns(10);

        txtYourPort = new JTextField();
        txtYourPort.setText("1999");
        txtYourPort.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtYourPort.setColumns(10);

        lblYourPassword = new JLabel("Password");
        lblYourPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));

        txtYourPassword = new JTextField();
        txtYourPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtYourPassword.setColumns(10);

        btnOpenConnect = new JButton("Allow Connection");
        btnOpenConnect.setBackground(SystemColor.control);
        btnOpenConnect.setFocusable(false);
        btnOpenConnect.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnOpenConnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (bll_LANForm.GetIsOpenConnection() == false) {
                    try {
                        bll_LANForm.OpenConnect(txtYourIP.getText(), txtYourPort.getText(), txtYourPassword.getText());
                        btnOpenConnect.setBackground(Color.RED);
                        btnOpenConnect.setText(GetLanguageString("lblCloseConnection"));
                        txtYourPort.setEditable(false);
                        txtYourPassword.setEditable(false);
                    } catch (Exception e) {
                        if (e.getMessage().equals("Port khong hop le!")) {
                            ShowMessage(msgPortInvalid, "Port problem", JOptionPane.ERROR_MESSAGE);
                        } else {
                            ShowMessage(msgOpenConnectFailed + txtYourIP.getText() + ":" + txtYourPort.getText(), "Open connection failed", JOptionPane.ERROR_MESSAGE);
                        }
                        System.out.println("Mo ket noi that bai!");
                        ShowStatus(GetLanguageString("sttOpenConnectFailed"));
                    }

                } else {
                    try {
                        btnOpenConnect.setBackground(new Color(240, 240, 240));
                        btnOpenConnect.setText(GetLanguageString("lblAllowConnection"));
                        txtYourPort.setEditable(true);
                        txtYourPassword.setEditable(true);
                        bll_LANForm.CloseConnect();
                        ShowStatus(GetLanguageString("sttCloseConnection"));

                    } catch (Exception e) {
                        System.out.println("Server dong ket noi that bai!");
                    }
                    try {
                        bll_LANForm.CloseChatWindow();
                    } catch (Exception e) {
                    }
                }
            }
        });

        btnOpenchat = new JButton("");
        btnOpenchat.setBorder(null);
        btnOpenchat.setBackground(new Color(248, 248, 255));
        try {
            btnOpenchat.setIcon(new ImageIcon(ImageIO.read(new File("./resource/chatIcon.png"))));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            System.out.println("Can not load icon!");
        }
        btnOpenchat.setFocusable(false);
        btnOpenchat.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnOpenchat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                bll_LANForm.ReOpenChat();
            }
        });

        lblInfoAllowConnection = new JLabel("<html>Please click \"Allow Connection\" and send your IP, port, password to your partner if you want they to remote your computer.</html>");
        lblInfoAllowConnection.setBackground(new Color(248, 248, 255));
        lblInfoAllowConnection.setFont(new Font("Tahoma", Font.ITALIC, 16));
        lblInfoAllowConnection.setOpaque(true);
        GroupLayout gl_pnlOpenCnn = new GroupLayout(pnlOpenCnn);
        gl_pnlOpenCnn.setHorizontalGroup(
                gl_pnlOpenCnn.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_pnlOpenCnn.createSequentialGroup()
                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(gl_pnlOpenCnn.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(lblInfoAllowConnection, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(gl_pnlOpenCnn.createSequentialGroup()
                                                .addGap(31)
                                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.TRAILING)
                                                        .addGroup(gl_pnlOpenCnn.createSequentialGroup()
                                                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(lblYourPassword)
                                                                        .addComponent(lblYourIP))
                                                                .addGap(19))
                                                        .addGroup(gl_pnlOpenCnn.createSequentialGroup()
                                                                .addComponent(lblYourPort)
                                                                .addGap(18)))
                                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(txtYourIP, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                                        .addGroup(Alignment.TRAILING, gl_pnlOpenCnn.createSequentialGroup()
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(btnOpenchat, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(btnOpenConnect))
                                                        .addComponent(txtYourPassword, 238, 238, Short.MAX_VALUE)
                                                        .addGroup(gl_pnlOpenCnn.createSequentialGroup()
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(txtYourPort, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)))))
                                .addGap(28))
        );
        gl_pnlOpenCnn.setVerticalGroup(
                gl_pnlOpenCnn.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_pnlOpenCnn.createSequentialGroup()
                                .addGap(5)
                                .addComponent(lblInfoAllowConnection)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(txtYourIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblYourIP))
                                .addGap(18)
                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.LEADING)
                                        .addComponent(lblYourPort)
                                        .addComponent(txtYourPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(13)
                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblYourPassword)
                                        .addComponent(txtYourPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addGroup(gl_pnlOpenCnn.createParallelGroup(Alignment.LEADING)
                                        .addComponent(btnOpenConnect)
                                        .addComponent(btnOpenchat, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                                .addGap(17))
        );
        pnlOpenCnn.setLayout(gl_pnlOpenCnn);

        JPanel pnlRemoteForm = new JPanel();
        pnlRemoteForm.setBackground(new Color(248, 248, 255));
        pnlRemoteForm.setBorder(new MatteBorder(0, 1, 0, 0, (Color) new Color(0, 0, 0)));

        lblPartnerIP = new JLabel("Partner IP");
        lblPartnerIP.setBounds(13, 84, 71, 20);
        lblPartnerIP.setAlignmentY(Component.TOP_ALIGNMENT);
        lblPartnerIP.setFont(new Font("Tahoma", Font.PLAIN, 16));

        lblPartnerPort = new JLabel("Partner Port");
        lblPartnerPort.setBounds(13, 124, 85, 20);
        lblPartnerPort.setAlignmentY(Component.TOP_ALIGNMENT);
        lblPartnerPort.setFont(new Font("Tahoma", Font.PLAIN, 16));

        lblPartnerPassword = new JLabel("Password");
        lblPartnerPassword.setBounds(13, 164, 67, 20);
        lblPartnerPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));

        txtPartnerIP = new JTextField();
        txtPartnerIP.setBounds(141, 79, 216, 26);
        txtPartnerIP.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPartnerIP.setAlignmentY(Component.TOP_ALIGNMENT);
        txtPartnerIP.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtPartnerIP.setColumns(10);

        txtPartnerPort = new JTextField();
        txtPartnerPort.setText("1999");
        txtPartnerPort.setBounds(141, 119, 216, 26);
        txtPartnerPort.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPartnerPort.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        txtPartnerPort.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtPartnerPort.setColumns(10);

        txtPartnerPassword = new JTextField();
        txtPartnerPassword.setBounds(142, 159, 216, 26);
        txtPartnerPassword.setAlignmentY(Component.TOP_ALIGNMENT);
        txtPartnerPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPartnerPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtPartnerPassword.setColumns(10);

        btnConnect = new JButton("Start Remote");
        btnConnect.setBackground(SystemColor.control);
        btnConnect.setBounds(181, 198, 176, 29);
        btnConnect.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnConnect.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        btnConnect.setFocusable(false);
        btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnConnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                bll_LANForm.ConnectAndShowRemoteForm(txtPartnerIP.getText(), txtPartnerPort.getText(), txtPartnerPassword.getText(), language);
            }
        });

        lblInfoRemoteAnother = new JLabel("<html>Please enter IP, port, password of the computer that you want to remote.</html>");
        lblInfoRemoteAnother.setBackground(new Color(248, 248, 255));
        lblInfoRemoteAnother.setBounds(43, 13, 284, 40);
        lblInfoRemoteAnother.setFont(new Font("Tahoma", Font.ITALIC, 16));
        lblInfoRemoteAnother.setVerticalTextPosition(SwingConstants.TOP);
        lblInfoRemoteAnother.setOpaque(true);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(SystemColor.inactiveCaption);
        panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

        lblAllowConnection = new JLabel("Allow Connection");
        panel_1.add(lblAllowConnection);
        lblAllowConnection.setFont(new Font("Tahoma", Font.BOLD, 20));

        JPanel panel_2 = new JPanel();
        panel_2.setBackground(SystemColor.inactiveCaption);
        panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

        lblRemoteAnother = new JLabel("Remote Another");
        panel_2.add(lblRemoteAnother);
        lblRemoteAnother.setFont(new Font("Tahoma", Font.BOLD, 20));
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
                gl_panel.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_panel.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(panel_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(pnlOpenCnn, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_panel.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(pnlRemoteForm, GroupLayout.PREFERRED_SIZE, 401, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(gl_panel.createSequentialGroup()
                                                .addGap(31)
                                                .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 342, GroupLayout.PREFERRED_SIZE)))
                                .addGap(12))
        );
        gl_panel.setVerticalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                        .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(pnlRemoteForm, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                                        .addComponent(pnlOpenCnn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        pnlRemoteForm.setLayout(null);
        pnlRemoteForm.add(lblInfoRemoteAnother);
        pnlRemoteForm.add(lblPartnerPassword);
        pnlRemoteForm.add(lblPartnerIP);
        pnlRemoteForm.add(lblPartnerPort);
        pnlRemoteForm.add(txtPartnerIP);
        pnlRemoteForm.add(txtPartnerPort);
        pnlRemoteForm.add(txtPartnerPassword);
        pnlRemoteForm.add(btnConnect);
        panel.setLayout(gl_panel);

        lblStatus = new JLabel("Status: ...");
        lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
        GroupLayout gl_statusPanel = new GroupLayout(statusPanel);
        gl_statusPanel.setHorizontalGroup(
                gl_statusPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_statusPanel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblStatus)
                                .addContainerGap(748, Short.MAX_VALUE))
        );
        gl_statusPanel.setVerticalGroup(
                gl_statusPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_statusPanel.createSequentialGroup()
                                .addComponent(lblStatus)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        statusPanel.setLayout(gl_statusPanel);
        contentPane.setLayout(null);
        contentPane.add(panel);
        contentPane.add(statusPanel);
    }


    public void ShowStatus(String status) {
        lblStatus.setText("Status: " + status);
    }

    public void ShowMessage(String message, String tile, int type) {
        JOptionPane.showMessageDialog(instance, message, tile, type);
    }


    private ResourceBundle languageRB = null;

    public String GetLanguageString(String key) {
        if (languageRB == null)
            languageRB = ResourceBundle.getBundle("internationalization.message.language", new Locale("en"));
        return languageRB.getString(key);
    }

    private int language = 0;

    public void SetLanguage(int language) {
        this.language = language;
        Locale locale = switch (language) {
            case 0 ->//en
                    new Locale("en");
            case 1 ->//vi
                    new Locale("vi");
            default -> new Locale("en");
        };
        languageRB = ResourceBundle.getBundle("internationalization.message.language", locale);
        lblAllowConnection.setText(languageRB.getString("lblAllowConnection"));
        lblRemoteAnother.setText(languageRB.getString("lblRemoteAnother"));
        lblInfoAllowConnection.setText(languageRB.getString("lblInfoAllowConnection"));
        lblInfoRemoteAnother.setText(languageRB.getString("lblInfoRemoteAnother"));
        lblYourIP.setText(languageRB.getString("lblYourIP"));
        lblYourPort.setText(languageRB.getString("lblYourPort"));
        lblYourPassword.setText(languageRB.getString("lblYourPassword"));
        btnOpenConnect.setText(languageRB.getString("btnOpenConnect"));
        lblPartnerIP.setText(languageRB.getString("lblPartnerIP"));
        lblPartnerPort.setText(languageRB.getString("lblPartnerPort"));
        lblPartnerPassword.setText(languageRB.getString("lblPartnerPassword"));
        btnConnect.setText(languageRB.getString("btnConnect"));

        txtYourIP.setToolTipText(languageRB.getString("tipYourIP"));
        txtYourPort.setToolTipText(languageRB.getString("tipYourPort"));
        txtYourPassword.setToolTipText(languageRB.getString("tipYourPassword"));
        btnOpenConnect.setToolTipText(languageRB.getString("tipAllowConnection"));
        txtPartnerIP.setToolTipText(languageRB.getString("tipPartnerIP"));
        btnOpenchat.setToolTipText(languageRB.getString("tipOpenChat"));
        txtPartnerPort.setToolTipText(languageRB.getString("tipPartnerPort"));
        txtPartnerPassword.setToolTipText(languageRB.getString("tipPartnerPassword"));
        btnConnect.setToolTipText(languageRB.getString("tipStartRemote"));

        msgOpenConnectFailed = languageRB.getString("msgOpenConnectFailed");
        msgPortInvalid = languageRB.getString("msgPortInvalid");
    }
}
