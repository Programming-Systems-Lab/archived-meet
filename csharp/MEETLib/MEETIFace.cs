using System;
using System.Collections;
using System.Net;
using System.Net.Sockets;

namespace MEETLib
{
	/// <summary>
	/// The physical interface, along with associated list of connections.
	/// </summary>
	public class MEETIFace : MEETContainer 
	{
		private MEETNode m_node;

		public Hashtable htConns {
			get { return m_htConns; }
		}
		private Hashtable m_htConns = null;

		public IPAddress ipaLocal {
			get { return m_ipaLocal; }
		}
		private IPAddress m_ipaLocal = null;

		public override string ToString() {
			return m_ipaLocal.ToString();
		}

		public MEETIFace(string theName, IPAddress local, MEETNode theNode) : 
			base(theName) {
			m_node = theNode;
			m_ipaLocal = local;
			m_htConns = Hashtable.Synchronized(new Hashtable());
			WriteLog(LogLevel.INFO, String.Format("IFace {0} created", theName));
		}

		public override void Shutdown() {
			WriteLog(LogLevel.INFO, String.Format("\t{0} shutting down...", m_name));
			base.Shutdown ();
			foreach (DictionaryEntry de in m_htConns) {
				( (MEETSockConnection)(de.Key)).Close();
			}
			WriteLog(LogLevel.INFO, String.Format("\t{0} shutdown.", m_name));


		}


		public override void WriteLog(LogLevel level, string msg) {
			m_node.WriteLog(level, msg);
		}

	}
}
