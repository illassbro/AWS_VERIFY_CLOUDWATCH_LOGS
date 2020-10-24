//ReadCwLogEvents.java
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.*;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsResponse;
//
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;  

public class ReadCwLogEvents {
    public static void main(String[] args) {

        // Create a CloudWatchLogClient
        Region region = Region.US_EAST_1;
        //Region region = Region.US_EAST_2;

        //INIT_CLIENT
        CloudWatchLogsClient cloudWatchLogsClient = CloudWatchLogsClient.create();
        boolean done = false;

        //NEED_PAGENATION
        System.out.println("[[[ LIST LOGS ]]]");

        //HASH_MAPS
        Map<String,String> map = new HashMap<>();  
        Map<String,String> smap = new HashMap<>();  

        //TOKEN_STRING
        String nextToken = null;

        try {
        //DO-WHILE-LOOP
            do {
                DescribeLogGroupsRequest request = DescribeLogGroupsRequest.builder().nextToken(nextToken).build(); //PAGENATION
                DescribeLogGroupsResponse response = cloudWatchLogsClient.describeLogGroups(request);

                //Class cls = response.getClass();
                //System.out.println("The type of the object is: " + cls.getName());

                //SPLIT RESPONCE
                String str[] = response.toString().split(",");
                System.out.println("size: "+response.logGroups().size());

                //MAKE_LIST
                List<String> logs = new ArrayList<String>();
	        logs = Arrays.asList(str);
               
                //FOREACH
                for(String log: logs){
                     String lstrs[] = log.split("=");
	             for(String lstr: lstrs){
	                  //System.out.println(lstr);
                          map.put(lstrs[0].trim(), lstrs[1].trim()); 
                     }
                     if (Pattern.matches("(?i:.*LogGroupName.*)", log)){
                          System.out.println("::::::::::::::::::::::::::::::::" +map.get("LogGroup(LogGroupName"));
                          //PRINT_LOG_STREAM_INFO
                          try{
                              DescribeLogStreamsRequest stream_request = DescribeLogStreamsRequest.builder().logGroupName(map.get("LogGroup(LogGroupName")).build();
                              String streams[] = cloudWatchLogsClient.describeLogStreams(stream_request).toString().split(",");
                              for (String stream : streams) {
                                  String sstrs[] = stream.split("=");
	                          for(String sstr: sstrs){
                                      smap.put(sstrs[0].trim(), sstrs[1].trim()); 
                                  }
                                  if (Pattern.matches("(?i:.*LogStreamName.*)", stream)){
                                      System.out.println(smap.get("LogStream(LogStreamName"));
                                  }
                              }
                           }catch(Exception e){
                               //DO_NOTHING
                           }
                           //END
                     }
	        }
            nextToken = response.nextToken();
            } while (nextToken != null);

        } catch (CloudWatchException e) {
            //e.getStackTrace();
        }
    }
}



