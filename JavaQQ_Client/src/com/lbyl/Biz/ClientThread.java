package com.lbyl.Biz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.JOptionPane;

import com.lbyl.Constant.CanstantValue_protocol;
import com.lbyl.UI.DiaUI;
import com.lbyl.UI.LoginUI;
import com.lbyl.Utils.LogUtil;

public class ClientThread extends Thread {

	private String readStr;
	private String type;
	private InputStream ins;
	private LoginUI Lui;
	private Socket socket;
	private OutputStream ous;
	private String regResult;
	private String logResult;
	private LoginUI LUI;
	private DiaUI diaUI;

	public ClientThread(LoginUI LUI) {
		this.LUI = LUI;
	}

	/**
	 * 连接服务器
	 */
	public boolean ConToServer() {
		try {

			socket = new Socket("localhost", 9090);

			ins = socket.getInputStream();
			ous = socket.getOutputStream();

			LogUtil.log("客户端连接了socket");
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.log("发生未知主机错误");
			return false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.log("发生IO错误");
			return false;
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		try {
			// 循环等待对话
			while (!interrupted()) {
				readStr = readString();
				type = getXmlType(readStr);// 解析类型
				actionType(type);// 根据类型做出反应

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 根据类型做出反应
	 * 
	 * @throws IOException
	 */
	private void actionType(String type) {

		if (type.equals(CanstantValue_protocol.REGISTER_RESPOND_TYPE)) {// 如果是注册回应
			actionRegRespon();
		} else if (type.equals(CanstantValue_protocol.LOGIN_RESPOND_TYPE)) {// 如果是登录回应
			actionLogRespon();
		}else if (type.equals(CanstantValue_protocol.CHAT_TYPE_STOC)) {//如果是聊天请求
			actionChat();
		}

	}
	/**
	 * 聊天回应事件
	 */
	private void actionChat() {
		// TODO Auto-generated method stub
		String parseXmlStr = parseXmlStr(CanstantValue_protocol.CHAT_MESSAGE, readStr);
		diaUI.getJtfConv().setText(parseXmlStr.toString());
	}

	/**
	 * 登录回应事件
	 */
	private void actionLogRespon() {
		logResult = parseXmlStr(CanstantValue_protocol.LOGIN_RESPOND_VALUE, readStr);
		if (logResult.equals(CanstantValue_protocol.LOGIN_RESPOND_SUCCESS)) {
			JOptionPane.showMessageDialog(LUI, "登录成功!");
			launchDiaUI();
		} else if (logResult.equals(CanstantValue_protocol.LOGIN_RESPOND_NODATA)) {
			JOptionPane.showMessageDialog(LUI, "该用户不存在!");

		} else if (logResult.equals(CanstantValue_protocol.LOGIN_RESPOND_WRONGPASSWORD)) {
			JOptionPane.showMessageDialog(LUI, "密码错误");
		}
	}
	//启动对话视窗
	private void launchDiaUI() {
		diaUI = new DiaUI(this);
		diaUI.initUI();
	}

	/**
	 * 注册回应事件
	 */
	private void actionRegRespon() {
		regResult = parseXmlStr(CanstantValue_protocol.REGISTER_RESPOND_VALUE, readStr);
		if (regResult.equals(CanstantValue_protocol.REGISTER_RESPOND_SUCCESS)) {
			JOptionPane.showMessageDialog(LUI, "注册成功!");
		} else if (regResult.equals(CanstantValue_protocol.REGISTER_RESPOND_EXISTINGDATA)) {
			JOptionPane.showMessageDialog(LUI, "该用户名已存在!");
			
		}
	}

	/**
	 * 从输入流中读取完整信息
	 * 
	 * @return
	 * @throws IOException
	 */
	public String readString() throws IOException {
		char b;
		StringBuffer sBuffer = new StringBuffer();

		while ((b = (char) ins.read()) != -1) {
			sBuffer.append(b);

			if (sBuffer.toString().endsWith("</msg>")) {
				break;
			}
		}
		String result = new String(sBuffer.toString().getBytes("ISO-8859-1"), "GBK");
		LogUtil.log("从流中读取到的完整信息是" + result);
		return result;

	}

	/**
	 * 消息组装器
	 * 
	 * @param reqString
	 *            将要组装的二维字符串数组
	 * @return 组装完毕的字符串
	 */
	public String buildStr(String[][] reqString) {

		StringBuilder sb = new StringBuilder();

		sb.append("<msg>");

		for (int i = 0; i < reqString.length; i++) {
			sb.append("<" + reqString[i][0] + ">" + reqString[i][1] + "</" + reqString[i][0] + ">");
		}
		sb.append("</msg>");

		return sb.toString();
	}

	/**
	 * 消息发送器
	 * 
	 * @param reqString
	 *            将要组装的二维字符数组，例如：{{"name","kibin"},{"age","12"},{"sex","男"}}
	 * @param ous
	 * @throws IOException
	 */
	public void sendXmlReq(String[][] reqString) throws IOException {
		String s = buildStr(reqString);

		ous.write(s.getBytes());
		LogUtil.log("cs数据传送完毕");
	}

	/**
	 * 消息解析器，根据指定类解析出内容。
	 * 
	 * @param typeStr
	 *            试图解析的类型
	 * @param ResPonStr
	 *            需要解析的字符串
	 * @return 解析完毕的内容
	 */

	public String parseXmlStr(String typeStr, String ResPonStr) {

		int startFlag = ResPonStr.indexOf("<" + typeStr + ">");
		int endFlag = ResPonStr.indexOf("</" + typeStr + ">");
		LogUtil.log("即将分割字符串：从" + startFlag + "到" + endFlag);
		String realString = ResPonStr.substring(startFlag + typeStr.length() + 2, endFlag);

		return realString;

	}

	/**
	 * 取得xml语句的的主要类型
	 * 
	 * @param ReqString
	 * @return
	 */
	public String getXmlType(String ReqString) {
		return parseXmlStr(CanstantValue_protocol.TYPE, ReqString);
	}

	/**
	 * 获取注册结果信息
	 * 
	 * @return
	 */
	public String getRegResult() {
		return regResult;
	}

	/**
	 * 获取登录结果
	 */
	public String getLogResult() {
		return logResult;
	}

}
