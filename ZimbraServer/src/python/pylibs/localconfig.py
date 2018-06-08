#
# 
#


from logmsg import *
import commands
import config
import re
import time

class LocalConfig(config.Config):
	def load(self):
		self.loaded = True
		self.config = {}

		t1 = time.clock()
		c = commands.commands["localconfig"]
		rc = c.execute();
		if (rc != 0):
			Log.logMsg(1, "Skipping "+c.desc+" update.");
			Log.logMsg(1, str(c));
			return None
		dt = time.clock()-t1
		Log.logMsg(5,"Localconfig fetched in %.2f seconds (%d entries)" % (dt,len(c.output)))

		if (len(c.output) == 0):
			Log.logMsg(2, "Skipping " + c.desc + " No data returned.")
			c.status = 1
			raise Exception, "Skipping " + c.desc + " No data returned."

		self.config = dict([(k,v) for (k,v) in c.output])

		# Set a default for this
		if self["zmmtaconfig_listen_port"] is None:
			self["zmmtaconfig_listen_port"] = "7171"

		dt = time.clock()-t1
		Log.logMsg(5,"Localconfig loaded in %.2f seconds" % dt)
