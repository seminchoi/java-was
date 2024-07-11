package codesquad.http;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicHtml {
    private Map<String, Object> arguments = new HashMap<>();


    public void setArg(String key, Object value) {
        arguments.put(key, value);
    }

    public String process(String html) {
        String result = html;
        result = processEachBlocks(result);
        return result;
    }

    private String processEachBlocks(String html) {
        Pattern pattern = Pattern.compile("\\{dh:each=\"(\\w+)\\s*:\\s*\\$\\{(\\w+)\\}\"\\}([\\s\\S]*?)\\{end-each\\}");
        Matcher matcher = pattern.matcher(html);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String itemName = matcher.group(1);
            String listName = matcher.group(2);
            String blockContent = matcher.group(3);

            List<?> items = (List<?>) arguments.get(listName);
            if (items != null) {
                StringBuilder replacement = new StringBuilder();
                for (Object item : items) {
                    String processedBlock = processBlock(blockContent, itemName, item);
                    replacement.append(processedBlock);
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement.toString()));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String processBlock(String block, String itemName, Object item) {
        Pattern pattern = Pattern.compile("\\$\\{" + itemName + "\\.(\\w+)\\}");
        Matcher matcher = pattern.matcher(block);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = getFieldValue(item, fieldName);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(fieldValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return value != null ? value.toString() : "";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }
}