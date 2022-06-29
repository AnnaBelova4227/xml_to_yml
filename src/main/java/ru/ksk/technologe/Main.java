package ru.ksk.technologe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.json.XML;

public class Main {
    private final static Logger log = Logger.getLogger("Main");
    private final static String INPUT_FILE_NAME = "src/main/resources/sample.xml";
    private final static String OUTPUT_FILE_NAME = "src/main/resources/output.yml";

    private static void mapToYaml(Object src, String accum, BufferedWriter writer) {
        if (src instanceof String || src instanceof Integer) {
            writeToFile(accum, src, writer);
            return;
        }
        if (src instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)src;
            for (String key : map.keySet()) {
                mapToYaml(map.get(key), accum + key + ":", writer);
            }
        } else if (src instanceof List) {
            List<Object> list = (List<Object>) src;
            for (Object obj : list) {
                mapToYaml(obj, accum, writer);
            }
        }
    }

    private static void writeToFile(String key, Object src, BufferedWriter writer) {
        String value = src.toString();
        if (value.contains(" ") || value.contains(": ")) {
            if (value.contains("\"")) {
                value = "'" + value + "'";
            } else {
                value = "\"" + value + "\"";
            }
        }
        String resultLine = key + " " + value + "\n";

        try {
            if (resultLine.contains("tns:Product:tns:TechRegs")) {
                log.info("Пропускаем строку " + resultLine.substring(0, resultLine.length() - 1) + " так как строка содержит tns:Product:tns:TechRegs");
            } else {
                writer.write(resultLine);
                log.info("Добавили строку " + resultLine.substring(0, resultLine.length() - 1) + " в файл");
            }
        } catch (IOException e) {
            log.warning("Не смогли записать строку " + resultLine.substring(0, resultLine.length() - 1) + " в файл");
        }
    }

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
        log.info("JSON string " + xmlJSONObj.toString());
        log.info("Сделали временную конвертацию данных из XML в JSON");

        Map<String, Object> map = xmlJSONObj.toMap();
        log.info("Сделали временную конвертацию из JSON в Map");

        log.info("Начало конвертации из Map в YAML строки");
        mapToYaml(map, "", writer);
        log.info("Конец конвертации из Map в YAML строки");

        try {
            writer.close();
        } catch (IOException e) {
            log.warning("Ошибка при закрытии выходного файла");
            System.exit(-1);
        }
        log.info("END");
    }
}
