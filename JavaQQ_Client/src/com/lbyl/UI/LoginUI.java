package com.lbyl.UI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.lbyl.Biz.ClientThread;
import com.lbyl.Constant.CanstantValue_protocol;
import com.lbyl.Utils.LogUtil;

public class LoginUI extends JFrame  {

	private JLabel jlb_user;
	private JTextField jtf_user;
	private JButton jb_login;
	private JLabel jlb_pwd;
	private JTextField jtf_pwd;
	private JLabel jlb_error;
	private String userName;
	private String userPwd;
	private JLabel jlb_ip;
	private JTextField jtf_ip;
	private JLabel jlb_port;
	private JTextField jtf_port;
	private JButton jb_reg;
	private ClientThread ct;

	private boolean isConnected = false;

	public void initUI() {

		initFrame();// ��ʼ�����ڼ����
		addComp();// add���

		this.setVisible(true);

	}

	/**
	 * add���
	 */
	private void addComp() {
		this.add(jlb_ip);
		this.add(jtf_ip);
		this.add(jlb_port);
		this.add(jtf_port);
		this.add(jlb_user);
		this.add(jtf_user);
		this.add(jlb_pwd);
		this.add(jtf_pwd);
		this.add(jb_login);
		this.add(jb_reg);
		this.add(jlb_error);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initFrame() {
		this.setTitle("JavaQQ");
		this.setLayout(new FlowLayout());
		this.setSize(200, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(3);

		jlb_ip = new JLabel("������IP��");
		jtf_ip = new JTextField(5);
		jtf_ip.setText("locaohost");
		jlb_port = new JLabel("�˿ڣ�");
		jtf_port = new JTextField(5);
		jtf_port.setText("9090");
		jlb_user = new JLabel("�û�����");
		jtf_user = new JTextField(10);
		jlb_pwd = new JLabel("���룺");
		jtf_pwd = new JTextField(10);
		jb_login = new JButton("��¼");
		jlb_error = new JLabel("");
		jb_reg = new JButton("ע��");

		// ���ö�������
		setActionCommand();

		// �Ӽ�����
		addListeners();
	}

	/**
	 * ���ö�������
	 */
	private void setActionCommand() {
		jb_login.setActionCommand("login");
		jb_reg.setActionCommand("register");
	}

	/**
	 * Ϊ������ϼ�����
	 */
	private void addListeners() {
		jb_login.addActionListener(commonListener);
		jb_reg.addActionListener(commonListener);
	}

	/**
	 * ͨ�ü�����
	 */
	ActionListener commonListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			
			switch (e.getActionCommand()) {
			
			case "login":
				actionLogin();
				break;
				
			case "register":

				actionRegister();
				break;
			}
		}
	};
	private String ResPonStr;

	/**
	 * ע���¼� 1.������������ 2.����û����������Ƿ�Ϊ�� 3.����ע����Ϣ ��Ҫ��Ϣ��ϣ�����û��������� �������������磬��ȡ��Ϣ����������
	 * 
	 * @throws IOException
	 */
	public void actionRegister() {
		if (!checkEmpty()) {// �����Ϣ��鲻����
			return;
			
		}

		if (!isConnected) {// ���û������

			ct = new ClientThread(this);// ʵ�����ͻ����߳��ࡣ��δ��ʼ�߳�
			if (!ct.ConToServer()) {// ������������

				LogUtil.log("�ͻ�������ʧ��");
				jlb_error.setText("����ʧ��");

				return;
			}
			ct.start();// ��ʼ��Ӧ�ͻ����߳�

			isConnected = true;// ��������״̬Ϊ��
		}

		String name = jtf_user.getText();
		String pwd = jtf_pwd.getText();
		String[][] s = { { CanstantValue_protocol.TYPE,  CanstantValue_protocol.REGISTER_TYPE}, { CanstantValue_protocol.REGISTER_NAME, name }, { CanstantValue_protocol.LOGIN_PASSWORD, pwd } };

		try {
			ct.sendXmlReq(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // ����ע������

	}
	/**
	 * ��¼����Ӧ��
	 */
	protected void actionLogResPon() {
		ct.parseXmlStr(CanstantValue_protocol.LOGIN_RESPOND_TYPE, ResPonStr);
	}
	/**
	 * ��¼�¼�
	 */
	protected void actionLogin() {
		if (!checkEmpty()) {// �����Ϣ��鲻����
			return;
		}
		if (!isConnected) {// ���û������

			ct = new ClientThread(this);// ʵ�����ͻ����߳��ࡣ��δ��ʼ�߳�
			if (!ct.ConToServer()) {// ������������

				LogUtil.log("�ͻ�������ʧ��");
				jlb_error.setText("����ʧ��");

				return;
			}
			ct.start();// ��ʼ��Ӧ�ͻ����߳�

			isConnected = true;// ��������״̬Ϊ��
		}

		String name = jtf_user.getText();
		String pwd = jtf_pwd.getText();
		String[][] s = { { CanstantValue_protocol.TYPE, CanstantValue_protocol.LOGIN_TYPE }, { CanstantValue_protocol.LOGIN_NAME, name }, { CanstantValue_protocol.LOGIN_PASSWORD, pwd } };
		// ���͵�¼����
		try {
			ct.sendXmlReq(s);
			LogUtil.log("�����˵�¼��Ϣ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * �����Ϣ
	 */
	private Boolean checkEmpty() {

		if (jtf_user.getText().isEmpty()) {
			jlb_error.setText("����д�û���");
			return false;
		}
		if (jtf_pwd.getText().isEmpty()) {
			jlb_error.setText("����д����");
			return false;
		}
		return true;
	}

}