using System;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Collections;


namespace MEETLib 
{
	/// <summary>
	/// Generic Socket send thread.  Packets to be sent are placed in queue.
	/// </summary>
	public abstract class MEETSockSender : MEETQModule 
	{
		
		protected MEETSockConnection m_conn = null;
		public override string ClassName {get {return "MEETSockSender";}}

		/// <summary>
		/// how many MEET packets have we seen?
		/// </summary>
		public int MeetCount { get {return m_meetCount; } }
		protected static int m_meetCount = 0;
		
		public MEETSockSender(
			string theName,
			MEETContainer theContainer, 
			MEETSockConnection conn, 
			MEETPCQueue inputQ
			) : base(theName, theContainer, inputQ) 
		{			
			m_conn = conn;
		}

		
	}
}
