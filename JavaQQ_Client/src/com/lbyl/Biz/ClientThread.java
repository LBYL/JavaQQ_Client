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
	 * ���ӷ�����
	 */
	public boolean ConToServer() {
		try {

			socket = new Socket("localhost", 9090);

			ins = socket.getInputStream();
			ous = socket.getOutputStream();

			LogUtil.log("�ͻ���������socket");
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.log("����δ֪��������");
			return false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.log("����IO����");
			return false;
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		try {
			// ѭ���ȴ��Ի�
			while (!interrupted()) {
				readStr = readString();
				type = getXmlType(readStr);// ��������
				actionType(type);// ��������������Ӧ

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ��������������Ӧ
	 * 
	 * @throws IOException
	 */
	private void actionType(String type) {

		if (type.equals(CanstantValue_protocol.REGISTER_RESPOND_TYPE)) {// �����ע���Ӧ
			actionRegRespon();
		} else if (type.equals(CanstantValue_protocol.LOGIN_RESPOND_TYPE)) {// ����ǵ�¼��Ӧ
			actionLogRespon();
		}else if (type.equals(CanstantValue_protocol.CHAT_TYPE_STOC)) {//�������������
			actionChat();
		}

	}
	/**
	 * �����Ӧ�¼�
	 */
	private void actionChat() {
		// TODO Auto-generated method stub
		String parseXmlStr = parseXmlStr(CanstantValue_protocol.CHAT_MESSAGE, readStr);
		diaUI.getJtfConv().setText(parseXmlStr.toString());
	}

	/**
	 * ��¼��Ӧ�¼�
	 */
	private void actionLogRespon() {
		logResult = parseXmlStr(CanstantValue_protocol.LOGIN_RESPOND_VALUE, readStr);
		if (logResult.equals(CanstantValue_protocol.LOGIN_RESPOND_SUCCESS)) {
			JOptionPane.showMessageDialog(LUI, "��¼�ɹ�!");
			launchDiaUI();
		} else if (logResult.equals(CanstantValue_protocol.LOGIN_RESPOND_NODATA)) {
			JOptionPane.showMessageDialog(LUI, "���û�������!");

		} else if (logResult.equals(CanstantValue_protocol.LOGIN_RESPOND_WRONGPASSWORD)) {
			JOptionPane.showMessageDialog(LUI, "�������");
		}
	}
	//�����Ի��Ӵ�
	private void launchDiaUI() {
		diaUI = new DiaUI(this);
		diaUI.initUI();
	}

	/**
	 * ע���Ӧ�¼�
	 */
	private void actionRegRespon() {
		regResult = parseXmlStr(CanstantValue_protocol.REGISTER_RESPOND_VALUE, readStr);
		if (regResult.equals(CanstantValue_protocol.REGISTER_RESPOND_SUCCESS)) {
			JOptionPane.showMessageDialog(LUI, "ע��ɹ�!");
		} else if (regResult.equals(CanstantValue_protocol.REGISTER_RESPOND_EXISTINGDATA)) {
			JOptionPane.showMessageDialog(LUI, "���û����Ѵ���!");
			
		}
	}

	/**
	 * ���������ж�ȡ������Ϣ
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
		LogUtil.log("�����ж�ȡ����������Ϣ��" + result);
		return result;

	}

	/**
	 * ��Ϣ��װ��
	 * 
	 * @param reqString
	 *            ��Ҫ��װ�Ķ�ά�ַ�������
	 * @return ��װ��ϵ��ַ���
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
	 * ��Ϣ������
	 * 
	 * @param reqString
	 *            ��Ҫ��װ�Ķ�ά�ַ����飬���磺{{"name","kibin"},{"age","12"},{"sex","��"}}
	 * @param ous
	 * @throws IOException
	 */
	public void sendXmlReq(String[][] reqString) throws IOException {
		String s = buildStr(reqString);

		ous.write(s.getBytes());
		LogUtil.log("cs���ݴ������");
	}

	/**
	 * ��Ϣ������������ָ������������ݡ�
	 * 
	 * @param typeStr
	 *            ��ͼ����������
	 * @param ResPonStr
	 *            ��Ҫ�������ַ���
	 * @return ������ϵ�����
	 */

	public String parseXmlStr(String typeStr, String ResPonStr) {

		int startFlag = ResPonStr.indexOf("<" + typeStr + ">");
		int endFlag = ResPonStr.indexOf("</" + typeStr + ">");
		LogUtil.log("�����ָ��ַ�������" + startFlag + "��" + endFlag);
		String realString = ResPonStr.substring(startFlag + typeStr.length() + 2, endFlag);

		return realString;

	}

	/**
	 * ȡ��xml���ĵ���Ҫ����
	 * 
	 * @param ReqString
	 * @return
	 */
	public String getXmlType(String ReqString) {
		return parseXmlStr(CanstantValue_protocol.TYPE, ReqString);
	}

	/**
	 * ��ȡע������Ϣ
	 * 
	 * @return
	 */
	public String getRegResult() {
		return regResult;
	}

	/**
	 * ��ȡ��¼���
	 */
	public String getLogResult() {
		return logResult;
	}

}
