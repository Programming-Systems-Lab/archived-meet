using System;
using System.IO;


namespace MEETLib
{


	/// <summary>
	/// Short packet structure: m_Type = 1
	/// Constructor from byte array assumes that magic and type have 
	/// already been checked
	/// 
	/// MEET_MAGIC: 2 bytes 0xEEEE
	/// 
	/// then a ushort with top level type info for fastest cut-through
	///		0 = non-propagating peer discovery/negotiation
	///		1-1024 = MEET well-known types
	///		1025-65535 = user types
	///	This virtual port will determine the format of the rest of the 
	///	header (post-length), as well as payload.
	///	
	///	then an int length, negatives and 0 reserved
	///	
	///	
	/// </summary>
	public class MEETDataPacket : MEETPacket {
		
		public const string m_className = "MEETDataPacket";
		const int MAX_DATA_SIZE=1024;
		// const int HDR_SIZE = 16; // header stuff in bytes

		public byte[] Data { get { return m_data; } }
		private byte [] m_data = null;		

		public int DataLength { get { return m_dataLength; } }
		private int m_dataLength = 0;
		
		public MEETDataPacket(MType type, byte[] input) : base(type) {
			if (input.Length > MAX_DATA_SIZE) {
				throw new MEETException(String.Format(
					"{0}: data too long: {1} > {2}",
					m_className, input.Length, MAX_DATA_SIZE));
			} else {
				m_data = input;
			}
		}
	}	 
}
