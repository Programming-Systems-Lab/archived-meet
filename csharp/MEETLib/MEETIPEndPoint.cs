using System;
using System.Net;

namespace MEETLib
{
	/// <summary>
	/// Clonable IPEndPoint for those incoming packets
	/// </summary>
	public class MEETIPEndPoint : IPEndPoint, ICloneable {

		public object Clone() {
			return new MEETIPEndPoint(Address, Port);
		}

		public MEETIPEndPoint(long address, int port) : base(address, port) {}
		public MEETIPEndPoint(IPAddress address, int port) : base(address, port) {}
		public MEETIPEndPoint(IPEndPoint ipep) : 
			base(ipep.Address, ipep.Port) {}
	}
}
