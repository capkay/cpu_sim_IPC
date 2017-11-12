README:

The code is developed in java. 
Files : 
1. CPU.java :
Just implements a class that creates CPU_core as an object and calls its run method to start the CPU.It is implemented this way so that more than one CPU can be created by creating many CPU cores. The arguments input to the command to run CPU are also checked to see if correct number of arguments are received. 

2. CPU_core.java :
It is the main CPU core code that implements the CPU logic. It also creates and runs the Memory process. Further details can be found in the summary.

3. MainMemory.java :
It contains the memory code, which runs as the child process.


All files should be in the same directory. It can be compiled using the command :

javac CPU.java CPU_core.java MainMemory.java

To run the CPU :

java CPU sample5.txt 30

 It needs to be made sure that the terminal is in fullscreen/maximized view to clearly see the output.
