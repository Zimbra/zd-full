#
# 
#


import os

class Config:
	def __init__(self):
		self.config = {}
		self.progname 	= "zmconfigd"
		if (os.getenv("zimbra_server_hostname") is not None):
			self.hostname 	= os.getenv("zimbra_server_hostname")
		else:
			self.hostname 	= os.popen("/opt/zimbra/bin/zmhostname").readline().strip()
		if (self.hostname is None or self.hostname == ""):
			os._exit(1)
		self.wd_all 	= False
		self.debug 		= False
		self.baseDir	= "/opt/zimbra"
		self.logStatus 	= { 
						4 : "Debug", 
						3 : "Info", 
						2 : "Warning", 
						1 : "Error", 
						0 : "Fatal"
						}
		self.configFile = self.baseDir+"/conf/zmmta.cf";
		self.logFile    = self.baseDir+"/log/"+self.progname+".log";
		self.pidFile    = self.baseDir+"/log/"+self.progname+".pid";
		self.interval 	= 60
		if self.debug:
			self.interval  = 10
		self.restartconfig = False
		self.watchdog 	= True
		self.wd_list	= [ "antivirus" ]
		self.loglevel 	= 3

	def __setitem__(self, key, val):
		self.config[key] = val

	def __getitem__(self, key):
		try:
			return self.config[key]
		except Exception, e:
			return None

	def setVals(self, state):
		self.ldap_is_master = state.localconfig["ldap_is_master"]
		self.ldap_root_password = state.localconfig["ldap_root_password"]
		self.ldap_master_url = state.localconfig["ldap_master_url"]
		self.loglevel 	= 3
		if state.localconfig["zmmtaconfig_log_level"] is not None:
			self.loglevel 	= int(state.localconfig["zmmtaconfig_log_level"])
		self.interval 	= 60
		if state.localconfig["zmmtaconfig_interval"] is not None and state.localconfig["zmmtaconfig_interval"] != "":
			self.interval 	= int(state.localconfig["zmmtaconfig_interval"])
		self.debug 		= False
		if state.localconfig["zmmtaconfig_debug"] is not None:
			self.debug 		= state.localconfig["zmmtaconfig_debug"]
		if state.localconfig["zmmtaconfig_watchdog"] is not None:
			self.watchdog	= (state.localconfig["zmmtaconfig_watchdog"].upper() != "FALSE")
		if state.localconfig["zmmtaconfig_enable_config_restarts"] is not None:
			self.restartconfig = (state.localconfig["zmmtaconfig_enable_config_restarts"].upper() != "FALSE")
		if state.localconfig["zmmtaconfig_watchdog_services"] is not None:
			self.wd_list = state.localconfig["zmmtaconfig_watchdog_services"].split()
