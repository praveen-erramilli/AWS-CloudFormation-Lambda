import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.Parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CloudFormationStarter implements RequestHandler<Object, Object> {
    @Override
    public Object handleRequest(Object o, Context context) {
        CloudFormationClient client = CloudFormationClient.builder()
                .region(Region.US_EAST_2)
                .build();

        List<Parameter> paramsList = new ArrayList<>(3);
        paramsList.add(Parameter.builder().parameterKey("User").parameterValue("admin").build());
        paramsList.add(Parameter.builder().parameterKey("Password").parameterValue("praveene").build());
        paramsList.add(Parameter.builder().parameterKey("InstanceID").parameterValue("praveendbinstance").build());

        String template = convertStreamToString(CloudFormationStarter.class.getClassLoader().getResourceAsStream("template.json"));

        CreateStackRequest createRequest = CreateStackRequest.builder()
                .stackName("MySQLCreatorStack")
                .templateBody(template)
                .parameters(paramsList)
                .build();
        client.createStack(createRequest);

        client.close();
        return "Started cloudformation execution";
    }

    // Convert a stream into a single, newline separated string
    public static String convertStreamToString(InputStream in) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringbuilder = new StringBuilder();
        String line = null;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringbuilder.append(line + "\n");
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringbuilder.toString();
    }
}
