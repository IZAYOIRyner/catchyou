from socket import *
import time
import random

current_milli_time = lambda: int(round(time.time() * 1000))
interval = 2200

hosts = '127.0.0.1'
port = 9090
addr = (hosts, port)
uss = socket(AF_INET, SOCK_DGRAM)
uss.bind(addr)

while True:
  print 'udp Server waiting 2 players'
  data,add1 = uss.recvfrom(1024)
  print data,add1
  print 'udp Server waiting 1 player'
  data,add2 = uss.recvfrom(1024)
  print data,add2
  uss.sendto(bytearray([1]), add1)
  uss.sendto(bytearray([2]), add2)
  lastWall = current_milli_time()
  lastTool = lastWall + 1500
  
  while True:
      print 'Running Game'
      
      current = current_milli_time()
      if (current - lastWall) > interval:
        lastWall += interval
        pos = random.randint(0,3)
        uss.sendto(bytearray([3,pos]), add1)
        uss.sendto(bytearray([3,pos]), add2)
      if (current - lastTool) > interval:
        lastTool += interval
        print 'send tool?'
        if random.random() > 0.4:
          print 'yes!'
          toolpos = random.randint(0,100)
          if random.random() > 0.35:
            tooltype = 1
          else:
            tooltype = 2
          print tooltype,"&",toolpos
          uss.sendto(bytearray([4,tooltype,toolpos]), add1)
          uss.sendto(bytearray([4,tooltype,toolpos]), add2)
          
      data,addc = uss.recvfrom(1024)
      print data,addc
      id = int(data[0].encode('hex'), 16)
      if id == 0:
        uss.sendto(bytearray([0]), addc)
      elif id == 1:
        add1 = addc
        uss.sendto(data, add1)
        uss.sendto(data, add2)
      elif id == 2:
        add2 = addc
        uss.sendto(data, add1)
        uss.sendto(data, add2)
      elif id == 3:
        id = int(data[1].encode('hex'), 16)
        if id == 1:
          uss.sendto(bytearray([5,0]), add1)
          uss.sendto(bytearray([5,1]), add2)
        elif id == 2:
          uss.sendto(bytearray([5,1]), add1)
          uss.sendto(bytearray([5,0]), add2)
        break

uss.close()