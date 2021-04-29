package com.json2pojofieldsgenerator.customclasses;

import com.json2pojofieldsgenerator.Json2PojoFromFieldsGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class CodeParser {

    public static void removeLineBreaks(String packageName) {
        Arrays.stream(Objects.requireNonNull(
                new File(
                        Json2PojoFromFieldsGenerator.FOLDER_NAME_FOR_GENERATED_POJO + "/" + packageName.replace(".", "/"))
                        .listFiles((f1, name) -> name.toLowerCase().endsWith(".java")))).forEach(file -> {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                removeLineBreaks(file.getName(), reader, file.getAbsolutePath());

                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void removeLineBreaks(String className, BufferedReader fd, String absolutePath) throws IOException {
        StringBuilder newFileContent = new StringBuilder();
        StringBuilder modifiedFragment = new StringBuilder();
        String s;

        while ((s = fd.readLine()) != null) {
            //to reach the desired line "public [className](";
            if (Pattern.matches("^\\s+public\\s" +
                    className.substring(0, className.length() - 5) +
                    "\\($", s)) {
                //to write the first "bad" line that we need to change
                modifiedFragment.append(s);
                break;
            }
            //to write all normal lines
            newFileContent.append(s).append("\n");
        }


        while ((s = fd.readLine()) != null) {
            if (Pattern.matches("^\\s+this.+$", s)) {
                //to write a string without line breaks
                modifiedFragment = new StringBuilder(modifiedFragment.toString().replaceAll("\\r\\n|\\r|\\n", " ")
                        .replaceAll("\\s+", " ")
                        .replaceAll("\\(\\s@", "(@"));
                newFileContent.append(modifiedFragment).append("\n");

                newFileContent.append(s).append("\n");
                break;
            }
            //to form string with line breaks
            modifiedFragment.append(s);
        }

        while ((s = fd.readLine()) != null) {
            //to write the rest of the lines
            newFileContent.append(s).append("\n");
        }

        //to create a new file without line breaks
        FileWriter writer = new FileWriter(absolutePath);
        writer.write(String.valueOf(newFileContent));
        writer.flush();
        writer.close();

    }
}