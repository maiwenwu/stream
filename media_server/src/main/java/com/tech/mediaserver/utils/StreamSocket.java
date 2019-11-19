package com.tech.mediaserver.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tech.mediaserver.constant.WebConstant;

import lombok.Data;

public class StreamSocket {
	public static final String CMD_ERROR_MARK_STRING = "ERROR:";
	public static final String CMD_SUCCESS_MARK_STRING = "SUCCESS:";
	public static final String CMD_DIV_MARK_CHAR = "\n";

	public static final int CMD_SUCCESS = 0;
	public static final int CMD_ERROR = -1;

	private static final boolean bTag = false;
	private static final int MAX_CMD_SIZE = 1024 * 64;
	private static final int MAX_SOCKET_BUFFER_SIZE = 1024 * 256;

	private DatagramSocket cmdSocket;

	private byte[] receiveBuffer;
	private DatagramPacket receivePacket;

	public static final ExecutorService executorService = Executors.newFixedThreadPool(5);

	public StreamSocket() throws SocketException
		{
			cmdSocket = new DatagramSocket();
			setSocketSoTimeout(10*1000);
		}

	public StreamSocket(int port) throws SocketException {
			cmdSocket = new DatagramSocket(port);
			setSocketSoTimeout(10*1000);
			setSocketSendBufferSize(MAX_SOCKET_BUFFER_SIZE);
			setSocketReceiveBufferSize(MAX_SOCKET_BUFFER_SIZE);
		}

	public void setSocketSendBufferSize(int iBufSize) throws SocketException {
		cmdSocket.setSendBufferSize(iBufSize);
	}

	public void setSocketReceiveBufferSize(int iBufSize) throws SocketException {
		cmdSocket.setReceiveBufferSize(iBufSize);
	}

	public int getSocketSendBufferSize() throws SocketException {
		return cmdSocket.getSendBufferSize();
	}

	public int getSocketReceiveBufferSize() throws SocketException {
		return cmdSocket.getReceiveBufferSize();
	}

	public void setSocketSoTimeout(int timeout) throws SocketException {
//			cmdSocket.setSoTimeout(timeout);
	}

	public int getSocketSoTimeout(int timeout) throws SocketException {
		return cmdSocket.getSoTimeout();
	}

	public synchronized void sendCommand(final int targetPort, String message) {
		sendCommand("localhost", targetPort, message);
	}

	public synchronized void sendCommand(final String targetAddr, final int targetPort, String message) {
		if (message == null) {
			return;
		}
		final byte[] data = message.getBytes();
		Runnable run = new Runnable() {
			public void run() {
				sendCommand(targetAddr, targetPort, data);
			}
		};
		executorService.execute(run);
//		if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
//			executorService.execute(run);
//		} else {
//			run.run();
//		}
	}

	private void sendCommand(String targetAddr, int targetPort, byte[] message) {
		DatagramPacket sendPacket = null;
		try {
			InetAddress ip = InetAddress.getByName(targetAddr);
			sendPacket = new DatagramPacket(message, message.length, ip, targetPort);
			cmdSocket.send(sendPacket);

		} catch (UnknownHostException e) {
			WebConstant.logger.info("UnknownHostException;" + "send:" + e.getMessage());
		} catch (SocketException e) {
			WebConstant.logger.info("SocketException;" + "send:" + e.getMessage());
		} catch (IOException e) {
			WebConstant.logger.info("IOException;" + "send:" + e.getMessage());
		}
	}

	public String packCommand(String moduleName, int moduleType, String data) {
		final String msg;
		if (data == null || data.length() == 0) {
			msg = String.format(Locale.US, "%s\n%04d\n%s", moduleName, moduleType, "none");
		} else {
			msg = String.format(Locale.US, "%s\n%04d\n%s", moduleName, moduleType, data);
		}
		if (bTag) {
			WebConstant.logger.info("send == " + msg);
		}

		return msg;
	}

	public String packCommand(String moduleName, int moduleType) {
		return packCommand(moduleName, moduleType, "");
	}

	public void closeSocket() {
		if (cmdSocket != null && !cmdSocket.isClosed()) {
			cmdSocket.close();
		}
	}

	public int getLocalSocketPort() {
		return cmdSocket.getLocalPort();
	}

	public String getIpAddress() {
		return cmdSocket.getLocalAddress().getHostAddress();
	}

	public InetAddress getInetAddress() {
		return cmdSocket.getLocalAddress();
	}

	private String receiveMessage() throws IOException {
		return receiveMessage(60 * 1000);
	}

	private String receiveMessage(int timeout) throws IOException {
		if (receiveBuffer == null) {
			receiveBuffer = new byte[MAX_CMD_SIZE];
			receivePacket = new DatagramPacket(receiveBuffer, MAX_CMD_SIZE);
		}
//			cmdSocket.setSoTimeout(timeout);
		try {
			cmdSocket.setSoTimeout(2000);
			cmdSocket.receive(receivePacket);
		} catch (SocketTimeoutException e) {
			return null;
		}
		String msg = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
		return msg;
	}

	public MessageBean receiveMessageSync() {
		String message = null;
		try {
			message = receiveMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bTag) {
		}

		return handleMessageInner(message);
	}

//	public MessageBean receiveMessageSync(int timeout) {
//		if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
//			if (timeout >= 5 * 1000) {
//			}
//			if (!allNetworkOnMain) {
//				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().build());
//				allNetworkOnMain = true;
//			}
//		}
//		String message = null;
//		try {
//			message = receiveMessage(timeout);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (bTag)
//			return handleMessageInner(message);
//	}

	public boolean checkMessageBean(MessageBean msgBean, String module) {
		if (msgBean != null && msgBean.module.equals(module)) {
			return msgBean.flag == CMD_SUCCESS;
		} else {
			return false;
		}
	}

	private MessageBean handleMessageInner(String msg) {
		MessageBean msgBean = null;
		if (msg == null) {
			return msgBean;
		}
		String[] contents = msg.split(CMD_DIV_MARK_CHAR);
		
		if (contents == null || contents.length < 3) {
			return msgBean;
		}
		msgBean = new MessageBean();
		msgBean.module = contents[0];
		msgBean.data = contents[2];
		msgBean.flag = 1;
//		if (contents[2].startsWith(CMD_SUCCESS_MARK_STRING)) {
//			msgBean.cmdType = Integer.parseInt(contents[1]);
//			msgBean.flag = CMD_SUCCESS;
//			String data = contents[2].substring(CMD_SUCCESS_MARK_STRING.length());
//			if (contents.length > 3) {
//				for (int i = 3; i < contents.length; i++) {
//					data += ("\n" + contents[i]);
//				}
//			}
//			msgBean.data = data;
//		} else if (contents[2].startsWith(CMD_ERROR_MARK_STRING)) {
//			msgBean.cmdType = Integer.parseInt(contents[1]);
//			msgBean.flag = CMD_ERROR;
//			msgBean.flag = Integer.parseInt(contents[2].substring(CMD_ERROR_MARK_STRING.length()));
//		}
		return msgBean;
	}

	@Data
	public static class MessageBean {
		public String module = "";
		public int cmdType;
		public int flag;
		public String data = "";
		
	}

}
