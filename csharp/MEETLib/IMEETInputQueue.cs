using System;

namespace MEETLib
{
	/// <summary>
	/// Enqueue-only (i.e. sink) interface to MEETPCQueues, for module clients
	/// </summary>
	public interface IMEETInputQueue
	{
		void Enqueue(object obj);
	}
}
