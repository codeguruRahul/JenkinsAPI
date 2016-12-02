package org.custom.jenkins.api;
import org.dom4j.io.*;

import java.awt.Color;
import java.io.*;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.dom4j.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.net.*;
import java.sql.Timestamp;
import java.util.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
public class FetchDetailsUsingJenkinsAPI {
	
	//Constants
	private static ArrayList<String> emailFromList = new ArrayList<String>();
	private static ArrayList<String> emailToList = new ArrayList<String>();
	private static ArrayList<String> emailCcList = new ArrayList<String>();
	private static String baseURL = null;
	private static int numberOfHours = 0;
	public static void main(String[] args) {
		
		emailFromList.add("rahul_kumar@homedepot.com");
		emailToList.add("rahul_kumar@homedepot.com");
		emailToList.add("KAMAL_TEJA@homedepot.com");
		emailCcList.add("rahul_kumar@homedepot.com");
		
		ArrayList<JenkinsAPIBean> listOfSuccessJobRecently = new ArrayList<JenkinsAPIBean>();
		try
		{
        URL url = new URL("http://ln2a42.homedepot.com:8080/api/xml");
        Document dom = new SAXReader().read(url);
        for( Element job : (List<Element>)dom.getRootElement().elements("job")) {
            if(job.elementText("color") != null )
            	{
            	if(job.elementText("color").toString().equalsIgnoreCase("blue") || job.elementText("color").toString().equalsIgnoreCase("red"))
			            {
			            	boolean ifLastRunRecently = new FetchDetailsUsingJenkinsAPI().buildURL(job.elementText("name"));
			            	if(ifLastRunRecently)
			            	{
			            		JenkinsAPIBean jenkinsBeanDTO = new JenkinsAPIBean();
			            		jenkinsBeanDTO.setJobName(job.elementText("name"));
			            		jenkinsBeanDTO.setJobStatus(job.elementText("color").equals("blue") ? "Passed" : "Failed");
			            		listOfSuccessJobRecently.add(jenkinsBeanDTO);
			            		//System.out.println("Passed/Failed Jobs ran in last 24 hour : "+job.elementText("name")+" Status : "+job.elementText("color"));
			            	}
			            }
            	}
        }
        //new FetchDetailsUsingJenkinsAPI().sendEmail(listOfSuccessJobRecently);
        System.out.println("Total Passed/Failed Jobs ran in last 24 hour : "+listOfSuccessJobRecently.size());
        }
		catch(Exception ex)
		{
		ex.printStackTrace();	
		}
	}
	
