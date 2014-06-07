import threading
import os
import collections
import socket
import stat
import json
import pyPdf

def exchange(mHost, mFile):
	print 'exchanging...'
	filename = mFile.split('.')[0]
	pdfFile = file('./pdf_to_be_changed/'+filename+'.pdf', 'rb')
	pdfContent = pyPdf.PdfFileReader(pdfFile)
	pageNum = pdfContent.getNumPages()
	pdfFile.close()
	for pageIndex in range(1, pageNum+1):
		os.system('cd ../pdf2html && ./pdf2htmlEX -f '+str(pageIndex)+' -l '+str(pageIndex)+' --process-outline 0 --dest-dir ../documents/'+mHost+'/'+mFile.split('_')[1]+' '+'../control/pdf_to_be_changed/'+filename+'.pdf'+' '+mFile.split('_')[1]+str(pageIndex)+'.html 2> '+'../control/log/'+filename+'.log')
		os.chmod('../documents/'+mHost+'/'+mFile.split('_')[1]+'/'+mFile.split('_')[1]+str(pageIndex)+'.html', stat.S_IRWXU|stat.S_IRWXG|stat.S_IRWXO)
	os.system('mkdir ../documents/'+mHost+'/'+mFile.split('_')[1]+'/complete')
	print 'Done'

class mWorker(threading.Thread):
	def __init__(self, threadPool):
		super(mWorker, self).__init__()
		self.threadPool = threadPool
		self.event = threading.Event() 
		self.target = None
		self.arg = None
		self.started = False

	def setArgs(self, arg, target=exchange):
		self.target = target
		self.arg = arg

	def run(self):
		while True:
			mHost = self.arg['name']
			#mHost = 'veen'
			mFile = self.arg['file']
			if not mFile.split('.')[1] == 'pdf':
				print mFile
				print 'debug'
				os.system('libreoffice --headless --invisible --convert-to pdf --outdir ./pdf_to_be_changed ./raw_files_to_be_changed/'+mFile) 
				#os.system('unoconv -f pdf -o ./pdf_to_be_changed ./raw_files_to_be_changed/'+mFile)
				print 'debug1'
			if not os.path.exists('../documents/' + mHost):
				os.mkdir('../documents/' + mHost)
				os.chmod('../documents/'+mHost, stat.S_IRWXU|stat.S_IRWXG|stat.S_IRWXO)
			if not os.path.exists('/var/www/islider-mobile/documents/' + mHost + '/' + mFile.split('_')[1]):
				os.mkdir('../documents/' + mHost + '/' + mFile.split('_')[1])
				os.chmod('../documents/'+mHost+'/'+mFile.split('_')[1], stat.S_IRWXU|stat.S_IRWXG|stat.S_IRWXO)
			print 'here'
			self.target(mHost, mFile)
			self.threadPool.taskDone(self)
			self.event.clear()
			self.event.wait()


class mThreadPool:
	def __init__(self, threadNum):
		self.idlePool = collections.deque()
		self.trashBin = collections.deque()
		for i in range(threadNum):
			self.idlePool.append(mWorker(self))
		print 'Done'

	def fetch(self):
		if (len(self.trashBin) >= 1) and (not self.trashBin[0].event.isSet()):
			self.idlePool.append(self.trashBin.popleft())
		if not len(self.idlePool) == 0:
			return self.idlePool.popleft()
		else:
			return None

	def taskDone(self, thread):
		self.trashBin.append(thread)


if __name__ == '__main__':
	threadPool = mThreadPool(100)
	#test
	messages = collections.deque([])
	#test
	innerSocket = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
	if os.path.exists('/tmp/UNIX.d'):
		os.unlink('/tmp/UNIX.d')
	innerSocket.bind('/tmp/UNIX.d')
	os.chmod('/tmp/UNIX.d', stat.S_IRWXU|stat.S_IRWXG|stat.S_IRWXO)
	innerSocket.listen(100)
	print 'listening...'
	while True:
		#print messages
		try:
			connection, address = innerSocket.accept()
			message = connection.recv(1024)
			print "message:", message
			mJson = json.loads(message.strip())
			print "json: ", mJson
			messages.append(mJson)
			#print messages
		except:
			pass
		while not len(messages) == 0:
			#print 'ok'
			thread = threadPool.fetch()
			#print thread
			if not thread == None:
				print thread
				#print messages
				thread.setArgs(arg = messages.popleft())
				if not thread.started:
					thread.start()
					thread.started = True
				else:
					thread.event.set()

