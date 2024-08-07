//Banking Application

import java.util.*;
import java.io.*;

class Customer{
	int custId;
	int accountNo;
	String name;
	int balance;
	String pwd;
	String encryptedPwd;
	String history = "";
	Customer(int custId,int accountNo,String name,int balance,String pwd,String encryptedPwd){
		this.custId = custId;
		this.accountNo = accountNo;
		this.name = name;
		this.balance = balance;
		this.pwd = pwd;
		this.encryptedPwd = encryptedPwd;
	}
	void decrypt(String encryptedPwd){
		
	}
}

class BankingApplication{
	static ArrayList<Customer> customerDetails;
	static int newId = 44;
	static int newAccNo = 11011;
	static final int defaultBalance = 10000;
	static String dataBase = "";
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args){
		try{
			FileReader file = new FileReader("bank_db.txt");
			BufferedReader reader = new BufferedReader(file);	

			customerDetails = new ArrayList<Customer>();
		
			String line = reader.readLine();
			dataBase += line + "\n";
			line = reader.readLine();
			dataBase += line;
			while(line != null){
				int custId = 0,accountNo = 0,balance = 0;
				String name = "",pwd = "",encryptedPwd = "";
				char[] custData = line.toCharArray();
				if(custData.length == 0)
					break;
				int flag = 0;
				//System.out.println(Arrays.toString(custData));
				for(char c : custData){
					//System.out.println("-"+c + "-" + flag);
					if(c != ' ' && flag == 0){
						custId = custId * 10 + (int)(c - '0');
						continue;
					}
					if(c == ' ') flag++;
					if(flag == 1 && c != ' '){
						accountNo = accountNo * 10 + (int)(c - '0');
					}
					if(flag == 2 && c != ' '){
						name += c;continue;
					}
					if(flag == 3 && c != ' '){
						balance = balance * 10 + (int)(c - '0');continue;
					}
					if(flag == 4)
						encryptedPwd += c;
				}
				line = reader.readLine();
				if(line != null) dataBase += "\n" + line;
				//System.out.println(dataBase);
				pwd = decrypt(encryptedPwd);
				customerDetails.add(new Customer(custId,accountNo,name,balance,pwd,encryptedPwd));
			}
			newId = customerDetails.get(customerDetails.size() - 1).custId;
			displayCustData();
			System.out.println("------------------------------------------------------------------------");
			reader.close();
			
		}catch(Exception e){
			System.out.println(e);
		}
		System.out.println("1.Add new Customer\n2.Withdraw\n3.Deposit\n4.Amount Transfer\n5.Display Customer Details\n6.Enter a negative no to exit.");
		int choice  = sc.nextInt();sc.nextLine();
		while(choice > 0){
			switch(choice){
				case 1:
					addNewCustomer();break;
				case 2:
					withdrawl();break;
				case 3:
					deposit();break;
				case 4:
					amountTransfer();break;
				case 5:
					displayCustData();break;
			}
			System.out.println("------------------------------------------------------------------------");
			System.out.println("1.Add new Customer\n2.Withdraw\n3.Deposit\n4.Amount Transfer\n5.Display Customer Details\n6.Enter a negative no to exit.");
			choice  = sc.nextInt();sc.nextLine();
		}
		//createTranscationHistory(customerDetails.get(0));
	}

	static void addNewCustomer(){
		
		System.out.println("Enter name:");
		String name = sc.nextLine();
		System.out.println("Enter a password:");
		String pwd = sc.nextLine();
		System.out.println("Re-enter password:");
		String reEnteredPwd = sc.nextLine();
		if(!pwd.equals(reEnteredPwd)){
			System.out.println("Password doesn't match. Re-enter:");
			reEnteredPwd = sc.nextLine();
			if(!pwd.equals(reEnteredPwd)){
				System.out.println("Still doesn't match");
				return;
			}
		}
		String encryptedPwd = encrypt(pwd);
		newId += 11;
		newAccNo = 11011 * (newId/11);;
		customerDetails.add(new Customer(newId,newAccNo,name,defaultBalance,pwd,encryptedPwd));
		System.out.println(dataBase);
		dataBase += "\n"+newId+" "+newAccNo+" "+name+" "+defaultBalance+" "+encryptedPwd;
		try{
			FileWriter fileWriter = new FileWriter("bank_db.txt");
			BufferedWriter writer = new BufferedWriter(fileWriter);
			writer.write(dataBase);
			writer.flush();
			writer.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}


	static String decrypt(String encryptedPwd){
		String decrypted = "";
		for(char c : encryptedPwd.toCharArray()){
			if(Character.isDigit(c)){
				int num = (int)(c - '0');
				if(num == 0) num = 9;
				else num--;
				decrypted += num;
			}else if(Character.isLetter(c)){
				if(c == 'A' || c == 'a') c += 25;
				else c = (char)((int)c - 1);
				decrypted += c;
			}
		}
		return decrypted;
	}
	static String encrypt(String pwd){
		String encrypted = "";
		for(char c : pwd.toCharArray()){
			if(Character.isDigit(c)){
				int num = (int)(c - '0');
				if(num == 9) num = 0;
				else num++;
				encrypted += num;
			}else if(Character.isLetter(c)){
				if(c == 'Z' || c == 'z') c -= 25;
				else c = (char)((int)c + 1);
				encrypted += c;
			}
		}
		return encrypted;
	}
	static void displayCustData(){
		System.out.println("Cust Id	Account No	Name	Balance	Password	Encrypted Password");
		for(Customer customer : customerDetails)
			System.out.println(customer.custId+"	"+customer.accountNo+"	"+customer.name+"	"+customer.balance+"	"+customer.pwd+"	"+customer.encryptedPwd);
	}

	static Customer authenticate(String name,String pwd){
		for(Customer customer : customerDetails){
			if(customer.name.equals(name)){
				if(customer.pwd.equals(pwd))
					return customer;
				else{
					System.out.println("Password doesn't match.");
					return null;
				}
			}
		}
		System.out.println("No user named "+name+" found");
		return null;
	}

	static void withdrawl(){
		System.out.println("---------------Withdrawl---------------");
		System.out.println("Enter name:");
		String name = sc.nextLine();
		System.out.println("Enter password:");
		String pwd = sc.nextLine();

		Customer customer = authenticate(name,pwd);
		if(customer == null)
			return;

		System.out.println("Enter amount to withdraw:");
		int amount = sc.nextInt();
		sc.nextLine();
		if(customer.balance - amount > 1000){
			System.out.println("Withdrawl success.");
			customer.balance -= amount;
		}else
			System.out.println("Insufficient Balance");
	}
	
	
	static void deposit(){
		System.out.println("---------------Deposit---------------");
		System.out.println("Enter name:");
		String name = sc.nextLine();
		System.out.println("Enter password:");
		String pwd = sc.nextLine();

		Customer customer = authenticate(name,pwd);
		if(customer == null)
			return;

		System.out.println("Enter amount to deposit:");
		int amount = sc.nextInt();
		sc.nextLine();
		
		System.out.println("Deposit success.");
		customer.balance += amount;
		
	}
	static void amountTransfer(){
		System.out.println("---------------Amount Transfer---------------");
		System.out.println("Enter name:");
		String name = sc.nextLine();
		System.out.println("Enter password:");
		String pwd = sc.nextLine();

		Customer customer = authenticate(name,pwd);
		if(customer == null)
			return;

		System.out.println("Enter To account No:");
		int toAccNo = sc.nextInt();
		
		sc.nextLine();
		for(Customer customer2 : customerDetails){
			if(customer2.accountNo == toAccNo){
				System.out.println("Enter amount to transfer:");
				int amount = sc.nextInt();
				if(customer.balance - amount < 1000){
					System.out.println("Can't transfer. Insufficient balance.");
					return;
				}
				customer.balance -= amount;
				customer2.balance += amount;
				System.out.println("Transfer successful.");
			}
		}
	}

	/*static void updateHistory(Customer customer){
		FileWriter fileWriter = new FileWriter("customer"+customer.custId+".txt");
		BufferedWriter writer = new BufferedWriter(fileWriter);
		writer.write(customer.history);
		
	}
	
	static void createTranscationHistory(Customer customer){
		try{customer.history = "		Account Statement\n		Name - "+customer.name+"\n		Account No - "+customer.accountNo+"\n		Customer Id - "+customer.custId+"\nTransID	TransType	Amount	Balance";
		String fileName = "customer"+customer.custId;
		File file = new File(fileName+".txt");
		file.createNewFile();
		}catch(Exception e){
			System.out.println(e);
		}
	}*/
}