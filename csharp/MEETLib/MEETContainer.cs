using System;
using System.Collections;

namespace MEETLib {

	/// <summary>
	/// levels following log4j model
	/// </summary>
	public enum LogLevel {FATAL=0, ERROR, WARN, INFO, DEBUG}


	/// <summary>
	/// Generic holder of MEETModules.  Only services provided
	/// are shutting everything down and providing a sink for log messages. 
	/// </summary>
	public abstract class MEETContainer {
		public virtual string ClassName {get {return "MEETContainer";}}

		/// <summary>
		/// Allows messages with a log level of DEBUG
		/// </summary>
		protected bool m_debug = true;

		/// <summary>
		/// Instance name
		/// </summary>
		protected string m_name = "AbstractContainer";
		public virtual string Name { get { return m_name; } }
		

		/// <summary>
		/// Write a message to the log
		/// </summary>
		/// <param name="level">level of this message</param>
		/// <param name="msg">String to write to the log</param>
		public abstract void WriteLog(LogLevel level, string msg);

		/// <summary>
		/// property to be defined by subclass
		/// </summary>
		private IList dummy = null;
		public virtual IList Log {
			get {return dummy;}
			set {dummy = value;}
		}
		
		/// <summary>
		/// should only hold MEETModules.  Someday my generics will come...
		/// </summary>
		protected IDictionary m_modules = null;

		public MEETContainer() {
			m_modules = SortedList.Synchronized(new SortedList());
		}

		/// <summary>
		/// create an instance with the given name
		/// </summary>
		/// <param name="theName"></param>
		public MEETContainer (string theName) : this() {		
			m_name = theName;
		}

		/// <summary>
		/// Add a module to this container and start it.  
		/// Requires that the instance have a unique name.
		/// Don't add a single module instance to more than 
		/// one container.
		/// </summary>
		/// <param name="theModule">the MEETModule to be added to this container</param>
		/// <returns>true on success</returns>
		public bool AddModule (MEETModule theModule) {
			if (m_modules.Contains(theModule.Name)) {				
				WriteLog(LogLevel.ERROR, String.Format(
					"{0}: module {1} already exists", m_name, theModule.Name)); 				
				return false;
			} 
			m_modules.Add(theModule.Name, theModule);
			WriteLog(LogLevel.DEBUG, String.Format("{0} added {1}", m_name, theModule.Name));
			return true;				
		}


		/// <summary>
		/// Stop and unregister a module.
		/// </summary>
		/// <param name="theModule"></param>
		/// <returns></returns>
		public bool RemoveModule (MEETModule theModule) {
			if (!m_modules.Contains(theModule.Name)) {				
				WriteLog(LogLevel.ERROR, String.Format(
					"{0}: module {1} not loaded", m_name, theModule.Name)); 				
				return false;
			} 
			if (theModule.Status != ModStatus.STOPPED) {
				WriteLog(LogLevel.ERROR, String.Format(
					"{0}: module {1} still running", m_name, theModule.Name));
				return false;
			} 
			m_modules.Remove(theModule.Name);
			WriteLog(LogLevel.DEBUG, String.Format("{0} removing {1}", m_name, theModule.Name));
			return true;
		}

		public virtual void Shutdown() {			
			foreach (DictionaryEntry de in m_modules) {
				WriteLog(LogLevel.DEBUG, String.Format("{0} shutting down {1}", m_name, de.Key));
				((MEETModule)de.Value).Stop();
			}
			m_modules.Clear();
		}

	}
}
