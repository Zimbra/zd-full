#
# 
#

import os
import conf
import sys
import logging
import time
import logging
import logging.handlers
import re
import threading

lvlmap = {
	5	: logging.DEBUG,
	4	: logging.INFO,
	3	: logging.WARNING,
	2	: logging.ERROR,
	1	: logging.CRITICAL,
	0	: logging.FATAL,
	}

class Log:
	loghandler = None
	sysloghandler = None

	@classmethod
	def initLogging(cls, c = None):
		if c:
			cls.cf = c
			if cls.cf.loglevel > 5:
				cls.cf.loglevel = 5
		else:
			cls.cf = conf.Config()

		fmt = logging.Formatter("%(asctime)s %(name)s %(levelname)s [%(process)d-%(threadName)s] %(message)s")
		sfmt = logging.Formatter("%(name)s %(levelname)s [%(process)d-%(threadName)s-%(thread)d] %(message)s")

		cls.logger = logging.getLogger('zmconfigd')
		cls.logger.setLevel(logging.DEBUG)
		if (cls.loghandler):
			cls.logger.removeHandler(cls.loghandler)
		cls.loghandler = logging.handlers.RotatingFileHandler("/opt/zimbra/log/zmconfigd.log",maxBytes=10000000,backupCount=5)
		cls.loghandler.setFormatter(fmt)
		cls.loghandler.setLevel(lvlmap[cls.cf.loglevel])
		cls.logger.addHandler(cls.loghandler)

		if (cls.sysloghandler):
			cls.logger.removeHandler(cls.sysloghandler)
		cls.sysloghandler = logging.handlers.SysLogHandler(('localhost',514),logging.handlers.SysLogHandler.LOG_LOCAL0)
		cls.sysloghandler.setFormatter(sfmt)
		cls.sysloghandler.setLevel(logging.CRITICAL)

		cls.logger.addHandler(cls.sysloghandler)

	@classmethod
	def logMsg(cls, lvl, msg):

		if lvl > 5:
			lvl = 5
		msg = re.sub(r"\s|\n", " ", msg)
		# print "Logging at %d (%d)" % (lvl, lvlmap[lvl])
		cls.logger.log( lvlmap[lvl], msg) 

		if lvl == 0:
			cls.logger.log( lvlmap[lvl], "%s: shutting down" % (cls.cf.progname,) )
			os._exit(1)

Log.initLogging()
