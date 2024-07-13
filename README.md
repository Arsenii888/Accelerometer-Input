# Accelerometer-Input
Android app for send data from accelerometer to server
# Server example
```python
import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(('localhost', 5050))
s.listen(5)
data=0
print('Server is listening')

while True:
    c, addr = s.accept()
    print('Got connection from', addr)
    request = c.recv(1024)
    if request[1]!=None:
        data=request[1]
        if data>=125:
            data-=125
            data*=-1
        print(data)
        
    c.close()
```
