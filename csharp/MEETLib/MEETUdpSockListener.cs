using System;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Collections;
using System.Diagnostics;


namespace MEETLib {
	/// <summary>
	/// Summary description for UdpListenerThread.
	/// </summary>
	public class MEETUdpSockListener : MEETSockListener {

		public override string ClassName {get {return "MEETUdpSockListener";}}
		public const int UDPLISTENER_DEFAULT_PORT = 0xEEEE;
		public const int UDP_BUF_SIZE = 4096;
		// protected int m_port = UDPLISTENER_DEFAULT_PORT;


		protected byte[] buffer = null;
		protected byte[] data = null;

		
		// public UdpListenerThread(MEETNode node, MEETIFace iface) 
		//	: this(node, iface, UDPLISTENER_DEFAULT_PORT) {}

		public MEETUdpSockListener(
			string theName,
			MEETContainer theContainer, 
			MEETSockConnection conn, 
			MEETPCQueue meetQ
			) : base(theName, theContainer, conn, meetQ) 
		{	
			buffer = new byte[UDP_BUF_SIZE];
		}		

		protected override void innerloop() {
			try {				
				// blocking call
				// kill socket to release
				int nBytesReceived = m_conn.Sock.Receive(buffer);
				// don't bother reading length: MEETPacket = UDP Packet
				data = new byte[nBytesReceived];
				Array.Copy(buffer, data, nBytesReceived);  // copy to new buffer
				MemoryStream mf = new MemoryStream(data);
				BinaryReader br = new BinaryReader(mf);
				ushort magic = br.ReadUInt16();
				if (MEETPacket.MEET_MAGIC == magic) {
					// it's a MEET packet
					MType type = (MType) br.ReadUInt16();
					Console.WriteLine("endpoint is {0}", m_conn.IpepRemote);
					// right now, calling time on every packet receipt
					// TODO: get time from lo-res clock
					m_recvQ.Enqueue(
						new MEETRecvPacket(type, br, DateTime.UtcNow, m_conn));					
					Interlocked.Increment(ref m_meetCount);
				} else {
					// m_node.InqOtherEvent.Enqueue(data);
					Interlocked.Increment(ref m_otherCount);
				}
				data = null;  // decrement ref count asap?	
			} 
			catch (Exception ex) {
				m_errorSrc = ex;		
				m_parent.WriteLog(LogLevel.ERROR, String.Format("Read Error : {0}", m_errorSrc.Message));
				Console.WriteLine("UDPSockL exception {0}: {1}", m_errorSrc.Message, m_errorSrc.StackTrace);
				Debug.WriteLine(String.Format("Exception from {0}, st {1}", ex.TargetSite, ex.StackTrace));

				// m_node.AlErrorEvents.Add(m_errorSrc.ToString());
				m_done = true;
			}
		}

		protected override void shutdown() {
			m_done = true;
			// m_sock.Close();
		}

	}
}
