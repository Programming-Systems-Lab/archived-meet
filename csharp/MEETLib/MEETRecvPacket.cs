using System;
using System.IO;

namespace MEETLib {
	/// <summary>
	/// extension of MEETPacket for received packets
	/// 
	/// At the moment, sibling of MEETDataPacket, rather than child.  Don't want to
	/// read the data array more than once.
	/// </summary>
	public class MEETRecvPacket : MEETPacket {
		public const int MAX_DATA_SIZE=1024;

		/// handle to packet data.  Enqueued incoming data will have current position
		///   four bytes in (magic and type read).
		private BinaryReader m_br = null;
		public BinaryReader Br { get { return m_br; } }

		private DateTime m_recvTime = DateTime.UtcNow;
		public DateTime RecvTime { get { return m_recvTime; } }

		private MEETSockConnection m_conn = null;
		public MEETSockConnection Conn { get { return m_conn; } }

		public MEETRecvPacket(MType type, BinaryReader br, DateTime dt, 
			MEETSockConnection conn) : base(type) {
			m_br = br;
			m_recvTime = dt;
			m_conn = conn;
		}

	}
}
