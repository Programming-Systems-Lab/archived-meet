using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using MEETLib;

namespace MEET_2
{
	/// <summary>
	/// Summary description for Connect.
	/// </summary>
	public class Connect : System.Windows.Forms.Form {
		private System.Windows.Forms.Button bConnect;
		private System.Windows.Forms.Button bCancel;
		private System.Windows.Forms.TextBox tbPort;
		private System.Windows.Forms.ListBox lbLocalIP;

		private System.Windows.Forms.CheckBox cbMulticast;
		private System.Windows.Forms.Label label3;

		private ushort m_LocalPort = 0;
		private ushort m_RemotePort = 0;
		private MEETIPEndPoint m_mipLocalEndPoint = null;
		private MEETIPEndPoint m_mipRemoteEndPoint = null;
		
		private MEETNode m_node = null;
		private System.Windows.Forms.Label lLocalIP;
		private System.Windows.Forms.Label lLocalPort;
		private System.Windows.Forms.ComboBox cbRemoteIP;
		private System.Windows.Forms.Label lRemotePort;
		private System.Windows.Forms.TextBox tbRemotePort;
		private System.Windows.Forms.Label label2;
		private IList m_log = null;

		/// <summary>
		/// local endpoint
		/// </summary>
		public MEETIPEndPoint MipLocalEndPoint {
			get { return m_mipLocalEndPoint;}
		}
		/// <summary>
		/// remote we're talking to
		/// </summary>
		public MEETIPEndPoint MipRemoteEndPoint {
			get { return m_mipRemoteEndPoint;}
		}

