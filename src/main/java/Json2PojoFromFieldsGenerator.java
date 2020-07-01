import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;


public class Json2PojoFromFieldsGenerator {

    public static final String FOLDER_NAME_FOR_GENERATED_POJO = "generated";

    public static void generatePojoFromUri(String uri, String className, String packageName, String fields) throws IOException {
        final String json = readJson(uri);
        generatePojoFromJson(json, className, packageName, fields);
    }

    public static void generatePojoFromJson(String json, String className, String packageName, String fields) throws IOException {
        generatePojoFromJson(json, className, packageName);
        if (fields != null) {
            generatePojoFromFields(json, fields, className, packageName);
        }
    }

    private static void generatePojoFromJson(String json, String className, String packageName) throws IOException {
        final GenerationConfig config = retrieveDefaultConfig();

        final JCodeModel codeModel = new JCodeModel();
        final SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
        mapper.generate(codeModel, className, packageName, json);

        final File dir = new File(FOLDER_NAME_FOR_GENERATED_POJO);
        if (dir.exists()) {
            Files.walk(Paths.get(FOLDER_NAME_FOR_GENERATED_POJO))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        dir.mkdir();
        codeModel.build(dir);
    }

    private static void generatePojoFromFields(String json, String fields, String className, String packageName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Object object = objectMapper.readValue(json, Object.class);

        objectMapper = Squiggly.init(new ObjectMapper(), fields);
        String jsonObject = SquigglyUtils.stringify(objectMapper, object);
        generatePojoFromJson(jsonObject, className, packageName);
    }

    private static GenerationConfig retrieveDefaultConfig() {
        return new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() {
                return true;
            }

            @Override
            public SourceType getSourceType() {
                return SourceType.JSON;
            }
        };
    }

    private static String readJson(URL url) throws IOException {
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        final int responseCode = con.getResponseCode();

        StringBuilder response = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        return response.toString();
    }

    private static String readJson(String uri) throws IOException {
        final URL obj = new URL(uri);
        return readJson(obj);
    }
}
