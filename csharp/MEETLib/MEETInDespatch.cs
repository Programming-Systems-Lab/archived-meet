using System;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Collections;
using System.Text;


namespace MEETLib {
	/// <summary>
	/// despatch incoming packets.
	/// </summary>
	public class MEETInDespatch : MEETQModule {

		private static int m_count = 0;
		public static int Count { get { return m_count; } }

		public override string ClassName {get {return "MEETInDespatch";}}

		public MEETInDespatch(string theName, MEETContainer theContainer, MEETPCQueue theQ) : 
			base(theName, theContainer, theQ) {	}

		protected override void innerloop(object o) {			
			MEETRecvPacket msg = (MEETRecvPacket) o;
			m_parent.WriteLog(LogLevel.DEBUG, String.Format("Received packet type {0} from {1} at {2}:",
				msg.MTypeString, msg.Conn.IpepRemote, msg.RecvTime));
			m_parent.WriteLog(LogLevel.DEBUG, Encoding.UTF8.GetString(msg.Br.ReadBytes(1024)));
			Interlocked.Increment(ref m_count);										
		}
	}
}
