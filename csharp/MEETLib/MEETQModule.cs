using System;

namespace MEETLib {
	/// <summary>
	/// a MEETModule that will be getting its input from a queue, not an external source 
	/// </summary>
	public abstract class MEETQModule : MEETModule {

		public override string ClassName {get {return "MEETQModule";}}
		
		protected MEETPCQueue m_inputQ = null;

		/// <summary>
		/// return an enqueue-only view
		/// </summary>
		public IMEETInputQueue InputQ { get { return m_inputQ; } }

		public MEETQModule(string theName, MEETContainer theContainer, MEETPCQueue q) : 
			base(theName, theContainer) {
			m_inputQ = q;			
		}

		protected virtual void innerloop(object o) {}

		protected override void run() {
			m_parent.WriteLog(LogLevel.INFO, String.Format("{0} loop running", m_name));
			while (!m_done) {
				object o = m_inputQ.Dequeue();
				// null values are theoretically allowed in the queue;
				// not bothering to distinguish from a true Count=0
				// situation.
				if (null == o) {
					m_done = true;
				} else {
					innerloop(o);
				}
			}
			m_parent.WriteLog(LogLevel.INFO, String.Format("{0} {1}: loop terminated", 
				m_name, m_status));
		}

		public override bool Stop() {
			if (m_status != ModStatus.STOPPED) {
				m_done = true;
				try {
					m_inputQ.Close();
					shutdown();
				} catch (Exception ex) {
					m_errorSrc = ex;
					m_parent.WriteLog(LogLevel.ERROR, String.Format("{0} {1}: {2}",
						m_name, m_status, m_errorSrc.ToString()));				
					return false;
				}
			}
			m_status = ModStatus.STOPPED;			
			return true;
		}


	}
}
