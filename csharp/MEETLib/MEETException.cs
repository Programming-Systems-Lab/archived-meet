using System;

namespace MEETLib
{
	/// <summary>
	/// Summary description for MEETException.
	/// </summary>
	public class MEETException : Exception
	{
		public MEETException() : base() {}
		public MEETException(string s) : base(s) {}
		public MEETException(string s, Exception inner) : base(s, inner) {}
		
	}
}
