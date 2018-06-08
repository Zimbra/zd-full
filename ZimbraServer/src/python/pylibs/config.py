#
# 
#


from logmsg import *

class Config:
	def __init__(self):
		self.loaded = False
		self.config = {}

	def __setitem__(self,key,val):
		self.config[key] = val
		return self.config[key]

	def __getitem__(self,key):
		if key in self.config:
			val = self.config[key]
			if isinstance (val, basestring):
				return val
			else:
				return " ".join(val)
		else:
			return None

	def __contains__(self,key):
		return key in self.config

	def load(self):
		self.loaded = True
		self.config = {}

	def dump(self):
		for k in sorted(self.config.iterkeys()):
			print "%s = %s" % (k, self[k])
