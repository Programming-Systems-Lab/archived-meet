using System;
using System.IO;

namespace MEETLib
{

	/// <summary>
	/// MEET Basic Type Enumeration
	/// </summary>
	public enum MType : ushort { 
		Announce=1,
		AnnounceReply,
		Advertise,
		Unadvertise,
		Subscribe,
		Unsubscribe,
		UserChannel,
		SysLimit=UserChannel
	}


	/// <summary>
	/// Basic MEET Packet: nothing but type and a Magic
	/// </summary>
	public class MEETPacket
	{
		public const ushort MEET_MAGIC = 0xEEEE;

		// unsafe name?
		private MType m_type = 0;
		public MType Type { get { return m_type; } }
		
		/// <summary>
		///  can't create instance directly.
		/// </summary>
		protected MEETPacket(MType type) {
			m_type = type;
		}

		public string MTypeString {
			get {
				switch (m_type) {
					case MType.Announce:		return "Announce";
					case MType.AnnounceReply:	return "AnnounceReply";
					case MType.Advertise:		return "Advertise";
					case MType.Unadvertise:		return "Unadvertise";
					case MType.Subscribe:		return "Subscribe";
					case MType.Unsubscribe:		return "Unsubscribe";
					case MType.UserChannel:		return "UserChannel";
					default:
						return "User Type " + (ushort)m_type;
				}
			}
		}
	}
}
