using System;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Collections;


namespace MEETLib {
	/// <summary>
	/// Generic Socket listener.  Puts meet packets into target queue
	/// </summary>
	public abstract class MEETSockListener : MEETModule {
		protected MEETSockConnection m_conn = null;
		protected MEETPCQueue m_recvQ = null;
		public override string ClassName {get {return "MEETSockListener";}}


		/// <summary>
		/// how many MEET packets have we seen?
		/// </summary>
		public int MeetCount { get {return m_meetCount; } }
		protected static int m_meetCount = 0;

		/// <summary>
		/// how many non-MEET packets have we seen?
		/// </summary>
		public int OtherCount { get {return m_otherCount; } }
		protected static int m_otherCount = 0;
		
		// public UdpListenerThread(MEETNode node, MEETIFace iface) 
		//	: this(node, iface, UDPLISTENER_DEFAULT_PORT) {}

		public MEETSockListener(
			string theName,
			MEETContainer theContainer, 
			MEETSockConnection conn, 
			MEETPCQueue recvQ
			) : base(theName, theContainer) 
		{	
			m_conn = conn;
			m_recvQ = recvQ;
		}		

	}
}
