Consistent-Hashing
==================

Problems worthy of attack prove their worth by hitting back.


To get a fair amount of idea about what my implementation is about, go through http://www.tom-e-white.com/2007/11/consistent-hashing.html


The code that i wrote uses the basic algorithm provided by Tim White in the page provided above.


To use this code, provide the total no. of initialServers in server.java file pertaining to which provide the initial server details(i have provided 10 server details).

Then give the initial no. of Orders and the no. of replicas in ConsistentHashTest.java file

The program will give you the ouput as how many orders were remapped to a different server when a server was added or deleted randomly.
