#
# 
#


from logmsg import *
import commands
import config
import re
import time

class GlobalConfig(config.Config):
	def load(self):
		self.loaded = True
		self.config = {}

		t1 = time.clock()
		c = commands.commands["gacf"]
		rc = c.execute();
		if (rc != 0):
			Log.logMsg(1, "Skipping "+c.desc+" update.");
			Log.logMsg(1, str(c));
			return None

		# if no output was returned we have a potential avoid stopping all services
		if (len(c.output) == 0):
			Log.logMsg(2, "Skipping " + c.desc + " No data returned.")
			c.status = 1
			return None

		self.config = dict([(e.getKey(), e.getValue()) for e in sorted(c.output, key=lambda x: x.getKey())])

		if self["zimbraMtaBlockedExtensionWarnRecipient"] == "TRUE" and self["zimbraAmavisQuarantineAccount"] is not None:
			self["zimbraQuarantineBannedItems"] = 'TRUE'
		else:
			self["zimbraQuarantineBannedItems"] = 'FALSE'

		if self["zimbraSSLExcludeCipherSuites"] is not None:
			v = self["zimbraSSLExcludeCipherSuites"]
			v = str(v)
			self["zimbraSSLExcludeCipherSuites"] = ' '.join(sorted(v.split(), key=str.lower))
			self["zimbraSSLExcludeCipherSuitesXML"] = '\n'.join([''.join(('<Item>',val,'</Item>')) for val in self["zimbraSSLExcludeCipherSuites"].split()])

		if self["zimbraMtaRestriction"] is not None:
			# Remove all the reject_rbl_client lines from MTA restriction and put the values in RBLs
			q = re.sub(r'reject_rbl_client\s+\S+\s+','',self["zimbraMtaRestriction"])
			p = re.findall(r'reject_rbl_client\s+(\S+)',self["zimbraMtaRestriction"])
			self["zimbraMtaRestriction"] = q
			self["zimbraMtaRestrictionRBLs"] = ' '.join(p)

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

		dt = time.clock()-t1
		Log.logMsg(5,"globalconfig loaded in %.2f seconds" % dt)