	public void sendEmail(ArrayList<JenkinsAPIBean> listOfSuccessJobRecently)
    {
		System.out.println("Sending Email");
		String emailBodyHtml = "";
		String imagePath = System.getProperty("user.dir");
		imagePath = imagePath+"/report.jpeg";
		System.out.println(imagePath);
		try
		{
		String emailSubject = "Job Status for last 24 Hours : ln2a42.homedepot.com:8080";
		int numberOfPassedJobs = 0;
		int numberOfFailedJobs = 0;
		for(int i = 0; i< listOfSuccessJobRecently.size(); i++)
		{
			String bgColor = "";
			
			
			if(listOfSuccessJobRecently.get(i).getJobStatus().equals("Passed"))
			{
				bgColor = "green";
				numberOfPassedJobs++;
			}
			else
			{
				bgColor = "red";
				numberOfFailedJobs++;
			}
			//System.out.println(listOfSuccessJobRecently.get(i).getJobStatus());
			emailBodyHtml = emailBodyHtml+"<tr><td style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color: "+bgColor+"\">"+listOfSuccessJobRecently.get(i).getJobName()+"</td><td style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color: "+bgColor+"\">"+listOfSuccessJobRecently.get(i).getJobStatus()+"</td></tr>";
		}
		 new FetchDetailsUsingJenkinsAPI().generateGraph(numberOfPassedJobs, numberOfFailedJobs);
		String emailBody = "<html><head></head><body><p style = \"font-weight: bold; font-size: 14px; font-family: arial;\">Please find the below list of jobs (Passed/failed) executed in last 24 hours : </p>"
				+ "<div style=\"float: left; height:400px; width:600px\"><p style = \"font-weight: bold; font-size: 14px; font-family: arial;\">Jenkins URL : http://ln2a42.homedepot.com:8080</p>"
				+ "<p style = \"font-weight: bold; font-size: 14px; font-family: arial;\">Total Number of Successful (Passed/Failed) Jobs in last 24 Hours : "+listOfSuccessJobRecently.size()+"</p>"
				+ "<p style = \"font-weight: bold; font-size: 14px; font-family: arial;\">Total Number of Passed Jobs in last 24 Hours: "+numberOfPassedJobs+"</p>"
				+ "<p style = \"font-weight: bold; font-size: 14px; font-family: arial;\">Total Number of Failed Jobs in last 24 Hours : "+numberOfFailedJobs+"</p></div>"
				+ "<div style=\"float:right; height:260px; width:370px\"><IMG SRC="+imagePath+"></div><br></br>"
				+ "<table style=\"font-family: arial, sans-serif; border-collapse: collapse; width: 100%;\">"
				+"  <tr>"
				+"   <th style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color: #dddddd\">Job Name</th>"
				+"   <th style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color: #dddddd\">Status</th>"
				+" </tr>"
				+emailBodyHtml
				+"</th>"
				+"</table>"
				+ "<p style = \"font-weight: bold; font-size: 14px; font-family: arial;\">This is a system generated mail. Please don't respond to this mail. Please contact rahul_kumar@homedepot.com for any query.</p>"
				+ "<p style = \"font-weight: bold; font-size: 16px; font-family: arial;\">Thanks,</p>"
				+ "<p style = \"font-weight: bold; font-size: 14px; font-family: arial;\">Rahul.</p>"
				+ "</body></html>";
		System.out.println(emailBody);
		Properties properties = new Properties();
        properties.put("mail.smtp.host", "mail1.homedepot.com");
        Session session = Session.getDefaultInstance(properties);
        Message msg = new MimeMessage(session);
        //_1fa628@homedepot.com
        // from
        for(int i=0; i<emailFromList.size(); i++){
        	 msg.setFrom(new InternetAddress(emailFromList.get(i)));
        }
        
        // to
        Set<Address> toContacts = new HashSet<Address>();
        for(int i=0; i<emailToList.size(); i++){
        	toContacts.add(new InternetAddress(emailToList.get(i)));
        }
        msg.addRecipients(Message.RecipientType.TO, toContacts.toArray(new Address[0]));
        //cc
        if(emailCcList.size() > 0){
        	ArrayList<Address> ccContacts = new ArrayList<Address>();
	        for(int i=0; i<emailCcList.size(); i++){
	        	ccContacts.add(new InternetAddress(emailCcList.get(i)));
	        }
	        msg.addRecipients(Message.RecipientType.CC, ccContacts.toArray(new Address[0]));
        }
        // subject
        msg.setSubject(emailSubject);
        //date
        msg.setSentDate(new Date());
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(new String(emailBody.getBytes(), "iso-8859-1"), "text/html; charset=\"iso-8859-1\"");
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        msg.setContent(multipart);
        Transport.send(msg);
        System.out.println("Email Sent");
    }
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
}
	public boolean buildURL(String param)
	{
		Properties prop = new Properties();
		InputStream input = null;
		String baseURL = "http://ln2a42.homedepot.com:8080/job/";
		String jobURL = baseURL+param+"/api/json";
		boolean returnResponse = false;
		try
		{
			input = new FileInputStream("C:\\temp\\input.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			System.out.println(prop.getProperty("database"));
			System.out.println(prop.getProperty("dbuser"));
			System.out.println(prop.getProperty("dbpassword"));
			jobURL = jobURL.replace(" ", "%20");
			URL job = new URL(jobURL);
			InputStream is = job.openStream();
			JsonReader rdr = Json.createReader(is);
			JsonObject obj = rdr.readObject();
			JsonObject obj1 = obj.getJsonObject("lastBuild");
			jobURL = obj1.getString("url").toString()+"/api/json";
			job = new URL(jobURL);
			is = job.openStream();
			rdr = Json.createReader(is);
			obj = rdr.readObject();
			long jobTimestamp = Long.parseLong(obj.get("timestamp").toString());
			Calendar c = Calendar.getInstance();
			long currentTimestamp = c.getTimeInMillis();
			long dayTimestamp = 1*24*60*60*1000L;
			long dayAgoTimestamp = (currentTimestamp - dayTimestamp);	
			if(jobTimestamp >= dayAgoTimestamp)
			{
				returnResponse =  true;
			}
			else
			{
				returnResponse =  false;
			}
			/*
			 * if(jobTimestamp >= DayAgoTimestamp)
			{
				new FetchDetailsUsingJenkinsAPI().getReport(baseURL, param);
			}*/
			
			/*System.out.println(timestamp);
			Timestamp times = new Timestamp(timestamp);
			System.out.println(times);
			Date date = new Date(times.getTime());
			System.out.println(date);
			Calendar c = Calendar.getInstance();
			long currenttimestamp = c.getTimeInMillis();
			long dayTimestamp = 24*60*60*1000L;
			System.out.println(currenttimestamp - dayTimestamp);
			Timestamp previousDay = new Timestamp(currenttimestamp - dayTimestamp);*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return returnResponse;
	}
	
	public void getReport(String baseURL, String param)
	{
		String reportPath = "/HTML_Report/SummaryReport.html";
		try{
			URL reportURL = new URL(baseURL+param+reportPath);
	        BufferedReader in = new BufferedReader(
	        new InputStreamReader(reportURL.openStream()));
	        BufferedWriter writer = new BufferedWriter(new FileWriter("outputfile.pdf"));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null){
	                writer.write(inputLine);
	        }
	        in.close();
	        writer.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//System.out.println(baseURL+param);
	}
	
	public void generateGraph(int passed, int failed)
	{
		try
		{
		  DefaultPieDataset dataset = new DefaultPieDataset( );
	      dataset.setValue("Passed", new Double( passed ) );
	      dataset.setValue("Failed", new Double( failed ) );
	      JFreeChart chart = ChartFactory.createPieChart(
	         "Job Status in last 24 Hours", // chart title
	         dataset, // data
	         true, // include legend
	         true,
	         false);
	      chart.setBackgroundPaint(java.awt.Color.gray);
	      chart.setBorderVisible(false);
	      PiePlot plot = (PiePlot) chart.getPlot(); 
	      plot.setSectionPaint(0, Color.green);
	      plot.setSectionPaint(1, Color.red);
	      int width = 370; /* Width of the image */
	      int height = 260; /* Height of the image */ 
	      File report = new File( "report.jpeg" ); 
		  ChartUtilities.saveChartAsJPEG( report , chart , width , height );
		}
		catch(Exception ex)
		{
		ex.printStackTrace();	
		}
	}
}
