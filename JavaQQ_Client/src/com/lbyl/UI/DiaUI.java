package com.lbyl.UI;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.lbyl.Biz.ClientThread;
import com.lbyl.Constant.CanstantValue_protocol;

public class DiaUI extends JFrame {

	private JTextArea jta_conversation;
	private JLabel jl_noticeInput;
	private JTextArea jta_input;
	private Button but_send;
	private ClientThread ct;

	public DiaUI(ClientThread ct) {
		this.ct = ct;
	}

	/**
	 * 初始化界面
	 */
	public void initUI() {

		initFrame();// 初始化控件
		addComp();// add控件
		addListeners();

		this.setVisible(true);
	}

	/**
	 * add监听器
	 */
	private void addListeners() {

		but_send.setActionCommand("sendMsg");

		but_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				switch (e.getActionCommand()) {
				case "sendMsg":
					actionSendMsg();
					break;

				default:
					break;
				}
			}
		});
	}

	/**
	 * 发送消息
	 */
	protected void actionSendMsg() {
		try {

			String inputTxt = jta_input.getText();
			if (inputTxt.equals("")) {
				JOptionPane.showMessageDialog(this, "发送内容不能为空。");
				
				return;
			}
			String[][] s = { { CanstantValue_protocol.TYPE, CanstantValue_protocol.CHAT_TYPE_CTOS },
					{ CanstantValue_protocol.CHAT_MESSAGE, inputTxt } };
			ct.sendXmlReq(s);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * add控件
	 */
	private void addComp() {

		this.add(jta_conversation);
		this.add(jl_noticeInput);
		this.add(jta_input);
		this.add(but_send);
	}

	/**
	 * 初始化界面及控件
	 */
	private void initFrame() {
		this.setTitle("JavaQQ");
		this.setLayout(new FlowLayout());
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(3);
		this.setSize(500, 500);

		jta_conversation = new JTextArea(20, 20);
		jl_noticeInput = new JLabel("请输入：");
		jta_input = new JTextArea(5, 10);
		but_send = new Button("发送");
	}
	public JTextArea getJtfConv(){
		return jta_conversation;
	}
	
}
