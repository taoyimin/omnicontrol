import urllib
import urllib2
import httplib

f = urllib2.urlopen("http://www.baishugroup.com/kegai/ShowClass.asp?ClassID=336")
s = f.read()
print s