using System;

namespace MEETLib
{
	/// <summary>
	/// Summary description for MEETTcpSockListener.
	/// </summary>
	public class MEETTcpSockListener : MEETSockListener	{
		public override string ClassName {get {return "MEETTcpSockListener";}}

		public MEETTcpSockListener(
			string theName,
			MEETContainer theContainer, 
			MEETSockConnection conn, 
			MEETPCQueue meetQ
			) : base(theName, theContainer, conn, meetQ) {
		
			//
			// TODO: Add constructor logic here
			//
		}
	}
}
