package org.net;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.util.IoUtil;
import org.util.Util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class Ssh {

	public int execute(String command) throws SftpException, JSchException,
			IOException {
		Session session = null;
		ChannelExec channel = null;

		try {
			session = createSession("kenchi.no-ip.org", 22, "sing", "abc123");

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.connect();

			while (!channel.isClosed())
				Util.sleep(100);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IoUtil.copyStream(channel.getInputStream(), bos);

			return channel.getExitStatus();
		} finally {
			close(session, channel);
		}
	}

	public void putFile(String src, String dest) throws FileNotFoundException,
			SftpException, JSchException {
		Session session = null;
		ChannelSftp channel = null;

		try {
			session = createSession("kenchi.no-ip.org", 22, "sing", "abc123");

			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			channel.put(new FileInputStream(src), dest);
		} finally {
			close(session, channel);
		}
	}

	private Session createSession(String host, int port, String user,
			final String password) throws JSchException {
		JSch jsch = new JSch();

		Properties config = new Properties();
		config.setProperty("StrictHostKeyChecking", "no");

		Session session = jsch.getSession(user, host, port);
		session.setUserInfo(new UserInfo() {
			public String getPassphrase() {
				return null;
			}

			public String getPassword() {
				return password;
			}

			public boolean promptPassphrase(String arg0) {
				return true;
			}

			public boolean promptPassword(String arg0) {
				return true;
			}

			public boolean promptYesNo(String arg0) {
				return true;
			}

			public void showMessage(String arg0) {
			}
		});
		session.setConfig(config);
		session.connect();
		return session;
	}

	private void close(Session session, Channel channel) {
		if (channel != null)
			channel.disconnect();
		if (session != null)
			session.disconnect();
	}

}
