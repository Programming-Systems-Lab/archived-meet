using System;
using System.Text;
using System.Security.Cryptography;
using System.IO;

namespace MEETLib
{
	/// <summary>
	/// Represents a channel name.
	/// Hash code is always first four bytes of m_guid.
	/// 
	/// If name (rendered in UTF-8) 16 chars or less, 
	/// m_guid is concatenation of standard string hashcode and
	/// the string itself.
	/// 
	/// Otherwise, m_guid is the SHA1 hash of the (UTF-8) name.
	/// </summary>

	public class MEETChannel
	{
		 
		private string m_name = null;
		public string Name {
			get { return m_name; }
		}

		/// <summary>
		/// 20 byte fixed-length version
		/// </summary>
		private byte[] m_guid = null;
		public byte[] Guid {
			get { return m_guid; }
		}

		public MEETChannel(string name)
		{
			m_name = name;
			byte [] buffer = Encoding.UTF8.GetBytes(m_name);
			if (buffer.Length > 16) {
				SHA1 sha = new SHA1CryptoServiceProvider();  // note: not in WinCE :-(
				m_guid = sha.ComputeHash(buffer);
			} else {
				m_guid = new byte[20];
				MemoryStream ms = new MemoryStream(m_guid);
				BinaryWriter bw = new BinaryWriter(ms);
				bw.Write(m_name.GetHashCode());
				bw.Write(m_name);
			}
		}

		/// <summary>
		/// little-endian?
		/// </summary>
		/// <returns></returns>
		public override int GetHashCode() { 
			return 
				(m_guid[3]<<24) |
				(m_guid[2]<<16) |
				(m_guid[1]<<8) |
				m_guid[0];
		}
				
	}
}
