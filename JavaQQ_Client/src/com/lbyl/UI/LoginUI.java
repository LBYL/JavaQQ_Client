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

		initFrame();// 初始化窗口及组件
		addComp();// add组件

		this.setVisible(true);

	}

	/**
	 * add组件
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
	 * 初始化控件
	 */
	private void initFrame() {
		this.setTitle("JavaQQ");
		this.setLayout(new FlowLayout());
		this.setSize(200, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(3);

		jlb_ip = new JLabel("服务器IP：");
		jtf_ip = new JTextField(5);
		jtf_ip.setText("locaohost");
		jlb_port = new JLabel("端口：");
		jtf_port = new JTextField(5);
		jtf_port.setText("9090");
		jlb_user = new JLabel("用户名：");
		jtf_user = new JTextField(10);
		jlb_pwd = new JLabel("密码：");
		jtf_pwd = new JTextField(10);
		jb_login = new JButton("登录");
		jlb_error = new JLabel("");
		jb_reg = new JButton("注册");

		// 设置动作命令
		setActionCommand();

		// 加监听器
		addListeners();
	}

	/**
	 * 设置动作命令
	 */
	private void setActionCommand() {
		jb_login.setActionCommand("login");
		jb_reg.setActionCommand("register");
	}

	/**
	 * 为组件加上监听器
	 */
	private void addListeners() {
		jb_login.addActionListener(commonListener);
		jb_reg.addActionListener(commonListener);
	}

	/**
	 * 通用监听器
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
	 * 注册事件 1.尝试连接网络 2.检查用户名和密码是否为空 3.发送注册消息 需要信息：希望的用户名和密码 动作：连接网络，获取信息，发送请求
	 * 
	 * @throws IOException
	 */
	public void actionRegister() {
		if (!checkEmpty()) {// 如果信息检查不过关
			return;
			
		}

		if (!isConnected) {// 如果没有联网

			ct = new ClientThread(this);// 实例化客户端线程类。尚未开始线程
			if (!ct.ConToServer()) {// 则与网络连接

				LogUtil.log("客户端联网失败");
				jlb_error.setText("联网失败");

				return;
			}
			ct.start();// 开始对应客户端线程

			isConnected = true;// 设置联网状态为真
		}

		String name = jtf_user.getText();
		String pwd = jtf_pwd.getText();
		String[][] s = { { CanstantValue_protocol.TYPE,  CanstantValue_protocol.REGISTER_TYPE}, { CanstantValue_protocol.REGISTER_NAME, name }, { CanstantValue_protocol.LOGIN_PASSWORD, pwd } };

		try {
			ct.sendXmlReq(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 发送注册请求

	}
	/**
	 * 登录反馈应答
	 */
	protected void actionLogResPon() {
		ct.parseXmlStr(CanstantValue_protocol.LOGIN_RESPOND_TYPE, ResPonStr);
	}
	/**
	 * 登录事件
	 */
	protected void actionLogin() {
		if (!checkEmpty()) {// 如果信息检查不过关
			return;
		}
		if (!isConnected) {// 如果没有联网

			ct = new ClientThread(this);// 实例化客户端线程类。尚未开始线程
			if (!ct.ConToServer()) {// 则与网络连接

				LogUtil.log("客户端联网失败");
				jlb_error.setText("联网失败");

				return;
			}
			ct.start();// 开始对应客户端线程

			isConnected = true;// 设置联网状态为真
		}

		String name = jtf_user.getText();
		String pwd = jtf_pwd.getText();
		String[][] s = { { CanstantValue_protocol.TYPE, CanstantValue_protocol.LOGIN_TYPE }, { CanstantValue_protocol.LOGIN_NAME, name }, { CanstantValue_protocol.LOGIN_PASSWORD, pwd } };
		// 发送登录请求
		try {
			ct.sendXmlReq(s);
			LogUtil.log("发送了登录消息");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 检查信息
	 */
	private Boolean checkEmpty() {

		if (jtf_user.getText().isEmpty()) {
			jlb_error.setText("请填写用户名");
			return false;
		}
		if (jtf_pwd.getText().isEmpty()) {
			jlb_error.setText("请填写密码");
			return false;
		}
		return true;
	}

}