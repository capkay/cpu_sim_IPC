import java.io.*;
import java.lang.Runtime;
import java.util.Scanner;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class CPU_start 
{
	private int timer_limit = 0;
	private String input_file = null;
	private int PC = 0;
	private int SP = 0;
	private int IR = 0;
	private int AC = 0;
	private int X = 0;
	private int Y = 0;
	private boolean system_mode = false; // user mode cannot access sys
	private boolean timer_exp = false; // user mode cannot access sys
	private boolean end_program = false;
	private boolean intr_en = true;
	private int local = 0; 			//local variable
	private int inst_count = 0; 	// number of instructions executed
	public enum ISA { 
		LOAD_VALUE(1),LOAD_ADDR(2),LOAD_IND_ADDR(3),LOAD_IDX_X_ADDR(4) ,LOAD_IDX_Y_ADDR(5) ,LOAD_SP_X(6),STORE_ADDR(7),GET(8),PUT_PORT(9),ADD_X(10),ADD_Y(11),SUB_X(12),SUB_Y(13),COPY_TO_X(14),COPY_FROM_X(15),COPY_TO_Y(16),COPY_FROM_Y(17),COPY_TO_SP(18),COPY_FROM_SP(19),JUMP_ADDR(20),JUMP_IF_EQUAL_ADDR(21),JUMP_IF_NOT_EQUAL_ADDR(22),CALL_ADDR(23),RET(24),INC_X(25),DEC_X(26),PUSH(27),POP(28),INT(29),IRET(30),END(50);
		private int value;
		private ISA(int value){
			this.value = value;
		}
	};
	private static final Map<Integer, ISA> intToTypeMap = new HashMap<Integer, ISA>();
	static {
		for (ISA type : ISA.values()) {
			intToTypeMap.put(type.value, type);
		}
	}

	public static ISA fromInt(int i) {
		ISA type = intToTypeMap.get(Integer.valueOf(i));
		return type;
	}


	//CPU ISA
	private static final int LOAD_VALUE          	= 1;
	private static final int LOAD_ADDR 			 	= 2;
	private static final int LOAD_IND_ADDR 		 	= 3;
	private static final int LOAD_IDX_X_ADDR     	= 4; 
	private static final int LOAD_IDX_Y_ADDR     	= 5; 
	private static final int LOAD_SP_X 			 	= 6;
	private static final int STORE_ADDR 		 	= 7;
	private static final int GET 					= 8;
	private static final int PUT_PORT 				= 9;
	private static final int ADD_X 					= 10; 
	private static final int ADD_Y 					= 11; 
	private static final int SUB_X 					= 12; 
	private static final int SUB_Y 					= 13; 
	private static final int COPY_TO_X 				= 14;
	private static final int COPY_FROM_X 			= 15;
	private static final int COPY_TO_Y 				= 16;
	private static final int COPY_FROM_Y 			= 17;
	private static final int COPY_TO_SP 			= 18;
	private static final int COPY_FROM_SP 			= 19;
	private static final int JUMP_ADDR 				= 20;
	private static final int JUMP_IF_EQUAL_ADDR 	= 21;
	private static final int JUMP_IF_NOT_EQUAL_ADDR = 22;
	private static final int CALL_ADDR 				= 23;
	private static final int RET 					= 24;
	private static final int INC_X 					= 25;
	private static final int DEC_X 					= 26;
	private static final int PUSH 					= 27;
	private static final int POP 					= 28;
	private static final int INT 					= 29;
	private static final int IRET 					= 30;
	private static final int END 					= 50;

	CPU_start(){
	timer_limit = 0;
	input_file = null;
	PC = 0;
	SP = 999;
	IR = 0;
	AC= 0;
	X = 0;
	Y = 0;
	system_mode = false;
	timer_exp = false;
	end_program = false;
	intr_en = true;
	local = 0;
	inst_count =0;
	}

	CPU_start(String file, int limit){
	timer_limit = limit;
	input_file = file;
	PC = 0;
	SP = 999;
	IR = 0;
	AC= 0;
	X = 0;
	Y = 0;
	system_mode = false;
	timer_exp = false;
	end_program = false;
	intr_en = true;
	local = 0;
	inst_count = 0;
	}
		
	public void mem_tx(PrintWriter p,String cmd){
		p.printf(cmd+"\n");
		p.flush();
	}

	public void print_regs(){
		ISA isa = this.fromInt(IR);
		System.out.println("IR = "+IR+" ~ "+isa);
		System.out.println("AC = "+AC);
		System.out.println("PC = "+PC);
		System.out.println("SP = "+SP);
		System.out.println("X = "+X);
		System.out.println("Y = "+Y);
		System.out.println("timer_exp = "+timer_exp);
		System.out.println("intr_en = "+intr_en);
		System.out.println("system_mode = "+system_mode);
		System.out.println("instruction count = "+inst_count+"\n\n");
	}

	public int read_mem(PrintWriter p,Scanner in,int addr){
		if((system_mode == false) && (addr > 999)){
		System.out.println("Memory access violation : User program tried to access kernel");
		return 0;
		} else {
		p.printf("read "+addr+"\n");
		p.flush();
		return Integer.parseInt(in.nextLine());
		}
	}

	public void write_mem(PrintWriter p,Scanner in,int addr,int data){
		if((system_mode == false) && (addr > 999)){
			System.out.println("Memory access violation : User program tried to modify kernel");
		} else {
		p.printf("write "+addr+" "+data+"\n");
		p.flush();
		//System.out.println(in.nextLine());
		String tmp = in.nextLine();
		}
	}

	public void mem_rx(Scanner in){
		System.out.println(in.nextLine()+"\n\n");
	}
	
	public void run()
	{
	try
		{ 
		File f = new File(input_file);
		if(!f.exists()) {
			System.err.println("Input program file " + input_file + " does not exist");
			System.exit(1);
		}
		Runtime rt = Runtime.getRuntime();

		Process main_mem = rt.exec("java MainMemory " + input_file);

		InputStream mem_resp = main_mem.getInputStream();
		OutputStream mem_cmd = main_mem.getOutputStream();

		PrintWriter pw = new PrintWriter(mem_cmd);
		Scanner sc = new Scanner(mem_resp);
		mem_tx(pw,"init");
    	mem_rx(sc); 
		Random rand = new Random();	
		/*	
		System.out.println(read_mem(pw,sc,0));
		System.out.println(read_mem(pw,sc,1));
		
		write_mem(pw,sc,0,999);

		System.out.println(read_mem(pw,sc,0));
		System.out.println(read_mem(pw,sc,1));
		*/
		do {
			if((timer_exp == true) && (intr_en == true))
				IR = INT;
			else
				IR = read_mem(pw,sc,PC++);

			switch(IR){
			case LOAD_VALUE :
				AC = read_mem(pw,sc,PC++);
				break;
			case LOAD_ADDR :
				AC = read_mem(pw,sc,read_mem(pw,sc,PC++));
				break;
			case LOAD_IND_ADDR :
				AC = read_mem(pw,sc,read_mem(pw,sc,read_mem(pw,sc,PC++)));
				break;
			case LOAD_IDX_X_ADDR :
				AC = read_mem(pw,sc,(read_mem(pw,sc,PC++)+X));
				break;
			case LOAD_IDX_Y_ADDR :
				AC = read_mem(pw,sc,(read_mem(pw,sc,PC++)+Y));
				break;
			case LOAD_SP_X :
				AC = read_mem(pw,sc,(SP+X));
				break;
			case STORE_ADDR :
				write_mem(pw,sc,read_mem(pw,sc,PC++),AC);
				break;
			case GET :
				AC = rand.nextInt(100)+1;
				break;
			case PUT_PORT :
				local = read_mem(pw,sc,PC++);
				if (local == 2){
					//System.out.println("PUT OUTPUT = "+(char)AC);
					System.out.print((char)AC);
				}
				else {
					System.out.print(AC);
					//System.out.println("PUT OUTPUT = "+AC);
				}
				break;
			case ADD_X :
				AC = AC + X;
				break;
			case ADD_Y :
				AC = AC + Y;
				break;
			case SUB_X :
				AC = AC - X;
				break;
			case SUB_Y :
				AC = AC + X;
				break;
			case COPY_TO_X :
				X = AC;
				break;
			case COPY_FROM_X :
				AC = X;
				break;
			case COPY_TO_Y :
				Y = AC;
				break;
			case COPY_FROM_Y :
				AC = Y;
				break;
			case COPY_TO_SP :
				SP = AC;
				break;
			case COPY_FROM_SP :
				AC = SP;
				break;
			case JUMP_ADDR :
				PC = read_mem(pw,sc,PC);
				break;
			case JUMP_IF_EQUAL_ADDR :
				if(AC == 0){PC = read_mem(pw,sc,PC);}
				else
				PC++;
				break;
			case JUMP_IF_NOT_EQUAL_ADDR :
				if(AC != 0){PC = read_mem(pw,sc,PC);}
				else
				PC++;
				break;
			case CALL_ADDR :
				write_mem(pw,sc,SP--,PC+1);
				PC = read_mem(pw,sc,PC);
				break;
			case RET :
				PC = read_mem(pw,sc,++SP);
				break;
			case INC_X :
				++X;
				break;
			case DEC_X :
				--X;
				break;
			case PUSH :
				write_mem(pw,sc,SP--,AC);
				break;
			case POP :
				AC = read_mem(pw,sc,++SP);
				break;
			case INT :
				if(intr_en == true){
				system_mode = true;
				intr_en = false;
				local = SP;
				SP = 1999;
				write_mem(pw,sc,SP--,local);
				write_mem(pw,sc,SP--,PC);
				PC = (timer_exp == true)? 1000 : 1500;
				timer_exp = false;
				} else PC++;
				break;
			case IRET :
				PC = read_mem(pw,sc,++SP);
				SP = read_mem(pw,sc,++SP);
				system_mode = false;
				intr_en = true;
				break;
			case END :
				end_program = true;
				System.out.println("\n\nProgram has completed successfully! CPU is shutting down!!!");
				break;
			
			default :
				System.err.println("\n\nYour CPU has encountered an undefined instruction and will be shut down!!!");
				mem_tx(pw,"exit");
				main_mem.waitFor();
				System.exit(1);
			}
			if(system_mode != true) ++inst_count;
			if (timer_limit == inst_count){
				inst_count = 0;
				timer_exp = true;
			}
			//print_regs();
		}while( end_program != true );
		

		mem_tx(pw,"exit");
		main_mem.waitFor();

		int exitVal = main_mem.exitValue();

		System.out.println("Memory Process exited: " + exitVal);

		}
	catch (Throwable t)
		{
			t.printStackTrace();
		}
	
	}
}
