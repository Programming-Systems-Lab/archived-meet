using System;
using System.Net;
using System.Net.Sockets;
using System.Collections;
using System.Threading;
using System.IO;

namespace MEETLib {
	/// <summary>
	/// Summary description for MEETNode.
	/// </summary>
	public class MEETNode : MEETContainer {
		public override string ClassName {get {return "MEETNode";}}

		private IList m_log = null;
		public override IList Log { 
			get { return m_log; } 
			set { m_log = value; }
		}

		protected Hashtable m_ifaces = null;
		/// <summary>
		/// holds the list of network interfaces (indexed by m_IPALocal)
		/// each of which in turn has a hashtable of SockConnections.
		/// </summary>
		public Hashtable IFaces {
			get { return m_ifaces; } 
		}

		public MEETNode(string theName) : base(theName) {
			m_log = new ArrayList(1024);
			m_ifaces = new Hashtable();
		}

		public override void Shutdown() {			
			WriteLog(LogLevel.INFO, String.Format("{0} shutting down...", m_name));
			base.Shutdown();
			foreach (DictionaryEntry de in m_ifaces) {
				( (MEETIFace)(de.Value)).Shutdown();
			}
			WriteLog(LogLevel.INFO, String.Format("{0} shutdown.", m_name));
			
		}

		public override void WriteLog(LogLevel level, string msg) {
			m_log.Add(String.Format("{0} {1}: {2}",
				DateTime.Now.ToString("yyyyMMdd-HH:mm:ss.fff"),
				level, msg));
		}


		/* private UdpListenerThread m_ultListener = null;
		private InHandlerThread m_iht = null;
		// private static EventLog _elLog = null;
		private String m_hostname = null;


		/// <summary>
		/// master input queue of likely MEET packets
		/// </summary>
		public IMEETInputQueue InqMEETEvent { 
			get { return m_inqMEETEvent; } 
			set { m_inqMEETEvent = value; }
		}
		private IMEETInputQueue m_inqMEETEvent = null;

		/// <summary>
		/// master input queue of non-MEET packets
		/// </summary>
		public MEETPCQueue InqOtherEvent {
			get { return m_inqOtherEvent; }
		}
		private MEETPCQueue m_inqOtherEvent = null;

		/// <summary>
		/// master output queue for MEET packets
		/// </summary>
		public MEETPCQueue OutqMEETEvent {
			get { return m_outqMEETEvent; }
		}
		private MEETPCQueue m_outqMEETEvent = null;

		/// <summary>
		/// reserved
		/// </summary>
		public MEETPCQueue OutqOtherEvent {
			get { return m_outqOtherEvent; }
		}
		private MEETPCQueue m_outqOtherEvent = null;

		/// <summary>
		/// Singleton hashtable of (IPEndPoint, MEETModule)
		/// </summary>
		public Hashtable htInterfaces {
			get { return m_htInterfaces; }
		}
		private Hashtable m_htInterfaces = null;
		
		public Hashtable htChannels {
			get { return m_htChannels; }
		}
		private Hashtable m_htChannels = null;

		public IList lstLogEvents {
			get { return m_lstLogEvents; }
			set { m_lstLogEvents = value; }
		}
		private IList m_lstLogEvents = null;

		public IList lstErrorEvents {
			get { return m_lstErrorEvents; }
			set { m_lstErrorEvents = value; }
		}
		private IList m_lstErrorEvents = null;


		public MEETNode() : base() {

			m_inqOtherEvent = new MEETPCQueue();
			m_outqMEETEvent = new MEETPCQueue();
			m_outqOtherEvent = new MEETPCQueue();			
			m_htInterfaces = Hashtable.Synchronized(new Hashtable());			
			m_htChannels = Hashtable.Synchronized(new Hashtable());

			if (m_debug) m_log = Console.Out;

			try {
				// Get the local computer host name.
				m_hostname = Dns.GetHostName();
				m_log.WriteLine("Computer name :{0}", m_hostname);
				IPHostEntry iphe = Dns.GetHostByName(m_hostname);
				m_log.WriteLine("Addresses:");
				foreach (IPAddress ipaddr in iphe.AddressList) {
					m_log.WriteLine("\t" + ipaddr);
					m_htInterfaces.Add(ipaddr, new MEETIFace(ipaddr));
				}
			} catch (Exception ex) {
				m_error.WriteLine(String.Format("{0} {1}: {2}",
					m_className, "Initializing", ex.ToString()));
			}

			foreach (DictionaryEntry deIF in m_htInterfaces) {
				try {
					m_ultListener = new UdpListenerThread(this, (MEETIFace) deIF.Value);
					m_ultListener.start();
				} catch (Exception ex) {
					m_error.WriteLine(String.Format("{0} {1} {2}: {3}",
						m_className, "starting UDP listener on", 
						deIF.Key, ex.ToString()));
				}
			}

			m_iht = new InHandlerThread(this);
			m_iht.start();

			m_log.WriteLine(String.Format("Hostname = {0}", m_hostname));

			//m_node = new MEETNode();
			//String cfloc = AppDomain.CurrentDomain.SetupInformation.ConfigurationFile;
			//m_log.Add(String.Format("config loc = {0}", cfloc));
			//m_log.Add(String.Format("cwd = {0}", Directory.GetCurrentDirectory()));
			//m_hostname = ConfigurationSettings.AppSettings["machineName"];

			/*			
			 string c1 = "shortname";
						string c2 = "http://www.example.com/namespace/sometype";
						MEETChannel mc1 = new MEETChannel(c1);
						m_log.Add(String.Format("hash {0:X}, channel hash {1:X}",
							c1.GetHashCode(), mc1.GetHashCode()));
						MEETChannel mc2 = new MEETChannel(c2);
						m_log.Add(String.Format("hash {0:X}, channel hash {1:X}",
							c2.GetHashCode(), mc2.GetHashCode()));
							*/

/*
		}

		public void Dispose() {			
			m_inqOtherEvent.Dispose();
			m_outqMEETEvent.Dispose();
			m_outqOtherEvent.Dispose();
			m_iht.stop();
			foreach (DictionaryEntry deIF in htInterfaces) {
				MEETIFace iface = (MEETIFace)deIF.Value;
				m_log.WriteLine("stopping iface " + iface.ipaLocal);				
				foreach (DictionaryEntry deIn in iface.htInConns) {
					MEETIPEndPoint ipep = (MEETIPEndPoint)deIn.Key;
					MEETModule module = (MEETModule)deIn.Value;
					m_log.WriteLine("\tstopping " + ipep);				
					module.stop();
				}
				foreach (DictionaryEntry deOut in iface.htOutConns) {
					MEETIPEndPoint ipep = (MEETIPEndPoint)deOut.Key;
					MEETModule module = (MEETModule)deOut.Value;
					m_log.WriteLine("\tstopping " + ipep);				
					module.stop();
				}
			}
			htInterfaces.Clear();
		}
*/
	}
}
