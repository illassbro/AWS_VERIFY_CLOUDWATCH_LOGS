//ReadCwLogEvents.java
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.*;
//import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
//import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
//import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
//import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsRequest;
//import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsResponse;
//import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;
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

        //NEED_PAGENATION
        System.out.println("[[[ LIST LOGS ]]]");

        //VAR
        //boolean done = false;

        //HASH_MAPS
        //Map<String,String> map = new HashMap<>();  
        //Map<String,String> smap = new HashMap<>();  

        //TOKEN_STRING
        String nextToken = null;

        try {
        //DO-WHILE-LOOP
            do {
                DescribeLogGroupsRequest request = DescribeLogGroupsRequest.builder().nextToken(nextToken).build(); //PAGENATION
                DescribeLogGroupsResponse response = cloudWatchLogsClient.describeLogGroups(request);

                //Class cls = response.getClass();
                //System.out.println("The type of the object is: " + cls.getName());

                //USE_CLASS_TO_GET_INFO
                 for ( LogGroup logg : response.logGroups()) {
                     Class clslog = logg.getClass();
                     System.out.println("The type of the object is: " + clslog.getName());
                     System.out.println("[[ LOG_NAME: " + logg.logGroupName()+ " ]] ");
                     try {
                         DescribeLogStreamsRequest stream_request = DescribeLogStreamsRequest.builder().logGroupName(logg.logGroupName()).build();
                         DescribeLogStreamsResponse stream_response = cloudWatchLogsClient.describeLogStreams(stream_request);
                         if (stream_response.hasLogStreams()) {
                             for ( LogStream stream : stream_response.logStreams()) {
                                 System.out.println(stream.logStreamName());
                                 if (Pattern.matches("(?i:.*localhost_access_log.*)", stream.logStreamName() )){
                                     System.out.println("READING: "+ stream.logStreamName());
                                     getCWLogEvebts(cloudWatchLogsClient, logg.logGroupName(), stream.logStreamName());
                                  }
                             }
                             System.out.println("..");
                         } else {
                             System.out.println("..NO_STREAMS..");
                         }
                         }catch(Exception e){
                               //DO_NOTHING
                         }

                 }

            nextToken = response.nextToken();
            } while (nextToken != null);

        } catch (CloudWatchException e) {
            //e.getStackTrace();
        }
    }

    //DUMP_LOG_STREAM_FUNCTION
    public static void getCWLogEvebts(CloudWatchLogsClient cloudWatchLogsClient, String logGroupName, String logStreamName) {
        try {
            GetLogEventsRequest getLogEventsRequest = GetLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .startFromHead(false)
                .build();
            //DUMP_ENTIRE_LOG
            int logLimit = cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().size();

            //DUMP Limited to # Lines
            if (logLimit > 5)
                   logLimit = 5;

            for (int c = 0; c < logLimit; c++) {
                //List<>.get(#) https://www.geeksforgeeks.org/list-get-method-in-java-with-examples
                System.out.println(cloudWatchLogsClient.getLogEvents(getLogEventsRequest).events().get(c).message());
            }
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Successfully got CloudWatch log events!");
    }
}



