using System;
using System.Threading;
using System.Collections;
using System.Diagnostics;
using MEETLib;

namespace MEETTest {
	/// <summary>
	/// MEET exerciser
	/// </summary>
	class MEETTest {

		public static MEETPCQueue pcq = null;
		
		public static TestQModule tqm1 = null;
		public static TestModule tm2 = null;
		public static MEETClock mc = null;
		
		public static Random r = null;

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		public static void Main(string[] args) {
			r = new Random();
			MEETNode mn = new MEETNode("TestContainer");
			pcq = new MEETPCQueue(5);
			tqm1 = new TestQModule("tqm1", mn, pcq);
			tm2 = new TestModule("tm2", mn);
			mc = new MEETClock("mc", mn);
			mc.Start();
			tqm1.Start();
			Thread.Sleep(500);
			tm2.Start();
			Thread.Sleep(2000);
			tm2.Stop();
			Thread.Sleep(2000);
			tqm1.Stop();
			Thread.Sleep(1000);
			mn.Shutdown();

			Debug.WriteLine("dumping Log:");
			IList log = mn.Log;
			Debug.WriteLine("dumping Log:");
			foreach (string s in log) {
				Debug.WriteLine(s);
			}
		}
	}

	class TestModule : MEETModule {
		public override string ClassName {get {return "TestModule";}}
		IMEETInputQueue inq = null;
		
		public TestModule(string myName, MEETContainer theContainer) : 
			base(myName, theContainer) {
			inq = MEETTest.tqm1.InputQ;
		}

		protected override void innerloop() {			
			Debug.WriteLine(String.Format("{0}:{1}:{2} enqueueing...", ClassName, m_name, MEETClock.Now));
			inq.Enqueue(String.Format("{0}:{1}:{2} message.", ClassName, m_name, MEETClock.Now));
			Thread.Sleep(MEETTest.r.Next(250, 750));
		}

	}

	class TestQModule : MEETQModule {
		public override string ClassName {get {return "TestQModule";}}
		
		public TestQModule(string myName, MEETContainer theContainer, MEETPCQueue theQ) : 
			base(myName, theContainer, theQ) {}

		protected override void innerloop(object o) {			
			System.Console.WriteLine("{0}:{1}:{2} received string {3}...", 
				ClassName, m_name, MEETClock.Now, (string) o);			
			Thread.Sleep(500);
			System.Console.WriteLine("{0}:{1}:{2} done pseudoprocessing.", 
				ClassName, m_name, MEETClock.Now, (string) o);			
		}

	}

}
