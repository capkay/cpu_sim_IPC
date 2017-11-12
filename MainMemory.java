import java.io.*;
import java.lang.Runtime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Scanner;

public class MainMemory 
{
	public static Pattern init = Pattern.compile("^init");
	public static Pattern exit = Pattern.compile("^exit");
	public static Pattern inst = Pattern.compile("^(\\d+).*");
	public static Pattern new_addr = Pattern.compile("^\\.(\\d+).*");
	public static Pattern read_mem = Pattern.compile("^read (\\d+).*"); // read address
	public static Pattern write_mem = Pattern.compile("^write (\\d+) (\\d+).*"); //write address data
	public static int[] memory = new int[2000];

	public static int rx_cmd(Scanner cmd){
	String cmd_in = null;
	if (cmd.hasNext())
		cmd_in = cmd.nextLine();

	Matcher m_rd = read_mem.matcher(cmd_in);
	Matcher m_wr = write_mem.matcher(cmd_in);
	Matcher m_init = init.matcher(cmd_in);
	Matcher m_exit = exit.matcher(cmd_in);
	if(m_rd.find()){
		int addr = Integer.parseInt(m_rd.group(1));
		tx_rsp(memory[addr]);
	} else if(m_wr.find()){
		int addr = Integer.parseInt(m_wr.group(1));
		int data = Integer.parseInt(m_wr.group(2));
		memory[addr] = data;
		if ( memory[addr] == data )
			tx_rsp("write done");
		else
			tx_rsp("write failed");
	} else if(m_init.find())
		tx_rsp("init done");
	else if(m_exit.find())
		return 0;
	
	return 1;
	}

	public static void tx_rsp(String input){
	System.out.println(input);
	}
	public static void tx_rsp(int input){
	System.out.println(input);
	}
	
	public static void main(String args[])
	{
	File f = new File(args[0]);

	//int[] memory = new int[2000]; 			//full memory
	try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
		String line;
		int i = 0;
		while ((line = br.readLine()) != null) {
			//System.out.println(line);
			Matcher m_inst = inst.matcher(line);
			Matcher m_addr = new_addr.matcher(line);
			if(m_addr.find()){
				//System.out.println("addr");
				i = Integer.parseInt(m_addr.group(1));
			} else if (m_inst.find()){
				//System.out.println("inst");
				//System.out.println(m_inst.group(0));
				memory[i] = Integer.parseInt(m_inst.group(1));
				i++;
			}
		}
	} catch (IOException e) {
		e.printStackTrace();
	}

	Scanner sc = new Scanner(System.in);

	while(rx_cmd(sc) != 0)
	{
	}

	//String init = null;
	//if (sc.hasNext())
		//init = sc.nextLine();
	//System.out.println(init+" done");
	/*
	for (int i = 0; i < memory.length; i++) {
	System.out.println(i+":"+memory[i]);
	}
	*/
     
	}
}
