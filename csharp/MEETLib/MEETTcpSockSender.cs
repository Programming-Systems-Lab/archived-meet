using System;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Collections;


namespace MEETLib {
	/// <summary>
	/// Generic Socket send thread.  Packets to be sent are placed in queue.
	/// </summary>
	public class MEETTcpSockSender : MEETSockSender {
		
		public override string ClassName {get {return "MEETTcpSockSender";}}

		private byte[] buffer = null;
		private MemoryStream ms = null;
		private BinaryWriter bw = null;		

		
		public MEETTcpSockSender(
			string theName,
			MEETContainer theContainer, 
			MEETSockConnection conn, 
			MEETPCQueue inputQ) : base (theName, theContainer, conn, inputQ)
		{
			buffer = new byte[1400];
			ms = new MemoryStream(buffer);
			bw = new BinaryWriter(ms);
			bw.Write(MEETPacket.MEET_MAGIC);
		}

		protected override void innerloop(object o) {
			try {
				MEETDataPacket msg = (MEETDataPacket)o;
				bw.Seek(2, SeekOrigin.Begin); // reset to just past magic
				bw.Write((ushort)msg.Type);
				bw.Write(msg.Data.Length); // int
				bw.Write(msg.Data);
				int len = (int) bw.Seek(0,SeekOrigin.Current);

				// blocking call
				// kill with m_sock.Close()
				int nBytesSent = m_conn.Sock.Send(buffer, len, SocketFlags.None);
				if (nBytesSent != len) {
					m_parent.WriteLog(LogLevel.WARN, String.Format(
						"{0} Warning: sent {1}, not {2} bytes",
						ClassName, nBytesSent, len));
				}

				Interlocked.Increment(ref m_meetCount);
			} catch (Exception ex) {
				m_errorSrc = ex;				
				// m_node.AlErrorEvents.Add(m_errorSrc.ToString());
				m_done = true;
			}
		}

		protected override void shutdown() {
			m_done = true;
		}		

	}
}
