using System;
using System.Net;
using System.Net.Sockets;

namespace MEETLib {
	/// <summary>
	/// IP/Port connection pair
	/// </summary>
	public class MEETSockConnection {
		protected MEETIFace m_ifParent = null;
		protected MEETIPEndPoint m_ipepLocal = null;
		public MEETIPEndPoint IpepLocal { get { return m_ipepLocal; } }
		protected MEETIPEndPoint m_ipepRemote = null;
		public MEETIPEndPoint IpepRemote { get { return m_ipepRemote; } }

		protected MEETPCQueue m_inQ = null;
		// TODO: use input and output interfaces to restrict...
		public MEETPCQueue InQ { get { return m_inQ; } }

		protected MEETPCQueue m_outQ = null;
		public MEETPCQueue OutQ { get { return m_outQ; } }



		/// <summary>
		/// packet receiver thread
		/// </summary>
		protected MEETSockListener m_mqmListener = null;		

		/// <summary>
		/// packet sender thread
		/// </summary>
		protected MEETSockSender m_mqmSender = null;	

		public bool IsStream { get { return m_isStream; } }
		protected bool m_isStream = false;
		
		public Socket Sock { get { return m_sock; } }
		protected Socket m_sock = null;

		public virtual Exception ErrorSrc { get { return m_errorSrc; } }
		protected Exception m_errorSrc = null;

		public MEETSockConnection(
			MEETIFace parent, 
			MEETIPEndPoint local, 
			MEETIPEndPoint remote,
			MEETPCQueue theInQ,
			MEETPCQueue theOutQ,
			bool isStream) 
		{
		m_ifParent = parent;
			m_ipepLocal = local;
			m_ipepRemote = remote;			
			m_isStream = isStream;
			m_inQ = theInQ;
			m_outQ = theOutQ;

			try {
				// TODO support for Raw Sockets
				if (m_isStream) {
					m_sock = new Socket(AddressFamily.InterNetwork,
						SocketType.Stream, ProtocolType.Tcp);
				} else {
					m_sock = new Socket(AddressFamily.InterNetwork,
						SocketType.Dgram, ProtocolType.Udp);
				}
				m_sock.Bind(m_ipepLocal);
				m_sock.Connect(m_ipepRemote);
			} catch (Exception ex) {
				m_sock = null;
				m_errorSrc = ex;
			}
		}

		public bool StartListener() {
			if (m_isStream) {
				m_mqmListener = new MEETTcpSockListener("TcpSockListener", m_ifParent, this, m_inQ);

			} else {
				m_mqmListener = new MEETUdpSockListener("UdpSockListener", m_ifParent, this, m_inQ);
			}
			m_mqmListener.Start();
			return true;
		}

		public bool StopListener() {
			m_mqmListener.Stop();
			return true;
		}

		public bool StartSender() {
			if (m_isStream) {
				m_mqmSender = new MEETTcpSockSender("TcpSockSender", m_ifParent, this, m_outQ);

			} else {
				m_mqmSender = new MEETUdpSockSender("UdpSockSender", m_ifParent, this, m_outQ);
			}
			m_mqmSender.Start();
			return true;
		}

		public bool StopSender() {
			m_mqmSender.Stop();
			return true;
		}

		public bool StartBidirectional() {
			StartSender();
			StartListener();
			return true;
		}

		public bool StopBidirectional() {
			StopListener();
			StopSender();
			return true;
		}

		public bool Close() {
			StopBidirectional();
			m_sock.Close();
			return true;
		}

	}
}
