import java.io.*;
import java.lang.Runtime;
import java.util.Scanner;

public class CPU 
{
	public static void main(String args[])
	{
	try
		{ 
		if (args.length != 2) {
			System.err.println("Usage: java CPU_start <input_file> timer_count");
			System.exit(1);
		}
		CPU_start cpu = new CPU_start(args[0],Integer.parseInt(args[1]));
		cpu.run();
		}
	catch (Throwable t)
		{
			t.printStackTrace();
		}
	
	}
}
