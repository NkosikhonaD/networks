import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;
import cern.jet.random.engine.RandomSeedGenerator;
import cern.jet.random.engine.DRand;

public class MM1QueSimulator {

	 
		final static String FILENAME ="/home/hltuser/assigmmentFile.txt" ;
		static Poisson poisson ;
		static Poisson poisson2;
		static RandomEngine engine;
		static double serviceRate; // this is link/packet size
		static double arrivalRate; // percentage of service rate packets
		static double averageInterArrivalRate; // 1/arrivalRate 
		static double linkCapacity; // given link capacity
		static double packetSizeMean; // given packet mean 
		static double currentTraffic; // calculated based on the arrival percentage of the service rate . 
		static double arrivalPercentage; // increment of 10% 20% ...90%
	public  MM1QueSimulator()
	 {
		  linkCapacity=100000000;
		  packetSizeMean =1000;
		  serviceRate =linkCapacity/packetSizeMean;
		  arrivalRate=(50.0/100.0)*serviceRate ;
		  averageInterArrivalRate = ((1.0/arrivalRate))*1000000;
		  
		  engine =new DRand();
		  poisson =new Poisson(averageInterArrivalRate,engine);
		  poisson2 =new Poisson(packetSizeMean,engine);
		  
		  
	 }
	 /*
	  * Computes service rate , as link capacity divided by package size average
	  * @param lc , link capacity ,
	  * @param packAv average packet size 
	  * 
	  *  returns service rate
	  */
	 
	 public static double calculateServiceRate(double lc,double packAv)
	 {
		 return (lc/packAv);
	 }
	 /*
	  * Computes arrival work load based on percentage of service Rate  100% arrival = service rate , 10 % arrival = 10% of serviceRate , as link capacity divided by package size average
	  * @param servRate , service rate ,
	  * @param percentage percentage 10.0,  20.0...90. 
	  *
	  *  returns arrival Rate or workload
	  */
	 public static double  calculateArrivalRate(double servRate, double percentage)
	 {
		 return (percentage/100)*servRate;
	 }
	 
	 /*Computes arrival Average 1/arrival Rate 
	  * @param arrivalRate  ,arrvial rate . 
	  * return arrival average in micro-seconds that is why its is multiplied by 1000000
	  */
	 public static double calculateArrivalRateAverage(double arrivalRate)
	 {
		 return (1.0/arrivalRate)*1000000; 
	 }
	 
	 
	 public MM1QueSimulator(double lc,double packSizeMean,double arrivRate )
	 {
		 linkCapacity =lc;
		 packetSizeMean=packSizeMean;
		 serviceRate = (lc/packSizeMean);
		 arrivalRate = arrivRate;
		 averageInterArrivalRate = (1.0/arrivalRate)*1000000;
		 
		 engine =new DRand();
		 poisson =new Poisson(averageInterArrivalRate,engine);
		 poisson2 =new Poisson(packetSizeMean,engine);
		 
	 }
	
	/*
	 * PLEASE IGNORE THIS METHOD FOR NOW
	 * MEANT FOR THE LAST PART OF THE PROJECT , NOT YET TESTED .
	 * 
	 */

	public void readFromFile(String filename)
	{
		int totalPackets = 0;
		double newServiceTime = 0;
		
		double traceQueingDelay = 0;
		double traceTransmissionTime = 0;
		
		double AverageQueingTime = 0;
		double AverageArrivalTime = 0;
		double linkRate =1000000;
		
		double arrival1 = 0; 
		double intensity = 0;
		int thousands= 0;
		int packetSize = 0 ;
		float queingTime=0;
		float transmissionTime1 = 0;
		float completeTrans1= 0;
		float temp = 0;
		int counter = 1;
		FileInputStream fstream ;
		  DataInputStream in ;
		  BufferedReader br;
		  String line = "";
		  String[] wordList = null; 
		try
		{
			fstream = new FileInputStream(filename);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in)) ;
			while((line =br.readLine())!=null)
			{
				wordList =line.split(",");
				arrival1 += Double.parseDouble(wordList[0]);
				packetSize = Integer.parseInt(wordList[1]);	
				totalPackets+=packetSize;
				transmissionTime1 = (float) ((packetSize/linkRate));
				traceTransmissionTime+=transmissionTime1;
				
				completeTrans1 = (float) (transmissionTime1 + arrival1);
				if(counter <2)
				{
					System.out.println("packet " +(counter)+" arrial time "+ (arrival1/1000000) +"  queing time "+ queingTime +"  "+" start transmision  "+ completeTrans1+ " " );
				}
				if(temp > arrival1)
				{
					queingTime= (float) (temp-arrival1);
					completeTrans1=temp+transmissionTime1;
					traceQueingDelay=+queingTime;
							
							
				}
				if(temp<=arrival1)
				{
						queingTime = 0;
				}
						
				
				temp = completeTrans1;
				
				if(counter % 1000 ==0)
				{
					newServiceTime =(linkRate/totalPackets);
					AverageQueingTime =(traceQueingDelay/counter);
					AverageArrivalTime= ((arrival1/counter)/1000000); // divide by 1000000 to make micro-seconds
					intensity = (AverageArrivalTime/newServiceTime);
					
					System.out.println("........................................................................................................................................................................ \n");
					System.out.println("\nAverage service time " +newServiceTime  + " Average Arrival Time " + AverageArrivalTime +" Intensity "+ intensity +" Average Queing Time "  + AverageQueingTime  + " "+  " Counter "+ counter );
					
				}
				counter+=1;
			}
		System.out.println("\nTotal counter " +counter);
		in.close();	
			
		}
		catch(Exception e )
		{
			System.out.println(e.getMessage());
		}		
	}
	/* Writes generated Poisson distributed values to a file.
	 * output is printed on console and is written to trace file 
	 * @Param workload is work load : 10.0 ,20.0 up to... 90.0
	 * @Param link linkCapacity, given link capacity ,
	 * @Param averagePacketSize average packet size,
	 * @param  pathToTracefile absolute path where trace file will be saved(please specify full path)
	 * Note serviceRate = linkCapacity /averagePacketSize
	 */		
	public static void writeToFile(double workLoad,double linkCapacity,double averagePacketSize,String pathToTracefile)
	{
		double servRate = calculateServiceRate(linkCapacity,averagePacketSize);
		double arrRate =calculateArrivalRate(servRate, workLoad);
		double averInterArrivalRate =calculateArrivalRateAverage(arrRate);
		Poisson interArrival;
		Poisson packetSize;
		String content="";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathToTracefile,true))) 
		{
			
			System.out.println("Writting to file \n ");
			for(int j=0;j<100;j++) 
			{
				engine = new DRand();
				interArrival =new Poisson(averInterArrivalRate,engine);
				
				packetSize =new Poisson(averagePacketSize,engine);
				for(int k=0;k<50000;k++)
				{
					content=interArrival.nextInt()+","+packetSize.nextInt()+"\n";
					bw.write(content);
					System.out.println(content);	
				}
				
			}
			//content =poisson(meanInterArrival) +","+ poisson(meanPacketSize)+"\n";
			System.out.println("Done");

		} 
		catch (IOException e) 
		{

			e.printStackTrace();

		}
	}
	
	public static void main(String[] args)
	{
		writeToFile(10.0,100000000.0,1000.0,"/home/hltuser/networksLoad10.txt");	

	}

}
