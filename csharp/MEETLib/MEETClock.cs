using System;
using System.Threading;

namespace MEETLib
{
	/// <summary>
	/// Lower-resolution clock to save unneccessary system calls.
	/// Possibly a useless and premature optimization.
	/// </summary>
	public class MEETClock : MEETModule
	{
		public static DateTime Now = DateTime.Now;

		protected int m_sleepTime = 50;
		public int SleepTime { get { return m_sleepTime; } }

		public MEETClock(string theName, MEETContainer theContainer) :
			base(theName, theContainer) 
		{}

		protected override void innerloop() {
			Now = DateTime.Now;
			Thread.Sleep(m_sleepTime);
		}
			
	}
}
