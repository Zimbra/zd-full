#
# 
#


from logmsg import *
import commands
import config
import re
import time

class ServerConfig(config.Config):
	def getServices(self, key=None):
		if key is not None:
			if key == "mailboxd":
				key = "mailbox"
			Log.logMsg(5, "Checking service %s in services %s (%s)" % (key, self.serviceconfig, key in self.serviceconfig.keys()))
			return key in self.serviceconfig
		return self.serviceconfig.iterkeys()
                
	def load(self, hostname):
		if (hostname is None):
			raise Exception, "Hostname required"
		self.loaded = True
		self.config = {}
		self.serviceconfig = {}

		t1 = time.clock()
		c = commands.commands["gs"]
		rc = c.execute((hostname,));
		if (rc != 0):
			Log.logMsg(1, "Skipping %s update." % c.desc);
			Log.logMsg(1, str(c));
			return None

		# if no output was returned we have a potential avoid stopping all services
		if (len(c.output) == 0):
			Log.logMsg(2, "Skipping %s - No data returned." % c.desc)
			c.status = 1
			return None

		self.config = dict([(e.getKey(), e.getValue()) for e in sorted(c.output, key=lambda x: x.getKey())])

		if self["zimbraSSLExcludeCipherSuites"] is not None:
			v = self["zimbraSSLExcludeCipherSuites"]
			v = str(v)
			self["zimbraSSLExcludeCipherSuites"] = ' '.join(sorted(v.split(), key=str.lower))
			self["zimbraSSLExcludeCipherSuitesXML"] = '\n'.join([''.join(('<Item>',val,'</Item>')) for val in self["zimbraSSLExcludeCipherSuites"].split()])

		if self["zimbraServiceEnabled"] is not None:
			for v in self["zimbraServiceEnabled"].split():
				self.serviceconfig[v] = "zimbraServiceEnabled"
				if (v == "mailbox"):
					self.serviceconfig["mailboxd"] = "zimbraServiceEnabled"
				elif (v == "mta"):
					self.serviceconfig["sasl"] = "zimbraServiceEnabled"

		if self["zimbraIPMode"] is not None:
			v = self["zimbraIPMode"]
			v = str(v)
			v = v.lower()
			if v == "ipv4":
				self["zimbraPostconfProtocol"] = "ipv4"
			if v == "ipv6":
				self["zimbraPostconfProtocol"] = "ipv6"
			if v == "both":
				self["zimbraPostconfProtocol"] = "all"

		milter = None
		if (self["zimbraMilterServerEnabled"] == "TRUE"):
			if self["zimbraMilterBindAddress"] is None:
				self["zimbraMilterBindAddress"] = "127.0.0.1"
			milter = "inet:%s:%s" % (self["zimbraMilterBindAddress"],self["zimbraMilterBindPort"])

		if self["zimbraMtaSmtpdMilters"] is not None and milter is not None:
			self["zimbraMtaSmtpdMilters"] = "%s, %s" % (self["zimbraMtaSmtpdMilters"], milter)
		elif self["zimbraMtaSmtpdMilters"] is None and milter is not None:
			self["zimbraMtaSmtpdMilters"] = milter

		dt = time.clock()-t1
		Log.logMsg(5,"Serverconfig loaded in %.2f seconds" % dt)
