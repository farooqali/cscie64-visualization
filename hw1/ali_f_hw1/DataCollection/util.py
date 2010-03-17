import re
import socket
import urllib2
from BeautifulSoup import BeautifulSoup

# number of seconds to wait before trying to load the webpage again
socket.setdefaulttimeout(10)

# number of failures to wait before giving up on a page
FAILS = 100

def mysoupopen(url):
    loaded = False
    ret = None
    fails = 0
    while(not loaded):
        try:
            ret = BeautifulSoup(urllib2.urlopen(url))
            loaded = True
        except:
            fails += 1
        if(fails > FAILS):
            break;
    return ret

def cleanhtml(str):
    str = str.replace("&quot;","\""); # &quot; -> "
    str = str.replace("&#174;",""); # registered tradmark symbol
    str = str.replace("&amp;","&"); # &amp; -> &
    str = str.replace("&#34;","\""); # &#34; -> "
    return str;
