using System;
using System.Collections;
using System.Threading;

namespace MEETLib
{
	/// <summary>
	/// Producer/consumer queue.  Dequeue blocks if queue is empty.
	/// </summary>
	public class MEETPCQueue :IMEETInputQueue {
		private Queue m_q = null;
		private Object m_lock = null;
		private bool m_done = false;
		// private bool m_disposed = false;

		/// hard limit on queue size; writes will block if at max.
		/// 0 => no limit
		private int m_sizeLimit = 0;
		public int SizeLimit { get { return m_sizeLimit; } }

		/// <summary>
		/// timeout for thread waiting on queue, to see if m_done has been set
		/// </summary>
		private int m_waitMS = 250;  // 4Hz
		public int WaitMS { get { return m_waitMS; } }

		
		// largest size reached by queue
		private int m_peakSize = 0;
		public int PeakSize { get { return m_peakSize; } }

		public MEETPCQueue() : this(0) {}
	
	    public MEETPCQueue(int sizeLimit) {
			m_sizeLimit = sizeLimit;
			m_q = new Queue();
			m_lock = m_q.SyncRoot;
		}

		public void Enqueue(Object obj) {
			if (!m_done) {
				Monitor.Enter(m_lock);
				if (m_sizeLimit != 0) {
					while (!m_done && (m_sizeLimit == m_q.Count)) {
						Monitor.Wait(m_lock, m_waitMS);
					}
				}
				m_q.Enqueue(obj);
				if (m_q.Count > m_peakSize) {
					m_peakSize = m_q.Count;
				}
				Monitor.Pulse(m_lock);
				Monitor.Exit(m_lock);
			}
		}

		public object Dequeue() {
			object result = null;
			if (!m_done) {
				Monitor.Enter(m_lock);
				while (!m_done && (0 == m_q.Count)) {
					Monitor.Wait(m_lock, m_waitMS);
				}
				if (!m_done) {
					result = m_q.Dequeue();
					if (m_sizeLimit != 0) {
						Monitor.Pulse(m_lock);
					}
				}
				Monitor.Exit(m_lock);				
			}
			return result;
		}

		public int Count {
			get {
				Monitor.Enter(m_lock);
				int result = m_q.Count;
				Monitor.Exit(m_lock);
				return result;
			}
		}

		public void Close() {
			Monitor.Enter(m_lock);			
			m_done = true;
			Monitor.PulseAll(m_lock);  // wakey wakey
			Monitor.Exit(m_lock);								
		}

/*		public void Dispose() {
			Dispose(true);
		}

		/// <summary>
		/// Not sure if all this disposing stuff is necessary,
		/// but I want to make sure at shutdown that no thread is still
		/// waiting around to read the queue.
		/// </summary>
		/// <param name="disposing"></param>
		void Dispose(bool disposing) {
			if (!m_disposed) {
				Monitor.Enter(m_lock);			
				m_done = true;
				Monitor.PulseAll(m_lock);  // wakey wakey
				if (disposing) {
					GC.SuppressFinalize(this);  // once is enough...
				}
				m_disposed = true;
				Monitor.Exit(m_lock);								
			}
		}

		~MEETPCQueue() { Dispose(false); }
		*/


	}
}
