#
# 
#


import os
import signal
import socket
import SocketServer
import threading
import time

import state

from logmsg import *

class ThreadedRequestHandler(SocketServer.BaseRequestHandler):

	def handle(self):

		data = self.request.recv(2048)
		Log.logMsg(5, "Received %s" % data)
		args = data.split()
		if len(args) == 0:
			response = "ERROR UNKNOWN COMMAND"
		elif args[0] == "STATUS":
			response = "SUCCESS ACTIVE"
		elif args[0] == "REWRITE":
			if len(args) < 2:
				response = "ERROR NO SERVICES LISTED"
			else:
				Log.logMsg (5, "LOCK myState.lAction requested")
				state.State.mState.lAction.acquire() # Don't interrupt the rewrite process
				Log.logMsg (5, "LOCK myState.lAction acquired")
				for arg in args[1:]:
					Log.logMsg(3, "Processing rewrite request for %s" % arg)
					state.State.mState.requestedconfig[arg] = arg
				os.kill(os.getpid(),signal.SIGUSR2) # wake up the main thread if it's sleeping
				Log.logMsg (5, "LOCK myState.lAction wait()")
				state.State.mState.lAction.wait()
				Log.logMsg (5, "LOCK myState.lAction released")
				state.State.mState.lAction.release()
				response = "SUCCESS REWRITES COMPLETE"
		else:
			response = "ERROR UNKNOWN COMMAND"

		Log.logMsg(5, "Sending %s" % response)
		self.request.send(response)

class ThreadedStreamServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):

	allow_reuse_address = True

	def shutdown(self):
		Log.logMsg(5, "Removing socket %s" % self.server_address)
