package ru.ksk.technologe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.json.JSONObject;
import org.json.XML;

public class Main {
    private final static Logger log = Logger.getLogger("Main");
    private final static String INPUT_FILE_NAME = "src/main/resources/sample.xml";
    private final static String OUTPUT_FILE_NAME = "src/main/resources/output.yml";

    public static void main(String[] args) {
        log.info("Start");
        String str = null;
        try {
            str = new String(Files.readAllBytes(Paths.get(INPUT_FILE_NAME)));
        } catch (IOException e) {
            log.warning("Ошибка при открытии входного файла");
            System.exit(-1);
        }

        log.info("Прочли входные данные из файла");

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME));
        } catch (IOException e) {
            log.warning("Ошибка при создании выходного файла");
            System.exit(-1);
        }
        log.info("Создали выходной файл для записи данных");

        JSONObject xmlJSONObj = XML.toJSONObject(str);

        JsonNode jsonNodeTree = null;
        try {
            jsonNodeTree = new ObjectMapper().readTree(xmlJSONObj.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        JsonNode jsonNode = jsonNodeTree.get("fsa:ResponseFsaType")
                .get("fsa:RdcTr")
                .get("tns:Product");
        if (jsonNode instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.remove("tns:TechRegs");
        }

        String jsonAsYaml = null;
        try {
            jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
        } catch (JsonProcessingException e) {
            log.warning("ошибка при создании выходного файлв");
            System.exit(-1);
        }

        try {
            writer.write(jsonAsYaml);
        } catch (IOException e) {
            log.warning("ошибка при записи в выходной файл");
            System.exit(-1);
        }
        try {
            writer.close();
        } catch (IOException e) {
            log.warning("ошибка при закрытии выходного файла");
            System.exit(-1);
        }
    }
}
