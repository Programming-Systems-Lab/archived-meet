using System;
using System.Threading;

namespace MEETLib
{

	/// <summary>
	///  Status of this module
	/// </summary>
	public enum ModStatus {NEW, RUNNING, STOPPED}

	/// <summary>
	/// a generic MEET subsystem: can start, stop, get status.
	/// TODO: reuse after stop?
	/// </summary>
	public abstract class MEETModule {
		
		public virtual string ClassName {get {return "MEETModule";}}
		protected bool m_done = false;


		protected ModStatus m_status = ModStatus.NEW;
		public virtual ModStatus Status { get {return m_status;} }

		protected string m_name = "GenericContainer";
		public virtual string Name { get { return m_name; } }

		protected MEETContainer m_parent = null;
		public virtual MEETContainer Parent { get { return m_parent; } }

		protected Exception m_errorSrc = null;
		public virtual Exception ErrorSrc { get { return m_errorSrc; } }

		protected Thread m_thread = null;


		public MEETModule(string theName, MEETContainer theContainer) {
			m_name = theName;
			if (!theContainer.AddModule(this)) {
				// leave m_parent as null
				return;
			}

			m_parent = theContainer;
			m_status = ModStatus.NEW;
				
			try {				
				m_thread = new Thread(new ThreadStart(run));
				m_thread.Name = m_name;
			} 
			catch (Exception ex) {
				m_status = ModStatus.STOPPED;
				m_errorSrc = ex;
				m_parent.WriteLog(LogLevel.ERROR, String.Format("{0} {1}: {2}",
					m_name, m_status, m_errorSrc.ToString()));
				return;
			}
		}

		public virtual bool Start() { 
			if (m_status != ModStatus.RUNNING) {
				try {				
					m_thread.Start();				
				} catch (Exception ex) {
					m_status = ModStatus.STOPPED;
					m_errorSrc = ex;
					m_parent.WriteLog(LogLevel.ERROR, String.Format("{0} {1}: {2}",
						m_name, m_status, m_errorSrc.ToString()));				
					return false;
				}
				m_status = ModStatus.RUNNING;
			}
			return true;
		}

		public virtual bool Stop() {
			if (m_status != ModStatus.STOPPED) {
				m_done = true;
				try {
					shutdown();
				} catch (Exception ex) {
					m_errorSrc = ex;
					m_parent.WriteLog(LogLevel.ERROR, String.Format("{0} {1}: {2}",
						m_name, m_status, m_errorSrc.ToString()));				
					return false;
				}
				m_status = ModStatus.STOPPED;			
			}
			return true;
		}

		protected virtual void run() {
			m_parent.WriteLog(LogLevel.INFO, String.Format("{0} loop running", m_name));
			while (!m_done) {
				innerloop();
			}
			m_parent.WriteLog(LogLevel.INFO, String.Format("{0} {1}: loop terminated", 
				m_name, m_status));

		}

		/// <summary>
		/// while (!m_done) {...}
		/// </summary>
		protected virtual void innerloop() {}

		/// <summary>
		/// kill anything that could be blocking, and cleanup if necessary
		/// </summary>
		protected virtual void shutdown() {}
		

	}
}
