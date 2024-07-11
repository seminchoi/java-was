package codesquad.template;

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

    private Object getArg(String key) {
        return arguments.getOrDefault(key, "null");
    }

    public String process(String html) {
        String result = html;
        result = processIfBlocks(result);
        result = processEachBlocks(result);
        result = processTextBlocks(result);
        return result;
    }

    private String processIfBlocks(String html) {
        Pattern pattern = Pattern.compile("\\{dh:if=\"\\$\\{(\\w+)\\}\"\\}([\\s\\S]*?)\\{end-if\\}\\s+(?:\\{dh:else\\}([\\s\\S]*?)\\{end-else\\})?");
        Matcher matcher = pattern.matcher(html);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String condition = matcher.group(1);
            String ifContent = matcher.group(2);
            String elseContent = matcher.groupCount() > 2 ? matcher.group(3) : "";

            Boolean conditionValue = (Boolean) arguments.get(condition);
            String replacement;

            if (conditionValue != null && conditionValue) {
                replacement = ifContent;
            } else if (elseContent != null && !elseContent.isEmpty()) {
                replacement = elseContent;
            } else {
                replacement = "";  // else 블록이 없는 경우 빈 문자열로 대체
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String processTextBlocks(String html) {
        Pattern pattern = Pattern.compile("\\{dh:text=\"\\$\\{(\\w+(?:\\.\\w+)?)\\}\"\\}");
        Matcher matcher = pattern.matcher(html);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String[] content = matcher.group(1).split("\\.");
            String itemName = content[0];
            Object item = arguments.get(itemName);
            String fieldValue = item.toString();
            if (content.length == 2) {
                fieldValue = getFieldValue(item, content[1]);
            }
            matcher.appendReplacement(builder, fieldValue);
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String processEachBlocks(String html) {
        Pattern pattern = Pattern.compile("\\{dh:each=\"(\\w+)\\s*:\\s*\\$\\{(\\w+)\\}\"\\}([\\s\\S]*?)\\{end-each\\}");
        Matcher matcher = pattern.matcher(html);
        StringBuilder builder = new StringBuilder();

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
                matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement.toString()));
            }
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String processBlock(String block, String itemName, Object item) {
        Pattern pattern = Pattern.compile("\\$\\{" + itemName + "\\.(\\w+)\\}");
        Matcher matcher = pattern.matcher(block);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = getFieldValue(item, fieldName);
            matcher.appendReplacement(builder, Matcher.quoteReplacement(fieldValue));
        }
        matcher.appendTail(builder);
        return builder.toString();
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