		/// <summary>
		/// Connect socket to remote system
		/// </summary>
		/// <param name="node"></param>
		public Connect(MEETNode node) {
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();

			//
			// TODO: Add any constructor code after InitializeComponent call
			//

			m_node = node;
			m_log = m_node.Log;


			// discover local IPs
			IPHostEntry localHE = null;
			try {
				localHE = Dns.GetHostByName(Dns.GetHostName());				
			} catch (SocketException se) {
				MessageBox.Show("Error getting list of local IPs: " + se.Message, 
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;
			}
			for (int i=0; i<localHE.AddressList.Length; ++i) {
				lbLocalIP.Items.Add(localHE.AddressList[i]);
			}
			lbLocalIP.SelectedItem = lbLocalIP.Items[0];

			// PNG - make dialog buttons work as expected
			bConnect.DialogResult = DialogResult.OK;
			bCancel.DialogResult = DialogResult.Cancel;	
			cbRemoteIP.Items.AddRange(new object[] {
																							 "128.59.14.137",
																							 "128.59.14.140",
																							 "128.59.14.153",
																							 "10.0.0.100",
																							 "10.0.0.102"
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
			this.bConnect = new System.Windows.Forms.Button();
			this.bCancel = new System.Windows.Forms.Button();
			this.lLocalIP = new System.Windows.Forms.Label();
			this.lLocalPort = new System.Windows.Forms.Label();
			this.tbPort = new System.Windows.Forms.TextBox();
			this.cbMulticast = new System.Windows.Forms.CheckBox();
			this.label3 = new System.Windows.Forms.Label();
			this.cbRemoteIP = new System.Windows.Forms.ComboBox();
			this.lRemotePort = new System.Windows.Forms.Label();
			this.tbRemotePort = new System.Windows.Forms.TextBox();
			this.label2 = new System.Windows.Forms.Label();
			this.lbLocalIP = new System.Windows.Forms.ListBox();
			this.SuspendLayout();
			// 
			// bConnect
			// 
			this.bConnect.Location = new System.Drawing.Point(8, 208);
			this.bConnect.Name = "bConnect";
			this.bConnect.Size = new System.Drawing.Size(56, 20);
			this.bConnect.TabIndex = 7;
			this.bConnect.Text = "Connect";
			this.bConnect.Click += new System.EventHandler(this.bConnect_Click);
			// 
			// bCancel
			// 
			this.bCancel.Location = new System.Drawing.Point(88, 208);
			this.bCancel.Name = "bCancel";
			this.bCancel.Size = new System.Drawing.Size(56, 20);
			this.bCancel.TabIndex = 6;
			this.bCancel.Text = "Cancel";
			this.bCancel.Click += new System.EventHandler(this.bCancel_Click);
			// 
			// lLocalIP
			// 
			this.lLocalIP.Location = new System.Drawing.Point(8, 12);
			this.lLocalIP.Name = "lLocalIP";
			this.lLocalIP.Size = new System.Drawing.Size(56, 16);
			this.lLocalIP.TabIndex = 4;
			this.lLocalIP.Text = " Local IP";
			// 
			// lLocalPort
			// 
			this.lLocalPort.Location = new System.Drawing.Point(8, 42);
			this.lLocalPort.Name = "lLocalPort";
			this.lLocalPort.Size = new System.Drawing.Size(56, 16);
			this.lLocalPort.TabIndex = 2;
			this.lLocalPort.Text = "Local Port";
			// 
			// tbPort
			// 
			this.tbPort.Location = new System.Drawing.Point(80, 40);
			this.tbPort.Name = "tbPort";
			this.tbPort.Size = new System.Drawing.Size(112, 20);
			this.tbPort.TabIndex = 3;
			this.tbPort.Text = "61166";
			// 
			// cbMulticast
			// 
			this.cbMulticast.Location = new System.Drawing.Point(88, 176);
			this.cbMulticast.Name = "cbMulticast";
			this.cbMulticast.Size = new System.Drawing.Size(16, 16);
			this.cbMulticast.TabIndex = 1;
			this.cbMulticast.Text = "checkBox1";
			// 
			// label3
			// 
			this.label3.Location = new System.Drawing.Point(8, 176);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(72, 20);
			this.label3.TabIndex = 0;
			this.label3.Text = "Multicast?";
			// 
			// cbRemoteIP
			// 
			this.cbRemoteIP.Location = new System.Drawing.Point(80, 88);
			this.cbRemoteIP.Name = "cbRemoteIP";
			this.cbRemoteIP.Size = new System.Drawing.Size(144, 21);
			this.cbRemoteIP.TabIndex = 12;
			// 
			// lRemotePort
			// 
			this.lRemotePort.Location = new System.Drawing.Point(0, 136);
			this.lRemotePort.Name = "lRemotePort";
			this.lRemotePort.Size = new System.Drawing.Size(72, 16);
			this.lRemotePort.TabIndex = 9;
			this.lRemotePort.Text = "Remote Port";
			// 
			// tbRemotePort
			// 
			this.tbRemotePort.Location = new System.Drawing.Point(80, 128);
			this.tbRemotePort.Name = "tbRemotePort";
			this.tbRemotePort.Size = new System.Drawing.Size(112, 20);
			this.tbRemotePort.TabIndex = 10;
			this.tbRemotePort.Text = "61166";
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(8, 96);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(59, 16);
			this.label2.TabIndex = 11;
			this.label2.Text = "Remote IP";
			// 
			// lbLocalIP
			// 
			this.lbLocalIP.Location = new System.Drawing.Point(80, 12);
			this.lbLocalIP.Name = "lbLocalIP";
			this.lbLocalIP.Size = new System.Drawing.Size(112, 17);
			this.lbLocalIP.TabIndex = 13;
			// 
			// Connect
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(234, 239);
			this.ControlBox = false;
			this.Controls.Add(this.lbLocalIP);
			this.Controls.Add(this.cbRemoteIP);
			this.Controls.Add(this.lRemotePort);
			this.Controls.Add(this.tbRemotePort);
			this.Controls.Add(this.label2);
			this.Controls.Add(this.label3);
			this.Controls.Add(this.cbMulticast);
			this.Controls.Add(this.lLocalPort);
			this.Controls.Add(this.tbPort);
			this.Controls.Add(this.lLocalIP);
			this.Controls.Add(this.bCancel);
			this.Controls.Add(this.bConnect);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "Connect";
			this.Text = "Connect";
			this.Load += new System.EventHandler(this.Connect_Load);
			this.ResumeLayout(false);

		}
		#endregion

		private void Connect_Load(object sender, System.EventArgs e) {
		
		}

		private void bCancel_Click(object sender, System.EventArgs e) {
		}

		private void bConnect_Click(object sender, System.EventArgs e) {
			IPHostEntry iphe;
			IPAddress locIpaddr;
			m_log.Add("Checking local address...");
			
			if (lbLocalIP.Text.Length == 0) {
				MessageBox.Show("Local IP address required", "Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;
			}
			try {
				iphe = Dns.Resolve(lbLocalIP.Text);
			} 
			catch (Exception ex) {
				MessageBox.Show("Invalid Local IP Address: " + ex.Message, 
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;				
			}
			locIpaddr = iphe.AddressList[0];
			m_log.Add("\tdone.");
			m_log.Add("Checking local port...");			

			if (tbPort.Text.Length == 0) {
				MessageBox.Show("Local UDP Port Required", "Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;
			}
			try {
				m_LocalPort = UInt16.Parse(tbPort.Text);
			} 
			catch (Exception ex) {
				MessageBox.Show("0 < local port < 65536: " + ex.Message,
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;
			}
			m_mipLocalEndPoint = new MEETIPEndPoint(locIpaddr, m_LocalPort);
			
			m_log.Add("\tdone.");

			IPAddress remIpaddr;
			m_log.Add("Checking remote address...");
			
			if (cbRemoteIP.Text.Length == 0) {
				MessageBox.Show("Remote IP address required", "Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;
			}
			try {
				iphe = Dns.Resolve(cbRemoteIP.Text);
			} 
			catch (Exception ex) {
				MessageBox.Show("Invalid Remote IP Address: " + ex.Message, 
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;				
			}
			remIpaddr = iphe.AddressList[0];
			m_log.Add("\tdone.");
			m_log.Add("Checking remote port...");			

			if (tbRemotePort.Text.Length == 0) {
				MessageBox.Show("UDP Remote Port Required", "Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;
			}
			try {
				m_RemotePort = UInt16.Parse(tbRemotePort.Text);
			} 
			catch (Exception ex) {
				MessageBox.Show("0 < remote port < 65536: " + ex.Message,
					"Error",
					MessageBoxButtons.OK,
					MessageBoxIcon.Exclamation,
					MessageBoxDefaultButton.Button1);
				return;
			}
			m_mipRemoteEndPoint = new MEETIPEndPoint(remIpaddr, m_RemotePort);
			m_log.Add("\tdone.");

		}

	}
	
}
