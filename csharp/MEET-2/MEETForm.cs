using System;
using System.Configuration;
using System.IO;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;
using System.Data;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using MEETLib;

namespace MEET_2
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class MEETForm : System.Windows.Forms.Form {
		
		private System.Windows.Forms.MainMenu mainMenu1;		

		private System.Windows.Forms.MenuItem mnuFile;
		private System.Windows.Forms.MenuItem mnuConnections;
		private System.Windows.Forms.MenuItem mnuConnect;
		private System.Windows.Forms.MenuItem mnuExit;		

		private System.Windows.Forms.MenuItem mnuChannels;
		private System.Windows.Forms.MenuItem mnuAdvertise;
		private System.Windows.Forms.MenuItem mnuUnadvertise;
		private System.Windows.Forms.MenuItem SepChan1;
		private System.Windows.Forms.MenuItem mnuSubscribe;
		private System.Windows.Forms.MenuItem mnuUnsubscribe;
		private System.Windows.Forms.MenuItem SepChan2;
		private System.Windows.Forms.MenuItem mnuPublish;

		private System.Windows.Forms.ListBox lbConnections;
		private System.Windows.Forms.ComboBox cbChannel;
		private System.Windows.Forms.Label lChannel;
		private System.Windows.Forms.Label lText;
		private System.Windows.Forms.TextBox tbText;
		private System.Windows.Forms.ListBox lbLog;
		
		/// <summary>
		/// Class name
		/// </summary>
		public const string m_name = "MEETForm";
		private IList m_log, m_error;
		private MEETNode m_node = null;
		internal string s_hostname = null;


		/// <summary>
		/// Input form for demo
		/// </summary>
		public MEETForm() {
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			//
			// TODO: Add any constructor code after InitializeComponent call
			//
			m_node = new MEETNode("RootNode");
			m_log = m_node.Log = lbLog.Items;
			m_error = m_node.Log = lbLog.Items; // same one, for now
			try {
				s_hostname = Dns.GetHostName();
			} catch (SocketException se) {
				MessageBox.Show("Error getting local hostname: " + se.Message, 
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				s_hostname = "<unknown>";
			}

			cbChannel.Items.AddRange(new object[] {
													  "http://www.example.com/namespace/types/foo",
													  "MyTypeName",
													  "ChannelZero",
													  "7thCav3rdInf",
													  "Sector12"
												  });

		}
		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing ) {
			base.Dispose( disposing );
		}
		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent() {
			this.mainMenu1 = new System.Windows.Forms.MainMenu();
			this.mnuFile = new System.Windows.Forms.MenuItem();
			this.mnuConnect = new System.Windows.Forms.MenuItem();
			this.mnuConnections = new System.Windows.Forms.MenuItem();
			this.mnuExit = new System.Windows.Forms.MenuItem();
			this.mnuChannels = new System.Windows.Forms.MenuItem();
			this.mnuAdvertise = new System.Windows.Forms.MenuItem();
			this.mnuUnadvertise = new System.Windows.Forms.MenuItem();
			this.SepChan1 = new System.Windows.Forms.MenuItem();
			this.mnuSubscribe = new System.Windows.Forms.MenuItem();
			this.mnuUnsubscribe = new System.Windows.Forms.MenuItem();
			this.SepChan2 = new System.Windows.Forms.MenuItem();
			this.mnuPublish = new System.Windows.Forms.MenuItem();
			this.tbText = new System.Windows.Forms.TextBox();
			this.lChannel = new System.Windows.Forms.Label();
			this.lText = new System.Windows.Forms.Label();
			this.cbChannel = new System.Windows.Forms.ComboBox();
			this.lbLog = new System.Windows.Forms.ListBox();
			this.lbConnections = new System.Windows.Forms.ListBox();
			this.SuspendLayout();
			// 
			// mainMenu1
			// 
			this.mainMenu1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																					  this.mnuFile,
																					  this.mnuChannels});
			// 
			// mnuFile
			// 
			this.mnuFile.Index = 0;
			this.mnuFile.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																					this.mnuConnect,
																					this.mnuConnections,
																					this.mnuExit});
			this.mnuFile.Text = "File";
			// 
			// mnuConnect
			// 
			this.mnuConnect.Index = 0;
			this.mnuConnect.Text = "Connect...";
			this.mnuConnect.Click += new System.EventHandler(this.mnuConnect_Click);
			// 
			// mnuConnections
			// 
			this.mnuConnections.Index = 1;
			this.mnuConnections.Text = "Connections...";
			this.mnuConnections.Click += new System.EventHandler(this.mnuConnections_Click);
			// 
			// mnuExit
			// 
			this.mnuExit.Index = 2;
			this.mnuExit.Text = "Exit";
			this.mnuExit.Click += new System.EventHandler(this.mnuExit_Click);
			// 
			// mnuChannels
			// 
			this.mnuChannels.Index = 1;
			this.mnuChannels.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
																						this.mnuAdvertise,
																						this.mnuUnadvertise,
																						this.SepChan1,
																						this.mnuSubscribe,
																						this.mnuUnsubscribe,
																						this.SepChan2,
																						this.mnuPublish});
			this.mnuChannels.Text = "Channels";
			// 
			// mnuAdvertise
			// 
			this.mnuAdvertise.Index = 0;
			this.mnuAdvertise.Text = "Advertise";
			// 
			// mnuUnadvertise
			// 
			this.mnuUnadvertise.Index = 1;
			this.mnuUnadvertise.Text = "Unadvertise";
			// 
			// SepChan1
			// 
			this.SepChan1.Index = 2;
			this.SepChan1.Text = "-";
			// 
			// mnuSubscribe
			// 
			this.mnuSubscribe.Index = 3;
			this.mnuSubscribe.Text = "Subscribe";
			// 
			// mnuUnsubscribe
			// 
			this.mnuUnsubscribe.Index = 4;
			this.mnuUnsubscribe.Text = "Unsubscribe";
			// 
			// SepChan2
			// 
			this.SepChan2.Index = 5;
			this.SepChan2.Text = "-";
			// 
			// mnuPublish
			// 
			this.mnuPublish.Index = 6;
			this.mnuPublish.Text = "Publish";
			this.mnuPublish.Click += new System.EventHandler(this.mnuPublish_Click);
			// 
			// tbText
			// 
			this.tbText.Location = new System.Drawing.Point(72, 192);
			this.tbText.Multiline = true;
			this.tbText.Name = "tbText";
			this.tbText.Size = new System.Drawing.Size(128, 64);
			this.tbText.TabIndex = 1;
			this.tbText.Text = "";
			// 
			// lChannel
			// 
			this.lChannel.Location = new System.Drawing.Point(8, 160);
			this.lChannel.Name = "lChannel";
			this.lChannel.Size = new System.Drawing.Size(48, 16);
			this.lChannel.TabIndex = 3;
			this.lChannel.Text = "Channel";
			this.lChannel.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// lText
			// 
			this.lText.Location = new System.Drawing.Point(16, 192);
			this.lText.Name = "lText";
			this.lText.Size = new System.Drawing.Size(40, 16);
			this.lText.TabIndex = 4;
			this.lText.Text = "Text";
			this.lText.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// cbChannel
			// 
			this.cbChannel.Location = new System.Drawing.Point(72, 158);
			this.cbChannel.Name = "cbChannel";
			this.cbChannel.Size = new System.Drawing.Size(160, 21);
			this.cbChannel.TabIndex = 9;
			// 
			// lbLog
			// 
			this.lbLog.HorizontalScrollbar = true;
			this.lbLog.Location = new System.Drawing.Point(0, 0);
			this.lbLog.Name = "lbLog";
			this.lbLog.Size = new System.Drawing.Size(424, 69);
			this.lbLog.TabIndex = 10;
			// 
			// lbConnections
			// 
			this.lbConnections.HorizontalScrollbar = true;
			this.lbConnections.Location = new System.Drawing.Point(0, 72);
			this.lbConnections.Name = "lbConnections";
			this.lbConnections.Size = new System.Drawing.Size(424, 69);
			this.lbConnections.TabIndex = 11;
			// 
			// MEETForm
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(440, 270);
			this.Controls.Add(this.lbConnections);
			this.Controls.Add(this.lbLog);
			this.Controls.Add(this.cbChannel);
			this.Controls.Add(this.lText);
			this.Controls.Add(this.lChannel);
			this.Controls.Add(this.tbText);
			this.Menu = this.mainMenu1;
			this.Name = "MEETForm";
			this.Text = "MEET";
			this.ResumeLayout(false);

		}
		#endregion

		/// <summary>
		/// The main entry point for the application.
		/// </summary>

		static void Main() {
			Application.Run(new MEETForm());
		}

		private void mnuExit_Click(object sender, System.EventArgs e) {
			m_node.Shutdown();
			Application.Exit();
		}

		private void mnuConnect_Click(object sender, EventArgs e) {			
			Connect _connect = new Connect(m_node);
			_connect.ShowDialog();
			if (_connect.DialogResult == DialogResult.Cancel) {
				m_log.Add("User selected cancel");
				_connect.Dispose();
				return;
			}			

			MEETIPEndPoint ipepLoc = _connect.MipLocalEndPoint;
			MEETIFace iface = (MEETIFace) m_node.IFaces[ipepLoc.Address];
			if (iface == null) {
				
				iface = new MEETIFace(ipepLoc.Address.ToString(), ipepLoc.Address, m_node);
				m_node.IFaces[ipepLoc.Address] = iface;
				
			}
			MEETIPEndPoint ipepRem = _connect.MipRemoteEndPoint;
			
			// create connection
			MEETPCQueue inQ = new MEETPCQueue();
			MEETPCQueue outQ = new MEETPCQueue();
			MEETSockConnection conn = new MEETSockConnection(iface, ipepLoc, ipepRem, inQ, outQ, false);
			
			// see if it failed
			if (conn.Sock == null) {
				MessageBox.Show("failed to connect to " + ipepRem + ":" +
					conn.ErrorSrc.Message,
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				conn.Close();
				return;
			}

			// check to see if already listed
			if (iface.htConns[conn] != null)
				{
				MessageBox.Show("Already connected to " + ipepRem,
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				conn.Close();
				return;
			}

			// add connection to interface
			// note stupid syntax; brain dead Collection class only sorts Dict objects...
			iface.htConns[conn] = conn;  

			_connect.Dispose();
			
			MEETInDespatch indesp = new MEETInDespatch("udp" + conn.IpepRemote, iface, inQ);
			indesp.Start();
			conn.StartBidirectional();
			m_log.Add("\tdone.");
		}

		private void mnuConnections_Click(object sender, System.EventArgs e) {
			/*
			m_log.Add("current inbound connections:");
			foreach (IPEndPoint ipep in m_node.HtInConnections.Keys) {
				m_log.Add("\t" + ipep);
			}
			m_log.Add("current outbound connections:");
			foreach (IPEndPoint ipep in m_node.HtInConnections.Keys) {
				m_log.Add("\t" + ipep);
			}
*/
		}

		private void mnuPublish_Click(object sender, System.EventArgs e) {
			String s = s_hostname + " says \"" + tbText.Text + "\" at " + DateTime.UtcNow;
			Byte [] buffer = new Byte[s.Length + 1];
			int len = Encoding.UTF8.GetBytes( s.ToCharArray(), 0, s.Length, buffer, 0);
			MEETDataPacket msp = new MEETDataPacket(MType.Announce, buffer);
			// manual broadcast...
			foreach (DictionaryEntry ifEntry in m_node.IFaces) {
				MEETIFace iface = (MEETIFace) ifEntry.Value;
				foreach (DictionaryEntry connEntry in iface.htConns) {
					MEETSockConnection conn = (MEETSockConnection)connEntry.Key;				
					m_log.Add("sending to " + conn.IpepRemote);
					conn.OutQ.Enqueue(msp);		
				}	
			}

		}

		/// <summary>
		/// Keep bottom of list in view
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="ea"></param>
		public void Scroller(object sender, EventArgs ea) {
			ListBox lb = (ListBox) sender;
			ArrayList al = (ArrayList)lb.DataSource;
			lb.TopIndex = Math.Max(0, al.Count - 3);
		}



	}
}
